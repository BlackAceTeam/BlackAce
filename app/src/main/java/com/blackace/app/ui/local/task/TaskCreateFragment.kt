package com.blackace.app.ui.local.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blackace.R
import com.blackace.app.base.BaseBottomDialogFragment
import com.blackace.app.ui.local.LocalViewModel
import com.blackace.data.entity.AppBean
import com.blackace.data.entity.http.FeatureBean
import com.blackace.data.entity.http.TaskBean
import com.blackace.data.state.FeatureState
import com.blackace.databinding.FragmentTaskCreateBinding
import com.blackace.databinding.ItemDialogFeatureBinding
import com.blackace.util.ext.emptyWatcherWithHint
import com.drake.brv.BindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

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
            if (it !is FeatureState.Success) {
                return@observe
            }

            appBean = it.appBean
            binding.recyclerView.models = it.list
            binding.ivIcon.setImageDrawable(it.appBean.icon)
            binding.nameEdit.setText(it.appBean.name)
            binding.versionNameEdit.setText(it.appBean.version)
            binding.versionCodeEdit.setText(it.appBean.versionCode)

        }
    }
}
