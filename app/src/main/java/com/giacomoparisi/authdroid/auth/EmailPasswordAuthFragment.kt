package com.giacomoparisi.authdroid.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import arrow.core.None
import arrow.core.Option
import arrow.core.some
import com.giacomoparisi.authdroid.auth.core.firebase.signInWithFirebaseEmailPassword
import com.giacomoparisi.authdroid.auth.core.firebase.signUpWithFirebaseEmailPassword
import com.giacomoparisi.kotlin.functional.extensions.arrow.option.getOrEmpty
import kotlinx.android.synthetic.main.email_password_auth.*

class EmailPasswordAuthFragment : Fragment() {

    private var email: Option<String> = None
    private var password: Option<String> = None

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? =
            inflater.inflate(R.layout.email_password_auth, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.email_field.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                this@EmailPasswordAuthFragment.email = s.toString().some()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        this.password_field.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                this@EmailPasswordAuthFragment.password = s.toString().some()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        this.sign_up.setOnClickListener {
            signUpWithFirebaseEmailPassword(
                    email.getOrEmpty(),
                    password.getOrEmpty())
                    .await(this.requireActivity())
        }

        this.sign_in.setOnClickListener {
            signInWithFirebaseEmailPassword(
                    email.getOrEmpty(),
                    password.getOrEmpty())
                    .await(this.requireActivity())
        }
    }
}