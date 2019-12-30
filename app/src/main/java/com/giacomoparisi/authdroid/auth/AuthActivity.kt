package com.giacomoparisi.authdroid.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import arrow.core.Option
import com.giacomoparisi.authdroid.auth.core.facebook.authWithFacebook
import com.giacomoparisi.authdroid.auth.core.facebook.facebookSignOut
import com.giacomoparisi.authdroid.auth.core.firebase.authWithFirebaseFacebook
import com.giacomoparisi.authdroid.auth.core.firebase.authWithFirebaseGoogle
import com.giacomoparisi.authdroid.auth.core.firebase.firebaseSignOut
import com.giacomoparisi.authdroid.auth.core.firebase.googleSignOut
import com.giacomoparisi.kotlin.functional.extensions.android.toast.showLongToast
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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
            ).subscribe({ option -> this.showLongToast(option.fold({ "Cancelled" }) { "Ok" }) }) { throwable -> this.showLongToast(throwable.message.orEmpty()) }
        }
    }

    private fun firebaseGoogleSignIn() {
        authWithFirebaseGoogle(
                this,
                this.getString(R.string.google_client_id_web))
                .await(this)
    }

    private fun firebaseFacebookSignIn() {
        authWithFirebaseFacebook(this).await(this)
    }

    private fun facebookSingIn() {
        authWithFacebook(this, 500).await(this)
    }
}

fun <T> Single<Option<T>>.await(activity: FragmentActivity) =
        this.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ option -> activity.showLongToast(option.fold({ "Cancelled" }) { it.toString() }) }) { throwable -> activity.showLongToast(throwable.message.orEmpty()) }

