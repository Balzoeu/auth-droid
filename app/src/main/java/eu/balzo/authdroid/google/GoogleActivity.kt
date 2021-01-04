package eu.balzo.authdroid.google

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.balzo.authdroid.auth.R
import eu.balzo.authdroid.BaseActivity
import eu.balzo.authdroid.openProfile
import kotlinx.android.synthetic.main.google.*

class GoogleActivity : BaseActivity(R.layout.google) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        google.setOnClickListener {
            lifecycleScope.launchSafe {

                val auth =
                        Google.auth(
                                this,
                                getString(R.string.google_client_id_web)
                        )

                auth.openProfile(this)

            }
        }

        google_logout.setOnClickListener {
            lifecycleScope.launchSafe {

                Google.signOut(
                        this,
                        getString(R.string.google_client_id_web)
                )

                Toast.makeText(
                        this,
                        "Done",
                        Toast.LENGTH_LONG
                ).show()

            }
        }
    }
}