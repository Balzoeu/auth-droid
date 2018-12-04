package com.giacomoparisi.arrow.social.auth.core.firebase

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import arrow.Kind
import arrow.core.Try
import arrow.core.getOrElse
import arrow.core.right
import arrow.core.toOption
import arrow.effects.typeclasses.Async
import com.giacomoparisi.arrow.social.auth.core.AuthResult
import com.giacomoparisi.arrow.social.auth.core.SocialAuthUser
import com.giacomoparisi.kotlin.functional.extensions.arrow.`try`.ifFailure
import com.giacomoparisi.kotlin.functional.extensions.arrow.`try`.ifSuccess
import com.github.florent37.inlineactivityresult.Result
import com.github.florent37.inlineactivityresult.kotlin.startForResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

fun <F> authWithFirebaseGoogle(async: Async<F>, activity: FragmentActivity, clientId: String): Kind<F, AuthResult<SocialAuthUser>> =
        async.async { function ->
            activity.startForResult(getGoogleSignInIntent(activity, clientId)) { result: Result ->
                authWithGoogle(result.data)
                        .ifFailure { function(AuthResult.Failed<SocialAuthUser>(it).right()) }
                        .ifSuccess { it ->
                            firebaseCredentialSignIn(
                                    GoogleAuthProvider.getCredential(
                                            it.idToken,
                                            null),
                                    function)
                        }
            }
        }

fun <F> googleSignOut(async: Async<F>, activity: FragmentActivity, clientId: String): Kind<F, AuthResult<Unit>> =
        async.async { function ->
            getGoogleSignInClient(activity, getGoogleSignInOptions(clientId))
                    .signOut()
                    .bindTask(function) { AuthResult.Completed(Unit) }
        }

private fun authWithGoogle(data: Intent?): Try<GoogleSignInAccount> = Try {
    GoogleSignIn.getSignedInAccountFromIntent(data)
            .getResult(ApiException::class.java)
            .toOption()
            .getOrElse { throw Exception("Unknown error during auth") }
}

private fun getGoogleSignInIntent(activity: FragmentActivity, clientId: String): Intent =
        getGoogleSignInClient(activity, getGoogleSignInOptions(clientId)).signInIntent

private fun getGoogleSignInOptions(clientId: String): GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(clientId)
                .requestEmail()
                .build()

private fun getGoogleSignInClient(
        activity: FragmentActivity,
        googleSignInOptions: GoogleSignInOptions
): GoogleSignInClient =
        GoogleSignIn.getClient(
                activity,
                googleSignInOptions)