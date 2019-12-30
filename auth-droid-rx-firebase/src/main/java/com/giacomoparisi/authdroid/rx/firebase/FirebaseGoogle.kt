package com.giacomoparisi.authdroid.rx.firebase

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.giacomoparisi.authdroid.core.Auth
import com.giacomoparisi.authdroid.core.AuthError
import com.github.florent37.inlineactivityresult.kotlin.startForResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import io.reactivex.Single
import io.reactivex.SingleEmitter

fun authWithFirebaseGoogle(activity: FragmentActivity, clientId: String): Single<Auth> =
        Single.create {
            activity.startForResult(getGoogleSignInIntent(activity, clientId)) { result ->

                val auth = authWithGoogle(result.data, it)

                if (auth != null) {
                    firebaseCredentialSignIn(
                            GoogleAuthProvider.getCredential(
                                    auth.idToken,
                                    null),
                            it)
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