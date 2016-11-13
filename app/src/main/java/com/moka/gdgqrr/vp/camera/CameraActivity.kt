package com.moka.gdgqrr.vp.camera


import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.moka.framework.base.BaseActivity
import com.moka.framework.util.MLog
import com.moka.gdgqrr.R
import kotlinx.android.synthetic.main.activity_with_toolbar.*


class CameraActivity : BaseActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_with_toolbar)

        toolbarLayout.setHomeVisible(true)
        toolbarLayout.setHomeListener { onBackPressed() }
        initFireBaseAuth()

        supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout_container, CameraFragment())
                .commit()
    }

    private fun initFireBaseAuth() {
        mAuth = FirebaseAuth.getInstance()
        mAuth.signInAnonymously().addOnCompleteListener(this) { task ->
            MLog.deb("signInAnonymously:onComplete:" + task.isSuccessful)

            if (!task.isSuccessful) {
                MLog.deb("signInAnonymously ${task.exception}")
                Toast.makeText(this@CameraActivity, "Authentication failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    var mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user != null)
            MLog.deb("onAuthStateChanged:signed_in:" + user.uid)
        else
            MLog.deb("onAuthStateChanged:signed_out")
    }

    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        mAuth.removeAuthStateListener(mAuthListener)
    }

}
