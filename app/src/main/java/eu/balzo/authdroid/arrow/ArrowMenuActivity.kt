package eu.balzo.authdroid.arrow

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.balzo.authdroid.auth.R
import eu.balzo.authdroid.arrow.google.GoogleArrowActivity
import kotlinx.android.synthetic.main.arrow_menu.*

class ArrowMenuActivity : FragmentActivity(R.layout.arrow_menu) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebase.setOnClickListener {
        }
        facebook.setOnClickListener {
        }
        google.setOnClickListener {
            startActivity(Intent(this, GoogleArrowActivity::class.java))
        }
    }
}