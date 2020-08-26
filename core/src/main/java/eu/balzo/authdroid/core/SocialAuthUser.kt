package eu.balzo.authdroid.core

import arrow.core.Option
import arrow.core.extensions.show

data class SocialAuthUser(
        val id: String,
        val token: String,
        val googleServerAuthCode: Option<String>,
        val displayName: Option<String>,
        val firstName: Option<String>,
        val lastName: Option<String>,
        val email: Option<String>,
        val profileImage: Option<String>
) {
    override fun toString(): String =
            "id: ${id}, " +
                    "display name: ${displayName.show(String.show())}, " +
                    "first name: ${firstName.show(String.show())}, " +
                    "last name: ${lastName.show(String.show())}, " +
                    "email: ${email.show(String.show())}, " +
                    "profileImage: ${profileImage.show(String.show())}" +
                    "token: $token" +
                    "google auth code: $googleServerAuthCode"
}