package com.giacomoparisi.arrowsocialauth.core.firebase

import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import arrow.core.Try
import arrow.core.getOrElse
import arrow.core.toOption
import com.giacomoparisi.kotlin.functional.extensions.arrow.ifNone
import com.giacomoparisi.kotlin.functional.extensions.arrow.ifSome
import com.giacomoparisi.kotlin.functional.extensions.core.ifFalse
import com.giacomoparisi.kotlin.functional.extensions.core.ifTrue
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

class FirebaseGoogleAuthFragment : FirebaseAuthFragment() {

    private lateinit var _googleSignInOptions: GoogleSignInOptions

    private lateinit var _googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialize google sign in options
        val clientId = arguments
                .toOption()
                .map { it.getString(CLIENT_ID) }
                .getOrElse { throw IllegalAccessException("No google client id provided") }

        this._googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(clientId)
                .requestEmail()
                .build()

        this._googleSignInClient = GoogleSignIn.getClient(
                this.requireActivity(),
                this._googleSignInOptions)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SIGN_IN_TASK) {
            val task =
                    Try {
                        // Google Sign In was successful
                        GoogleSignIn.getSignedInAccountFromIntent(data)
                                .getResult(ApiException::class.java)
                                .toOption()
                                .ifSome { firebaseAuthWithGoogle(it) }
                                .ifNone { throw Exception("Unknown error during auth") }
                    }
        }
    }

    private fun signIn() {
        val signInIntent = this._googleSignInClient.signInIntent
        this.startActivityForResult(signInIntent, SIGN_IN_TASK)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {

        GoogleAuthProvider.getCredential(account.idToken, null)
                .also {
                    this.auth.signInWithCredential(it)
                            .addOnCompleteListener { task ->
                                task.isSuccessful
                                        .ifTrue { auth.currentUser.toOption() }
                                        .ifFalse {  }
                            }
                            .addOnCanceledListener {  }
                            .addOnFailureListener {  }
                }
    }

    companion object {

        private const val CLIENT_ID = "client_id"

        fun build(clientId: String): FirebaseGoogleAuthFragment {
            val fragment = FirebaseGoogleAuthFragment()
            fragment.arguments = bundleOf(CLIENT_ID to clientId)
            return fragment
        }
    }
}