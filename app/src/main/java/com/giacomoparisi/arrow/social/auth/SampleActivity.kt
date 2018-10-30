package com.giacomoparisi.arrow.social.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import arrow.effects.DeferredK
import arrow.effects.async
import arrow.effects.await
import com.giacomoparisi.arrow.social.auth.core.AuthResult
import com.giacomoparisi.arrow.social.auth.core.Cancelled
import com.giacomoparisi.arrow.social.auth.core.Completed
import com.giacomoparisi.arrow.social.auth.core.Failed
import com.giacomoparisi.arrow.social.auth.core.firebase.FirebaseGoogleSocialAuthenticator
import com.giacomoparisi.kotlin.functional.extensions.android.toast.showLongToast
import kotlinx.android.synthetic.main.activity_sample.*
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.launch

class SampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        this.firebase_google_login.setOnClickListener {
            CoroutineScope(Dispatchers.Default).launch {
                this@SampleActivity.googleSignIn()
            }
        }
    }

    private suspend fun googleSignIn() {
        FirebaseGoogleSocialAuthenticator(
                this.getString(R.string.google_client_id_web),
                this,
                DeferredK.async())
                .signIn()
                .await()
                .showMessage()
    }

    private fun AuthResult.showMessage() {
        CoroutineScope(Dispatchers.Main).launch {
            when (this@showMessage) {
                is Cancelled -> this@SampleActivity.showLongToast("Cancelled")
                is Completed -> this@SampleActivity.showLongToast(this@showMessage.user.toString())
                is Failed -> this@SampleActivity.showLongToast(this@showMessage.throwable.message.orEmpty())
            }
        }
    }
}
