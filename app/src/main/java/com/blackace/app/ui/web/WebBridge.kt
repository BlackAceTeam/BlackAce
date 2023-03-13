package com.blackace.app.ui.web

import android.os.Build
import android.os.Environment
import android.webkit.JavascriptInterface
import androidx.lifecycle.lifecycleScope
import com.blackace.R
import com.blackace.util.ToastUtil
import com.blackace.util.ext.getString
import com.blackace.util.ext.log
import com.blackace.util.ext.nowStr
import com.blackace.util.ext.str
import com.blackace.util.holder.ContextHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.File
import java.util.Date

/**
 *
 * @author: magicHeimdall
 * @create: 9/3/2023 3:06 PM
 */
class WebBridge(private val activity: WebActivity) {

    @JavascriptInterface
    fun exportSecret(secret: String, appName: String, start: String, count: String) {
        activity.lifecycleScope.launch(Dispatchers.Main) {
            activity.showLoadingDialog()
            val file = withContext(Dispatchers.IO) {

                val startIndex = start.toIntOrNull() ?: 0
                val endIndex = startIndex + (count.toIntOrNull() ?: 0)
                val name = "$appName-$startIndex-$endIndex--${Date().str("MM-dd")}.txt"

                val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    File(Environment.getExternalStorageDirectory(), "Download/$name")
                } else {
                    File(ContextHolder.get().getExternalFilesDir("secret"), name)
                }

                val json = JSONArray(secret)
                val sb = StringBuilder()
                for (index in 0 until json.length()) {
                    val item = json.getJSONObject(index)
                    sb.append(item.getString("secret"))
                    sb.append(" , ")
                    sb.append(item.getInt("time"))
                    sb.append(getString(R.string.hour))
                    sb.append(" , ")
                    val use = if (item.getInt("status") == 0) {
                        getString(R.string.unuse)
                    } else {
                        getString(R.string.used)
                    }
                    sb.append(use)


                    sb.append("\n")
                }

                file.writeText(sb.toString())
                file
            }

            activity.dismissLoadingDialog()
            ToastUtil.showToast(getString(R.string.export_success, file.absolutePath))
        }

    }
}
