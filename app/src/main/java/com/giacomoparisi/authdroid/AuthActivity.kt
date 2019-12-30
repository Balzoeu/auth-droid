package com.giacomoparisi.authdroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.giacomoparisi.authdroid.auth.R
import com.giacomoparisi.authdroid.rx.facebook.authWithFacebook
import com.giacomoparisi.authdroid.rx.facebook.facebookSignOut
import com.giacomoparisi.authdroid.rx.firebase.authWithFirebaseFacebook
import com.giacomoparisi.authdroid.rx.firebase.firebaseSignOut
import com.giacomoparisi.authdroid.rx.firebase.googleSignOut
import kotlinx.android.synthetic.main.auth.*

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth)

        this.firebase_google_login.setOnClickListener {
            this@AuthActivity.firebaseGoogleSignIn()
        }

        this.firebase_facebook_login.setOnClickListener {
            this@AuthActivity.firebaseFacebookSignIn()
        }

        this.firebase_email_password_login.setOnClickListener {
            this.supportFragmentManager.beginTransaction()
                    .replace(R.id.root, EmailPasswordAuthFragment())
                    .addToBackStack(null)
                    .commit()
        }

        this.facebook_login.setOnClickListener {
            this.facebookSingIn()
        }

        this.logout.setOnClickListener {
            firebaseSignOut()
            facebookSignOut()
            googleSignOut(
                    this@AuthActivity,
                    this@AuthActivity.getString(R.string.google_client_id_web)
            )
        }
    }

    private fun firebaseGoogleSignIn() {
        this.getString(R.string.google_client_id_web)

    }

    private fun firebaseFacebookSignIn() {
        authWithFirebaseFacebook(this)
    }

    private fun facebookSingIn() {
        authWithFacebook(this, 500)
    }
}
