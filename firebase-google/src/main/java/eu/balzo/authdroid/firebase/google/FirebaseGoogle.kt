package eu.balzo.authdroid.firebase.google

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.github.florent37.inlineactivityresult.Result
import com.github.florent37.inlineactivityresult.kotlin.startForResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import eu.balzo.authdroid.core.Auth
import eu.balzo.authdroid.core.AuthError
import eu.balzo.authdroid.firebase.core.Firebase
import eu.balzo.authdroid.firebase.core.Firebase.bindTask
import eu.balzo.authdroid.google.core.getGoogleSignInClient
import eu.balzo.authdroid.google.core.getGoogleSignInIntent
import eu.balzo.authdroid.google.core.getGoogleSignInOptions
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object FirebaseGoogle {

    suspend fun auth(
            activity: FragmentActivity,
            clientId: String
    ): Auth {

        val googleAuth =
                suspendCoroutine<Result> { continuation ->
                    activity.startForResult(
                            getGoogleSignInIntent(activity, clientId)
                    ) { continuation.resume(it) }
                            .onFailed {
                                when (it.resultCode) {
                                    Activity.RESULT_CANCELED ->
                                        continuation.resumeWithException(
                                                AuthError.Cancelled()
                                        )
                                    else ->
                                        continuation.resumeWithException(
                                                it.cause ?: AuthError.GoogleAuth()
                                        )
                                }
                            }

                }

        val auth = auth(googleAuth)

        val user = Firebase.currentUser()

        return Auth(auth.additionalUserInfo?.isNewUser, user)
    }

    private suspend fun auth(result: Result): AuthResult {

        val credentials = authWithGoogle(result.data) ?: throw AuthError.Unknown()

        return Firebase.signInWithCredential(
                GoogleAuthProvider.getCredential(credentials.idToken, null)
        )

    }

    suspend fun signOut(
            activity: FragmentActivity,
            clientId: String
    ) {

        getGoogleSignInClient(activity, getGoogleSignInOptions(clientId))
                .signOut()
                .bindTask()
    }


    private suspend fun authWithGoogle(
            data: Intent?
    ): GoogleSignInAccount? =
            GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException::class.java)
}