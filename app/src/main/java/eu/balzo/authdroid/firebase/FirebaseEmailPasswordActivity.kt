package eu.balzo.authdroid.firebase

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.FragmentActivity
import com.balzo.authdroid.auth.R
import eu.balzo.authdroid.arrow.startCoroutine
import eu.balzo.authdroid.firebase.core.Firebase
import eu.balzo.authdroid.logError
import eu.balzo.authdroid.openProfile
import kotlinx.android.synthetic.main.firebase_email_password_auth.*

class FirebaseEmailPasswordActivity : FragmentActivity() {

    private var email: String = ""
    private var password: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.firebase_email_password_auth)

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

            startCoroutine {

                Firebase.signUpWithFirebaseEmailPassword(
                        email,
                        password)
                        .fold(
                                { it.logError(this) },
                                { it.openProfile(this) }
                        )

            }
        }

        sign_in.setOnClickListener {

            startCoroutine {

                Firebase.signInWithFirebaseEmailPassword(
                        email,
                        password)
                        .fold(
                                { it.logError(this) },
                                { it.openProfile(this) }
                        )

            }
        }
    }
}