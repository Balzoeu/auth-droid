package eu.balzo.authdroid.firebase.core

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import eu.balzo.authdroid.core.Auth
import eu.balzo.authdroid.core.AuthError
import eu.balzo.authdroid.core.SocialAuthUser
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object Firebase {

    suspend fun token(): String =
            when (val user = FirebaseAuth.getInstance().currentUser) {
                null -> throw AuthError.FirebaseUserNotLogged()
                else ->
                    user.getIdToken(true)
                            .bindTask()
                            .token ?: throw AuthError.FirebaseUnknown()
            }

    fun id(): String? = auth().currentUser?.uid

    suspend fun currentUser(): SocialAuthUser =

            auth().currentUser?.toSocialAuthUser(token())
                    ?: throw  AuthError.FirebaseUserNotLogged()


    private fun currentFirebaseUser(): FirebaseUser =
            auth().currentUser ?: throw AuthError.FirebaseUserNotLogged()

    suspend fun updateProfile(
            displayName: String? = null,
            photoUrl: String? = null
    ) {

        val user = currentFirebaseUser()

        val request = UserProfileChangeRequest.Builder()

        displayName?.let { request.setDisplayName(it) }
        photoUrl?.let { request.setPhotoUri(Uri.parse(it)) }

        user.updateProfile(request.build()).bindTask()


    }

    suspend fun updatePassword(
            password: String
    ) {

        val user = currentFirebaseUser()
        user.updatePassword(password).bindTask()

    }

    suspend fun resetPassword(email: String) {

        auth().also { it.useAppLanguage() }
                .sendPasswordResetEmail(email)
                .bindTask()

    }

    fun signOut(): Unit = auth().signOut()

    suspend fun signInWithFirebaseEmailPassword(
            email: String,
            password: String
    ): Auth {

        val auth = auth().signInWithEmailAndPassword(email, password).bindTask()
        val user = currentUser()

        return Auth(auth.additionalUserInfo?.isNewUser, user)

    }

    suspend fun signUpWithFirebaseEmailPassword(
            email: String,
            password: String
    ): Auth {

        val auth = auth().createUserWithEmailAndPassword(email, password).bindTask()
        val user = currentUser()

        return Auth(auth.additionalUserInfo?.isNewUser, user)

    }

    suspend fun signInWithCredential(
            credential: AuthCredential
    ): AuthResult =
            auth().signInWithCredential(credential).bindTask()

    suspend fun <T> Task<T>.bindTask(): T =
            suspendCoroutine { continuation ->
                addOnSuccessListener { continuation.resume(it) }
                        .addOnCanceledListener {
                            continuation.resumeWithException(AuthError.Cancelled())
                        }
                        .addOnFailureListener { continuation.resumeWithException(it) }
            }

    fun auth(): FirebaseAuth = FirebaseAuth.getInstance()

    fun test(): Unit = Unit
}