package eu.balzo.authdroid.arrow.facebook

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

class FacebookArrowActivity : FragmentActivity(R.layout.facebook) {

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        val fx = IO.concurrent().fx

        facebook.setOnClickListener {

            fx.concurrent {

                val auth = !FacebookArrow.auth(fx, supportFragmentManager)

                auth.fold(
                        { it.logError(this@FacebookArrowActivity) },
                        { it.openProfile(this@FacebookArrowActivity) }
                )

            }.unsafeRunAsync()
        }
        facebook_logout.setOnClickListener {

            FacebookArrow.signOut()
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show()
        }
    }
}