package com.giacomoparisi.arrow.social.auth.core.firebase

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import arrow.core.*
import com.giacomoparisi.arrow.social.auth.core.Auth
import com.giacomoparisi.arrow.social.auth.core.UnknownFirebaseError
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
import io.reactivex.Single

fun authWithFirebaseGoogle(activity: FragmentActivity, clientId: String): Single<Option<Auth>> =
        Single.create {
            activity.startForResult(getGoogleSignInIntent(activity, clientId)) { result: Result ->
                authWithGoogle(result.data)
                        .ifFailure { throwable -> it.onError(throwable) }
                        .ifSuccess { account ->
                            firebaseCredentialSignIn(
                                    GoogleAuthProvider.getCredential(
                                            account.idToken,
                                            null),
                                    it)
                        }
            }
        }

fun googleSignOut(activity: FragmentActivity, clientId: String): Single<Option<Unit>> =
        Single.create {
            getGoogleSignInClient(activity, getGoogleSignInOptions(clientId))
                    .signOut()
                    .bindTask(it) { Unit.some() }
        }

private fun authWithGoogle(data: Intent?): Try<GoogleSignInAccount> = Try {
    GoogleSignIn.getSignedInAccountFromIntent(data)
            .getResult(ApiException::class.java)
            .toOption()
            .getOrElse { throw UnknownFirebaseError }
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