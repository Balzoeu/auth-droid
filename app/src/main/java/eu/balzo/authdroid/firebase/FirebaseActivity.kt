package eu.balzo.authdroid.firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.balzo.authdroid.auth.R
import com.balzo.authdroid.auth.databinding.FirebaseBinding
import eu.balzo.authdroid.BaseActivity
import eu.balzo.authdroid.firebase.core.Firebase
import eu.balzo.authdroid.firebase.facebook.FirebaseFacebook
import eu.balzo.authdroid.firebase.google.FirebaseGoogle
import eu.balzo.authdroid.openProfile
import eu.balzo.authdroid.showToast

class FirebaseActivity : BaseActivity() {

    private lateinit var binding: FirebaseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FirebaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.firebaseGoogle.setOnClickListener {

            lifecycleScope.launchSafe {

                FirebaseGoogle.auth(
                        this,
                        getString(R.string.google_client_id_web)
                ).openProfile(this)

            }
        }

        binding.firebaseFacebook.setOnClickListener {

            lifecycleScope.launchSafe {

                FirebaseFacebook.auth(supportFragmentManager)
                        .openProfile(this)
            }

        }

        binding.firebaseCustom.setOnClickListener {
            startActivity(
                    Intent(this, FirebaseEmailPasswordActivity::class.java)
            )
        }





        binding.firebasePasswordReset.setOnClickListener {
            startActivity(
                    Intent(this, FirebasePasswordResetActivity::class.java)
            )
        }

        binding.firebasePasswordUpdate.setOnClickListener {
            startActivity(
                    Intent(this, FirebasePasswordUpdateActivity::class.java)
            )
        }



        binding.firebaseGetId.setOnClickListener {
            lifecycleScope.launchSafe { showToast(Firebase.id() ?: "Firebase user not logged") }
        }

        binding.firebaseGetToken.setOnClickListener {

            lifecycleScope.launchSafe {

                val token = Firebase.token()
                showToast(token)

            }
        }

        binding.firebaseGetUser.setOnClickListener {

            lifecycleScope.launchSafe {

                Firebase.currentUser().openProfile(this)

            }
        }





        binding.firebaseUpdateProfile.setOnClickListener {
            startActivity(
                    Intent(this, FirebaseProfileUpdateActivity::class.java)
            )
        }




        binding.firebaseGoogleLogout.setOnClickListener {

            lifecycleScope.launchSafe {

                FirebaseGoogle.signOut(
                        this,
                        getString(R.string.google_client_id_web))

                showToast("Done")

            }
        }

        binding.firebaseFacebookLogout.setOnClickListener {
            FirebaseFacebook.signOut()
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show()
        }

        binding.firebaseLogout.setOnClickListener {
            Firebase.signOut()
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show()
        }
    }
}