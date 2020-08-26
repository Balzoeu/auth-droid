package eu.balzo.authdroid.core

import arrow.core.None
import arrow.core.Option
import arrow.core.some

sealed class AuthError(val source: Option<Throwable> = None) {

    object Cancelled : AuthError()

    object FirebaseUnknown : AuthError()

    object FirebaseUserNotLogged : AuthError()

    object GoogleAuth : AuthError()

    object FacebookAuth : AuthError()

    class Unknown(source: Option<Throwable>) : AuthError(source)

    companion object {

        fun Throwable.toAuthError(): AuthError = Unknown(some())

    }
}

data class Auth(val isFirstAuth: Option<Boolean>, val socialAuthUser: SocialAuthUser)

