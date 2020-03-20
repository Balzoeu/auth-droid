package eu.balzo.authdroid.arrow.google

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import arrow.fx.IO
import arrow.fx.extensions.io.concurrent.concurrent
import com.balzo.authdroid.auth.R
import eu.balzo.authdroid.arrow.unsafeRunAsync
import eu.balzo.authdroid.logError
import eu.balzo.authdroid.openProfile
import kotlinx.android.synthetic.main.google.*

class GoogleArrowActivity : FragmentActivity(R.layout.google) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fx = IO.concurrent().fx

        google.setOnClickListener {
            fx.concurrent {

                val auth =
                        !authWithGoogle(
                                IO.concurrent().fx,
                                this@GoogleArrowActivity,
                                getString(R.string.google_client_id_web)
                        )

                auth.fold(
                        { it.logError(this@GoogleArrowActivity) },
                        { it.openProfile(this@GoogleArrowActivity) }
                )

            }.unsafeRunAsync()
        }

        google_logout.setOnClickListener {
            fx.concurrent {

                val signOut =
                        !googleSignOut(
                                IO.concurrent().fx,
                                this@GoogleArrowActivity,
                                getString(R.string.google_client_id_web)
                        )

                signOut.fold(
                        {
                            Toast.makeText(
                                    this@GoogleArrowActivity,
                                    "Error",
                                    Toast.LENGTH_LONG
                            ).show()
                        },
                        {
                            Toast.makeText(
                                    this@GoogleArrowActivity,
                                    "Done",
                                    Toast.LENGTH_LONG
                            ).show()
                        }
                )

            }.unsafeRunAsync()
        }
    }
}