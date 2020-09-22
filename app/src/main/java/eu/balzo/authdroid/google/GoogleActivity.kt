package eu.balzo.authdroid.google

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.balzo.authdroid.auth.R
import eu.balzo.authdroid.arrow.startCoroutine
import eu.balzo.authdroid.logError
import eu.balzo.authdroid.openProfile
import kotlinx.android.synthetic.main.google.*

class GoogleActivity : FragmentActivity(R.layout.google) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        google.setOnClickListener {
            startCoroutine {

                val auth =
                        Google.auth(
                                this,
                                getString(R.string.google_client_id_web)
                        )

                auth.fold(
                        { it.logError(this) },
                        { it.openProfile(this) }
                )

            }
        }

        google_logout.setOnClickListener {
            startCoroutine {

                val signOut =
                        Google.signOut(
                                this,
                                getString(R.string.google_client_id_web)
                        )

                signOut.fold(
                        {
                            Toast.makeText(
                                    this,
                                    "Error",
                                    Toast.LENGTH_LONG
                            ).show()
                        },
                        {
                            Toast.makeText(
                                    this,
                                    "Done",
                                    Toast.LENGTH_LONG
                            ).show()
                        }
                )

            }
        }
    }
}