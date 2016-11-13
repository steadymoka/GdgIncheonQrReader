package com.moka.gdgqrr.vp.camera

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import com.google.firebase.database.*
import com.google.zxing.Result
import com.moka.framework.base.BaseFragment
import com.moka.framework.extenstion.showToast
import com.moka.framework.extenstion.workInBackground
import com.moka.framework.util.MLog
import com.moka.framework.util.ScreenUtil
import com.moka.framework.util.TextUtil
import com.moka.framework.widget.dialog.AlertDialogNoButtonFragment
import com.moka.gdgqrr.R
import com.moka.gdgqrr.model.User
import com.moka.gdgqrr.server.Api
import com.moka.gdgqrr.vp.attendee.AttendeeListActivity
import kotlinx.android.synthetic.main.fragment_camera.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import permissions.dispatcher.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

@RuntimePermissions
class CameraFragment : BaseFragment(), ZXingScannerView.ResultHandler {

    private lateinit var mDatabase: DatabaseReference
    private var mScannerView: ZXingScannerView? = null

    /**
     * LifeCycle
     */

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val rootView = inflater!!.inflate(R.layout.fragment_camera, container, false)

        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mDatabase = FirebaseDatabase.getInstance().reference
        initZxingView()
        textView_search.setOnClickListener {
            activity.startActivity(Intent(activity, AttendeeListActivity::class.java))
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out_short)
        }
    }

    override fun onResume() {
        super.onResume()
        CameraFragmentPermissionsDispatcher.checkCameraWithCheck(this)
    }

    override fun onPause() {
        super.onPause()
        workInBackground { mScannerView?.stopCamera() }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        CameraFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults)
    }

    /**
     */

    private fun initZxingView() {
        mScannerView = ZXingScannerView(activity)
        frameLayout_qrReader.layoutParams = RelativeLayout.LayoutParams(ScreenUtil.getWidthPixels(activity), ScreenUtil.getWidthPixels(activity))
        frameLayout_qrReader.addView(mScannerView)
    }

    override fun handleResult(rawResult: Result) {
        MLog.deb(rawResult.text)
        MLog.deb(rawResult.barcodeFormat.toString())

        val idArray = rawResult.text.split(":")
        if (idArray.size < 2) {
            mScannerView?.resumeCameraPreview(this@CameraFragment)
            return
        }

        val id = idArray[1]
        mDatabase.child("attendees").child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                val visitText: String
                if (user.isVisit == 0)
                    visitText = "\n\n${user.email!!}\n아직 참석확인이 되지 않았어요"
                else
                    visitText = "\n\n${user.email!!}\n이미 참석하신 분입니다"

                AlertDialogNoButtonFragment
                        .newInstance()
                        .setOkText("참석")
                        .setOkTextVisible(user.isVisit == 0)
                        .setMessage(TextUtil.afterSmall(user.name!!, visitText))
                        .showDialog(fragmentManager, { isOk ->
                            mScannerView?.resumeCameraPreview(this@CameraFragment)
                            if (isOk) {
                                mDatabase.child("attendees").child(id).child("isVisit").setValue(1)

                                showToast(activity, "'${user.name}' 님 참석 되었습니다")
                                requestEmail(user.email!!)
                            }
                        })
            }

        })
    }

    private fun requestEmail(email: String) {
        Api.api.confirmEmail(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            MLog.deb("onNext : server")
                        }, { e -> e.printStackTrace() },
                        {
                            MLog.deb("onComplete : server")
                        })
    }

    /**
     * permission
     */

    @NeedsPermission(Manifest.permission.CAMERA)
    fun checkCamera() {
        mScannerView?.setResultHandler(this)
        mScannerView?.startCamera()
    }

    @OnShowRationale(Manifest.permission.CAMERA)
    fun showRationaleForCamera(request: PermissionRequest) {
        request.proceed()
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    fun showDeniedForCamera() {
        Toast.makeText(activity, "카메라 권한을 거부하였습니다", Toast.LENGTH_SHORT).show()
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    fun onCameraNeverAskAgain() {
    }

}