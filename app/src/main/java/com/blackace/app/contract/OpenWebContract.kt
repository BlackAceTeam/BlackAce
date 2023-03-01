package com.blackace.app.contract

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.blackace.app.ui.web.WebActivity

/**
 *
 * @author: magicHeimdall
 * @create: 1/3/2023 5:01 PM
 */

data class WebViewParam(val url: String, val enableToolbar: Boolean = true)

class OpenWebContract : ActivityResultContract<WebViewParam, Unit>() {

    override fun createIntent(context: Context, input: WebViewParam): Intent {
        val intent = Intent(context, WebActivity::class.java)
        intent.putExtra(WebActivity.INTENT_URL, input.url)
        intent.putExtra(WebActivity.INTENT_ENABLE_TOOLBAR, input.enableToolbar)
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?) {

    }
}
