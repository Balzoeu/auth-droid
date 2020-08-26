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
import kotlinx.android.synthetic.main.firebase_password_update.update
import kotlinx.android.synthetic.main.firebase_profile_update.*

class FirebaseProfileUpdateActivity : FragmentActivity() {

    private val fx: ConcurrentFx<ForIO> = IO.concurrent().fx

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.firebase_profile_update)

        update.setOnClickListener {

            fx.concurrent {

                Firebase.updateProfile(
                        fx,
                        name_field.text.toString(),
                        photo_field.text.toString())
                        .bind()
                        .fold(
                                { it.logError(this@FirebaseProfileUpdateActivity) },
                                { this@FirebaseProfileUpdateActivity.showToast("Done") }
                        )

            }.unsafeRunAsync()

        }
    }
}