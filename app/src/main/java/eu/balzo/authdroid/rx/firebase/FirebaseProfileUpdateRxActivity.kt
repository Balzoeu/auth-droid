package eu.balzo.authdroid.rx.firebase

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.balzo.authdroid.auth.R
import eu.balzo.authdroid.logError
import eu.balzo.authdroid.rx.firebase.updateFirebaseProfile
import eu.balzo.authdroid.showToast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.firebase_password_update.update
import kotlinx.android.synthetic.main.firebase_profile_update.*

class FirebaseProfileUpdateRxActivity : FragmentActivity() {

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        this.setContentView(R.layout.firebase_profile_update)

        this.update.setOnClickListener {
            updateFirebaseProfile(this.name_field.text.toString(), this.photo_field.text.toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ this.showToast("Done") }) { it.logError(this) }
        }
    }
}