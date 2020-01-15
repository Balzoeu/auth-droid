package eu.balzo.authdroid.facebook.core

import android.content.Intent
import androidx.fragment.app.Fragment
import com.facebook.CallbackManager
import com.facebook.login.LoginManager

class FacebookFragment : Fragment() {

    val callbackManager: CallbackManager = CallbackManager.Factory.create()

    override fun onStart() {
        super.onStart()

        LoginManager.getInstance()
                .logInWithReadPermissions(
                        this,
                        listOf("email", "public_profile")
                )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        this.callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        val TAG: String = FacebookFragment::class.java.simpleName
    }
}