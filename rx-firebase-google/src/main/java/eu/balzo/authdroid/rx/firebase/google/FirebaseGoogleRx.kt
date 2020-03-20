package eu.balzo.authdroid.rx.firebase.google

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.github.florent37.inlineactivityresult.kotlin.startForResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import eu.balzo.authdroid.core.Auth
import eu.balzo.authdroid.core.AuthError
import eu.balzo.authdroid.google.core.getGoogleSignInClient
import eu.balzo.authdroid.google.core.getGoogleSignInIntent
import eu.balzo.authdroid.google.core.getGoogleSignInOptions
import eu.balzo.authdroid.rx.firebase.FirebaseRx
import eu.balzo.authdroid.rx.firebase.FirebaseRx.bindTask
import eu.balzo.authdroid.rx.firebase.toSocialAuthUser
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.schedulers.Schedulers

object FirebaseGoogleRx {
    fun auth(activity: FragmentActivity, clientId: String): Single<Auth> =
            Single.create<AuthResult> {
                activity.startForResult(
                        getGoogleSignInIntent(activity, clientId)
                ) { result ->

                    val auth = authWithGoogle(result.data, it)

                    if (auth != null) {
                        FirebaseRx.signInWithCredential(
                                GoogleAuthProvider.getCredential(
                                        auth.idToken,
                                        null),
                                it)
                    } else {
                        it.onError(AuthError.UnknownFirebaseError)
                    }
                }.onFailed { result ->
                    when (result.resultCode) {
                        Activity.RESULT_CANCELED -> it.onError(AuthError.Cancelled)
                        else -> it.onError(AuthError.UnknownFirebaseError)
                    }
                }

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

    fun signOut(activity: FragmentActivity, clientId: String): Single<Unit> =
            Single.create { emitter ->
                getGoogleSignInClient(activity, getGoogleSignInOptions(clientId))
                        .signOut()
                        .bindTask(emitter) { emitter.onSuccess(Unit) }
            }

    private fun authWithGoogle(
            data: Intent?,
            emitter: SingleEmitter<AuthResult>
    ): GoogleSignInAccount? =
            try {
                GoogleSignIn.getSignedInAccountFromIntent(data)
                        .getResult(ApiException::class.java)
            } catch (throwable: Throwable) {
                when (throwable) {
                    is ApiException -> emitter.onError(throwable)
                    else -> emitter.onError(AuthError.GoogleAuthError)
                }
                null
            }
}