package com.giacomoparisi.authdroid.firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.giacomoparisi.auth.droid.rx.firebase.google.authWithFirebaseGoogle
import com.giacomoparisi.auth.droid.rx.firebase.google.googleSignOut
import com.giacomoparisi.authdroid.auth.R
import com.giacomoparisi.authdroid.logError
import com.giacomoparisi.authdroid.openProfile
import com.giacomoparisi.authdroid.rx.facebook.facebookSignOut
import com.giacomoparisi.authdroid.rx.firebase.facebook.authWithFirebaseFacebook
import com.giacomoparisi.authdroid.rx.firebase.firebaseSignOut
import com.giacomoparisi.authdroid.rx.firebase.getCurrentFirebaseUser
import com.giacomoparisi.authdroid.rx.firebase.getFirebaseId
import com.giacomoparisi.authdroid.rx.firebase.getFirebaseToken
import com.giacomoparisi.authdroid.showToast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.firebase.*

class FirebaseActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.setContentView(R.layout.firebase)

        this.firebase_google.setOnClickListener {
            authWithFirebaseGoogle(this, this.getString(R.string.google_client_id_web))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ it.openProfile(this) }) { it.logError(this) }
        }

        this.firebase_facebook.setOnClickListener {
            authWithFirebaseFacebook(this)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ it.openProfile(this) }) { it.logError(this) }
        }

        this.firebase_custom.setOnClickListener {
            this.startActivity(
                    Intent(this, FirebaseEmailPasswordActivity::class.java)
            )
        }





        this.firebase_password_reset.setOnClickListener {
            this.startActivity(
                    Intent(this, FirebasePasswordReset::class.java)
            )
        }

        this.firebase_password_update.setOnClickListener {
            this.startActivity(
                    Intent(this, FirebasePasswordUpdate::class.java)
            )
        }




        this.firebase_get_id.setOnClickListener {
            this.showToast(getFirebaseId() ?: "Firebase user not logged")
        }

        this.firebase_get_token.setOnClickListener {
            getFirebaseToken()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ this.showToast(it) }) { it.logError(this) }
        }

        this.firebase_get_user.setOnClickListener {
            getCurrentFirebaseUser()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ it.openProfile(this) }) { it.logError(this) }
        }



        this.firebase_google_logout.setOnClickListener {
            googleSignOut(this, this.getString(R.string.google_client_id_web))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { Toast.makeText(this, "Done", Toast.LENGTH_LONG).show() })
                    { Toast.makeText(this, "Error", Toast.LENGTH_LONG).show() }
        }

        this.firebase_facebook_logout.setOnClickListener {
            facebookSignOut()
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show()
        }

        this.firebase_logout.setOnClickListener {
            firebaseSignOut()
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show()
        }
    }
}