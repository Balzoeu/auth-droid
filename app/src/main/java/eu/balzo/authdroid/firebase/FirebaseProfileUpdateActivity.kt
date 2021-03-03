package eu.balzo.authdroid.firebase

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.balzo.authdroid.auth.R
import com.balzo.authdroid.auth.databinding.FirebaseProfileUpdateBinding
import eu.balzo.authdroid.BaseActivity
import eu.balzo.authdroid.firebase.core.Firebase
import eu.balzo.authdroid.showToast

class FirebaseProfileUpdateActivity : BaseActivity(R.layout.firebase_profile_update) {

    private lateinit var binding: FirebaseProfileUpdateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FirebaseProfileUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.update.setOnClickListener {

            lifecycleScope.launchSafe {

                Firebase.updateProfile(
                        binding.nameField.text.toString(),
                        binding.photoField.text.toString()
                )

                showToast("Done")

            }

        }
    }
}