package eu.balzo.authdroid.google.core

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import eu.balzo.authdroid.core.SocialAuthUser

fun GoogleSignInAccount.toSocialAuthUser(): SocialAuthUser =
        SocialAuthUser(
                id.orEmpty(),
                idToken.orEmpty(),
                serverAuthCode?.emptyOrBlankToNull(),
                displayName?.emptyOrBlankToNull(),
                displayName?.split(" ")?.getOrNull(0)?.emptyOrBlankToNull(),
                displayName?.split(" ")?.getOrNull(1)?.emptyOrBlankToNull(),
                email?.emptyOrBlankToNull(),
                photoUrl?.toString()?.emptyOrBlankToNull()
        )

private fun String.emptyOrBlankToNull(): String? =
        if (isEmpty() || isBlank()) null else this