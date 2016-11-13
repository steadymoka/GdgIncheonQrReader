package com.moka.gdgqrr.vp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.moka.framework.base.BaseFragment
import com.moka.gdgqrr.R
import com.moka.gdgqrr.vp.attendee.AttendeeListActivity
import com.moka.gdgqrr.vp.camera.CameraActivity
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : BaseFragment() {

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

        textView_camera.setOnClickListener {
            activity.startActivity(Intent(activity, CameraActivity::class.java))
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out_short)
        }
        textView_search.setOnClickListener {
            activity.startActivity(Intent(activity, AttendeeListActivity::class.java))
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out_short)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

}