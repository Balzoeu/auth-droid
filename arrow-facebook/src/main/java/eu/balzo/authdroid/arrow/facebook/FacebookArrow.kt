package eu.balzo.authdroid.arrow.facebook

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import arrow.Kind
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.fx.typeclasses.ConcurrentFx
import com.facebook.*
import com.facebook.internal.ImageRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import eu.balzo.authdroid.core.Auth
import eu.balzo.authdroid.core.AuthError
import eu.balzo.authdroid.core.SocialAuthUser
import eu.balzo.authdroid.facebook.core.FacebookFragment
import org.json.JSONObject

object FacebookArrow {

    fun <F> auth(
            fx: ConcurrentFx<F>,
            fragmentManager: FragmentManager,
            profileImageDimension: Int = 500
    ): Kind<F, Either<Throwable, Auth>> =
            fx.concurrent {

                val fragment = FacebookFragment()
                val transaction = fragmentManager.beginTransaction()
                transaction.add(fragment, FacebookFragment.TAG).addToBackStack(null).commit()

                val auth =
                        !!fx.async.async<Kind<F, Either<Throwable, Auth>>> {

                            LoginManager.getInstance().registerCallback(
                                    fragment.callbackManager,
                                    object : FacebookCallback<LoginResult> {

                                        override fun onSuccess(result: LoginResult?) {
                                            it(result.handleFacebookLogin(
                                                    fx,
                                                    profileImageDimension
                                            ).right())
                                        }

                                        override fun onCancel() {
                                            it(AuthError.Cancelled.left())
                                        }

                                        override fun onError(error: FacebookException?) {
                                            it((error ?: AuthError.FacebookAuthError).left())
                                        }
                                    }
                            )
                        }

                fragmentManager.popBackStack()

                auth
            }

    fun signOut(): Unit = LoginManager.getInstance().logOut()

    private fun <F> LoginResult?.handleFacebookLogin(
            fx: ConcurrentFx<F>,
            imageDimension: Int
    ): Kind<F, Either<Throwable, Auth>> =
            fx.concurrent {

                when (this@handleFacebookLogin) {
                    null -> AuthError.FacebookAuthError.left()
                    else -> {
                        !fx.async.async<Auth> {
                            val graphRequest =
                                    GraphRequest.newMeRequest(
                                            this@handleFacebookLogin?.accessToken
                                    )
                                    { json, _ ->
                                        val id =
                                                Profile.getCurrentProfile()?.id
                                        val token =
                                                AccessToken.getCurrentAccessToken().token
                                        val email =
                                                json.getStringOrNull("email")
                                        val name =
                                                json.getStringOrNull("name")
                                        val firstName =
                                                name?.split(" ")?.firstOrNull()
                                        val lastName =
                                                name?.split(" ")?.getOrNull(1)
                                        val profilePicture =
                                                id?.let {
                                                    ImageRequest.getProfilePictureUri(
                                                            it,
                                                            imageDimension,
                                                            imageDimension
                                                    ).toString()
                                                }

                                        val user =
                                                SocialAuthUser(
                                                        id.orEmpty(),
                                                        token,
                                                        name,
                                                        firstName,
                                                        lastName,
                                                        email,
                                                        profilePicture
                                                )

                                        it(Auth(null, user).right())
                                    }
                            val parameters = Bundle()
                            parameters.putString("fields", "id,name,email,gender,birthday")
                            graphRequest.parameters = parameters
                            graphRequest.executeAsync()

                        }.attempt()
                    }
                }
            }

    private fun JSONObject.getStringOrNull(name: String) =
            try {
                getString(name)
            } catch (error: Throwable) {
                null
            }
}
