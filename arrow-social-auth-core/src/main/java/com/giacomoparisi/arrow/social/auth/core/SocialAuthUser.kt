package com.giacomoparisi.arrow.social.auth.core

import arrow.core.Option
import arrow.core.fix
import arrow.core.monad
import arrow.core.toOption
import arrow.typeclasses.binding
import com.giacomoparisi.kotlin.functional.extensions.arrow.option.getOrEmpty
import com.google.firebase.auth.FirebaseUser

data class SocialAuthUser(
        val firstName: Option<String>,
        val lastName: Option<String>,
        val email: Option<String>,
        val profileImage: Option<String>
) {
    override fun toString(): String =
            "first name: ${firstName.getOrEmpty()}, " +
                    "last name: ${lastName.getOrEmpty()}, " +
                    "email: ${email.getOrEmpty()}, " +
                    "profileImage: ${profileImage.getOrEmpty()}"
}

fun FirebaseUser.toSocialAuthUser(): SocialAuthUser =
        SocialAuthUser(
                Option.monad().binding {
                    this@toSocialAuthUser.displayName
                            .toOption()
                            .bind()
                            .split(" ")
                            .getOrNull(0)
                            .toOption()
                            .bind()
                }.fix(),
                Option.monad().binding {
                    this@toSocialAuthUser.displayName
                            .toOption()
                            .bind()
                            .split(" ")
                            .getOrNull(1)
                            .toOption()
                            .bind()
                }.fix(),
                this.email.toOption(),
                this.photoUrl.toOption().map { it.toString() }
        )