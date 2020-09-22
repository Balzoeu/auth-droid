package eu.balzo.authdroid.firebase

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.balzo.authdroid.auth.R
import eu.balzo.authdroid.arrow.startCoroutine
import eu.balzo.authdroid.firebase.core.Firebase
import eu.balzo.authdroid.logError
import eu.balzo.authdroid.showToast
import kotlinx.android.synthetic.main.firebase_password_update.update
import kotlinx.android.synthetic.main.firebase_profile_update.*

class FirebaseProfileUpdateActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.firebase_profile_update)

        update.setOnClickListener {

            startCoroutine {

                Firebase.updateProfile(
                        name_field.text.toString(),
                        photo_field.text.toString())
                        .fold(
                                { it.logError(this) },
                                { showToast("Done") }
                        )

            }

        }
    }
}