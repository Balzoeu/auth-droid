package com.giacomoparisi.arrow.social.auth.core.firebase

import androidx.fragment.app.FragmentActivity
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
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth

abstract class FirebaseSocialAuthenticator<F>(
        async: Async<F>,
        activity: FragmentActivity
) : SocialAuthenticator<F>(async, activity) {

    protected val auth: FirebaseAuth = FirebaseAuth.getInstance()

    protected fun firebaseSignIn(
            credential: AuthCredential,
            function: (Either<Throwable, AuthResult>) -> Unit) {
        this.auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    task.isSuccessful
                            .ifTrue {
                                // firebase auth task completed
                                auth.currentUser.toOption()
                                        .ifSome { function(Completed(it.toSocialAuthUser()).right()) }
                                        .ifNone { function(Failed(Throwable("Unknown error during auth")).right()) }
                            }
                            .ifFalse {
                                // firebase auth task completed with an error
                                function(Failed(task.exception
                                        .toOption()
                                        .getOrElse { Throwable("Unknown error during auth") }
                                ).right())
                            }
                }
                .addOnCanceledListener { function(Cancelled.right()) }
                .addOnFailureListener { function(Failed(it).right()) }
    }
}
