package eu.balzo.authdroid.google.core

import arrow.core.None
import arrow.core.Option
import arrow.core.some
import arrow.core.toOption
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import eu.balzo.authdroid.core.SocialAuthUser

fun GoogleSignInAccount.toSocialAuthUser(): SocialAuthUser =
        SocialAuthUser(
                id.orEmpty(),
                idToken.orEmpty(),
                serverAuthCode.emptyOrBlankToNone(),
                displayName.emptyOrBlankToNone(),
                displayName?.split(" ")?.getOrNull(0).emptyOrBlankToNone(),
                displayName?.split(" ")?.getOrNull(1).emptyOrBlankToNone(),
                email.emptyOrBlankToNone(),
                photoUrl?.toString().emptyOrBlankToNone()
        )

private fun String?.emptyOrBlankToNone(): Option<String> =
        toOption().flatMap { if (it.isEmpty() || it.isBlank()) None else it.some() }