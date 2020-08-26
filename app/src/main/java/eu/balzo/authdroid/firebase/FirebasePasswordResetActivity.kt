package eu.balzo.authdroid.firebase

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.typeclasses.ConcurrentFx
import com.balzo.authdroid.auth.R
import eu.balzo.authdroid.arrow.unsafeRunAsync
import eu.balzo.authdroid.firebase.core.Firebase
import eu.balzo.authdroid.logError
import eu.balzo.authdroid.showToast
import kotlinx.android.synthetic.main.firebase_password_reset.*

class FirebasePasswordResetActivity : FragmentActivity() {

    private val fx: ConcurrentFx<ForIO> = IO.concurrent().fx

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.firebase_password_reset)

        reset.setOnClickListener {

            fx.concurrent {

                val email = email_field.text.toString()

                if (email.isEmpty() || email.isBlank())
                    this@FirebasePasswordResetActivity.showToast("Email must be valid")
                else
                    Firebase.resetPassword(fx, email)
                            .bind()
                            .fold(
                                    { it.logError(this@FirebasePasswordResetActivity) },
                                    {
                                        this@FirebasePasswordResetActivity
                                                .showToast("Check your email")
                                    }
                            )
            }.unsafeRunAsync()
        }
    }
}