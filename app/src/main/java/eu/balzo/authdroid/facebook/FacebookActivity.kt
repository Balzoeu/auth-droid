package eu.balzo.authdroid.facebook

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import arrow.fx.IO
import arrow.fx.extensions.io.concurrent.concurrent
import com.balzo.authdroid.auth.R
import eu.balzo.authdroid.arrow.unsafeRunAsync
import eu.balzo.authdroid.logError
import eu.balzo.authdroid.openProfile
import kotlinx.android.synthetic.main.facebook.*

class FacebookActivity : FragmentActivity(R.layout.facebook) {

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        val fx = IO.concurrent().fx

        facebook.setOnClickListener {

            fx.concurrent {

                val auth = !Facebook.auth(fx, supportFragmentManager)

                auth.fold(
                        { it.logError(this@FacebookActivity) },
                        { it.openProfile(this@FacebookActivity) }
                )

            }.unsafeRunAsync()
        }
        facebook_logout.setOnClickListener {

            Facebook.signOut()
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show()
        }
    }
}