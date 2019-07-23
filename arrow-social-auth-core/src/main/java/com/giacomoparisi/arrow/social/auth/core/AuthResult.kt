package com.giacomoparisi.arrow.social.auth.core

import arrow.core.Option


sealed class AuthError(message: String) : Throwable(message)

object UnknownFirebaseError : AuthError("Unknown firebase error")

class FirebaseTaskError(message: String) : AuthError(message)

data class Auth(val isFirstAuth: Option<Boolean>, val socialAuthUser: SocialAuthUser)

