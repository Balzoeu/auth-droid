package eu.balzo.authdroid.facebook

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.facebook.*
import com.facebook.internal.ImageRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import eu.balzo.authdroid.core.Auth
import eu.balzo.authdroid.core.AuthError
import eu.balzo.authdroid.core.SocialAuthUser
import eu.balzo.authdroid.facebook.core.FacebookFragment
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object Facebook {

    suspend fun auth(
        fragmentManager: FragmentManager,
        profileImageDimension: Int = 500
    ): Auth {

        val fragment = FacebookFragment()
        val transaction = fragmentManager.beginTransaction()
        transaction.add(fragment, FacebookFragment.TAG).addToBackStack(null).commit()

        try {

            val facebookAuth =
                suspendCoroutine<LoginResult?> { continuation ->

                    LoginManager.getInstance().registerCallback(
                        fragment.callbackManager,
                        object : FacebookCallback<LoginResult> {

                            var isResumed = false

                            override fun onSuccess(result: LoginResult) {
                                if (isResumed.not()) {
                                    continuation.resume(result)
                                    isResumed = true
                                }
                            }

                            override fun onCancel() {
                                if (isResumed.not()) {
                                    continuation.resumeWithException(AuthError.Cancelled())
                                    isResumed = true
                                }
                            }

                            override fun onError(error: FacebookException) {
                                if (isResumed.not()) {
                                    continuation.resumeWithException(error)
                                    isResumed = true
                                }
                            }

                        }
                    )
                }

            val auth = facebookAuth.handleFacebookLogin(profileImageDimension)

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

    fun signOut(): Unit = LoginManager.getInstance().logOut()

    private suspend fun LoginResult?.handleFacebookLogin(
        imageDimension: Int
    ): Auth {

        val json =
            when (this) {
                null -> throw AuthError.FacebookAuth()
                else -> {
                    suspendCoroutine<JSONObject> { continuation ->
                        val graphRequest =
                            GraphRequest.newMeRequest(accessToken)
                            { json, _ ->
                                if (json == null)
                                    continuation.resumeWithException(AuthError.Unknown())
                                else
                                    continuation.resume(json)
                            }
                        val parameters = Bundle()
                        parameters.putString("fields", "id,name,email,gender,birthday")
                        graphRequest.parameters = parameters
                        graphRequest.executeAsync()

                    }
                }
            }

        return Auth(null, json.parseUser(imageDimension))

    }

    private fun JSONObject.parseUser(imageDimension: Int): SocialAuthUser {

        val id = Profile.getCurrentProfile()?.id?.emptyOrBlankToNull()
        val token = AccessToken.getCurrentAccessToken()?.token!!
        val email = string("email")?.emptyOrBlankToNull()
        val name = string("name")?.emptyOrBlankToNull()
        val firstName =
            name?.split(" ")?.firstOrNull()?.emptyOrBlankToNull()
        val lastName =
            name?.split(" ")
                ?.getOrNull(1)
                ?.emptyOrBlankToNull()
        val profilePicture =
            id?.let {
                ImageRequest.getProfilePictureUri(
                    it,
                    imageDimension,
                    imageDimension
                ).toString()
            }?.emptyOrBlankToNull()

        return SocialAuthUser(
            id ?: "",
            token,
            null,
            name,
            firstName,
            lastName,
            email,
            profilePicture
        )

    }

    private fun JSONObject.string(name: String): String? =
        try {
            getString(name)
        } catch (error: Throwable) {
            null
        }

    private fun String.emptyOrBlankToNull(): String? =
        if (isEmpty() || isBlank()) null else this
}
