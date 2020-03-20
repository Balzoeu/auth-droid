package eu.balzo.authdroid.rx.facebook

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.facebook.*
import com.facebook.internal.ImageRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import eu.balzo.authdroid.core.Auth
import eu.balzo.authdroid.core.AuthError
import eu.balzo.authdroid.core.SocialAuthUser
import eu.balzo.authdroid.facebook.core.FacebookFragment
import io.reactivex.Single
import io.reactivex.SingleEmitter
import org.json.JSONObject

object FacebookRx {

    fun auth(
            fragmentManager: FragmentManager,
            profileImageDimension: Int = 500): Single<Auth> =
            Single.create {
                val fragment = FacebookFragment()
                val transaction = fragmentManager.beginTransaction()
                transaction.add(fragment, FacebookFragment.TAG).addToBackStack(null).commit()

                LoginManager.getInstance().registerCallback(
                        fragment.callbackManager,
                        object : FacebookCallback<LoginResult> {

                            override fun onSuccess(result: LoginResult?) {
                                result.handleFacebookLogin(it, profileImageDimension)
                                fragmentManager.popBackStack()
                            }

                            override fun onCancel() {
                                it.onError(AuthError.Cancelled)
                                fragmentManager.popBackStack()
                            }

                            override fun onError(error: FacebookException?) {
                                it.onError(error ?: AuthError.FacebookAuthError)
                                fragmentManager.popBackStack()
                            }
                        }
                )
            }

    fun signOut() {
        LoginManager.getInstance().logOut()
    }

    private fun LoginResult?.handleFacebookLogin(
            emitter: SingleEmitter<Auth>,
            imageDimension: Int) {

        when (this) {
            null -> emitter.onError(AuthError.FacebookAuthError)
            else -> {
                val graphRequest =
                        GraphRequest.newMeRequest(this.accessToken)
                        { json, _ ->
                            val id = Profile.getCurrentProfile()?.id
                            val token = AccessToken.getCurrentAccessToken().token
                            val email = json.getStringOrNull("email")
                            val name = json.getStringOrNull("name")
                            val firstName = name?.split(" ")?.firstOrNull()
                            val lastName = name?.split(" ")?.getOrNull(1)
                            val profilePicture = id?.let {
                                ImageRequest.getProfilePictureUri(
                                        it,
                                        imageDimension,
                                        imageDimension
                                ).toString()
                            }

                            SocialAuthUser(
                                    id.orEmpty(),
                                    token,
                                    name,
                                    firstName,
                                    lastName,
                                    email,
                                    profilePicture
                            ).also { emitter.onSuccess(Auth(null, it)) }
                        }
                val parameters = Bundle()
                parameters.putString("fields", "id,name,email,gender,birthday")
                graphRequest.parameters = parameters
                graphRequest.executeAsync()
            }
        }
    }

    private fun JSONObject.getStringOrNull(name: String) =
            try {
                this.getString(name)
            } catch (error: Throwable) {
                null
            }
}
