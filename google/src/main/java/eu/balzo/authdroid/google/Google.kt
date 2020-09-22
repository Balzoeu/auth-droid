package eu.balzo.authdroid.google

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.github.florent37.inlineactivityresult.Result
import com.github.florent37.inlineactivityresult.kotlin.startForResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import eu.balzo.authdroid.core.Auth
import eu.balzo.authdroid.core.AuthError
import eu.balzo.authdroid.core.AuthError.Companion.toAuthError
import eu.balzo.authdroid.google.core.getGoogleSignInClient
import eu.balzo.authdroid.google.core.getGoogleSignInIntent
import eu.balzo.authdroid.google.core.getGoogleSignInOptions
import eu.balzo.authdroid.google.core.toSocialAuthUser
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object Google {

    suspend fun auth(
            activity: FragmentActivity,
            clientId: String
    ): Either<AuthError, Auth> {

        val auth =
                suspendCoroutine<Either<AuthError, Result>> { continuation ->

                    activity.startForResult(getGoogleSignInIntent(activity, clientId)) {
                        continuation.resume(it.right())
                    }.onFailed { result ->
                        continuation.resume(
                                when (result.resultCode) {
                                    Activity.RESULT_CANCELED ->
                                        AuthError.Cancelled.left()
                                    else ->
                                        AuthError.FirebaseUnknown.left()
                                }
                        )
                    }
                }

        return auth.flatMap { googleAuth(it) }

    }

    private suspend fun googleAuth(
            result: Result
    ): Either<AuthError, Auth> =
            authWithGoogle(result.data)
                    .map { it?.toSocialAuthUser() }
                    .flatMap { it?.right() ?: AuthError.FirebaseUnknown.left() }
                    .map { Auth(null, it) }

    suspend fun signOut(
            activity: FragmentActivity,
            clientId: String
    ): Either<AuthError, Unit> =
            suspendCoroutine { continuation ->

                getGoogleSignInClient(activity, getGoogleSignInOptions(clientId))
                        .signOut()
                        .addOnSuccessListener { continuation.resume(Unit.right()) }
                        .addOnCanceledListener { continuation.resume(AuthError.Cancelled.left()) }
                        .addOnFailureListener { continuation.resume(it.toAuthError().left()) }

            }

    private suspend fun authWithGoogle(
            data: Intent?
    ): Either<AuthError, GoogleSignInAccount?> =
            Either.catch {

                GoogleSignIn.getSignedInAccountFromIntent(data)
                        .getResult(ApiException::class.java)

            }.mapLeft { it.toAuthError() }
}

