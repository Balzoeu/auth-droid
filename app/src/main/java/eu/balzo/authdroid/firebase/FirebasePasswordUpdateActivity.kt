package eu.balzo.authdroid.firebase

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.balzo.authdroid.auth.R
import eu.balzo.authdroid.arrow.startCoroutine
import eu.balzo.authdroid.firebase.core.Firebase
import eu.balzo.authdroid.logError
import eu.balzo.authdroid.showToast
import kotlinx.android.synthetic.main.firebase_email_password_auth.password_field
import kotlinx.android.synthetic.main.firebase_password_update.*

class FirebasePasswordUpdateActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.firebase_password_update)

        update.setOnClickListener {

            startCoroutine {

                val password = password_field.text.toString()

                if (password.isEmpty() || password.isBlank())
                    showToast("Password is empty")
                else
                    Firebase.updatePassword(password)
                            .fold(
                                    { it.logError(this) },
                                    { showToast("Done") }
                            )

            }
        }
    }
}