package eu.balzo.authdroid.rx.firebase

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.balzo.authdroid.auth.R
import eu.balzo.authdroid.logError
import eu.balzo.authdroid.showToast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.firebase_email_password_auth.password_field
import kotlinx.android.synthetic.main.firebase_password_update.*

class FirebasePasswordUpdateRxActivity : FragmentActivity() {

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        this.setContentView(R.layout.firebase_password_update)

        this.update.setOnClickListener {

            val password = this.password_field.text.toString()

            if (password.isEmpty() || password.isBlank()) {
                this.showToast("Password is empty")
            } else {
                FirebaseRx.updatePassword(password)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ this.showToast("Done") }) { it.logError(this) }
            }
        }
    }
}