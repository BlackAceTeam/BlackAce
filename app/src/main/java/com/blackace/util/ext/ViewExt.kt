package com.blackace.util.ext

import android.view.View

/**
 *
 * @author: magicHeimdall
 * @create: 2022/12/18 上午2:12
 */
fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide(visibility: Int = View.GONE) {
    this.visibility = visibility
}

fun View.show(any: Any?) {
    when (any) {
        is Boolean -> {
            if (any) {
                this.show()
            } else {
                this.hide()
            }
        }

        is String -> {
            show(any.isNotEmpty())
        }

        else -> {
            show(any != null)
        }
    }
}
