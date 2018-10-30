package com.giacomoparisi.arrow.social.auth.core.firebase

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import arrow.Kind
import arrow.core.*
import arrow.effects.typeclasses.Async
import com.giacomoparisi.arrow.social.auth.core.Cancelled
import com.giacomoparisi.arrow.social.auth.core.Completed
import com.giacomoparisi.arrow.social.auth.core.SignInResult
import com.giacomoparisi.arrow.social.auth.core.toSocialAuthUser
import com.giacomoparisi.kotlin.functional.extensions.arrow.ifFailure
import com.giacomoparisi.kotlin.functional.extensions.arrow.ifNone
import com.giacomoparisi.kotlin.functional.extensions.arrow.ifSome
import com.giacomoparisi.kotlin.functional.extensions.arrow.ifSuccess
import com.giacomoparisi.kotlin.functional.extensions.core.ifFalse
import com.giacomoparisi.kotlin.functional.extensions.core.ifTrue
import com.github.florent37.inlineactivityresult.kotlin.startForResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

class FirebaseGoogleSocialAuthenticator<F>(
        private val _clientId: String,
        private val _activity: FragmentActivity,
        private val _async: Async<F>
) : FirebaseSocialAuthenticator<F>() {

    private val _googleSignInOptions: GoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(this._clientId)
                    .requestEmail()
                    .build()

    private var _googleSignInClient: GoogleSignInClient =
            GoogleSignIn.getClient(
                    this._activity,
                    this._googleSignInOptions)

    override fun signIn(): Kind<F, SignInResult> =
            this._async.async { function ->
                this._activity.startForResult(this._googleSignInClient.signInIntent) { result ->
                    this.authWithGoogle(result.data)
                            .ifFailure { function(it.left()) }
                            .ifSuccess { this.authWithFirebase(it, function) }
                }
            }

    private fun authWithGoogle(data: Intent?): Try<GoogleSignInAccount> = Try {
        GoogleSignIn.getSignedInAccountFromIntent(data)
                .getResult(ApiException::class.java)
                .toOption()
                .getOrElse { throw Exception("Unknown error during auth") }
    }

    private fun authWithFirebase(
            account: GoogleSignInAccount,
            function: (Either<Throwable, SignInResult>) -> Unit) {

        GoogleAuthProvider.getCredential(account.idToken, null)
                .also { authCredential ->
                    // start the firebase auth flow
                    this.auth.signInWithCredential(authCredential)
                            .addOnCompleteListener { task ->
                                task.isSuccessful
                                        .ifTrue {
                                            // firebase auth task completed
                                            auth.currentUser.toOption()
                                                    .ifSome { function(Completed(it.toSocialAuthUser()).right()) }
                                                    .ifNone { function(Throwable("Unknown error during auth").left()) }
                                        }
                                        .ifFalse {
                                            // firebase auth task completed with an error
                                            function(task.exception
                                                    .toOption()
                                                    .getOrElse { Throwable("Unknown error during auth") }
                                                    .left())
                                        }
                            }
                            .addOnCanceledListener { function(Cancelled.right()) }
                            .addOnFailureListener { function(it.left()) }
                }
    }
}