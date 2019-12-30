package com.giacomoparisi.authdroid.rx.firebase

import androidx.fragment.app.FragmentActivity
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.giacomoparisi.auth.droid.facebook.core.FacebookFragment
import com.giacomoparisi.authdroid.core.Auth
import com.giacomoparisi.authdroid.core.AuthError
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.schedulers.Schedulers


fun authWithFirebaseFacebook(activity: FragmentActivity): Single<Auth> =
        Single.create<AuthResult> { emitter ->
            val fragment = FacebookFragment()
            val transaction = activity.supportFragmentManager.beginTransaction()
            transaction.add(fragment, FacebookFragment.TAG).addToBackStack(null).commit()

            LoginManager.getInstance()
                    .registerCallback(
                            fragment.callbackManager,
                            object : FacebookCallback<LoginResult> {

                                override fun onSuccess(result: LoginResult?) {
                                    result.handleFirebaseFacebookLogin(emitter)
                                    activity.supportFragmentManager.popBackStack()
                                }

                                override fun onCancel() {
                                    emitter.onError(AuthError.Cancelled)
                                    activity.supportFragmentManager.popBackStack()
                                }

                                override fun onError(error: FacebookException?) {
                                    error ?: AuthError.UnknownFirebaseError
                                            .let { emitter.onError(it) }
                                    activity.supportFragmentManager.popBackStack()
                                }
                            }
                    )
        }.flatMap { auth ->
            getFirebaseToken()
                    .map { it to auth }
                    .subscribeOn(Schedulers.io())
        }.flatMap {
            when (val user = firebaseAuth().currentUser) {
                null -> Single.error<Auth>(AuthError.UnknownFirebaseError)
                else -> Single.just(Auth(
                        it.second.additionalUserInfo?.isNewUser,
                        user.toSocialAuthUser(it.first)
                ))
            }.subscribeOn(Schedulers.io())
        }

private fun LoginResult?.handleFirebaseFacebookLogin(emitter: SingleEmitter<AuthResult>) {
    when (this) {
        null -> emitter.onError(AuthError.UnknownFirebaseError)
        else -> {
            val credential =
                    FacebookAuthProvider.getCredential(this.accessToken.token)
            firebaseCredentialSignIn(credential, emitter)
        }
    }
}