package com.giacomoparisi.arrow.social.auth.core.firebase.google

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import arrow.Kind
import arrow.core.Try
import arrow.core.getOrElse
import arrow.core.right
import arrow.core.toOption
import arrow.effects.typeclasses.Async
import com.giacomoparisi.arrow.social.auth.core.AuthResult
import com.giacomoparisi.arrow.social.auth.core.Failed
import com.giacomoparisi.arrow.social.auth.core.firebase.FirebaseSocialAuthenticator
import com.giacomoparisi.kotlin.functional.extensions.arrow.`try`.ifFailure
import com.giacomoparisi.kotlin.functional.extensions.arrow.`try`.ifSuccess
import com.github.florent37.inlineactivityresult.kotlin.startForResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

class FirebaseGoogleSocialAuthenticator<F>(
        private val _clientId: String,
        async: Async<F>,
        activity: FragmentActivity
) : FirebaseSocialAuthenticator<F>(async, activity) {

    private val _googleSignInOptions: GoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(this._clientId)
                    .requestEmail()
                    .build()

    private var _googleSignInClient: GoogleSignInClient =
            GoogleSignIn.getClient(
                    this.activity,
                    this._googleSignInOptions)

    override fun signIn(): Kind<F, AuthResult> =
            this.async.async { function ->
                this.activity.startForResult(this._googleSignInClient.signInIntent) { result ->
                    this.authWithGoogle(result.data)
                            .ifFailure { function(Failed(it).right()) }
                            .ifSuccess { it ->
                                this.firebaseSignIn(GoogleAuthProvider.getCredential(
                                        it.idToken,
                                        null
                                ), function)
                            }
                }
            }

    private fun authWithGoogle(data: Intent?): Try<GoogleSignInAccount> = Try {
        GoogleSignIn.getSignedInAccountFromIntent(data)
                .getResult(ApiException::class.java)
                .toOption()
                .getOrElse { throw Exception("Unknown error during auth") }
    }
}