package eu.balzo.authdroid

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import eu.balzo.authdroid.core.Auth
import eu.balzo.authdroid.core.AuthError
import eu.balzo.authdroid.core.SocialAuthUser
import eu.balzo.authdroid.profile.ProfileActivity

fun Auth.openProfile(context: Context) {
    ProfileActivity.start(context, this.socialAuthUser)
}

fun SocialAuthUser.openProfile(context: Context) {
    ProfileActivity.start(context, this)
}

fun Throwable.logError(context: Context) {

    when (this) {
        is AuthError.Cancelled -> "Cancelled"
        else -> this.message
    }.let {
        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
    }
}

fun FragmentActivity.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}