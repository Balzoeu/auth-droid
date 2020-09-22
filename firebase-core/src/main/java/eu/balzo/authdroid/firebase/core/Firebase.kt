package eu.balzo.authdroid.firebase.core

import android.net.Uri
import arrow.core.*
import arrow.core.computations.either
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import eu.balzo.authdroid.core.Auth
import eu.balzo.authdroid.core.AuthError
import eu.balzo.authdroid.core.AuthError.Companion.toAuthError
import eu.balzo.authdroid.core.SocialAuthUser
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object Firebase {

    suspend fun token(): Either<AuthError, String> =
            when (val user = FirebaseAuth.getInstance().currentUser) {
                null -> AuthError.FirebaseUserNotLogged.left()
                else ->
                    user.getIdToken(true)
                            .bindTask()
                            .flatMap {
                                it.token.toOption().toEither { AuthError.FirebaseUnknown }
                            }
            }

    fun id(): String? = auth().currentUser?.uid

    suspend fun currentUser(): Either<AuthError, SocialAuthUser> =

            token().flatMap {

                auth().currentUser
                        ?.toSocialAuthUser(it)
                        ?.right() ?: AuthError.FirebaseUserNotLogged.left()

            }


    private fun currentFirebaseUser(): Either<AuthError.FirebaseUserNotLogged, FirebaseUser> =
            auth().currentUser?.right() ?: AuthError.FirebaseUserNotLogged.left()

    suspend fun updateProfile(
            displayName: String? = null,
            photoUrl: String? = null
    ): Either<AuthError, Unit> =
            currentFirebaseUser()
                    .map { firebaseUser ->

                        val request = UserProfileChangeRequest.Builder()

                        displayName?.let { request.setDisplayName(it) }
                        photoUrl?.let { request.setPhotoUri(Uri.parse(it)) }

                        firebaseUser.updateProfile(request.build()).bindTask()
                    }

    suspend fun updatePassword(
            password: String
    ): Either<AuthError, Unit> =
            currentFirebaseUser()
                    .map { it.updatePassword(password).bindTask() }

    suspend fun resetPassword(email: String): Either<AuthError, Unit> =
            auth().also { it.useAppLanguage() }
                    .sendPasswordResetEmail(email)
                    .bindTask()
                    .map { Unit }

    fun signOut(): Unit = auth().signOut()

    suspend fun signInWithFirebaseEmailPassword(
            email: String,
            password: String
    ): Either<AuthError, Auth> {

        val auth =
                auth().signInWithEmailAndPassword(email, password).bindTask()

        val user = currentUser()

        return either.invoke {
            Auth(auth.bind().additionalUserInfo?.isNewUser, user.bind())
        }

    }

    suspend fun signUpWithFirebaseEmailPassword(
            email: String,
            password: String
    ): Either<AuthError, Auth> {

        val auth =
                auth().createUserWithEmailAndPassword(email, password).bindTask()

        val user = currentUser()

        return either.invoke {
            Auth(auth.bind().additionalUserInfo?.isNewUser, user.bind())
        }

    }

    suspend fun signInWithCredential(
            credential: AuthCredential
    ): Either<AuthError, AuthResult> =
            auth().signInWithCredential(credential).bindTask()


    suspend fun <T> Task<T>.bindTask(): Either<AuthError, T> =
            suspendCoroutine { continuation ->
                addOnSuccessListener { continuation.resume(it.right()) }
                        .addOnCanceledListener { continuation.resume(AuthError.Cancelled.left()) }
                        .addOnFailureListener { continuation.resume(it.toAuthError().left()) }
            }

    fun auth(): FirebaseAuth = FirebaseAuth.getInstance()
}