package com.giacomoparisi.arrow.social.auth.core.firebase.facebook

import androidx.fragment.app.FragmentActivity
import arrow.Kind
import arrow.core.Either
import arrow.core.Option
import arrow.core.right
import arrow.core.toOption
import arrow.effects.typeclasses.Async
import com.facebook.login.LoginResult
import com.giacomoparisi.arrow.social.auth.core.AuthResult
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
                transaction.add(fragment, FacebookFragment.TAG).commit()

                fragment.signIn(function) {
                    it.toOption().handleFacebookLogin(function)
                }
            }

    private fun Option<LoginResult>.handleFacebookLogin(
            function: (Either<Throwable, AuthResult>) -> Unit) {
        this@handleFacebookLogin.ifSome { loginResult ->
            val credential = FacebookAuthProvider.getCredential(loginResult.accessToken.token)
            this@FirebaseFacebookSocialAuthenticator.firebaseSignIn(credential, function)
        }.ifNone { function(Failed(Exception("Unknown error during auth")).right()) }
    }
}