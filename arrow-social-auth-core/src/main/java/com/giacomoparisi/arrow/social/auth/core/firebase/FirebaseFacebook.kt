package com.giacomoparisi.arrow.social.auth.core.firebase

import androidx.fragment.app.FragmentActivity
import arrow.Kind
import arrow.core.*
import arrow.effects.typeclasses.Async
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.giacomoparisi.arrow.social.auth.core.AuthCancelled
import com.giacomoparisi.arrow.social.auth.core.AuthFailed
import com.giacomoparisi.arrow.social.auth.core.AuthResult
import com.giacomoparisi.kotlin.functional.extensions.arrow.option.ifNone
import com.giacomoparisi.kotlin.functional.extensions.arrow.option.ifSome
import com.google.firebase.auth.FacebookAuthProvider


fun <F> authWithFirebaseFacebook(async: Async<F>, activity: FragmentActivity): Kind<F, AuthResult> =
        async.async { function ->
            val fragment = FacebookFragment()
            val transaction = activity.supportFragmentManager.beginTransaction()
            transaction.add(fragment, FacebookFragment.TAG).addToBackStack(null).commit()

            LoginManager.getInstance().registerCallback(
                    fragment.callbackManager,
                    object : FacebookCallback<LoginResult> {
                        override fun onSuccess(result: LoginResult?) {
                            result.toOption().handleFacebookLogin(function)
                            activity.supportFragmentManager.popBackStack()
                        }

                        override fun onCancel() {
                            function(AuthCancelled.right())
                            activity.supportFragmentManager.popBackStack()
                        }

                        override fun onError(error: FacebookException?) {
                            function(AuthFailed(error.toOption()
                                    .getOrElse { Exception("Unknown error during auth") })
                                    .right())
                            activity.supportFragmentManager.popBackStack()
                        }
                    }
            )
        }

private fun Option<LoginResult>.handleFacebookLogin(
        function: (Either<Throwable, AuthResult>) -> Unit) {
    this@handleFacebookLogin.ifSome { loginResult ->
        val credential = FacebookAuthProvider.getCredential(loginResult.accessToken.token)
        firebaseCredentialSignIn(credential, function)
    }.ifNone { function(AuthFailed(Exception("Unknown error during auth")).right()) }
}