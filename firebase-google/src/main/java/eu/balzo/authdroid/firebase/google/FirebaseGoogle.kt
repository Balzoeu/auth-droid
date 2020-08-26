package eu.balzo.authdroid.firebase.google

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import arrow.Kind
import arrow.core.*
import arrow.core.extensions.fx
import arrow.fx.typeclasses.ConcurrentFx
import com.github.florent37.inlineactivityresult.Result
import com.github.florent37.inlineactivityresult.kotlin.startForResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import eu.balzo.authdroid.core.Auth
import eu.balzo.authdroid.core.AuthError
import eu.balzo.authdroid.core.AuthError.Companion.toAuthError
import eu.balzo.authdroid.core.accumulateError
import eu.balzo.authdroid.core.noneToError
import eu.balzo.authdroid.firebase.core.Firebase
import eu.balzo.authdroid.firebase.core.Firebase.bindTask
import eu.balzo.authdroid.google.core.getGoogleSignInClient
import eu.balzo.authdroid.google.core.getGoogleSignInIntent
import eu.balzo.authdroid.google.core.getGoogleSignInOptions

object FirebaseGoogle {

    fun <F> auth(
            fx: ConcurrentFx<F>,
            activity: FragmentActivity,
            clientId: String
    ): Kind<F, Either<AuthError, Auth>> =
            fx.concurrent {

                val auth =
                        !!fx.M.async<Kind<F, Either<AuthError, AuthResult>>> { callback ->

                            activity.startForResult(
                                    getGoogleSignInIntent(activity, clientId)
                            ) { callback(auth(fx, it).right()) }
                                    .onFailed {
                                        when (it.resultCode) {
                                            Activity.RESULT_CANCELED ->
                                                callback(just(AuthError.Cancelled.left()).right())
                                            else ->
                                                callback(just(AuthError.GoogleAuth.left()).right())
                                        }
                                    }

                        }

                val user = !Firebase.currentUser(fx)


                Either.fx {
                    Auth(auth.bind().additionalUserInfo?.isNewUser.toOption(), user.bind())
                }
            }

    private fun <F> auth(fx: ConcurrentFx<F>, result: Result): Kind<F, Either<AuthError, AuthResult>> =
            fx.concurrent {

                !authWithGoogle(fx, result.data)
                        .bind()
                        .noneToError(AuthError.Unknown(None))
                        .map {
                            Firebase.signInWithCredential(
                                    fx,
                                    GoogleAuthProvider.getCredential(it.idToken, null)
                            )
                        }
                        .accumulateError(fx)

            }

    fun <F> signOut(
            fx: ConcurrentFx<F>,
            activity: FragmentActivity,
            clientId: String
    ): Kind<F, Either<AuthError, Unit>> =
            fx.concurrent {

                !getGoogleSignInClient(activity, getGoogleSignInOptions(clientId))
                        .signOut()
                        .bindTask(fx)
                        .map { it.map { Unit } }

            }

    private fun <F> authWithGoogle(
            fx: ConcurrentFx<F>,
            data: Intent?
    ): Kind<F, Either<AuthError, Option<GoogleSignInAccount>>> =
            fx.concurrent {

                effect {

                    GoogleSignIn.getSignedInAccountFromIntent(data)
                            .getResult(ApiException::class.java)
                            .toOption()

                }.attempt().bind().mapLeft { it.toAuthError() }
            }
}