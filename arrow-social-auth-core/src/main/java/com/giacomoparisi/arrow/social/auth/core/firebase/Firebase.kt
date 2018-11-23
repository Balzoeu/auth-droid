package com.giacomoparisi.arrow.social.auth.core.firebase

import android.net.Uri
import arrow.Kind
import arrow.core.*
import arrow.effects.typeclasses.Async
import arrow.syntax.function.pipe
import com.giacomoparisi.arrow.social.auth.core.AuthResult
import com.giacomoparisi.arrow.social.auth.core.SocialAuthUser
import com.giacomoparisi.arrow.social.auth.core.toSocialAuthUser
import com.giacomoparisi.kotlin.functional.extensions.arrow.option.ifNone
import com.giacomoparisi.kotlin.functional.extensions.arrow.option.ifSome
import com.giacomoparisi.kotlin.functional.extensions.core.ifFalse
import com.giacomoparisi.kotlin.functional.extensions.core.ifTrue
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.auth.UserProfileChangeRequest


fun <F> getFirebaseToken(async: Async<F>): Kind<F, AuthResult<String>> =
        async.async { function ->
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            firebaseUser.toOption()
                    .ifNone { function(AuthResult.Failed<String>(Throwable("User non logged with firebase")).right()) }
                    .ifSome {
                        it.getIdToken(true).bindToTokenListener(function)
                    }
        }

fun getFirebaseId(): Option<String> =
        firebaseAuth().currentUser.toOption().map { it.uid }

fun <F> updateFirebaseProfile(async: Async<F>, displayName: String? = null, photoUrl: String? = null): Kind<F, AuthResult<Unit>> =
        async.async { function ->
            FirebaseAuth.getInstance()
                    .currentUser
                    .toOption()
                    .ifNone { function(AuthResult.Failed<Unit>(Throwable("User non logged with firebase")).right()) }
                    .ifSome { firebaseUser ->
                        UserProfileChangeRequest.Builder()
                                .also { builder -> displayName.toOption().ifSome { builder.setDisplayName(it) } }
                                .also { builder -> photoUrl.toOption().ifSome { builder.setPhotoUri(Uri.parse(it)) } }
                                .build()
                                .pipe {
                                    firebaseUser.updateProfile(it)
                                            .addOnCanceledListener { function(AuthResult.Cancelled<Unit>().right()) }
                                            .addOnFailureListener { exception -> function(AuthResult.Failed<Unit>(exception).right()) }
                                            .addOnCompleteListener { task ->
                                                task.isSuccessful
                                                        .ifTrue { function(AuthResult.Completed(Unit).right()) }
                                                        .ifFalse {
                                                            function(AuthResult.Failed<Unit>(
                                                                    task.exception
                                                                            .toOption()
                                                                            .getOrElse { Exception("Unknown error during profile update") }
                                                            ).right())
                                                        }
                                            }
                                }
                    }
        }

fun <F> updateFirebasePassword(async: Async<F>, password: String): Kind<F, AuthResult<Unit>> =
        async.async { function ->
            FirebaseAuth.getInstance()
                    .currentUser
                    .toOption()
                    .ifNone { AuthResult.Failed<Unit>(Throwable("User non logged with firebase")).right() }
                    .ifSome { firebaseUser ->
                        firebaseUser.updatePassword(password)
                                .addOnCanceledListener { function(AuthResult.Cancelled<Unit>().right()) }
                                .addOnFailureListener { exception -> function(AuthResult.Failed<Unit>(exception).right()) }
                                .addOnCompleteListener { task ->
                                    task.isSuccessful
                                            .ifTrue { function(AuthResult.Completed(Unit).right()) }
                                            .ifFalse {
                                                function(AuthResult.Failed<Unit>(
                                                        task.exception
                                                                .toOption()
                                                                .getOrElse { Exception("Unknown error during password update") }
                                                ).right())
                                            }
                                }
                    }
        }

fun <F> resetFirebasePassword(async: Async<F>, email: String): Kind<F, AuthResult<Unit>> =
        async.async { function ->
            FirebaseAuth.getInstance()
                    .also { it.useAppLanguage() }
                    .sendPasswordResetEmail(email)
                    .addOnCanceledListener { function(AuthResult.Cancelled<Unit>().right()) }
                    .addOnFailureListener { exception -> function(AuthResult.Failed<Unit>(exception).right()) }
                    .addOnCompleteListener { task ->
                        task.isSuccessful
                                .ifTrue { function(AuthResult.Completed(Unit).right()) }
                                .ifFalse {
                                    function(AuthResult.Failed<Unit>(
                                            task.exception
                                                    .toOption()
                                                    .getOrElse { Exception("Unknown error during password reset") }
                                    ).right())
                                }
                    }
        }


internal fun firebaseCredentialSignIn(
        credential: AuthCredential,
        function: (Either<Throwable, AuthResult<SocialAuthUser>>) -> Unit) {
    firebaseAuth().signInWithCredential(credential).bindToAuthListener(function)
}

internal fun Task<com.google.firebase.auth.AuthResult>.bindToAuthListener(
        function: (Either<Throwable, AuthResult<SocialAuthUser>>) -> Unit) {
    this.addOnCompleteListener { task ->
        task.isSuccessful
                .ifTrue {
                    // firebase auth task completed
                    firebaseAuth().currentUser.toOption()
                            .ifSome { function(AuthResult.Completed(it.toSocialAuthUser()).right()) }
                            .ifNone { function(AuthResult.Failed<SocialAuthUser>(Throwable("Unknown error during auth")).right()) }
                }
                .ifFalse {
                    // firebase auth task completed with an error
                    function(AuthResult.Failed<SocialAuthUser>(task.exception
                            .toOption()
                            .getOrElse { Throwable("Unknown error during auth") }
                    ).right())
                }
    }.addOnCanceledListener { function(AuthResult.Cancelled<SocialAuthUser>().right()) }
            .addOnFailureListener { function(AuthResult.Failed<SocialAuthUser>(it).right()) }
}

internal fun Task<GetTokenResult>.bindToTokenListener(
        function: (Either<Throwable, AuthResult<String>>) -> Unit) {
    this.addOnCompleteListener { task ->
        task.isSuccessful
                .ifTrue {
                    // firebase auth task completed
                    task.result?.token.toOption()
                            .ifSome { function(AuthResult.Completed(it).right()) }
                            .ifNone { function(AuthResult.Failed<String>(Throwable("Unknown error")).right()) }
                }
                .ifFalse {
                    // firebase auth task completed with an error
                    function(AuthResult.Failed<String>(task.exception
                            .toOption()
                            .getOrElse { Throwable("Unknown error") }
                    ).right())
                }
    }.addOnCanceledListener { function(AuthResult.Cancelled<String>().right()) }
            .addOnFailureListener { function(AuthResult.Failed<String>(it).right()) }
}

internal fun firebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()