package com.giacomoparisi.arrow.social.auth.core

import arrow.Kind

abstract class SocialAuthenticator<F> {

    abstract fun signIn(): Kind<F, AuthResult>
}