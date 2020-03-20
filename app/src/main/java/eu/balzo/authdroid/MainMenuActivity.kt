package eu.balzo.authdroid

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.balzo.authdroid.auth.R
import eu.balzo.authdroid.facebook.FacebookActivity
import eu.balzo.authdroid.firebase.FirebaseActivity
import eu.balzo.authdroid.google.GoogleActivity
import kotlinx.android.synthetic.main.main_menu.*

class MainMenuActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_menu)

        firebase.setOnClickListener {
            startActivity(Intent(this, FirebaseActivity::class.java))
        }
        facebook.setOnClickListener {
            startActivity(Intent(this, FacebookActivity::class.java))
        }
        google.setOnClickListener {
            startActivity(Intent(this, GoogleActivity::class.java))
        }
    }
}