package com.giacomoparisi.authdroid

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.giacomoparisi.authdroid.auth.R
import com.giacomoparisi.authdroid.rx.firebase.signInWithFirebaseEmailPassword
import com.giacomoparisi.authdroid.rx.firebase.signUpWithFirebaseEmailPassword
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.email_password_auth.*

class EmailPasswordAuthFragment : Fragment() {

    private var email: String = ""
    private var password: String = ""

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
                this@EmailPasswordAuthFragment.email = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        this.password_field.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                this@EmailPasswordAuthFragment.password = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        this.sign_up.setOnClickListener {
            signUpWithFirebaseEmailPassword(
                    email,
                    password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ it.log(this.requireContext()) }) { it.logError(this.requireContext()) }
        }

        this.sign_in.setOnClickListener {
            signInWithFirebaseEmailPassword(
                    email,
                    password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ it.log(this.requireContext()) }) { it.logError(this.requireContext()) }
        }
    }
}