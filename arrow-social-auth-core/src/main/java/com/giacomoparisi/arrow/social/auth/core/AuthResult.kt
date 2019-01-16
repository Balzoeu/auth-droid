package com.giacomoparisi.arrow.social.auth.core


sealed class AuthError(message: String) : Throwable(message)

object UnknownFirebaseError : AuthError("Unknown firebase error")

class FirebaseTaskError(message: String) : AuthError(message)

