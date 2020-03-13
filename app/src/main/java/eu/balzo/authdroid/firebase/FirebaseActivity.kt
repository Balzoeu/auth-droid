package eu.balzo.authdroid.firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.balzo.authdroid.auth.R
import eu.balzo.authdroid.logError
import eu.balzo.authdroid.openProfile
import eu.balzo.authdroid.rx.facebook.facebookSignOut
import eu.balzo.authdroid.rx.firebase.facebook.authWithFirebaseFacebook
import eu.balzo.authdroid.rx.firebase.firebaseSignOut
import eu.balzo.authdroid.rx.firebase.getCurrentFirebaseUser
import eu.balzo.authdroid.rx.firebase.getFirebaseId
import eu.balzo.authdroid.rx.firebase.getFirebaseToken
import eu.balzo.authdroid.rx.firebase.google.authWithFirebaseGoogle
import eu.balzo.authdroid.rx.firebase.google.googleSignOut
import eu.balzo.authdroid.showToast
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
                    Intent(this, FirebasePasswordResetActivity::class.java)
            )
        }

        this.firebase_password_update.setOnClickListener {
            this.startActivity(
                    Intent(this, FirebasePasswordUpdateActivity::class.java)
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





        this.firebase_update_profile.setOnClickListener {
            this.startActivity(
                    Intent(this, FirebaseProfileUpdateActivity::class.java)
            )
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