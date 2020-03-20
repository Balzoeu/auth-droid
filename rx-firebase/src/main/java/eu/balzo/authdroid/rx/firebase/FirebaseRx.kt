package eu.balzo.authdroid.rx.firebase

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import eu.balzo.authdroid.core.Auth
import eu.balzo.authdroid.core.AuthError
import eu.balzo.authdroid.core.SocialAuthUser
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.schedulers.Schedulers

object FirebaseRx {

    fun token(): Single<String> =
            Single.create {
                when (val user = FirebaseAuth.getInstance().currentUser) {
                    null -> it.onError(AuthError.FirebaseUserNotLogged)
                    else -> user.getIdToken(true).bindTask(it) { result ->
                        when (val token = result.token) {
                            null -> it.onError(AuthError.UnknownFirebaseError)
                            else -> it.onSuccess(token)
                        }
                    }
                }
            }

    fun id(): String? =
            auth().currentUser?.uid

    fun currentUser(): Single<SocialAuthUser> =
            token()
                    .flatMap {
                        when (val user = auth().currentUser?.toSocialAuthUser(it)) {
                            null -> Single.error(AuthError.FirebaseUserNotLogged)
                            else -> Single.just(user)
                        }
                    }

    fun updateProfile(displayName: String? = null, photoUrl: String? = null): Single<Unit> =
            Single.create { emitter ->
                when (val user = FirebaseAuth.getInstance().currentUser) {
                    null -> emitter.onError(AuthError.FirebaseUserNotLogged)
                    else -> {
                        val request = UserProfileChangeRequest.Builder()
                                .also { builder ->
                                    displayName?.let { name -> builder.setDisplayName(name) }
                                }
                                .also { builder ->
                                    photoUrl?.let { url -> builder.setPhotoUri(Uri.parse(url)) }
                                }
                                .build()

                        user.updateProfile(request).bindTask(emitter) { emitter.onSuccess(Unit) }
                    }
                }
            }

    fun updatePassword(password: String): Single<Unit> =
            Single.create { emitter ->
                when (val user = FirebaseAuth.getInstance().currentUser) {
                    null -> emitter.onError(AuthError.FirebaseUserNotLogged)
                    else -> user.updatePassword(password).bindTask(emitter) { emitter.onSuccess(Unit) }

                }
            }

    fun resetPassword(email: String): Single<Unit> =
            Single.create { emitter ->
                FirebaseAuth.getInstance()
                        .also { firebaseAuth -> firebaseAuth.useAppLanguage() }
                        .sendPasswordResetEmail(email)
                        .bindTask(emitter) { emitter.onSuccess(Unit) }
            }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    fun signInWithFirebaseEmailPassword(
            email: String,
            password: String
    ): Single<Auth> =
            Single.create<AuthResult> { emitter ->
                auth().signInWithEmailAndPassword(email, password)
                        .bindTask(emitter) { emitter.onSuccess(it) }
            }.flatMap { auth ->
                token()
                        .map { it to auth }
                        .subscribeOn(Schedulers.io())
            }.flatMap {
                when (val user = auth().currentUser) {
                    null -> Single.error(AuthError.UnknownFirebaseError)
                    else -> Single.just(Auth(
                            it.second.additionalUserInfo?.isNewUser,
                            user.toSocialAuthUser(it.first)
                    ))
                }.subscribeOn(Schedulers.io())
            }


    fun signUpWithFirebaseEmailPassword(
            email: String,
            password: String
    ): Single<Auth> =
            Single.create<AuthResult> { emitter ->
                auth().createUserWithEmailAndPassword(email, password)
                        .bindTask(emitter) { emitter.onSuccess(it) }
            }.flatMap { auth ->
                token()
                        .map { it to auth }
                        .subscribeOn(Schedulers.io())
            }.flatMap {
                when (val user = auth().currentUser) {
                    null -> Single.error(AuthError.UnknownFirebaseError)
                    else -> Single.just(Auth(
                            it.second.additionalUserInfo?.isNewUser,
                            user.toSocialAuthUser(it.first)
                    ))
                }.subscribeOn(Schedulers.io())
            }

    fun signInWithCredential(
            credential: AuthCredential,
            emitter: SingleEmitter<AuthResult>) {
        auth().signInWithCredential(credential)
                .bindTask(emitter) { emitter.onSuccess(it) }
    }

    fun <F, T> Task<F>.bindTask(
            emitter: SingleEmitter<T>,
            onSuccess: (F) -> Unit
    ) {
        this.addOnSuccessListener {
            if (emitter.isDisposed.not()) {
                onSuccess(it)
            }
        }.addOnCanceledListener {
            if (emitter.isDisposed.not()) {
                emitter.onError(AuthError.Cancelled)
            }
        }.addOnFailureListener {
            if (emitter.isDisposed.not()) {
                emitter.onError(it)
            }
        }
    }

    fun auth(): FirebaseAuth = FirebaseAuth.getInstance()
}