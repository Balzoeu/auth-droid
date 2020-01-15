package eu.balzo.authdroid.droid.rx.firebase.google

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import eu.balzo.authdroid.core.Auth
import eu.balzo.authdroid.core.AuthError
import com.balzo.authdroid.rx.firebase.*
import com.github.florent37.inlineactivityresult.kotlin.startForResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import eu.balzo.authdroid.rx.firebase.*
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.schedulers.Schedulers

fun authWithFirebaseGoogle(activity: FragmentActivity, clientId: String): Single<Auth> =
        Single.create<AuthResult> {
            activity.startForResult(getGoogleSignInIntent(activity, clientId)) { result ->

                val auth = authWithGoogle(result.data, it)

                if (auth != null) {
                    firebaseCredentialSignIn(
                            GoogleAuthProvider.getCredential(
                                    auth.idToken,
                                    null),
                            it)
                } else {
                    it.onError(AuthError.UnknownFirebaseError)
                }
            }.onFailed { result ->
                when (result.resultCode) {
                    Activity.RESULT_CANCELED -> it.onError(AuthError.Cancelled)
                    else -> it.onError(AuthError.UnknownFirebaseError)
                }
            }
        }.flatMap { auth ->
            getFirebaseToken()
                    .map { it to auth }
                    .subscribeOn(Schedulers.io())
        }.flatMap {
            when (val user = firebaseAuth().currentUser) {
                null -> Single.error<Auth>(AuthError.UnknownFirebaseError)
                else -> Single.just(Auth(
                        it.second.additionalUserInfo?.isNewUser,
                        user.toSocialAuthUser(it.first)
                ))
            }.subscribeOn(Schedulers.io())
        }

fun googleSignOut(activity: FragmentActivity, clientId: String): Single<Unit> =
        Single.create { emitter ->
            getGoogleSignInClient(activity, getGoogleSignInOptions(clientId))
                    .signOut()
                    .bindTask(emitter) { emitter.onSuccess(Unit) }
        }

private fun authWithGoogle(data: Intent?, emitter: SingleEmitter<AuthResult>): GoogleSignInAccount? =
        try {
            GoogleSignIn.getSignedInAccountFromIntent(data)
                    .getResult(ApiException::class.java)
        } catch (throwable: Throwable) {
            when (throwable) {
                is ApiException -> emitter.onError(throwable)
                else -> emitter.onError(AuthError.GoogleAuthError)
            }
            null
        }

private fun getGoogleSignInIntent(activity: FragmentActivity, clientId: String): Intent =
        getGoogleSignInClient(activity, getGoogleSignInOptions(clientId)).signInIntent

private fun getGoogleSignInOptions(clientId: String): GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(clientId)
                .requestEmail()
                .build()

private fun getGoogleSignInClient(
        activity: FragmentActivity,
        googleSignInOptions: GoogleSignInOptions
): GoogleSignInClient =
        GoogleSignIn.getClient(
                activity,
                googleSignInOptions)