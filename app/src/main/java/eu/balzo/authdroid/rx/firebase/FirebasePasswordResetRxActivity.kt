package eu.balzo.authdroid.rx.firebase

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.balzo.authdroid.auth.R
import eu.balzo.authdroid.logError
import eu.balzo.authdroid.showToast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.firebase_password_reset.*

class FirebasePasswordResetRxActivity : FragmentActivity() {

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        this.setContentView(R.layout.firebase_password_reset)

        this.reset.setOnClickListener {

            val email = this.email_field.text.toString()

            if (email.isEmpty() || email.isBlank()) {
                this.showToast("Email must be valid")
            } else {
                resetFirebasePassword(email)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ this.showToast("Check your email") }) { it.logError(this) }
            }
        }
    }
}