package com.giacomoparisi.authdroid

import android.content.Context
import android.widget.Toast
import com.giacomoparisi.authdroid.core.Auth
import com.giacomoparisi.authdroid.core.AuthError
import com.giacomoparisi.authdroid.profile.ProfileActivity

fun Auth.openProfile(context: Context) {
    ProfileActivity.start(context, this.socialAuthUser)
}

fun Throwable.logError(context: Context) {

    when (this) {
        is AuthError.Cancelled -> "Cancelled"
        else -> this.message
    }.let {
        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
    }
}