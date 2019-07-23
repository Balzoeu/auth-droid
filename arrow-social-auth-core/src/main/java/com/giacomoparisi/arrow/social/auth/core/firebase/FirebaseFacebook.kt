package com.giacomoparisi.arrow.social.auth.core.firebase

import androidx.fragment.app.FragmentActivity
import arrow.core.None
import arrow.core.Option
import arrow.core.getOrElse
import arrow.core.toOption
import arrow.syntax.function.pipe
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.giacomoparisi.arrow.social.auth.core.Auth
import com.giacomoparisi.arrow.social.auth.core.UnknownFirebaseError
import com.giacomoparisi.arrow.social.auth.core.facebook.FacebookFragment
import com.giacomoparisi.kotlin.functional.extensions.arrow.option.ifNone
import com.giacomoparisi.kotlin.functional.extensions.arrow.option.ifSome
import com.google.firebase.auth.FacebookAuthProvider
import io.reactivex.Single
import io.reactivex.SingleEmitter


fun authWithFirebaseFacebook(activity: FragmentActivity): Single<Option<Auth>> =
        Single.create {
            val fragment = FacebookFragment()
            val transaction = activity.supportFragmentManager.beginTransaction()
            transaction.add(fragment, FacebookFragment.TAG).addToBackStack(null).commit()

            LoginManager.getInstance().registerCallback(
                    fragment.callbackManager,
                    object : FacebookCallback<LoginResult> {
                        override fun onSuccess(result: LoginResult?) {
                            result.toOption().handleFirebaseFacebookLogin(it)
                            activity.supportFragmentManager.popBackStack()
                        }

                        override fun onCancel() {
                            it.onSuccess(None)
                            activity.supportFragmentManager.popBackStack()
                        }

                        override fun onError(error: FacebookException?) {
                            error.toOption().getOrElse { UnknownFirebaseError }
                                    .pipe { throwable -> it.onError(throwable) }
                            activity.supportFragmentManager.popBackStack()
                        }
                    }
            )
        }

private fun Option<LoginResult>.handleFirebaseFacebookLogin(
        emitter: SingleEmitter<Option<Auth>>) {
    this@handleFirebaseFacebookLogin.ifSome { loginResult ->
        val credential = FacebookAuthProvider.getCredential(loginResult.accessToken.token)
        firebaseCredentialSignIn(credential, emitter)
    }.ifNone { emitter.onError(UnknownFirebaseError) }
}