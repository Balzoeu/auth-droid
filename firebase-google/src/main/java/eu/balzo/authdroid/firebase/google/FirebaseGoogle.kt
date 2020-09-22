package eu.balzo.authdroid.firebase.google

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import arrow.core.Either
import arrow.core.computations.either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.github.florent37.inlineactivityresult.Result
import com.github.florent37.inlineactivityresult.kotlin.startForResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import eu.balzo.authdroid.core.Auth
import eu.balzo.authdroid.core.AuthError
import eu.balzo.authdroid.core.AuthError.Companion.toAuthError
import eu.balzo.authdroid.core.nullToError
import eu.balzo.authdroid.firebase.core.Firebase
import eu.balzo.authdroid.firebase.core.Firebase.bindTask
import eu.balzo.authdroid.google.core.getGoogleSignInClient
import eu.balzo.authdroid.google.core.getGoogleSignInIntent
import eu.balzo.authdroid.google.core.getGoogleSignInOptions
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object FirebaseGoogle {

    suspend fun auth(
            activity: FragmentActivity,
            clientId: String
    ): Either<AuthError, Auth> {

        val googleAuth =
                suspendCoroutine<Either<AuthError, Result>> { continuation ->
                    activity.startForResult(
                            getGoogleSignInIntent(activity, clientId)
                    ) { continuation.resume(it.right()) }
                            .onFailed {
                                when (it.resultCode) {
                                    Activity.RESULT_CANCELED ->
                                        continuation.resume(AuthError.Cancelled.left())
                                    else ->
                                        continuation.resume(AuthError.GoogleAuth.left())
                                }
                            }

                }

        val auth = googleAuth.flatMap { auth(it) }

        val user = Firebase.currentUser()

        return either.invoke {
            Auth(auth.bind().additionalUserInfo?.isNewUser, user.bind())
        }
    }

    private suspend fun auth(result: Result): Either<AuthError, AuthResult> =

            authWithGoogle(result.data)
                    .nullToError(AuthError.Unknown(null))
                    .flatMap {
                        Firebase.signInWithCredential(
                                GoogleAuthProvider.getCredential(it.idToken, null)
                        )
                    }

    suspend fun signOut(
            activity: FragmentActivity,
            clientId: String
    ): Either<AuthError, Unit> =
            getGoogleSignInClient(activity, getGoogleSignInOptions(clientId))
                    .signOut()
                    .bindTask()
                    .map { Unit }


    private suspend fun authWithGoogle(
            data: Intent?
    ): Either<AuthError, GoogleSignInAccount?> =
            Either.catch {
                GoogleSignIn.getSignedInAccountFromIntent(data)
                        .getResult(ApiException::class.java)

            }.mapLeft { it.toAuthError() }
}