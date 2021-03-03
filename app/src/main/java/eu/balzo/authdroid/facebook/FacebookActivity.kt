package eu.balzo.authdroid.facebook

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.balzo.authdroid.auth.databinding.FacebookBinding
import eu.balzo.authdroid.BaseActivity
import eu.balzo.authdroid.openProfile

class FacebookActivity : BaseActivity() {

    private lateinit var binding: FacebookBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FacebookBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.facebook.setOnClickListener {

            lifecycleScope.launchSafe {

                val auth = Facebook.auth(supportFragmentManager)
                auth.openProfile(this)

            }
        }

        binding.facebookLogout.setOnClickListener {

            Facebook.signOut()
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show()

        }
    }
}