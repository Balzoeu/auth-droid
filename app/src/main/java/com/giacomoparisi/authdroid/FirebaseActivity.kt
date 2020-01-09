package com.giacomoparisi.authdroid

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.giacomoparisi.authdroid.auth.R
import com.giacomoparisi.authdroid.rx.facebook.facebookSignOut
import com.giacomoparisi.authdroid.rx.firebase.authWithFirebaseFacebook
import com.giacomoparisi.authdroid.rx.firebase.authWithFirebaseGoogle
import com.giacomoparisi.authdroid.rx.firebase.firebaseSignOut
import com.giacomoparisi.authdroid.rx.firebase.googleSignOut
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

        this.firebase_google_logout.setOnClickListener {
            googleSignOut(this, this.getString(R.string.google_client_id_web))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                firebaseSignOut()
                                Toast.makeText(this, "Done", Toast.LENGTH_LONG).show()
                            })
                    { Toast.makeText(this, "Error", Toast.LENGTH_LONG).show() }
        }

        this.firebase_facebook_logout.setOnClickListener {
            facebookSignOut()
            firebaseSignOut()
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show()
        }
    }
}