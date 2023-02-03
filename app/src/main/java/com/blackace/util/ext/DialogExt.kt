package com.blackace.util.ext

import android.content.Context
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.blackace.R

/**
 *
 * @author: magicHeimdall
 * @create: 5/1/2023 8:54 PM
 */
fun Context.showConfirmDialog(title: Int, message: Int, block: () -> Unit) {
    MaterialDialog(this).show {
        title(title)
        message(message)
        positiveButton(R.string.done) {
            block.invoke()
        }
        negativeButton(R.string.cancel)
    }
}
fun Fragment.showConfirmDialog(title: Int, message: Int, block: () -> Unit) {
    requireContext().showConfirmDialog(title, message,  block)
}
