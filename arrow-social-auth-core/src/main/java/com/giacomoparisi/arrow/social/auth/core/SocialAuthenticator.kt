package com.giacomoparisi.arrow.social.auth.core

import androidx.fragment.app.FragmentActivity
import arrow.Kind
import arrow.effects.typeclasses.Async

abstract class SocialAuthenticator<F>(
        protected val async: Async<F>,
        protected val activity: FragmentActivity
) {

    abstract fun signIn(): Kind<F, AuthResult>
}