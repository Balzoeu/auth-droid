package eu.balzo.authdroid.firebase

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.balzo.authdroid.auth.R
import eu.balzo.authdroid.BaseActivity
import eu.balzo.authdroid.firebase.core.Firebase
import eu.balzo.authdroid.showToast
import kotlinx.android.synthetic.main.firebase_password_reset.*
import kotlinx.coroutines.GlobalScope

class FirebasePasswordResetActivity : BaseActivity(R.layout.firebase_password_reset) {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        reset.setOnClickListener {

            lifecycleScope.launchSafe {

                val email = email_field.text.toString()

                if (email.isEmpty() || email.isBlank())
                    showToast("Email must be valid")
                else {
                    Firebase.resetPassword(email)
                    showToast("Check your email")
                }

            }
        }
    }
}