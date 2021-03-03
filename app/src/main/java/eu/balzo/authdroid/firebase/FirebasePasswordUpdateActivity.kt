package eu.balzo.authdroid.firebase

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.balzo.authdroid.auth.databinding.FirebasePasswordUpdateBinding
import eu.balzo.authdroid.BaseActivity
import eu.balzo.authdroid.firebase.core.Firebase
import eu.balzo.authdroid.showToast

class FirebasePasswordUpdateActivity : BaseActivity() {

    private lateinit var binding: FirebasePasswordUpdateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FirebasePasswordUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.update.setOnClickListener {

            lifecycleScope.launchSafe {

                val password = binding.passwordField.text.toString()

                if (password.isEmpty() || password.isBlank())
                    showToast("Password is empty")
                else {
                    Firebase.updatePassword(password)
                    showToast("Done")
                }

            }
        }
    }
}