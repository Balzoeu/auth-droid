package com.giacomoparisi.authdroid.rx.firebase

import com.giacomoparisi.authdroid.core.Auth
import com.giacomoparisi.authdroid.core.AuthError
import io.reactivex.Single


fun signInWithFirebaseEmailPassword(
        email: String,
        password: String
): Single<Auth> =
        Single.create { emitter ->
            firebaseAuth().signInWithEmailAndPassword(email, password)
                    .bindTask(emitter) {
                        when (val user = firebaseAuth().currentUser) {
                            null -> emitter.onError(AuthError.UnknownFirebaseError)
                            else -> Auth(it.additionalUserInfo?.isNewUser, user.toSocialAuthUser())
                        }
                    }
        }

fun signUpWithFirebaseEmailPassword(
        email: String,
        password: String
): Single<Auth> =
        Single.create { emitter ->
            firebaseAuth().createUserWithEmailAndPassword(email, password)
                    .bindTask(emitter) {
                        when (val user = firebaseAuth().currentUser) {
                            null -> emitter.onError(AuthError.UnknownFirebaseError)
                            else -> Auth(it.additionalUserInfo?.isNewUser, user.toSocialAuthUser())
                        }
                    }
        }
