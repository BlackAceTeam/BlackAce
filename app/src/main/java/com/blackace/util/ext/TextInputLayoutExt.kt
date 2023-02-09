package com.blackace.util.ext

import android.text.Editable
import android.widget.EditText
import android.widget.FrameLayout
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout

/**
 *
 * @author: magicHeimdall
 * @create: 7/2/2023 8:43 PM
 */

fun TextInputLayout.autoClearError(block: ((Editable?) -> Unit)? = null) {
    val frameLayout = getChildAt(0) as FrameLayout
    val editText = frameLayout.getChildAt(0)
    if (editText !is EditText) {
        return
    }

    editText.addTextChangedListener {
        this.error = null
        this.isErrorEnabled = false
        block?.invoke(it)
    }
}
