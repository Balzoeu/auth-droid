package eu.balzo.authdroid.firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.balzo.authdroid.auth.R
import eu.balzo.authdroid.arrow.startCoroutine
import eu.balzo.authdroid.firebase.core.Firebase
import eu.balzo.authdroid.firebase.facebook.FirebaseFacebook
import eu.balzo.authdroid.firebase.google.FirebaseGoogle
import eu.balzo.authdroid.logError
import eu.balzo.authdroid.openProfile
import eu.balzo.authdroid.showToast
import kotlinx.android.synthetic.main.firebase.*

class FirebaseActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.firebase)

        firebase_google.setOnClickListener {

            startCoroutine {

                FirebaseGoogle.auth(
                        this,
                        getString(R.string.google_client_id_web)
                ).fold(
                        { it.logError(this) },
                        { it.openProfile(this) }
                )

            }
        }

        firebase_facebook.setOnClickListener {

            startCoroutine {

                FirebaseFacebook.auth(supportFragmentManager)
                        .fold(
                                { it.logError(this) },
                                { it.openProfile(this) }
                        )

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
            startCoroutine { showToast(Firebase.id() ?: "Firebase user not logged") }
        }

        firebase_get_token.setOnClickListener {

            startCoroutine {

                Firebase.token()
                        .fold(
                                { it.logError(this) },
                                { showToast(it) }
                        )

            }
        }

        firebase_get_user.setOnClickListener {

            startCoroutine {

                Firebase.currentUser()
                        .fold(
                                { it.logError(this) },
                                { it.openProfile(this) }
                        )

            }
        }





        firebase_update_profile.setOnClickListener {
            startActivity(
                    Intent(this, FirebaseProfileUpdateActivity::class.java)
            )
        }




        firebase_google_logout.setOnClickListener {

            startCoroutine {

                FirebaseGoogle.signOut(
                        this,
                        getString(R.string.google_client_id_web))
                        .fold(
                                { showToast("Error") },
                                { showToast("Done") }
                        )

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