package eu.balzo.authdroid.facebook

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.balzo.authdroid.auth.R
import eu.balzo.authdroid.BaseActivity
import eu.balzo.authdroid.openProfile
import kotlinx.android.synthetic.main.facebook.*

class FacebookActivity : BaseActivity(R.layout.facebook) {

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        facebook.setOnClickListener {

            lifecycleScope.launchSafe {

                val auth = Facebook.auth(supportFragmentManager)
                auth.openProfile(this)

            }
        }

        facebook_logout.setOnClickListener {

            Facebook.signOut()
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show()

        }
    }
}