package eu.balzo.authdroid.core

sealed class AuthError(message: String) : Throwable(message) {

    object Cancelled: AuthError("Cancelled")

    object UnknownFirebaseError : AuthError("Unknown firebase error")

    object FirebaseUserNotLogged : AuthError("Firebase user not logged")

    class FirebaseTaskError(message: String) : AuthError(message)

    object GoogleAuthError: AuthError("Google auth error")

    object FacebookAuthError: AuthError("Facebook auth error")
}

data class Auth(val isFirstAuth: Boolean?, val socialAuthUser: SocialAuthUser)

