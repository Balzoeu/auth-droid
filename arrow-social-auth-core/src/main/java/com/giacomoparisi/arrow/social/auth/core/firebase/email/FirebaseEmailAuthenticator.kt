package com.giacomoparisi.arrow.social.auth.core.firebase.email

import androidx.fragment.app.FragmentActivity
import arrow.Kind
import arrow.effects.typeclasses.Async
import com.giacomoparisi.arrow.social.auth.core.AuthResult
import com.giacomoparisi.arrow.social.auth.core.firebase.FirebaseSocialAuthenticator

class FirebaseEmailAuthenticator<F>(
        private val _email: String,
        private val _password: String,
        async: Async<F>,
        activity: FragmentActivity
) : FirebaseSocialAuthenticator<F>(async, activity) {

    override fun signIn(): Kind<F, AuthResult> =
            this.async.async {
                this.auth.signInWithEmailAndPassword(this._email, this._password)
                        .bindToListener(it)
            }

    fun signUp(): Kind<F, AuthResult> =
            this.async.async {
                this.auth.createUserWithEmailAndPassword(this._email, this._password)
                        .bindToListener(it)
            }
}