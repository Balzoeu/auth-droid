package eu.balzo.authdroid.facebook

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.balzo.authdroid.auth.R
import eu.balzo.authdroid.arrow.startCoroutine
import eu.balzo.authdroid.logError
import eu.balzo.authdroid.openProfile
import kotlinx.android.synthetic.main.facebook.*

class FacebookActivity : FragmentActivity(R.layout.facebook) {

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        facebook.setOnClickListener {

            startCoroutine {

                val auth = Facebook.auth(supportFragmentManager)

                auth.fold(
                        { it.logError(this) },
                        { it.openProfile(this) }
                )

            }
        }

        facebook_logout.setOnClickListener {

            Facebook.signOut()
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show()
        }
    }
}