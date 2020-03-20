package eu.balzo.authdroid.rx

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.balzo.authdroid.auth.R
import eu.balzo.authdroid.rx.facebook.FacebookRxActivity
import eu.balzo.authdroid.rx.firebase.FirebaseRxActivity
import eu.balzo.authdroid.rx.google.GoogleRxActivity
import kotlinx.android.synthetic.main.rx_menu.*

class RxMenuActivity : FragmentActivity(R.layout.rx_menu) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebase.setOnClickListener {
            startActivity(Intent(this, FirebaseRxActivity::class.java))
        }
        facebook.setOnClickListener {
            startActivity(Intent(this, FacebookRxActivity::class.java))
        }
        google.setOnClickListener {
            startActivity(Intent(this, GoogleRxActivity::class.java))
        }
    }
}