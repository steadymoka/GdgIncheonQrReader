package com.moka.gdgqrr.vp.attendee


import android.os.Bundle
import com.moka.framework.base.BaseActivity
import com.moka.gdgqrr.R


class AttendeeListActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_toolbar)

        supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout_container, AttendeeListFragment())
                .commit()
    }

}
