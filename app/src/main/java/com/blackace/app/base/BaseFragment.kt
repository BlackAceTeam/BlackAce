package com.blackace.app.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 *
 * @author: magicHeimdall
 * @create: 2022/12/17 下午11:55
 */
open class BaseFragment(private val layoutID: Int) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutID, container, false)
    }

    open fun onBack(): Boolean {
        return true
    }
}
