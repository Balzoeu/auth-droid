package com.giacomoparisi.arrow.social.auth.core.firebase.facebook

import androidx.fragment.app.FragmentActivity
import arrow.Kind
import arrow.core.*
import arrow.effects.typeclasses.Async
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.giacomoparisi.arrow.social.auth.core.AuthResult
import com.giacomoparisi.arrow.social.auth.core.Cancelled
import com.giacomoparisi.arrow.social.auth.core.Failed
import com.giacomoparisi.arrow.social.auth.core.firebase.FirebaseSocialAuthenticator
import com.giacomoparisi.kotlin.functional.extensions.arrow.option.ifNone
import com.giacomoparisi.kotlin.functional.extensions.arrow.option.ifSome
import com.google.firebase.auth.FacebookAuthProvider


class FirebaseFacebookSocialAuthenticator<F>(
        async: Async<F>,
        activity: FragmentActivity
) : FirebaseSocialAuthenticator<F>(async, activity) {

    override fun signIn(): Kind<F, AuthResult> =
            this.async.async { function ->
                val fragment = FacebookFragment()
                val transaction = this.activity.supportFragmentManager.beginTransaction()
                transaction.add(fragment, FacebookFragment.TAG).addToBackStack(null).commit()

                LoginManager.getInstance().registerCallback(
                        fragment.callbackManager,
                        object : FacebookCallback<LoginResult> {
                            override fun onSuccess(result: LoginResult?) {
                                result.toOption().handleFacebookLogin(function)
                                this@FirebaseFacebookSocialAuthenticator.activity.supportFragmentManager.popBackStack()
                            }

                            override fun onCancel() {
                                function(Cancelled.right())
                                this@FirebaseFacebookSocialAuthenticator.activity.supportFragmentManager.popBackStack()
                            }

                            override fun onError(error: FacebookException?) {
                                function(Failed(error.toOption()
                                        .getOrElse { Exception("Unknown error during auth") })
                                        .right())
                                this@FirebaseFacebookSocialAuthenticator.activity.supportFragmentManager.popBackStack()
                            }
                        }
                )
            }

    private fun Option<LoginResult>.handleFacebookLogin(
            function: (Either<Throwable, AuthResult>) -> Unit) {
        this@handleFacebookLogin.ifSome { loginResult ->
            val credential = FacebookAuthProvider.getCredential(loginResult.accessToken.token)
            this@FirebaseFacebookSocialAuthenticator.firebaseSignIn(credential, function)
        }.ifNone { function(Failed(Exception("Unknown error during auth")).right()) }
    }
}