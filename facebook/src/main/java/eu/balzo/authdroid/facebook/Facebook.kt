package eu.balzo.authdroid.facebook

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import arrow.Kind
import arrow.core.*
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

object Facebook {

    fun <F> auth(
            fx: ConcurrentFx<F>,
            fragmentManager: FragmentManager,
            profileImageDimension: Int = 500
    ): Kind<F, Either<AuthError, Auth>> =
            fx.concurrent {

                val fragment = FacebookFragment()
                val transaction = fragmentManager.beginTransaction()
                transaction.add(fragment, FacebookFragment.TAG).addToBackStack(null).commit()

                val auth =
                        !!fx.M.async<Kind<F, Either<AuthError, Auth>>> {

                            LoginManager.getInstance().registerCallback(
                                    fragment.callbackManager,
                                    object : FacebookCallback<LoginResult> {

                                        override fun onSuccess(result: LoginResult?) {
                                            it(
                                                    result.handleFacebookLogin(
                                                            fx,
                                                            profileImageDimension
                                                    ).right()
                                            )
                                        }

                                        override fun onCancel() {
                                            it(just(AuthError.Cancelled.left()).right())
                                        }

                                        override fun onError(error: FacebookException?) {
                                            it(
                                                    just(
                                                            AuthError.Unknown(error.toOption())
                                                                    .left()
                                                    ).right()
                                            )
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
    ): Kind<F, Either<AuthError, Auth>> =
            fx.concurrent {

                when (this@handleFacebookLogin) {
                    null -> AuthError.FacebookAuth.left()
                    else -> {
                        !fx.M.async<Either<AuthError, Auth>> { callback ->
                            val graphRequest =
                                    GraphRequest.newMeRequest(
                                            this@handleFacebookLogin.accessToken
                                    )
                                    { json, _ ->

                                        json.parseUser(fx, imageDimension)
                                                .map { callback(Auth(None, it).right().right()) }


                                    }
                            val parameters = Bundle()
                            parameters.putString("fields", "id,name,email,gender,birthday")
                            graphRequest.parameters = parameters
                            graphRequest.executeAsync()

                        }
                    }
                }
            }

    private fun <F> JSONObject.parseUser(
            fx: ConcurrentFx<F>,
            imageDimension: Int
    ): Kind<F, SocialAuthUser> =
            fx.concurrent {

                val id = Profile.getCurrentProfile()?.id.emptyOrBlankToNone()
                val token = AccessToken.getCurrentAccessToken().token
                val email =
                        effect { string("email") }
                                .attempt()
                                .bind()
                                .toOption()
                                .flatMap { it.emptyOrBlankToNone() }
                val name =
                        effect { string("name") }
                                .attempt()
                                .bind()
                                .toOption()
                                .flatMap { it.emptyOrBlankToNone() }
                val firstName =
                        name.map { it.split(" ") }
                                .flatMap { it.firstOrNone() }
                                .flatMap { it.emptyOrBlankToNone() }
                val lastName =
                        name.map { it.split(" ") }
                                .flatMap { it.getOrNull(1).toOption() }
                                .flatMap { it.emptyOrBlankToNone() }
                val profilePicture =
                        id.map {
                            ImageRequest.getProfilePictureUri(
                                    it,
                                    imageDimension,
                                    imageDimension
                            ).toString()
                        }.flatMap { it.emptyOrBlankToNone() }

                SocialAuthUser(
                        id.getOrElse { "" },
                        token,
                        None,
                        name,
                        firstName,
                        lastName,
                        email,
                        profilePicture
                )

            }

    private suspend fun JSONObject.string(name: String): String = getString(name)

    private fun String?.emptyOrBlankToNone(): Option<String> =
            toOption().flatMap { if (it.isEmpty() || it.isBlank()) None else it.some() }
}
