package com.giacomoparisi.arrow.social.auth.core.firebase

import arrow.core.Option
import arrow.core.toOption
import com.giacomoparisi.arrow.social.auth.core.SocialAuthUser
import com.giacomoparisi.arrow.social.auth.core.toSocialAuthUser
import io.reactivex.Single


fun signInWithFirebaseEmailPassword(
        email: String,
        password: String
): Single<Option<SocialAuthUser>> =
        Single.create {
            firebaseAuth().signInWithEmailAndPassword(email, password).bindTask(it) {
                firebaseAuth()
                        .currentUser
                        .toOption()
                        .map { firebaseUser -> firebaseUser.toSocialAuthUser() }
            }
        }

fun signUpWithFirebaseEmailPassword(
        email: String,
        password: String
): Single<Option<SocialAuthUser>> =
        Single.create {
            firebaseAuth().createUserWithEmailAndPassword(email, password)
                    .bindTask(it) {
                        firebaseAuth()
                                .currentUser
                                .toOption()
                                .map { firebaseUser -> firebaseUser.toSocialAuthUser() }
                    }
        }
