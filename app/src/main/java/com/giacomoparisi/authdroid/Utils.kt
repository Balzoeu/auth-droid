package com.giacomoparisi.authdroid

import android.content.Context
import android.widget.Toast
import com.giacomoparisi.authdroid.core.Auth
import com.giacomoparisi.authdroid.core.AuthError

fun Auth.log(context: Context) {
    Toast.makeText(context, this.socialAuthUser.toString(), Toast.LENGTH_LONG).show()
}

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