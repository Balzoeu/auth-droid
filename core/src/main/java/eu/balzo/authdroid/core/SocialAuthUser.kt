package eu.balzo.authdroid.core

data class SocialAuthUser(
        val id: String,
        val token: String,
        val googleServerAuthCode: String?,
        val displayName: String?,
        val firstName: String?,
        val lastName: String?,
        val email: String?,
        val profileImage: String?
) {
    override fun toString(): String =
            "id: ${id}, " +
                    "display name: ${displayName ?: "none"}, " +
                    "first name: ${firstName ?: "none"}, " +
                    "last name: ${lastName ?: "none"}, " +
                    "email: ${email ?: "none"}, " +
                    "profileImage: ${profileImage ?: "none"}" +
                    "token: $token" +
                    "google auth code: $googleServerAuthCode"
}