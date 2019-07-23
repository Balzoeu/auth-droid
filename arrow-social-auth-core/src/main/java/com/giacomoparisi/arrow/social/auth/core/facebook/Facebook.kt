package com.giacomoparisi.arrow.social.auth.core.facebook

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import arrow.core.*
import arrow.syntax.function.pipe
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.Profile
import com.facebook.internal.ImageRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.giacomoparisi.arrow.social.auth.core.Auth
import com.giacomoparisi.arrow.social.auth.core.SocialAuthUser
import com.giacomoparisi.arrow.social.auth.core.UnknownFirebaseError
import com.giacomoparisi.kotlin.functional.extensions.arrow.option.getOrEmpty
import com.giacomoparisi.kotlin.functional.extensions.arrow.option.ifNone
import com.giacomoparisi.kotlin.functional.extensions.arrow.option.ifSome
import io.reactivex.Single
import io.reactivex.SingleEmitter

fun authWithFacebook(
        activity: FragmentActivity,
        profileImageDimension: Int = 500): Single<Option<Auth>> =
        Single.create {
            val fragment = FacebookFragment()
            val transaction = activity.supportFragmentManager.beginTransaction()
            transaction.add(fragment, FacebookFragment.TAG).addToBackStack(null).commit()

            LoginManager.getInstance().registerCallback(
                    fragment.callbackManager,
                    object : FacebookCallback<LoginResult> {
                        override fun onSuccess(result: LoginResult?) {
                            result.toOption().handleFacebookLogin(it, profileImageDimension)
                            activity.supportFragmentManager.popBackStack()
                        }

                        override fun onCancel() {
                            it.onSuccess(None)
                            activity.supportFragmentManager.popBackStack()
                        }

                        override fun onError(error: FacebookException?) {
                            error.toOption().getOrElse { UnknownFirebaseError }
                                    .pipe { throwable -> it.onError(throwable) }
                            activity.supportFragmentManager.popBackStack()
                        }
                    }
            )
        }

fun facebookSignOut() {
    LoginManager.getInstance().logOut()
}

private fun Option<LoginResult>.handleFacebookLogin(
        emitter: SingleEmitter<Option<Auth>>,
        imageDimension: Int) {
    this@handleFacebookLogin.ifSome { loginResult ->
        val graphRequest = GraphRequest.newMeRequest(
                loginResult.accessToken
        ) { json, _ ->
            val id = Try { Profile.getCurrentProfile()?.id }.toOption().flatMap { it.toOption() }
            val email = Try { json.getString("email") }.toOption()
            val name = Try { json.getString("name") }.toOption()
            val firstName = name.flatMap { it.split("").firstOrNull().toOption() }
            val lastName = name.flatMap { it.split("").getOrNull(1).toOption() }
            val profilePicture = id.map {
                ImageRequest.getProfilePictureUri(
                        it,
                        imageDimension,
                        imageDimension
                ).toString()
            }

            SocialAuthUser(id.getOrEmpty(), name, firstName, lastName, email, profilePicture)
                    .pipe { emitter.onSuccess(Auth(None, it).toOption()) }
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,name,email,gender,birthday")
        graphRequest.parameters = parameters
        graphRequest.executeAsync()
    }.ifNone { emitter.onError(UnknownFirebaseError) }
}