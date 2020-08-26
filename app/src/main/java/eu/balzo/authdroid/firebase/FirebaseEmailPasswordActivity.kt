package eu.balzo.authdroid.firebase

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.FragmentActivity
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.typeclasses.ConcurrentFx
import com.balzo.authdroid.auth.R
import eu.balzo.authdroid.arrow.unsafeRunAsync
import eu.balzo.authdroid.firebase.core.Firebase
import eu.balzo.authdroid.logError
import eu.balzo.authdroid.openProfile
import kotlinx.android.synthetic.main.firebase_email_password_auth.*

class FirebaseEmailPasswordActivity : FragmentActivity() {

    private var email: String = ""
    private var password: String = ""

    private val fx: ConcurrentFx<ForIO> = IO.concurrent().fx

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.setContentView(R.layout.firebase_email_password_auth)

        this.email_field.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                this@FirebaseEmailPasswordActivity.email = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        this.password_field.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                this@FirebaseEmailPasswordActivity.password = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        this.sign_up.setOnClickListener {

            fx.concurrent {

                Firebase.signUpWithFirebaseEmailPassword(
                        fx,
                        email,
                        password)
                        .bind()
                        .fold(
                                { it.logError(this@FirebaseEmailPasswordActivity) },
                                { it.openProfile(this@FirebaseEmailPasswordActivity) }
                        )

            }.unsafeRunAsync()
        }

        this.sign_in.setOnClickListener {

            fx.concurrent {

                Firebase.signInWithFirebaseEmailPassword(
                        fx,
                        email,
                        password)
                        .bind()
                        .fold(
                                { it.logError(this@FirebaseEmailPasswordActivity) },
                                { it.openProfile(this@FirebaseEmailPasswordActivity) }
                        )

            }.unsafeRunAsync()
        }
    }
}