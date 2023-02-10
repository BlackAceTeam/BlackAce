package com.blackace.util.ext

import android.text.Editable
import androidx.core.widget.addTextChangedListener
import com.blackace.R
import com.google.android.material.textfield.TextInputLayout

/**
 *
 * @author: magicHeimdall
 * @create: 7/2/2023 8:43 PM
 */
fun TextInputLayout.autoClearError(block: ((Editable?) -> Unit)? = null) {
    this.editText?.addTextChangedListener {
        this.error = null
        this.isErrorEnabled = false
        block?.invoke(it)
    }
}


fun TextInputLayout.emptyWatcherWithHint() {
    this.editText?.addTextChangedListener {
        if (it?.length == 0) {
            this.isErrorEnabled = true
            this.error = getString(R.string.input_no_null_with_hint, this.hint.toString())
        } else {
            this.error = null
            this.isErrorEnabled = false
        }
    }
}
