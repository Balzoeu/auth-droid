package com.giacomoparisi.arrow.social.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import arrow.effects.DeferredK
import arrow.effects.async
import arrow.effects.await
import com.giacomoparisi.arrow.social.auth.core.firebase.facebook.FirebaseFacebookSocialAuthenticator
import com.giacomoparisi.arrow.social.auth.core.firebase.google.FirebaseGoogleSocialAuthenticator
import kotlinx.android.synthetic.main.auth.*
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.launch

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth)

        this.firebase_google_login.setOnClickListener {
            CoroutineScope(Dispatchers.Default).launch {
                this@AuthActivity.googleSignIn()
            }
        }

        this.firebase_facebook_login.setOnClickListener {
            CoroutineScope(Dispatchers.Default).launch {
                this@AuthActivity.facebookSignIn()
            }
        }

        this.firebase_email_password_login.setOnClickListener {
            this.supportFragmentManager.beginTransaction()
                    .replace(R.id.root, EmailPasswordAuthFragment())
                    .addToBackStack(null)
                    .commit()
        }
    }

    private suspend fun googleSignIn() {
        FirebaseGoogleSocialAuthenticator(
                this.getString(R.string.google_client_id_web), DeferredK.async(), this)
                .signIn()
                .await()
                .showMessage(this)
    }

    private suspend fun facebookSignIn() {
        FirebaseFacebookSocialAuthenticator(DeferredK.async(), this)
                .signIn()
                .await()
                .showMessage(this)
    }
}
