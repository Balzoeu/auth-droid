package eu.balzo.authdroid.rx.firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.balzo.authdroid.auth.R
import eu.balzo.authdroid.logError
import eu.balzo.authdroid.openProfile
import eu.balzo.authdroid.rx.firebase.facebook.FirebaseFacebookRx
import eu.balzo.authdroid.rx.firebase.google.FirebaseGoogleRx
import eu.balzo.authdroid.showToast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.firebase.*

class FirebaseRxActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.setContentView(R.layout.firebase)

        this.firebase_google.setOnClickListener {
            FirebaseGoogleRx.auth(this, this.getString(R.string.google_client_id_web))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ it.openProfile(this) }) { it.logError(this) }
        }

        this.firebase_facebook.setOnClickListener {
            FirebaseFacebookRx.auth(supportFragmentManager)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ it.openProfile(this) }) { it.logError(this) }
        }

        this.firebase_custom.setOnClickListener {
            this.startActivity(
                    Intent(this, FirebaseEmailPasswordRxActivity::class.java)
            )
        }





        this.firebase_password_reset.setOnClickListener {
            this.startActivity(
                    Intent(this, FirebasePasswordResetRxActivity::class.java)
            )
        }

        this.firebase_password_update.setOnClickListener {
            this.startActivity(
                    Intent(this, FirebasePasswordUpdateRxActivity::class.java)
            )
        }




        this.firebase_get_id.setOnClickListener {
            this.showToast(FirebaseRx.id() ?: "Firebase user not logged")
        }

        this.firebase_get_token.setOnClickListener {
            FirebaseRx.token()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ this.showToast(it) }) { it.logError(this) }
        }

        this.firebase_get_user.setOnClickListener {
            FirebaseRx.currentUser()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ it.openProfile(this) }) { it.logError(this) }
        }





        this.firebase_update_profile.setOnClickListener {
            this.startActivity(
                    Intent(this, FirebaseProfileUpdateRxActivity::class.java)
            )
        }




        this.firebase_google_logout.setOnClickListener {
            FirebaseGoogleRx.signOut(this, this.getString(R.string.google_client_id_web))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { Toast.makeText(this, "Done", Toast.LENGTH_LONG).show() })
                    { Toast.makeText(this, "Error", Toast.LENGTH_LONG).show() }
        }

        this.firebase_facebook_logout.setOnClickListener {
            FirebaseFacebookRx.signOut()
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show()
        }

        this.firebase_logout.setOnClickListener {
            FirebaseRx.signOut()
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show()
        }
    }
}