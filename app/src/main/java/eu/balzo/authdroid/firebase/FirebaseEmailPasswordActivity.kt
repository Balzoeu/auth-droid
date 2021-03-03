package eu.balzo.authdroid.firebase

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.lifecycleScope
import com.balzo.authdroid.auth.databinding.FirebaseEmailPasswordAuthBinding
import eu.balzo.authdroid.BaseActivity
import eu.balzo.authdroid.firebase.core.Firebase
import eu.balzo.authdroid.openProfile

class FirebaseEmailPasswordActivity : BaseActivity() {

    private var email: String = ""
    private var password: String = ""

    private lateinit var binding: FirebaseEmailPasswordAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FirebaseEmailPasswordAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.emailField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                email = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.passwordField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                password = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.signUp.setOnClickListener {

            lifecycleScope.launchSafe {

                Firebase.signUpWithFirebaseEmailPassword(email, password)
                        .openProfile(this)

            }
        }

        binding.signIn.setOnClickListener {

            lifecycleScope.launchSafe {

                Firebase.signInWithFirebaseEmailPassword(email, password)
                        .openProfile(this)

            }
        }
    }
}