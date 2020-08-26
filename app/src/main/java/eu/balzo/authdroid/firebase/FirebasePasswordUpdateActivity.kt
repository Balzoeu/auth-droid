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
import kotlinx.android.synthetic.main.firebase_email_password_auth.password_field
import kotlinx.android.synthetic.main.firebase_password_update.*

class FirebasePasswordUpdateActivity : FragmentActivity() {

    private val fx: ConcurrentFx<ForIO> = IO.concurrent().fx

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.firebase_password_update)

        update.setOnClickListener {

            fx.concurrent {

                val password = password_field.text.toString()

                if (password.isEmpty() || password.isBlank())
                    this@FirebasePasswordUpdateActivity.showToast("Password is empty")
                else
                    Firebase.updatePassword(fx, password)
                            .bind()
                            .fold(
                                    { it.logError(this@FirebasePasswordUpdateActivity) },
                                    { this@FirebasePasswordUpdateActivity.showToast("Done") }
                            )

            }.unsafeRunAsync()
        }
    }
}