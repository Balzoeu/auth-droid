package eu.balzo.authdroid.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.balzo.authdroid.auth.R
import com.bumptech.glide.Glide
import eu.balzo.authdroid.core.SocialAuthUser
import kotlinx.android.synthetic.main.profile.*

class ProfileActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.profile)

        name_value.text = intent.getStringExtra(NAME) ?: "/"
        first_name_value.text = intent.getStringExtra(FIRST_NAME) ?: "/"
        last_name_value.text = intent.getStringExtra(LAST_NAME) ?: "/"
        email_value.text = intent.getStringExtra(EMAIL) ?: "/"
        id_value.text = intent.getStringExtra(ID) ?: "/"
        token_value.text = intent.getStringExtra(TOKEN) ?: "/"
        google_auth_code_value.text = intent.getStringExtra(GOOGLE_AUTH_CODE) ?: "/"

        Glide.with(this)
                .load(intent.getStringExtra(PROFILE_IMAGE).orEmpty())
                .error(R.drawable.profile)
                .into(profile_image)
    }

    companion object {

        private const val EMAIL = "email"
        private const val NAME = "name"
        private const val FIRST_NAME = "first_name"
        private const val LAST_NAME = "last_name"
        private const val ID = "id"
        private const val TOKEN = "token"
        private const val GOOGLE_AUTH_CODE = "google_auth_code"
        private const val PROFILE_IMAGE = "profile_image"

        fun start(
                context: Context,
                user: SocialAuthUser
        ) {
            val intent = Intent(context, ProfileActivity::class.java)

            intent.putExtra(NAME, user.displayName)
            intent.putExtra(FIRST_NAME, user.firstName)
            intent.putExtra(LAST_NAME, user.lastName)
            intent.putExtra(EMAIL, user.email)
            intent.putExtra(ID, user.id)
            intent.putExtra(TOKEN, user.token)
            intent.putExtra(GOOGLE_AUTH_CODE, user.googleServerAuthCode)
            intent.putExtra(PROFILE_IMAGE, user.profileImage)

            context.startActivity(intent)
        }
    }
}