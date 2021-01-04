package eu.balzo.authdroid.google

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.github.florent37.inlineactivityresult.Result
import com.github.florent37.inlineactivityresult.kotlin.startForResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import eu.balzo.authdroid.core.Auth
import eu.balzo.authdroid.core.AuthError
import eu.balzo.authdroid.google.core.getGoogleSignInClient
import eu.balzo.authdroid.google.core.getGoogleSignInIntent
import eu.balzo.authdroid.google.core.getGoogleSignInOptions
import eu.balzo.authdroid.google.core.toSocialAuthUser
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object Google {

    suspend fun auth(
            activity: FragmentActivity,
            clientId: String
    ): Auth {

        val auth =
                suspendCoroutine<Result> { continuation ->

                    activity.startForResult(getGoogleSignInIntent(activity, clientId)) {
                        continuation.resume(it)
                    }.onFailed { result ->
                        continuation.resumeWithException(
                                when (result.resultCode) {
                                    Activity.RESULT_CANCELED ->
                                        AuthError.Cancelled()
                                    else ->
                                        result.cause ?: AuthError.FirebaseUnknown()
                                }
                        )
                    }
                }

        return googleAuth(auth)

    }

    private suspend fun googleAuth(
            result: Result
    ): Auth =
            authWithGoogle(result.data)
                    ?.toSocialAuthUser()
                    ?.let { Auth(null, it) } ?: throw AuthError.FirebaseUnknown()

    suspend fun signOut(
            activity: FragmentActivity,
            clientId: String
    ) {

        suspendCoroutine<Unit> { continuation ->

            getGoogleSignInClient(activity, getGoogleSignInOptions(clientId))
                    .signOut()
                    .addOnSuccessListener { continuation.resume(Unit) }
                    .addOnCanceledListener {
                        continuation.resumeWithException(AuthError.Cancelled())
                    }
                    .addOnFailureListener {
                        continuation.resumeWithException(it)
                    }

        }
    }

    private suspend fun authWithGoogle(
            data: Intent?
    ): GoogleSignInAccount? =
            GoogleSignIn.getSignedInAccountFromIntent(data)
                    .getResult(ApiException::class.java)


}

