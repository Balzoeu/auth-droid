package eu.balzo.authdroid.firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.balzo.authdroid.auth.R
import eu.balzo.authdroid.BaseActivity
import eu.balzo.authdroid.firebase.core.Firebase
import eu.balzo.authdroid.firebase.facebook.FirebaseFacebook
import eu.balzo.authdroid.firebase.google.FirebaseGoogle
import eu.balzo.authdroid.openProfile
import eu.balzo.authdroid.showToast
import kotlinx.android.synthetic.main.firebase.*

class FirebaseActivity : BaseActivity(R.layout.firebase) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebase_google.setOnClickListener {

            lifecycleScope.launchSafe {

                FirebaseGoogle.auth(
                        this,
                        getString(R.string.google_client_id_web)
                ).openProfile(this)

            }
        }

        firebase_facebook.setOnClickListener {

            lifecycleScope.launchSafe {

                FirebaseFacebook.auth(supportFragmentManager)
                        .openProfile(this)
            }

        }

        firebase_custom.setOnClickListener {
            startActivity(
                    Intent(this, FirebaseEmailPasswordActivity::class.java)
            )
        }





        firebase_password_reset.setOnClickListener {
            startActivity(
                    Intent(this, FirebasePasswordResetActivity::class.java)
            )
        }

        firebase_password_update.setOnClickListener {
            startActivity(
                    Intent(this, FirebasePasswordUpdateActivity::class.java)
            )
        }



        firebase_get_id.setOnClickListener {
            lifecycleScope.launchSafe { showToast(Firebase.id() ?: "Firebase user not logged") }
        }

        firebase_get_token.setOnClickListener {

            lifecycleScope.launchSafe {

                val token = Firebase.token()
                showToast(token)

            }
        }

        firebase_get_user.setOnClickListener {

            lifecycleScope.launchSafe {

                Firebase.currentUser().openProfile(this)

            }
        }





        firebase_update_profile.setOnClickListener {
            startActivity(
                    Intent(this, FirebaseProfileUpdateActivity::class.java)
            )
        }




        firebase_google_logout.setOnClickListener {

            lifecycleScope.launchSafe {

                FirebaseGoogle.signOut(
                        this,
                        getString(R.string.google_client_id_web))

                showToast("Done")

            }
        }

        firebase_facebook_logout.setOnClickListener {
            FirebaseFacebook.signOut()
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show()
        }

        firebase_logout.setOnClickListener {
            Firebase.signOut()
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show()
        }
    }
}