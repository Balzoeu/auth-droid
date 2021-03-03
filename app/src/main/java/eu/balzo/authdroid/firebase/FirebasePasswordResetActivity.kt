package eu.balzo.authdroid.firebase

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.balzo.authdroid.auth.databinding.FirebasePasswordResetBinding
import eu.balzo.authdroid.BaseActivity
import eu.balzo.authdroid.firebase.core.Firebase
import eu.balzo.authdroid.showToast

class FirebasePasswordResetActivity : BaseActivity() {

    private lateinit var binding: FirebasePasswordResetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FirebasePasswordResetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.reset.setOnClickListener {

            lifecycleScope.launchSafe {

                val email = binding.emailField.text.toString()

                if (email.isEmpty() || email.isBlank())
                    showToast("Email must be valid")
                else {
                    Firebase.resetPassword(email)
                    showToast("Check your email")
                }

            }
        }
    }
}