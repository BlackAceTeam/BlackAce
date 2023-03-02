package com.blackace.app.base

import androidx.fragment.app.Fragment
import com.blackace.util.ext.baseActivity

/**
 *
 * @author: magicHeimdall
 * @create: 2022/12/17 下午11:55
 */
open class BaseFragment(private val layoutID: Int) : Fragment(layoutID) {

    fun showSnackBar(id: Int) {
        showSnackBar(getString(id))
    }

    fun showSnackBar(msg: String) {
        baseActivity().showSnackBar(msg)
    }

}
