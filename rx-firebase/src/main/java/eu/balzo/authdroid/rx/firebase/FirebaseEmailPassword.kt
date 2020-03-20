package eu.balzo.authdroid.rx.firebase

import eu.balzo.authdroid.core.Auth
import eu.balzo.authdroid.core.AuthError
import com.google.firebase.auth.AuthResult
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers


fun signInWithFirebaseEmailPassword(
        email: String,
        password: String
): Single<Auth> =
        Single.create<AuthResult> { emitter ->
            firebaseAuth().signInWithEmailAndPassword(email, password)
                    .bindTask(emitter) { emitter.onSuccess(it) }
        }.flatMap { auth ->
            getFirebaseToken()
                    .map { it to auth }
                    .subscribeOn(Schedulers.io())
        }.flatMap {
            when (val user = firebaseAuth().currentUser) {
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
            firebaseAuth().createUserWithEmailAndPassword(email, password)
                    .bindTask(emitter) { emitter.onSuccess(it) }
        }.flatMap { auth ->
            getFirebaseToken()
                    .map { it to auth }
                    .subscribeOn(Schedulers.io())
        }.flatMap {
            when (val user = firebaseAuth().currentUser) {
                null -> Single.error(AuthError.UnknownFirebaseError)
                else -> Single.just(Auth(
                        it.second.additionalUserInfo?.isNewUser,
                        user.toSocialAuthUser(it.first)
                ))
            }.subscribeOn(Schedulers.io())
        }
