package com.blackace.app.ui.local.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.afollestad.materialdialogs.MaterialDialog
import com.blackace.R
import com.blackace.app.base.BaseFragment
import com.blackace.app.ui.local.LocalViewModel
import com.blackace.data.entity.AppBean
import com.blackace.data.state.LocalAppState
import com.blackace.databinding.FragmentAppBinding
import com.blackace.databinding.ItemLocalAppBinding
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup

/**
 *
 * @author: magicHeimdall
 * @create: 2022/12/17 下午11:55
 */
class AppFragment : BaseFragment(R.layout.fragment_app) {

    private val binding by viewBinding(FragmentAppBinding::bind)

    private val viewModel by activityViewModels<LocalViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initViewModel()
    }

    @SuppressLint("SetTextI18n")
    private fun initRecyclerView() {
        binding.recyclerView.linear().setup {
            addType<AppBean>(R.layout.item_local_app)
            onBind {
                val binding = getBinding<ItemLocalAppBinding>()
                val model = getModel<AppBean>()
                binding.ivIcon.setImageDrawable(model.icon)
                binding.tvTitle.text = model.name
                binding.tvInfo.text = "${model.version} ${model.size}"
                binding.tvPkg.text = model.pkg
            }
            onClick(R.id.appParent) {
                val model = getModel<AppBean>()
                if (model.isSplitApk) {
                    MaterialDialog(requireContext()).show {
                        title(R.string.task_create_fail)
                        message(R.string.not_support_apks)
                        positiveButton(R.string.done)
                    }
                } else {
                    viewModel.loadFeatures(model)

                }
            }
        }
    }

    private fun initViewModel() {
        viewModel.appListLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is LocalAppState.Loading -> {
                    binding.stateView.showLoading()
                }

                is LocalAppState.Success -> {
                    binding.recyclerView.models = it.list
                    binding.stateView.showContent()
                }
            }
        }

    }


    companion object {

        fun create(): AppFragment {
            return AppFragment()
        }
    }
}
