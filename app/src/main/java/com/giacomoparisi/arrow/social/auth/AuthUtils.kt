package com.giacomoparisi.arrow.social.auth

import androidx.fragment.app.FragmentActivity
import com.giacomoparisi.arrow.social.auth.core.AuthResult
import com.giacomoparisi.arrow.social.auth.core.Cancelled
import com.giacomoparisi.arrow.social.auth.core.Completed
import com.giacomoparisi.arrow.social.auth.core.Failed
import com.giacomoparisi.kotlin.functional.extensions.android.toast.showLongToast
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.launch

fun AuthResult.showMessage(activity: FragmentActivity) {
    CoroutineScope(Dispatchers.Main).launch {
        when (this@showMessage) {
            is Cancelled -> activity.showLongToast("Cancelled")
            is Completed -> activity.showLongToast(this@showMessage.user.toString())
            is Failed -> activity.showLongToast(this@showMessage.throwable.message.orEmpty())
        }
    }
}