package eu.balzo.authdroid.firebase

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.lifecycleScope
import com.balzo.authdroid.auth.R
import eu.balzo.authdroid.BaseActivity
import eu.balzo.authdroid.firebase.core.Firebase
import eu.balzo.authdroid.openProfile
import kotlinx.android.synthetic.main.firebase_email_password_auth.*

class FirebaseEmailPasswordActivity : BaseActivity(R.layout.firebase_email_password_auth) {

    private var email: String = ""
    private var password: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        email_field.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                email = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        password_field.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                password = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        sign_up.setOnClickListener {

            lifecycleScope.launchSafe {

                Firebase.signUpWithFirebaseEmailPassword(email, password)
                        .openProfile(this)

            }
        }

        sign_in.setOnClickListener {

            lifecycleScope.launchSafe {

                Firebase.signInWithFirebaseEmailPassword(email, password)
                        .openProfile(this)

            }
        }
    }
}