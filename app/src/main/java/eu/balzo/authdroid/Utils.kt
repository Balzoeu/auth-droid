package eu.balzo.authdroid

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import arrow.core.getOrElse
import arrow.core.toOption
import eu.balzo.authdroid.core.Auth
import eu.balzo.authdroid.core.AuthError
import eu.balzo.authdroid.core.SocialAuthUser
import eu.balzo.authdroid.profile.ProfileActivity

fun Auth.openProfile(context: Context): Unit =
        ProfileActivity.start(context, this.socialAuthUser)

fun SocialAuthUser.openProfile(context: Context): Unit =
        ProfileActivity.start(context, this)

fun AuthError.logError(context: Context): Unit =

        when (this) {

            is AuthError.Cancelled -> "Cancelled"
            AuthError.FirebaseUnknown -> "Firebase Unknown Error"
            AuthError.FirebaseUserNotLogged -> "Firebase User Not Logged"
            AuthError.GoogleAuth -> "GoogleAuth Auth Error"
            AuthError.FacebookAuth -> "Facebook Auth Error"
            is AuthError.Unknown ->
                source.flatMap { it.message.toOption() }.getOrElse { "Unknown Error" }

        }.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }

fun FragmentActivity.showToast(message: String): Unit =
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()