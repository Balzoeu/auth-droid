package eu.balzo.authdroid.rx.google

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.github.florent37.inlineactivityresult.kotlin.startForResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import eu.balzo.authdroid.core.Auth
import eu.balzo.authdroid.core.AuthError
import eu.balzo.authdroid.google.core.*
import io.reactivex.Single
import io.reactivex.SingleEmitter

fun authWithGoogle(activity: FragmentActivity, clientId: String): Single<Auth> =
        Single.create {
            activity.startForResult(getGoogleSignInIntent(activity, clientId)) { result ->

                val auth = authWithGoogle(result.data, it)

                if (auth != null)
                    it.onSuccess(Auth(null, auth.toSocialAuthUser()))
                else
                    it.onError(AuthError.UnknownFirebaseError)

            }.onFailed { result ->
                when (result.resultCode) {
                    Activity.RESULT_CANCELED -> it.onError(AuthError.Cancelled)
                    else -> it.onError(AuthError.UnknownFirebaseError)
                }
            }
        }

fun googleSignOut(activity: FragmentActivity, clientId: String): Single<Unit> =
        Single.create { emitter ->
            getGoogleSignInClient(activity, getGoogleSignInOptions(clientId))
                    .signOut()
                    .bindTask(emitter) { emitter.onSuccess(Unit) }
        }

private fun authWithGoogle(data: Intent?, emitter: SingleEmitter<Auth>): GoogleSignInAccount? =
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

fun <F, T> Task<F>.bindTask(
        emitter: SingleEmitter<T>,
        onSuccess: (F) -> Unit
) {
    this.addOnSuccessListener {
        if (emitter.isDisposed.not()) {
            onSuccess(it)
        }
    }.addOnCanceledListener {
        if (emitter.isDisposed.not()) {
            emitter.onError(AuthError.Cancelled)
        }
    }.addOnFailureListener {
        if (emitter.isDisposed.not()) {
            emitter.onError(it)
        }
    }
}

