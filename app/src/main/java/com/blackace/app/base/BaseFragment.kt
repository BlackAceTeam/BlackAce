package com.blackace.app.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.blackace.util.ext.baseActivity

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    fun showSnackBar(id: Int) {
        showSnackBar(getString(id))
    }

    fun showSnackBar(msg: String) {
        baseActivity().showSnackBar(msg)
    }

    fun registerOnBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            onBack()
        }
    }

    open fun onBack() {

    }
}
