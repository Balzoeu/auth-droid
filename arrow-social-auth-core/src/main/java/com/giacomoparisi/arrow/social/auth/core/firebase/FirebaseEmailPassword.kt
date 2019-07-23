package com.giacomoparisi.arrow.social.auth.core.firebase

import arrow.core.Option
import arrow.core.toOption
import com.giacomoparisi.arrow.social.auth.core.Auth
import com.giacomoparisi.arrow.social.auth.core.toSocialAuthUser
import io.reactivex.Single


fun signInWithFirebaseEmailPassword(
        email: String,
        password: String
): Single<Option<Auth>> =
        Single.create {
            firebaseAuth().signInWithEmailAndPassword(email, password).bindTask(it) {
                firebaseAuth()
                        .currentUser
                        .toOption()
                        .map { firebaseUser ->
                            Auth(
                                    it.additionalUserInfo.toOption().fold({ false }) { userInfo -> userInfo.isNewUser }.toOption(),
                                    firebaseUser.toSocialAuthUser())
                        }
            }
        }

fun signUpWithFirebaseEmailPassword(
        email: String,
        password: String
): Single<Option<Auth>> =
        Single.create {
            firebaseAuth().createUserWithEmailAndPassword(email, password)
                    .bindTask(it) {
                        firebaseAuth()
                                .currentUser
                                .toOption()
                                .map { firebaseUser ->
                                    Auth(
                                            it.additionalUserInfo.toOption().fold({ false }) { userInfo -> userInfo.isNewUser }.toOption(),
                                            firebaseUser.toSocialAuthUser())
                                }
                    }
        }
