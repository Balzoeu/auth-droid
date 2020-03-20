package eu.balzo.authdroid.google.core

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

fun getGoogleSignInIntent(activity: FragmentActivity, clientId: String): Intent =
        getGoogleSignInClient(activity, getGoogleSignInOptions(clientId)).signInIntent

fun getGoogleSignInOptions(clientId: String): GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(clientId)
                .requestEmail()
                .build()

fun getGoogleSignInClient(
        activity: FragmentActivity,
        googleSignInOptions: GoogleSignInOptions
): GoogleSignInClient =
        GoogleSignIn.getClient(
                activity,
                googleSignInOptions)