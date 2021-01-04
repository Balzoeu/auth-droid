package eu.balzo.authdroid.core

sealed class AuthError : Throwable() {

    class Cancelled : AuthError()

    class FirebaseUnknown : AuthError()

    class FirebaseUserNotLogged : AuthError()

    class GoogleAuth : AuthError()

    class FacebookAuth : AuthError()

    class Unknown : AuthError()

}

data class Auth(val isFirstAuth: Boolean?, val socialAuthUser: SocialAuthUser)

