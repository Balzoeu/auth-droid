package eu.balzo.authdroid.firebase.core

import android.net.Uri
import arrow.Kind
import arrow.core.*
import arrow.core.extensions.fx
import arrow.fx.typeclasses.ConcurrentFx
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import eu.balzo.authdroid.core.Auth
import eu.balzo.authdroid.core.AuthError
import eu.balzo.authdroid.core.AuthError.Companion.toAuthError
import eu.balzo.authdroid.core.SocialAuthUser

object Firebase {

    fun <F> token(fx: ConcurrentFx<F>): Kind<F, Either<AuthError, String>> =
            fx.concurrent {
                when (val user = FirebaseAuth.getInstance().currentUser) {
                    null -> AuthError.FirebaseUserNotLogged.left()
                    else ->
                        user.getIdToken(true)
                                .bindTask(fx)
                                .bind()
                                .flatMap {
                                    it.token.toOption().toEither { AuthError.FirebaseUnknown }
                                }
                }
            }

    fun id(): Option<String> = auth().currentUser?.uid.toOption()

    fun <F> currentUser(fx: ConcurrentFx<F>): Kind<F, Either<AuthError, SocialAuthUser>> =
            fx.concurrent {

                token(fx)
                        .bind()
                        .flatMap {

                            auth().currentUser
                                    ?.toSocialAuthUser(it)
                                    .toOption()
                                    .toEither { AuthError.FirebaseUserNotLogged }

                        }
            }

    private fun currentFirebaseUser(): Either<AuthError.FirebaseUserNotLogged, FirebaseUser> =
            auth().currentUser.toOption().toEither { AuthError.FirebaseUserNotLogged }

    fun <F> updateProfile(
            fx: ConcurrentFx<F>,
            displayName: String? = null,
            photoUrl: String? = null
    ): Kind<F, Either<AuthError, Unit>> =
            fx.concurrent {
                !currentFirebaseUser()
                        .fold(
                                { just(it.left()) },
                                { user ->
                                    val request = UserProfileChangeRequest.Builder()

                                    displayName.toOption().map { request.setDisplayName(it) }
                                    photoUrl.toOption().map { request.setPhotoUri(Uri.parse(it)) }

                                    user.updateProfile(request.build()).bindTask(fx)

                                }
                        )
                        .map { it.map { Unit } }

            }

    fun <F> updatePassword(
            fx: ConcurrentFx<F>,
            password: String
    ): Kind<F, Either<AuthError, Unit>> =
            fx.concurrent {
                !currentFirebaseUser()
                        .fold(
                                { just(it.left()) },
                                { it.updatePassword(password).bindTask(fx) }
                        )
                        .map { it.map { Unit } }

            }

    fun <F> resetPassword(fx: ConcurrentFx<F>, email: String): Kind<F, Either<AuthError, Unit>> =
            fx.concurrent {

                !auth().also { firebaseAuth -> firebaseAuth.useAppLanguage() }
                        .sendPasswordResetEmail(email)
                        .bindTask(fx)
                        .map { it.map { Unit } }
            }

    fun signOut(): Unit = auth().signOut()

    fun <F> signInWithFirebaseEmailPassword(
            fx: ConcurrentFx<F>,
            email: String,
            password: String
    ): Kind<F, Either<AuthError, Auth>> =
            fx.concurrent {

                val auth =
                        !auth().signInWithEmailAndPassword(email, password).bindTask(fx)

                val user = !currentUser(fx)

                Either.fx {
                    Auth(auth.bind().additionalUserInfo?.isNewUser.toOption(), user.bind())
                }

            }

    fun <F> signUpWithFirebaseEmailPassword(
            fx: ConcurrentFx<F>,
            email: String,
            password: String
    ): Kind<F, Either<AuthError, Auth>> =
            fx.concurrent {

                val auth =
                        !auth().createUserWithEmailAndPassword(email, password).bindTask(fx)

                val user = !currentUser(fx)

                Either.fx {
                    Auth(auth.bind().additionalUserInfo?.isNewUser.toOption(), user.bind())
                }

            }

    fun <F> signInWithCredential(
            fx: ConcurrentFx<F>,
            credential: AuthCredential
    ): Kind<F, Either<AuthError, AuthResult>> =
            auth().signInWithCredential(credential).bindTask(fx)


    fun <F, T> Task<T>.bindTask(fx: ConcurrentFx<F>): Kind<F, Either<AuthError, T>> =
            fx.M.async { callback ->
                addOnSuccessListener { callback(it.right().right()) }
                        .addOnCanceledListener { callback(AuthError.Cancelled.left().right()) }
                        .addOnFailureListener { callback(it.toAuthError().left().right()) }
            }

    fun auth(): FirebaseAuth = FirebaseAuth.getInstance()
}