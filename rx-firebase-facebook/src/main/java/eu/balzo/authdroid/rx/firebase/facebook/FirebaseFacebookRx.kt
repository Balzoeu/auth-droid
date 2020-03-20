package eu.balzo.authdroid.rx.firebase.facebook

import androidx.fragment.app.FragmentManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import eu.balzo.authdroid.core.Auth
import eu.balzo.authdroid.core.AuthError
import eu.balzo.authdroid.facebook.core.FacebookFragment
import eu.balzo.authdroid.rx.firebase.FirebaseRx
import eu.balzo.authdroid.rx.firebase.toSocialAuthUser
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.schedulers.Schedulers

object FirebaseFacebookRx {

    fun auth(fragmentManager: FragmentManager): Single<Auth> =
            Single.create<AuthResult> { emitter ->
                val fragment = FacebookFragment()
                val transaction = fragmentManager.beginTransaction()
                transaction.add(fragment, FacebookFragment.TAG).addToBackStack(null).commit()

                LoginManager.getInstance()
                        .registerCallback(
                                fragment.callbackManager,
                                object : FacebookCallback<LoginResult> {

                                    override fun onSuccess(result: LoginResult?) {
                                        result.handleFirebaseFacebookLogin(emitter)
                                        fragmentManager.popBackStack()
                                    }

                                    override fun onCancel() {
                                        emitter.onError(AuthError.Cancelled)
                                        fragmentManager.popBackStack()
                                    }

                                    override fun onError(error: FacebookException?) {
                                        error ?: AuthError.UnknownFirebaseError
                                                .let { emitter.onError(it) }
                                        fragmentManager.popBackStack()
                                    }
                                }
                        )
            }.flatMap { auth ->
                FirebaseRx.token()
                        .map { it to auth }
                        .subscribeOn(Schedulers.io())
            }.flatMap {
                when (val user = FirebaseRx.auth().currentUser) {
                    null -> Single.error(AuthError.UnknownFirebaseError)
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
                FirebaseRx.signInWithCredential(credential, emitter)
            }
        }
    }

    fun signOut() {
        LoginManager.getInstance().logOut()
    }
}