package com.giacomoparisi.arrow.social.auth.core.firebase

import arrow.Kind
import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.right
import arrow.core.toOption
import arrow.effects.typeclasses.Async
import com.giacomoparisi.arrow.social.auth.core.*
import com.giacomoparisi.kotlin.functional.extensions.arrow.option.ifNone
import com.giacomoparisi.kotlin.functional.extensions.arrow.option.ifSome
import com.giacomoparisi.kotlin.functional.extensions.core.ifFalse
import com.giacomoparisi.kotlin.functional.extensions.core.ifTrue
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult

fun <F> getFirebaseToken(async: Async<F>): Kind<F, FirebaseTokenResult> =
        async.async { function ->
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            firebaseUser.toOption()
                    .ifNone { function(FirebaseTokenRequestFailed(Throwable("User non logged with firebase")).right()) }
                    .ifSome {
                        it.getIdToken(true).bindToTokenListener(function)
                    }
        }

internal fun firebaseCredentialSignIn(
        credential: AuthCredential,
        function: (Either<Throwable, AuthResult>) -> Unit) {
    firebaseAuth().signInWithCredential(credential).bindToAuthListener(function)
}

internal fun Task<com.google.firebase.auth.AuthResult>.bindToAuthListener(
        function: (Either<Throwable, AuthResult>) -> Unit) {
    this.addOnCompleteListener { task ->
        task.isSuccessful
                .ifTrue {
                    // firebase auth task completed
                    firebaseAuth().currentUser.toOption()
                            .ifSome { function(AuthCompleted(it.toSocialAuthUser()).right()) }
                            .ifNone { function(AuthFailed(Throwable("Unknown error during auth")).right()) }
                }
                .ifFalse {
                    // firebase auth task completed with an error
                    function(AuthFailed(task.exception
                            .toOption()
                            .getOrElse { Throwable("Unknown error during auth") }
                    ).right())
                }
    }.addOnCanceledListener { function(AuthCancelled.right()) }
            .addOnFailureListener { function(AuthFailed(it).right()) }
}

internal fun Task<GetTokenResult>.bindToTokenListener(
        function: (Either<Throwable, FirebaseTokenResult>) -> Unit) {
    this.addOnCompleteListener { task ->
        task.isSuccessful
                .ifTrue {
                    // firebase auth task completed
                    task.result?.token.toOption()
                            .ifSome { function(FirebaseTokenRequestCompleted(it).right()) }
                            .ifNone { function(FirebaseTokenRequestFailed(Throwable("Unknown error")).right()) }
                }
                .ifFalse {
                    // firebase auth task completed with an error
                    function(FirebaseTokenRequestFailed(task.exception
                            .toOption()
                            .getOrElse { Throwable("Unknown error") }
                    ).right())
                }
    }.addOnCanceledListener { function(FirebaseTokenRequestCancelled.right()) }
            .addOnFailureListener { function(FirebaseTokenRequestFailed(it).right()) }
}

internal fun firebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()