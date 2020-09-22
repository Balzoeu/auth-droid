package eu.balzo.authdroid

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import arrow.fx.coroutines.evalOn
import eu.balzo.authdroid.core.Auth
import eu.balzo.authdroid.core.AuthError
import eu.balzo.authdroid.core.SocialAuthUser
import eu.balzo.authdroid.profile.ProfileActivity
import kotlinx.coroutines.Dispatchers

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
                source?.message ?: "Unknown Error"

        }.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }

suspend fun FragmentActivity.showToast(message: String): Unit =
        evalOn(Dispatchers.Main) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }