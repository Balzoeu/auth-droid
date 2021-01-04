package eu.balzo.authdroid

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import eu.balzo.authdroid.core.Auth
import eu.balzo.authdroid.core.AuthError
import eu.balzo.authdroid.core.SocialAuthUser
import eu.balzo.authdroid.profile.ProfileActivity

fun Auth.openProfile(context: Context): Unit =
        ProfileActivity.start(context, this.socialAuthUser)

fun SocialAuthUser.openProfile(context: Context): Unit =
        ProfileActivity.start(context, this)

fun Throwable.logError(context: Context): Unit =

        when (this) {

            is AuthError.Cancelled -> "Cancelled"
            is AuthError.FirebaseUnknown -> "Firebase Unknown Error"
            is AuthError.FirebaseUserNotLogged -> "Firebase User Not Logged"
            is AuthError.GoogleAuth -> "GoogleAuth Auth Error"
            is AuthError.FacebookAuth -> "Facebook Auth Error"
            is AuthError.Unknown -> "Unknown Error"
            else -> message ?: "Unknown Error"

        }.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }

fun FragmentActivity.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}
