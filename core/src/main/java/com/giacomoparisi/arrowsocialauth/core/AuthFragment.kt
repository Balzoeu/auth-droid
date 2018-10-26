package com.giacomoparisi.arrowsocialauth.core

import androidx.fragment.app.Fragment

abstract class AuthFragment: Fragment() {

    companion object {
        const val SIGN_IN_TASK = 1
    }
}