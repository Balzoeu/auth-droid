package com.giacomoparisi.arrow.social.auth.core

import arrow.core.Option
import arrow.core.toOption
import com.giacomoparisi.kotlin.functional.extensions.arrow.option.getOrEmpty
import com.google.firebase.auth.FirebaseUser

data class SocialAuthUser(
        val displayName: Option<String>,
        val firstName: Option<String>,
        val lastName: Option<String>,
        val email: Option<String>,
        val profileImage: Option<String>
) {
    override fun toString(): String =
            "display name: ${displayName.getOrEmpty()}, " +
                    "first name: ${firstName.getOrEmpty()}, " +
                    "last name: ${lastName.getOrEmpty()}, " +
                    "email: ${email.getOrEmpty()}, " +
                    "profileImage: ${profileImage.getOrEmpty()}"
}

fun FirebaseUser.toSocialAuthUser(): SocialAuthUser =
        SocialAuthUser(
                this.displayName.toOption(),
                this.displayName
                        ?.split(" ")
                        ?.getOrNull(0)
                        .toOption(),
                this.displayName
                        ?.split(" ")
                        ?.getOrNull(1)
                        .toOption(),
                this.email.toOption(),
                this.photoUrl.toOption().map { it.toString() })