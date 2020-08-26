package eu.balzo.authdroid.firebase.facebook

import androidx.fragment.app.FragmentManager
import arrow.Kind
import arrow.core.*
import arrow.core.extensions.fx
import arrow.fx.typeclasses.ConcurrentFx
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import eu.balzo.authdroid.core.Auth
import eu.balzo.authdroid.core.AuthError
import eu.balzo.authdroid.core.AuthError.Companion.toAuthError
import eu.balzo.authdroid.facebook.core.FacebookFragment
import eu.balzo.authdroid.firebase.core.Firebase

object FirebaseFacebook {

    fun <F> auth(
            fx: ConcurrentFx<F>,
            fragmentManager: FragmentManager
    ): Kind<F, Either<AuthError, Auth>> =
            fx.concurrent {

                val auth = !authWithFacebook(fx, fragmentManager)

                val user = !Firebase.currentUser(fx)

                Either.fx {

                    Auth(auth.bind().additionalUserInfo?.isNewUser.toOption(), user.bind())

                }

            }

    private fun <F> authWithFacebook(
            fx: ConcurrentFx<F>,
            fragmentManager: FragmentManager
    ): Kind<F, Either<AuthError, AuthResult>> =
            fx.concurrent {

                !!fx.M.async<Kind<F, Either<AuthError, AuthResult>>> { callback ->

                    val fragment = FacebookFragment()
                    val transaction = fragmentManager.beginTransaction()

                    transaction.add(fragment, FacebookFragment.TAG)
                            .addToBackStack(null)
                            .commit()

                    LoginManager.getInstance()
                            .registerCallback(
                                    fragment.callbackManager,
                                    object : FacebookCallback<LoginResult> {

                                        override fun onSuccess(result: LoginResult?) {
                                            callback(
                                                    result.handleFirebaseFacebookLogin(fx).right()
                                            ).right()
                                            fragmentManager.popBackStack()
                                        }

                                        override fun onCancel() {
                                            callback(just(AuthError.Cancelled.left()).right())
                                            fragmentManager.popBackStack()
                                        }

                                        override fun onError(error: FacebookException?) {

                                            val result =
                                                    error.toOption()
                                                            .map { it.toAuthError() }
                                                            .getOrElse { AuthError.FacebookAuth }
                                                            .left()

                                            callback(just(result).right())
                                            fragmentManager.popBackStack()
                                        }
                                    }
                            )
                }
            }

    private fun <F> LoginResult?.handleFirebaseFacebookLogin(
            fx: ConcurrentFx<F>
    ): Kind<F, Either<AuthError, AuthResult>> =
            fx.concurrent {

                !this@handleFirebaseFacebookLogin.toOption()
                        .fold(
                                { just(AuthError.FirebaseUnknown.left()) },
                                {
                                    val credential =
                                            FacebookAuthProvider.getCredential(it.accessToken.token)
                                    Firebase.signInWithCredential(fx, credential)
                                }
                        )

            }

    fun signOut(): Unit = LoginManager.getInstance().logOut()
}