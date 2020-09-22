package eu.balzo.authdroid.firebase

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.balzo.authdroid.auth.R
import eu.balzo.authdroid.arrow.startCoroutine
import eu.balzo.authdroid.firebase.core.Firebase
import eu.balzo.authdroid.logError
import eu.balzo.authdroid.showToast
import kotlinx.android.synthetic.main.firebase_password_reset.*

class FirebasePasswordResetActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.firebase_password_reset)

        reset.setOnClickListener {

            startCoroutine {

                val email = email_field.text.toString()

                if (email.isEmpty() || email.isBlank())
                    showToast("Email must be valid")
                else
                    Firebase.resetPassword(email)
                            .fold(
                                    { it.logError(this) },
                                    { showToast("Check your email") }
                            )
            }
        }
    }
}