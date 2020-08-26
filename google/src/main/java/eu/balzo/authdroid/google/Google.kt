package eu.balzo.authdroid.google

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import arrow.Kind
import arrow.core.*
import arrow.fx.typeclasses.ConcurrentFx
import com.github.florent37.inlineactivityresult.Result
import com.github.florent37.inlineactivityresult.kotlin.startForResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import eu.balzo.authdroid.core.Auth
import eu.balzo.authdroid.core.AuthError
import eu.balzo.authdroid.core.AuthError.Companion.toAuthError
import eu.balzo.authdroid.google.core.getGoogleSignInClient
import eu.balzo.authdroid.google.core.getGoogleSignInIntent
import eu.balzo.authdroid.google.core.getGoogleSignInOptions
import eu.balzo.authdroid.google.core.toSocialAuthUser

object Google {

    fun <F> auth(
            fx: ConcurrentFx<F>,
            activity: FragmentActivity,
            clientId: String
    ): Kind<F, Either<AuthError, Auth>> =
            fx.concurrent {

                !!fx.M.async<Kind<F, Either<AuthError, Auth>>> { callback ->

                    activity.startForResult(getGoogleSignInIntent(activity, clientId)) {
                        callback(googleAuth(fx, it).right())
                    }.onFailed { result ->
                        callback(
                                fx.concurrent {
                                    when (result.resultCode) {
                                        Activity.RESULT_CANCELED ->
                                            AuthError.Cancelled.left()
                                        else ->
                                            AuthError.FirebaseUnknown.left()
                                    }
                                }.right())
                    }
                }
            }

    private fun <F> googleAuth(
            fx: ConcurrentFx<F>,
            result: Result
    ): Kind<F, Either<AuthError, Auth>> =
            fx.concurrent {

                authWithGoogle(fx, result.data)
                        .bind()
                        .map { googleAccount ->
                            googleAccount.map { it.toSocialAuthUser() }
                        }
                        .fold(
                                { it.left() },
                                { user ->
                                    user.fold(
                                            { AuthError.FirebaseUnknown.left() },
                                            { it.right() }
                                    )
                                }
                        )
                        .map { Auth(None, it) }

            }

    fun <F> signOut(
            fx: ConcurrentFx<F>,
            activity: FragmentActivity,
            clientId: String
    ): Kind<F, Either<AuthError, Unit>> =
            fx.concurrent {

                !fx.M.async<Either<AuthError, Unit>> { callback ->

                    getGoogleSignInClient(activity, getGoogleSignInOptions(clientId))
                            .signOut()
                            .addOnSuccessListener { callback(Unit.right().right()) }
                            .addOnCanceledListener { callback(AuthError.Cancelled.left().right()) }
                            .addOnFailureListener { callback(it.toAuthError().left().right()) }

                }
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

