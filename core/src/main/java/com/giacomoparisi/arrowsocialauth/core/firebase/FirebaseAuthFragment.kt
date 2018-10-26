package com.giacomoparisi.arrowsocialauth.core.firebase

import android.os.Bundle
import com.giacomoparisi.arrowsocialauth.core.AuthFragment
import com.google.firebase.auth.FirebaseAuth

abstract class FirebaseAuthFragment : AuthFragment() {

    protected lateinit var auth: FirebaseAuth

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        auth = FirebaseAuth.getInstance()
    }
}
