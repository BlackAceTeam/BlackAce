package com.blackace.util.ext

import android.content.Intent
import android.net.Uri
import com.blackace.R
import com.blackace.app.base.BaseActivity

/**
 *
 * @author: magicHeimdall
 * @create: 21/4/2023 3:26 PM
 */


fun BaseActivity.startBrowser(url: String?) {
    if (url.isNullOrEmpty()) {
        showSnackBar("Url is Empty")
        return
    }
    runCatching {
        val uri = Uri.parse(url)
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.data = uri
        startActivity(intent)
    }.onFailure {
        showSnackBar(getString(R.string.open_fail))
    }
}
