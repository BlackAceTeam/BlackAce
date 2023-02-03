package com.blackace.util.ext

import androidx.fragment.app.Fragment
import com.blackace.app.base.BaseActivity

/**
 *
 * @author: magicHeimdall
 * @create: 2022/12/17 下午11:55
 */
fun Fragment.baseActivity(): BaseActivity {
    return this.requireActivity() as BaseActivity
}

inline fun <reified T> Fragment.attachActivity(): T {
    return this.requireActivity() as T
}

fun Fragment.showLoadingDialog(){
    baseActivity().showLoadingDialog()
}

fun Fragment.dismissLoadingDialog(){
    baseActivity().dismissLoadingDialog()
}
