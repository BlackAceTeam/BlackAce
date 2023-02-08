package com.blackace.util

import android.widget.Toast
import com.blackace.util.holder.ContextHolder

/**
 *
 * @author: magicHeimdall
 * @create: 8/2/2023 11:06 AM
 */
object ToastUtil {

    private var toast: Toast? = null

    fun showToast(msg: String) {
        toast?.cancel()
        toast = Toast.makeText(ContextHolder.get(), msg, Toast.LENGTH_SHORT)
        toast?.show()
    }

    fun showToast(id:Int){
        showToast(getString(id))
    }
}
