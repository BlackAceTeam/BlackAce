package com.blackace.app.ui.main

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.blackace.R
import com.blackace.data.entity.db.SignBean
import com.blackace.data.entity.http.TaskBean
import com.blackace.data.state.PackageState
import com.blackace.databinding.FragmentPackageConfigBinding
import com.blackace.util.ext.log

/**
 *
 * @author: magicHeimdall
 * @create: 6/1/2023 4:46 PM
 */
class PackageFragment : DialogFragment() {

    private lateinit var binding: FragmentPackageConfigBinding

    private val viewModel by activityViewModels<MainViewModel>()

    private val signList = arrayListOf<SignBean>()

    private val modelList by lazy {
        listOf(
            "V1 + V2 + V3" to 28,
            "V1 + V2" to 12,
            "V1 + V3" to 20,
            "V1" to 4,
            "V2 + V3 (Android7.0+)" to 24,
            "V2 (Android7.0+)" to 8,
            "V3 (Android7.0+)" to 16,
        )
    }

    private var signBean: SignBean? = null
    private var signModel = 28
    private var taskBean: TaskBean? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog =
            MaterialDialog(requireContext())
                .title(R.string.package_apk)
                .customView(R.layout.fragment_package_config)
                .positiveButton(R.string.start_package) {
                    if (taskBean == null || signBean == null) {
                        return@positiveButton
                    }
                    viewModel.packageApk(taskBean!!,signBean!!,signModel)
                }
                .negativeButton(R.string.cancel)
        binding = FragmentPackageConfigBinding.bind(dialog.getCustomView())
        initView()
        initViewModel()
        return dialog
    }

    private fun initView() {
        binding.llStore.setOnClickListener {
            val popup = PopupMenu(requireContext(), binding.ivStore)
            popup.menu.apply {
                signList.forEachIndexed { index, signBean ->
                    add(index, index, index, signBean.name)
                }
            }
            popup.setOnMenuItemClickListener {
                signBean = signList[it.itemId]
                binding.tvStoreImpl.text = signBean?.name
                true
            }
            popup.show()
        }
        binding.tvModelImpl.text = modelList[0].first
        binding.llModel.setOnClickListener {
            val popup = PopupMenu(requireContext(), binding.ivModel)
            popup.menu.apply {
                modelList.forEachIndexed { index, pair ->
                    add(index, index, index, pair.first)
                }
            }
            popup.setOnMenuItemClickListener {
                val bean = modelList[it.itemId]
                signModel = bean.second
                binding.tvModelImpl.text = bean.first
                true
            }
            popup.show()
        }
    }


    private fun initViewModel() {
        viewModel.packageState.observe(this) {
            if (it is PackageState.LoadSignSuccess) {
                taskBean = it.model
                signList.clear()
                signList.addAll(it.list)
                signBean = it.list[0]
            }
        }
    }

}
