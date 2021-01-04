package eu.balzo.authdroid.firebase

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.balzo.authdroid.auth.R
import eu.balzo.authdroid.BaseActivity
import eu.balzo.authdroid.firebase.core.Firebase
import eu.balzo.authdroid.showToast
import kotlinx.android.synthetic.main.firebase_email_password_auth.password_field
import kotlinx.android.synthetic.main.firebase_password_update.*
import kotlinx.coroutines.GlobalScope

class FirebasePasswordUpdateActivity : BaseActivity(R.layout.firebase_password_update) {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        update.setOnClickListener {

            lifecycleScope.launchSafe {

                val password = password_field.text.toString()

                if (password.isEmpty() || password.isBlank())
                    showToast("Password is empty")
                else {
                    Firebase.updatePassword(password)
                    showToast("Done")
                }

            }
        }
    }
}