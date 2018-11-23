package com.giacomoparisi.arrow.social.auth.core.firebase

import androidx.fragment.app.FragmentActivity
import arrow.Kind
import arrow.core.*
import arrow.effects.typeclasses.Async
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.giacomoparisi.arrow.social.auth.core.AuthResult
import com.giacomoparisi.arrow.social.auth.core.SocialAuthUser
import com.giacomoparisi.kotlin.functional.extensions.arrow.option.ifNone
import com.giacomoparisi.kotlin.functional.extensions.arrow.option.ifSome
import com.google.firebase.auth.FacebookAuthProvider


fun <F> authWithFirebaseFacebook(async: Async<F>, activity: FragmentActivity): Kind<F, AuthResult<SocialAuthUser>> =
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
                            function(AuthResult.Cancelled<SocialAuthUser>().right())
                            activity.supportFragmentManager.popBackStack()
                        }

                        override fun onError(error: FacebookException?) {
                            function(AuthResult.Failed<SocialAuthUser>(error.toOption()
                                    .getOrElse { Exception("Unknown error during auth") })
                                    .right())
                            activity.supportFragmentManager.popBackStack()
                        }
                    }
            )
        }

private fun Option<LoginResult>.handleFacebookLogin(
        function: (Either<Throwable, AuthResult<SocialAuthUser>>) -> Unit) {
    this@handleFacebookLogin.ifSome { loginResult ->
        val credential = FacebookAuthProvider.getCredential(loginResult.accessToken.token)
        firebaseCredentialSignIn(credential, function)
    }.ifNone { function(AuthResult.Failed<SocialAuthUser>(Exception("Unknown error during auth")).right()) }
}