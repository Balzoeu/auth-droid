package com.giacomoparisi.arrow.social.auth.core.firebase

import arrow.Kind
import arrow.effects.typeclasses.Async
import com.giacomoparisi.arrow.social.auth.core.AuthResult


fun <F> signInWithFirebaseEmailPassword(
        async: Async<F>,
        email: String,
        password: String
): Kind<F, AuthResult> =
        async.async {
            firebaseAuth().signInWithEmailAndPassword(email, password).bindToAuthListener(it)
        }

fun <F> signUpWithFirebaseEmailPassword(
        async: Async<F>,
        email: String,
        password: String
): Kind<F, AuthResult> =
        async.async {
            firebaseAuth().createUserWithEmailAndPassword(email, password)
                    .bindToAuthListener(it)
        }
