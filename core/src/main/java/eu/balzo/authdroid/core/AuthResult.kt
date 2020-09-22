package eu.balzo.authdroid.core

sealed class AuthError(val source: Throwable? = null) {

    object Cancelled : AuthError()

    object FirebaseUnknown : AuthError()

    object FirebaseUserNotLogged : AuthError()

    object GoogleAuth : AuthError()

    object FacebookAuth : AuthError()

    class Unknown(source: Throwable?) : AuthError(source)

    companion object {

        fun Throwable.toAuthError(): AuthError = Unknown(this)

    }
}

data class Auth(val isFirstAuth: Boolean?, val socialAuthUser: SocialAuthUser)

