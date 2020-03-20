package eu.balzo.authdroid

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.balzo.authdroid.auth.R
import eu.balzo.authdroid.arrow.ArrowMenuActivity
import eu.balzo.authdroid.rx.RxMenuActivity
import kotlinx.android.synthetic.main.main_menu.*

class MainMenuActivity : FragmentActivity(R.layout.main_menu) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rx.setOnClickListener {
            startActivity(Intent(this, RxMenuActivity::class.java))
        }
        arrow.setOnClickListener {
            startActivity(Intent(this, ArrowMenuActivity::class.java))
        }
    }
}