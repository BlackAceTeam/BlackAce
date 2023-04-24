package com.blackace.app.ui.local.task

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blackace.R
import com.blackace.app.base.BaseBottomDialogFragment
import com.blackace.app.ui.local.LocalViewModel
import com.blackace.data.config.AceConfig
import com.blackace.data.entity.AppBean
import com.blackace.data.entity.http.FeatureBean
import com.blackace.data.state.SimpleActionState
import com.blackace.databinding.FragmentTaskCreateBinding
import com.blackace.databinding.ItemDialogFeatureBinding
import com.blackace.util.ToastUtil
import com.blackace.util.ext.emptyWatcherWithHint
import com.drake.brv.BindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup

/**
 *
 * @author: magicHeimdall
 * @create: 9/2/2023 4:09 PM
 */
class TaskCreateFragment : BaseBottomDialogFragment(R.layout.fragment_task_create) {

    private val binding by viewBinding(FragmentTaskCreateBinding::bind)

    private val viewModel by activityViewModels<LocalViewModel>()

    private lateinit var mAdapter: BindingAdapter

    private var appBean: AppBean? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initRecyclerView()
        initView()
    }

    private fun initView() {
        binding.versionNameLayout.emptyWatcherWithHint()
        binding.versionCodeLayout.emptyWatcherWithHint()
        binding.nameLayout.emptyWatcherWithHint()
        binding.btnConfirm.setOnClickListener {
            if (binding.versionNameLayout.isErrorEnabled || binding.versionCodeLayout.isErrorEnabled || binding.nameLayout.isErrorEnabled) {
                return@setOnClickListener
            }
            val tmpAppBean = appBean ?: return@setOnClickListener

            if (AceConfig.getUser() == null) {
                ToastUtil.showToast(R.string.login_pls)
                dismiss()
                return@setOnClickListener
            }
            tmpAppBean.version = binding.versionNameEdit.text.toString()
            tmpAppBean.versionCode = binding.versionCodeEdit.text.toString()
            tmpAppBean.name = binding.nameEdit.text.toString()
            val feature = mAdapter.getCheckedModels<FeatureBean>().joinToString(",") { it.key }
            viewModel.createTask(tmpAppBean, feature)
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun initRecyclerView() {
        mAdapter = binding.recyclerView.linear().setup {
            addType<FeatureBean>(R.layout.item_dialog_feature)

            onBind {
                val binding = getBinding<ItemDialogFeatureBinding>()
                val bean = getModel<FeatureBean>()
                binding.tvDesc.text = bean.desc
                binding.tvTitle.text = bean.title
                binding.checkbox.isChecked = bean.isCheck
            }

            onChecked { position, checked, _ ->
                val model = getModel<FeatureBean>(position)
                model.isCheck = checked
                notifyItemChanged(position)
            }

            onClick(R.id.featureParent) {
                val checked = getModel<FeatureBean>().isCheck
                setChecked(bindingAdapterPosition, !checked)
            }

            onClick(R.id.checkbox) {
                setChecked(bindingAdapterPosition, !getModel<FeatureBean>().isCheck)
            }
        }
    }

    private fun initViewModel() {
        viewModel.featureState.observe(viewLifecycleOwner) {
            if (it !is SimpleActionState.Success) {
                return@observe
            }

            val tmpAppBean = it.bean.second
            appBean = tmpAppBean
            binding.recyclerView.models = it.bean.first
            binding.ivIcon.setImageDrawable(tmpAppBean.icon)
            binding.nameEdit.setText(tmpAppBean.name)
            binding.versionNameEdit.setText(tmpAppBean.version)
            binding.versionCodeEdit.setText(tmpAppBean.versionCode)

        }
    }
}
