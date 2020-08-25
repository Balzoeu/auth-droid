package eu.balzo.authdroid.arrow.google

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import arrow.Kind
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.fx.typeclasses.ConcurrentFx
import com.github.florent37.inlineactivityresult.kotlin.startForResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import eu.balzo.authdroid.core.Auth
import eu.balzo.authdroid.core.AuthError
import eu.balzo.authdroid.google.core.getGoogleSignInClient
import eu.balzo.authdroid.google.core.getGoogleSignInIntent
import eu.balzo.authdroid.google.core.getGoogleSignInOptions
import eu.balzo.authdroid.google.core.toSocialAuthUser

object GoogleArrow {

    fun <F> auth(
            fx: ConcurrentFx<F>,
            activity: FragmentActivity,
            clientId: String
    ): Kind<F, Either<Throwable, Auth>> =
            fx.concurrent {


                val googleAuth =
                        !fx.M.async<Kind<F, Either<Throwable, Auth>>> { emitter ->

                            activity.startForResult(
                                    getGoogleSignInIntent(activity, clientId)
                            ) { result ->

                                emitter(fx.concurrent {

                                    val auth =
                                            !authWithGoogle(fx, result.data)

                                    auth.fold(
                                            { it.left() },
                                            {
                                                if (it != null)
                                                    Auth(null, it.toSocialAuthUser()).right()
                                                else
                                                    AuthError.UnknownFirebaseError.left()
                                            }
                                    )

                                }.right())

                            }.onFailed { result ->
                                emitter(
                                        fx.concurrent {
                                            when (result.resultCode) {
                                                Activity.RESULT_CANCELED ->
                                                    AuthError.Cancelled.left()
                                                else ->
                                                    AuthError.UnknownFirebaseError.left()
                                            }

                                        }.right())
                            }

                        }.attempt()

                when (googleAuth) {
                    is Either.Left -> googleAuth.a.left()
                    is Either.Right -> googleAuth.b.bind()
                }
            }

    fun <F> signOut(
            fx: ConcurrentFx<F>,
            activity: FragmentActivity,
            clientId: String
    ): Kind<F, Either<Throwable, Unit>> =
            fx.concurrent {

                !fx.M.async<Unit> { emitter ->

                    getGoogleSignInClient(activity, getGoogleSignInOptions(clientId))
                            .signOut()
                            .addOnSuccessListener { emitter(Unit.right()) }
                            .addOnCanceledListener { emitter(AuthError.Cancelled.left()) }
                            .addOnFailureListener { emitter(it.left()) }

                }.attempt()
            }

    private fun <F> authWithGoogle(
            fx: ConcurrentFx<F>,
            data: Intent?
    ): Kind<F, Either<Throwable, GoogleSignInAccount?>> =
            fx.concurrent {

                !effect {
                    GoogleSignIn.getSignedInAccountFromIntent(data)
                            .getResult(ApiException::class.java)
                }.attempt()
            }
}

