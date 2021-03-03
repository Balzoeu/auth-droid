package eu.balzo.authdroid

import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

abstract class BaseActivity : FragmentActivity {

    constructor() : super()
    constructor(contentLayoutId: Int) : super(contentLayoutId)

    fun <T> CoroutineScope.launchSafe(block: suspend () -> T) {

        launch(
                CoroutineExceptionHandler { _, throwable ->
                    throwable.logError(this@BaseActivity)
                }
        ) {
            block()
        }

    }

}