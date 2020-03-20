package eu.balzo.authdroid.rx.google

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import eu.balzo.authdroid.core.AuthError
import eu.balzo.authdroid.core.SocialAuthUser
import io.reactivex.SingleEmitter

fun GoogleSignInAccount.toSocialAuthUser(): SocialAuthUser =
        SocialAuthUser(
                id.orEmpty(),
                idToken.orEmpty(),
                displayName,
                displayName?.split(" ")?.getOrNull(0),
                displayName?.split(" ")?.getOrNull(1),
                email,
                photoUrl?.toString()
        )

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