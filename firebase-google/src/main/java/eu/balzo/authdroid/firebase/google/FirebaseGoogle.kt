package eu.balzo.authdroid.firebase.google

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.afollestad.inlineactivityresult.ActivityResult
import com.afollestad.inlineactivityresult.coroutines.startActivityAwaitResult
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

object FirebaseGoogle {

    suspend fun auth(
        activity: FragmentActivity,
        clientId: String
    ): Auth {

        val googleAuth =
            activity.startActivityAwaitResult(getGoogleSignInIntent(activity, clientId))

        val auth = auth(googleAuth)

        val user = Firebase.currentUser()

        return Auth(auth.additionalUserInfo?.isNewUser, user)
    }

    private suspend fun auth(result: ActivityResult): AuthResult {

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


    private fun authWithGoogle(
        data: Intent?
    ): GoogleSignInAccount? =
        GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException::class.java)
}