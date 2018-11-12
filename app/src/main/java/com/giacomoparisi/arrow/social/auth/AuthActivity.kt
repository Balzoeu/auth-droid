package com.giacomoparisi.arrow.social.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import arrow.effects.DeferredK
import arrow.effects.await
import arrow.effects.deferredk.async.async
import com.giacomoparisi.arrow.social.auth.core.firebase.authWithFirebaseFacebook
import com.giacomoparisi.arrow.social.auth.core.firebase.authWithFirebaseGoogle
import kotlinx.android.synthetic.main.auth.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        authWithFirebaseGoogle(
                DeferredK.async(),
                this,
                this.getString(R.string.google_client_id_web))
                .await()
                .showMessage(this)
    }

    private suspend fun facebookSignIn() {
        authWithFirebaseFacebook(DeferredK.async(), this)
                .await()
                .showMessage(this)
    }
}
