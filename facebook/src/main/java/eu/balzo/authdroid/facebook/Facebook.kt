package eu.balzo.authdroid.facebook

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
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
import kotlin.coroutines.suspendCoroutine

object Facebook {

    suspend fun auth(
            fragmentManager: FragmentManager,
            profileImageDimension: Int = 500
    ): Either<AuthError, Auth> {

        val fragment = FacebookFragment()
        val transaction = fragmentManager.beginTransaction()
        transaction.add(fragment, FacebookFragment.TAG).addToBackStack(null).commit()

        val facebookAuth =
                suspendCoroutine<Either<AuthError, LoginResult?>> { continuation ->

                    LoginManager.getInstance().registerCallback(
                            fragment.callbackManager,
                            object : FacebookCallback<LoginResult> {

                                var isResumed = false

                                override fun onSuccess(result: LoginResult?) {
                                    if (isResumed.not()) {
                                        continuation.resume(result.right())
                                        isResumed = true
                                    }
                                }

                                override fun onCancel() {
                                    if (isResumed.not()) {
                                        continuation.resume(AuthError.Cancelled.left())
                                        isResumed = true
                                    }
                                }

                                override fun onError(error: FacebookException?) {
                                    if (isResumed.not()) {
                                        continuation.resume(AuthError.Unknown(error).left())
                                        isResumed = true
                                    }
                                }

                            }
                    )
                }

        val auth =
                facebookAuth.flatMap { it.handleFacebookLogin(profileImageDimension) }

        removeFragment(fragmentManager, fragment)

        return auth
    }

    private fun removeFragment(fragmentManager: FragmentManager, fragment: Fragment): Unit {

        val transaction = fragmentManager.beginTransaction()
        transaction.remove(fragment)
        transaction.commit()

    }

    fun signOut(): Unit = LoginManager.getInstance().logOut()

    private suspend fun LoginResult?.handleFacebookLogin(
            imageDimension: Int
    ): Either<AuthError, Auth> {

        val json =
                when (this) {
                    null -> AuthError.FacebookAuth.left()
                    else -> {
                        suspendCoroutine<Either<AuthError, JSONObject>> { continuation ->
                            val graphRequest =
                                    GraphRequest.newMeRequest(accessToken)
                                    { json, _ ->
                                        continuation.resume(json.right())
                                    }
                            val parameters = Bundle()
                            parameters.putString("fields", "id,name,email,gender,birthday")
                            graphRequest.parameters = parameters
                            graphRequest.executeAsync()

                        }
                    }
                }

        return json.map { Auth(null, it.parseUser(imageDimension)) }
    }

    private suspend fun JSONObject.parseUser(
            imageDimension: Int
    ): SocialAuthUser {

        val id = Profile.getCurrentProfile()?.id?.emptyOrBlankToNull()
        val token = AccessToken.getCurrentAccessToken().token
        val email = string("email").orNull()?.emptyOrBlankToNull()
        val name = string("name").orNull()?.emptyOrBlankToNull()
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

    private suspend fun JSONObject.string(name: String): Either<Throwable, String> =
            Either.catch { getString(name) }

    private fun String.emptyOrBlankToNull(): String? =
            if (isEmpty() || isBlank()) null else this
}
