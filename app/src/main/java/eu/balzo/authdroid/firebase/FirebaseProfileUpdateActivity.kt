package eu.balzo.authdroid.firebase

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.balzo.authdroid.auth.R
import eu.balzo.authdroid.BaseActivity
import eu.balzo.authdroid.firebase.core.Firebase
import eu.balzo.authdroid.showToast
import kotlinx.android.synthetic.main.firebase_password_update.update
import kotlinx.android.synthetic.main.firebase_profile_update.*
import kotlinx.coroutines.GlobalScope

class FirebaseProfileUpdateActivity : BaseActivity(R.layout.firebase_profile_update) {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        update.setOnClickListener {

            lifecycleScope.launchSafe {

                Firebase.updateProfile(
                        name_field.text.toString(),
                        photo_field.text.toString()
                )

                showToast("Done")

            }

        }
    }
}