package eu.balzo.authdroid.firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import arrow.core.getOrElse
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.typeclasses.ConcurrentFx
import com.balzo.authdroid.auth.R
import eu.balzo.authdroid.arrow.unsafeRunAsync
import eu.balzo.authdroid.firebase.core.Firebase
import eu.balzo.authdroid.firebase.facebook.FirebaseFacebook
import eu.balzo.authdroid.firebase.google.FirebaseGoogle
import eu.balzo.authdroid.logError
import eu.balzo.authdroid.openProfile
import eu.balzo.authdroid.showToast
import kotlinx.android.synthetic.main.firebase.*

class FirebaseActivity : FragmentActivity() {

    private val fx: ConcurrentFx<ForIO> = IO.concurrent().fx

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.setContentView(R.layout.firebase)

        this.firebase_google.setOnClickListener {

            fx.concurrent {

                FirebaseGoogle.auth(
                        fx,
                        this@FirebaseActivity,
                        getString(R.string.google_client_id_web))
                        .bind()
                        .fold(
                                { it.logError(this@FirebaseActivity) },
                                { it.openProfile(this@FirebaseActivity) }
                        )

            }.unsafeRunAsync()
        }

        this.firebase_facebook.setOnClickListener {

            fx.concurrent {

                FirebaseFacebook.auth(
                        fx,
                        this@FirebaseActivity.supportFragmentManager)
                        .bind()
                        .fold(
                                { it.logError(this@FirebaseActivity) },
                                { it.openProfile(this@FirebaseActivity) }
                        )

            }.unsafeRunAsync()
        }

        this.firebase_custom.setOnClickListener {
            this.startActivity(
                    Intent(this, FirebaseEmailPasswordActivity::class.java)
            )
        }





        this.firebase_password_reset.setOnClickListener {
            this.startActivity(
                    Intent(this, FirebasePasswordResetActivity::class.java)
            )
        }

        this.firebase_password_update.setOnClickListener {
            this.startActivity(
                    Intent(this, FirebasePasswordUpdateActivity::class.java)
            )
        }




        this.firebase_get_id.setOnClickListener {
            this.showToast(Firebase.id().getOrElse { "Firebase user not logged" })
        }

        this.firebase_get_token.setOnClickListener {

            fx.concurrent {

                Firebase.token(fx)
                        .bind()
                        .fold(
                                { it.logError(this@FirebaseActivity) },
                                { this@FirebaseActivity.showToast(it) }
                        )

            }.unsafeRunAsync()
        }

        this.firebase_get_user.setOnClickListener {

            fx.concurrent {

                Firebase.currentUser(fx)
                        .bind()
                        .fold(
                                { it.logError(this@FirebaseActivity) },
                                { it.openProfile(this@FirebaseActivity) }
                        )

            }.unsafeRunAsync()
        }





        this.firebase_update_profile.setOnClickListener {
            this.startActivity(
                    Intent(this, FirebaseProfileUpdateActivity::class.java)
            )
        }




        this.firebase_google_logout.setOnClickListener {

            fx.concurrent {

                FirebaseGoogle.signOut(
                        fx,
                        this@FirebaseActivity,
                        getString(R.string.google_client_id_web))
                        .bind()
                        .fold(
                                { this@FirebaseActivity.showToast("Error") },
                                { this@FirebaseActivity.showToast("Done") }
                        )

            }.unsafeRunAsync()
        }

        this.firebase_facebook_logout.setOnClickListener {
            FirebaseFacebook.signOut()
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show()
        }

        this.firebase_logout.setOnClickListener {
            Firebase.signOut()
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show()
        }
    }
}