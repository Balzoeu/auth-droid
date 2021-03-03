package eu.balzo.authdroid.firebase.facebook

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import eu.balzo.authdroid.core.Auth
import eu.balzo.authdroid.core.AuthError
import eu.balzo.authdroid.facebook.core.FacebookFragment
import eu.balzo.authdroid.firebase.core.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object FirebaseFacebook {

    suspend fun auth(
            fragmentManager: FragmentManager
    ): Auth {

        val auth = authWithFacebook(fragmentManager)

        val user = Firebase.currentUser()

        return Auth(auth.additionalUserInfo?.isNewUser, user)

    }

    private suspend fun authWithFacebook(
            fragmentManager: FragmentManager
    ): AuthResult {

        val fragment = FacebookFragment()
        val transaction = fragmentManager.beginTransaction()

        transaction.add(fragment, FacebookFragment.TAG)
                .addToBackStack(null)
                .commit()

        try {

            val facebookAuth =
                    suspendCoroutine<LoginResult?> { continuation ->
                        LoginManager.getInstance()
                                .registerCallback(
                                        fragment.callbackManager,
                                        object : FacebookCallback<LoginResult> {

                                            var isResumed = false

                                            override fun onSuccess(result: LoginResult?) {
                                                if (isResumed.not()) {
                                                    continuation.resume(result)
                                                    isResumed = true
                                                }
                                            }

                                            override fun onCancel() {
                                                if (isResumed.not()) {
                                                    continuation.resumeWithException(
                                                            AuthError.Cancelled()
                                                    )
                                                    isResumed = true
                                                }
                                            }

                                            override fun onError(error: FacebookException?) {

                                                if (isResumed.not()) {
                                                    continuation.resumeWithException(
                                                            error ?: AuthError.FacebookAuth()
                                                    )
                                                    isResumed = true
                                                }

                                            }
                                        }
                                )
                    }

            val auth = facebookAuth.handleFirebaseFacebookLogin()
            removeFragment(fragmentManager, fragment)
            return auth

        } catch (throwable: Throwable) {

            removeFragment(fragmentManager, fragment)
            throw throwable

        }

    }

    private fun removeFragment(fragmentManager: FragmentManager, fragment: Fragment) {

        val transaction = fragmentManager.beginTransaction()
        transaction.remove(fragment)
        transaction.commit()

    }

    private suspend fun LoginResult?.handleFirebaseFacebookLogin(): AuthResult =
            this?.let {

                val credential =
                        FacebookAuthProvider.getCredential(it.accessToken.token)
                Firebase.signInWithCredential(credential)

            } ?: throw AuthError.FirebaseUnknown()

    fun signOut(): Unit = LoginManager.getInstance().logOut()

}