package com.giacomoparisi.arrow.social.auth.core.firebase.facebook

import android.content.Intent
import androidx.fragment.app.Fragment
import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.right
import arrow.core.toOption
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.giacomoparisi.arrow.social.auth.core.AuthResult
import com.giacomoparisi.arrow.social.auth.core.Cancelled
import com.giacomoparisi.arrow.social.auth.core.Failed

class FacebookFragment : Fragment() {

    private val _callbackManager = CallbackManager.Factory.create()

    fun signIn(
            function: (Either<Throwable, AuthResult>) -> Unit,
            onSuccess: (LoginResult?) -> Unit) {
        LoginManager.getInstance().registerCallback(
                this._callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult?) {
                        onSuccess(result)
                    }

                    override fun onCancel() {
                        function(Cancelled.right())
                    }

                    override fun onError(error: FacebookException?) {
                        function(Failed(error.toOption()
                                .getOrElse { Exception("Unknown error during auth") })
                                .right())
                    }

                }
        )
        LoginManager.getInstance().logInWithReadPermissions(
                this,
                mutableListOf("email", "public_profile")
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        this._callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        val TAG = FacebookFragment::class.java.toString()
    }
}