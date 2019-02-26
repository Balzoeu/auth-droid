package com.giacomoparisi.arrow.social.auth.core.firebase

import android.net.Uri
import arrow.core.None
import arrow.core.Option
import arrow.core.some
import arrow.core.toOption
import arrow.syntax.function.pipe
import com.giacomoparisi.arrow.social.auth.core.Auth
import com.giacomoparisi.arrow.social.auth.core.UnknownFirebaseError
import com.giacomoparisi.arrow.social.auth.core.toSocialAuthUser
import com.giacomoparisi.kotlin.functional.extensions.arrow.option.ifNone
import com.giacomoparisi.kotlin.functional.extensions.arrow.option.ifSome
import com.giacomoparisi.kotlin.functional.extensions.core.ifFalse
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import io.reactivex.Single
import io.reactivex.SingleEmitter


fun getFirebaseToken(): Single<Option<String>> =
        Single.create {
            FirebaseAuth.getInstance()
                    .currentUser
                    .toOption()
                    .ifNone { it.onError(UnknownFirebaseError) }
                    .ifSome { user ->
                        user.getIdToken(true).bindTask(it) { result -> result.token.toOption() }
                    }
        }

fun getFirebaseId(): Option<String> =
        firebaseAuth().currentUser.toOption().map { it.uid }

fun getCurrentFirebaseUser() =
        firebaseAuth().currentUser.toOption().map { it.toSocialAuthUser() }

fun updateFirebaseProfile(displayName: String? = null, photoUrl: String? = null): Single<Option<Unit>> =
        Single.create {
            FirebaseAuth.getInstance()
                    .currentUser
                    .toOption()
                    .ifNone { it.onError(UnknownFirebaseError) }
                    .ifSome { firebaseUser ->
                        UserProfileChangeRequest.Builder()
                                .also { builder -> displayName.toOption().ifSome { name -> builder.setDisplayName(name) } }
                                .also { builder -> photoUrl.toOption().ifSome { url -> builder.setPhotoUri(Uri.parse(url)) } }
                                .build()
                                .pipe { request ->
                                    firebaseUser.updateProfile(request)
                                            .bindTask(it) { Unit.some() }
                                }
                    }
        }

fun updateFirebasePassword(password: String): Single<Option<Unit>> =
        Single.create {
            FirebaseAuth.getInstance()
                    .currentUser
                    .toOption()
                    .ifNone { it.onError(UnknownFirebaseError) }
                    .ifSome { firebaseUser ->
                        firebaseUser.updatePassword(password)
                                .bindTask(it) { Unit.some() }
                    }
        }

fun resetFirebasePassword(email: String): Single<Option<Unit>> =
        Single.create {
            FirebaseAuth.getInstance()
                    .also { firebaseAuth -> firebaseAuth.useAppLanguage() }
                    .sendPasswordResetEmail(email)
                    .bindTask(it) { Unit.some() }
        }

fun firebaseSignOut() {
    FirebaseAuth.getInstance().signOut()
}

internal fun firebaseCredentialSignIn(
        credential: AuthCredential,
        emitter: SingleEmitter<Option<Auth>>) {
    firebaseAuth().signInWithCredential(credential).bindTask(emitter) {
        firebaseAuth()
                .currentUser
                .toOption()
                .map { firebaseUser ->
                    Auth(
                            it.additionalUserInfo.toOption().fold({ false }) { userInfo -> userInfo.isNewUser },
                            firebaseUser.toSocialAuthUser())
                }
    }
}

internal fun <F, T> Task<F>.bindTask(
        emitter: SingleEmitter<Option<T>>,
        map: (F) -> Option<T>
) {
    this.addOnSuccessListener { emitter.onSuccess(map(it)) }
            .addOnCanceledListener { emitter.onSuccess(None) }
            .addOnFailureListener { emitter.isDisposed.ifFalse { emitter.onError(it) } }
}

internal fun firebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()