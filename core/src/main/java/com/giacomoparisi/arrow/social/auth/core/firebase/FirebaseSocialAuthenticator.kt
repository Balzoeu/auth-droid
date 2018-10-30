package com.giacomoparisi.arrow.social.auth.core.firebase

import com.giacomoparisi.arrow.social.auth.core.SocialAuthenticator
import com.google.firebase.auth.FirebaseAuth

abstract class FirebaseSocialAuthenticator<F> : SocialAuthenticator<F>() {

    protected val auth: FirebaseAuth = FirebaseAuth.getInstance()
}
