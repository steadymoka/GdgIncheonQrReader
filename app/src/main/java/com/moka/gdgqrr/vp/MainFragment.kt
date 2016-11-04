package com.moka.gdgqrr.vp

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
import com.moka.gdgqrr.vp.audience.AudienceListActivity
import kotlinx.android.synthetic.main.fragment_main.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import permissions.dispatcher.*

@RuntimePermissions
class MainFragment : BaseFragment(), ZXingScannerView.ResultHandler {

    private lateinit var mDatabase: DatabaseReference
    private var mScannerView: ZXingScannerView? = null

    /**
     * LifeCycle
     */

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val rootView = inflater!!.inflate(R.layout.fragment_main, container, false)

        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mDatabase = FirebaseDatabase.getInstance().reference
        initZxingView()
        textView_search.setOnClickListener {
            activity.startActivity(Intent(activity, AudienceListActivity::class.java))
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out_short)
        }
    }

    override fun onResume() {
        super.onResume()
        MainFragmentPermissionsDispatcher.checkCameraWithCheck(this)
    }

    override fun onPause() {
        super.onPause()
        workInBackground { mScannerView?.stopCamera() }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        MainFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults)
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

        val id = rawResult.text.split(":")[1]
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
                        .setMessage(TextUtil.afterSmall(user.name!!, visitText))
                        .showDialog(fragmentManager, { isOk ->
                            mScannerView?.resumeCameraPreview(this@MainFragment)
                            if (isOk) {
                                mDatabase.child("attendees").child(id).child("isVisit").setValue(1)
                                showToast(activity, "'${user.name}' 님 참석 되었습니다")
                            }
                        })
            }

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