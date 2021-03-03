package eu.balzo.authdroid

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.balzo.authdroid.auth.databinding.MainMenuBinding
import eu.balzo.authdroid.facebook.FacebookActivity
import eu.balzo.authdroid.firebase.FirebaseActivity
import eu.balzo.authdroid.google.GoogleActivity

class MainMenuActivity : FragmentActivity() {

    private lateinit var binding: MainMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.firebase.setOnClickListener {
            startActivity(Intent(this, FirebaseActivity::class.java))
        }

        binding.facebook.setOnClickListener {
            startActivity(Intent(this, FacebookActivity::class.java))
        }

        binding.google.setOnClickListener {
            startActivity(Intent(this, GoogleActivity::class.java))
        }

    }
}