package com.giacomoparisi.arrow.social.auth.core.firebase

import arrow.Kind
import arrow.core.toOption
import arrow.effects.typeclasses.Async
import com.giacomoparisi.arrow.social.auth.core.AuthResult
import com.giacomoparisi.arrow.social.auth.core.SocialAuthUser
import com.giacomoparisi.arrow.social.auth.core.toSocialAuthUser


fun <F> signInWithFirebaseEmailPassword(
        async: Async<F>,
        email: String,
        password: String
): Kind<F, AuthResult<SocialAuthUser>> =
        async.async {
            firebaseAuth().signInWithEmailAndPassword(email, password).bindTask(it) {
                firebaseAuth()
                        .currentUser
                        .toOption()
                        .fold({ AuthResult.Failed(Throwable("Unknown error during auth")) })
                        { firebaseUser -> AuthResult.Completed(firebaseUser.toSocialAuthUser()) }
            }
        }

fun <F> signUpWithFirebaseEmailPassword(
        async: Async<F>,
        email: String,
        password: String
): Kind<F, AuthResult<SocialAuthUser>> =
        async.async {
            firebaseAuth().createUserWithEmailAndPassword(email, password)
                    .bindTask(it) {
                        firebaseAuth()
                                .currentUser
                                .toOption()
                                .fold({ AuthResult.Failed(Throwable("Unknown error during auth")) })
                                { firebaseUser -> AuthResult.Completed(firebaseUser.toSocialAuthUser()) }
                    }
        }
