package com.giacomoparisi.arrow.social.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import arrow.core.Option
import com.giacomoparisi.arrow.social.auth.core.firebase.*
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
            this@AuthActivity.googleSignIn()
        }

        this.firebase_facebook_login.setOnClickListener {
            this@AuthActivity.facebookSignIn()
        }

        this.firebase_email_password_login.setOnClickListener {
            this.supportFragmentManager.beginTransaction()
                    .replace(R.id.root, EmailPasswordAuthFragment())
                    .addToBackStack(null)
                    .commit()
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

    private fun googleSignIn() {
        authWithFirebaseGoogle(
                this,
                this.getString(R.string.google_client_id_web))
                .await(this)
    }

    private fun facebookSignIn() {
        authWithFirebaseFacebook(this).await(this)
    }
}

fun <T> Single<Option<T>>.await(activity: FragmentActivity) =
        this.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ option -> activity.showLongToast(option.fold({ "Cancelled" }) { it.toString() }) }) { throwable -> activity.showLongToast(throwable.message.orEmpty()) }

