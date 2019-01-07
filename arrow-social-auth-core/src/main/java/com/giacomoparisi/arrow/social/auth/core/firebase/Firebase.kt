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
import com.google.firebase.auth.UserProfileChangeRequest


fun <F> getFirebaseToken(async: Async<F>): Kind<F, AuthResult<String>> =
        async.async { function ->
            FirebaseAuth.getInstance()
                    .currentUser
                    .toOption()
                    .ifNone { function(AuthResult.Failed<String>(Throwable("User non logged with firebase")).right()) }
                    .ifSome { user ->
                        user.getIdToken(true).bindTask(function) { task ->
                            task.result?.token.toOption()
                                    .fold({ AuthResult.Failed(Throwable("Unknown error")) })
                                    { AuthResult.Completed(it) }
                        }
                    }
        }

fun getFirebaseId(): Option<String> =
        firebaseAuth().currentUser.toOption().map { it.uid }

fun getCurrentFirebaseUser() =
        firebaseAuth().currentUser.toOption().map { it.toSocialAuthUser() }

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
                                            .bindTask(function) { AuthResult.Completed(Unit) }
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
                                .bindTask(function) { AuthResult.Completed(Unit) }
                    }
        }

fun <F> resetFirebasePassword(async: Async<F>, email: String): Kind<F, AuthResult<Unit>> =
        async.async { function ->
            FirebaseAuth.getInstance()
                    .also { it.useAppLanguage() }
                    .sendPasswordResetEmail(email)
                    .bindTask(function) { AuthResult.Completed(Unit) }
        }

fun firebaseSignOut() {
    FirebaseAuth.getInstance().signOut()
}

internal fun firebaseCredentialSignIn(
        credential: AuthCredential,
        function: (Either<Throwable, AuthResult<SocialAuthUser>>) -> Unit) {
    firebaseAuth().signInWithCredential(credential).bindTask(function) {
        firebaseAuth()
                .currentUser
                .toOption()
                .fold({ AuthResult.Failed(Throwable("Unknown error during auth")) })
                { firebaseUser -> AuthResult.Completed(firebaseUser.toSocialAuthUser()) }
    }
}

internal fun <F, T> Task<F>.bindTask(
        function: (Either<Throwable, AuthResult<T>>) -> Unit,
        map: (Task<F>) -> AuthResult<T>
) {
    this.addOnCompleteListener { task ->
        task.isSuccessful
                .ifTrue { function(map(task).right()) }
                .ifFalse {
                    // firebase task completed with an error
                    function(
                            AuthResult.Failed<T>(
                                    task.exception
                                            .toOption()
                                            .getOrElse { Throwable("Unknown error") }
                            ).right()
                    )
                }
    }.addOnCanceledListener { function(AuthResult.Cancelled<T>().right()) }
            .addOnFailureListener { function(AuthResult.Failed<T>(it).right()) }
}

internal fun firebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()