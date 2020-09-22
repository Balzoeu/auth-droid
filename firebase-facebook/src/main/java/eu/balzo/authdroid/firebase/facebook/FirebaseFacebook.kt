package eu.balzo.authdroid.firebase.facebook

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import arrow.core.*
import arrow.core.computations.either
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
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object FirebaseFacebook {

    suspend fun auth(
            fragmentManager: FragmentManager
    ): Either<AuthError, Auth> {

        val auth = authWithFacebook(fragmentManager)

        val user = Firebase.currentUser()

        return either {

            Auth(auth.bind().additionalUserInfo?.isNewUser, user.bind())

        }

    }

    private suspend fun authWithFacebook(
            fragmentManager: FragmentManager
    ): Either<AuthError, AuthResult> {

        val fragment = FacebookFragment()
        val transaction = fragmentManager.beginTransaction()

        transaction.add(fragment, FacebookFragment.TAG)
                .addToBackStack(null)
                .commit()

        val facebookAuth =
                suspendCoroutine<Either<AuthError, LoginResult?>> { continuation ->
                    LoginManager.getInstance()
                            .registerCallback(
                                    fragment.callbackManager,
                                    object : FacebookCallback<LoginResult> {

                                        override fun onSuccess(result: LoginResult?) {
                                            continuation.resume(result.right())
                                        }

                                        override fun onCancel() {
                                            continuation.resume(AuthError.Cancelled.left())
                                        }

                                        override fun onError(error: FacebookException?) {

                                            val result =
                                                    error.toOption()
                                                            .map { it.toAuthError() }
                                                            .getOrElse { AuthError.FacebookAuth }
                                                            .left()

                                            continuation.resume(result)
                                        }
                                    }
                            )
                }

        val auth =
                facebookAuth.flatMap { it.handleFirebaseFacebookLogin() }

        removeFragment(fragmentManager, fragment)

        return auth
    }

    private fun removeFragment(fragmentManager: FragmentManager, fragment: Fragment): Unit {

        val transaction = fragmentManager.beginTransaction()
        transaction.remove(fragment)
        transaction.commit()

    }

    private suspend fun LoginResult?.handleFirebaseFacebookLogin(): Either<AuthError, AuthResult> =
            this?.let {

                val credential =
                        FacebookAuthProvider.getCredential(it.accessToken.token)
                Firebase.signInWithCredential(credential)

            } ?: AuthError.FirebaseUnknown.left()

    fun signOut(): Unit = LoginManager.getInstance().logOut()
}