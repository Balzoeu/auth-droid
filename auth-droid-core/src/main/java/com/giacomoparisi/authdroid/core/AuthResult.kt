package com.giacomoparisi.authdroid.core

sealed class AuthError(message: String) : Throwable(message) {

    object Cancelled: AuthError("Cancelled")

    object UnknownFirebaseError : AuthError("Unknown firebase error")

    class FirebaseTaskError(message: String) : AuthError(message)

    object GoogleAuthError: AuthError("Google auth error")

    object FacebookAuthError: AuthError("Facebook auth error")
}

data class Auth(val isFirstAuth: Boolean?, val socialAuthUser: SocialAuthUser)

