package com.giacomoparisi.arrow.social.auth.core.firebase.facebook

import android.content.Intent
import androidx.fragment.app.Fragment
import com.facebook.CallbackManager
import com.facebook.login.LoginManager

class FacebookFragment : Fragment() {

    val callbackManager: CallbackManager = CallbackManager.Factory.create()

    override fun onStart() {
        super.onStart()

        LoginManager.getInstance().logInWithReadPermissions(
                this,
                mutableListOf("email", "public_profile")
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        this.callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        val TAG = FacebookFragment::class.java.toString()
    }
}