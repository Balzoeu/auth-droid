package eu.balzo.authdroid.google

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.balzo.authdroid.auth.R
import com.balzo.authdroid.auth.databinding.GoogleBinding
import eu.balzo.authdroid.BaseActivity
import eu.balzo.authdroid.openProfile

class GoogleActivity : BaseActivity() {

    private lateinit var binding: GoogleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = GoogleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.google.setOnClickListener {
            lifecycleScope.launchSafe {

                val auth =
                        Google.auth(
                                this,
                                getString(R.string.google_client_id_web)
                        )

                auth.openProfile(this)

            }
        }

        binding.googleLogout.setOnClickListener {
            lifecycleScope.launchSafe {

                Google.signOut(
                        this,
                        getString(R.string.google_client_id_web)
                )

                Toast.makeText(
                        this,
                        "Done",
                        Toast.LENGTH_LONG
                ).show()

            }
        }
    }
}