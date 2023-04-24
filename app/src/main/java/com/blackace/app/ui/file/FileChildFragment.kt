package com.blackace.app.ui.file

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blackace.R
import com.blackace.app.base.BaseFragment
import com.blackace.data.entity.FileBean
import com.blackace.data.state.SimpleActionState
import com.blackace.databinding.FragmentChildFileBinding
import com.blackace.databinding.ItemLocalFileBinding
import com.blackace.util.ext.baseActivity
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup

/**
 *
 * @author: magicHeimdall
 * @create: 2022/12/17 下午11:55
 */
class FileChildFragment : BaseFragment(R.layout.fragment_child_file) {

    private val binding by viewBinding(FragmentChildFileBinding::bind)

    private val fileViewModel by viewModels<FileViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initViewModel()
        initData()
        baseActivity().addBackCallback(lifecycle, callback = this::onBack)
    }

    private fun initData() {
        val path = requireArguments().getString(FLAG_PATH) ?: "/"
        fileViewModel.loadFileList(path)
    }

    @SuppressLint("SetTextI18n")
    private fun initRecyclerView() {
        binding.recyclerView.linear().setup {
            addType<FileBean>(R.layout.item_local_file)
            onBind {
                val model = getModel<FileBean>()
                val binding = getBinding<ItemLocalFileBinding>()

                binding.ivIcon.setImageResource(getIcon(model))
                binding.tvTitle.text = model.name
                binding.tvInfo.text = "${model.lastTime} ${model.size ?: ""}"
            }
            onClick(R.id.fileParent) {
                val model = getModel<FileBean>()
                if (model.path.isNullOrEmpty()) {
                    onBack()
                } else if (model.size.isNullOrEmpty()) {
                    //文件夹
                    nextFragment(model.path)
                } else {
                    //文件
                    (requireActivity() as FileDecoder).fileClick(model.path)
                }
            }
        }
    }


    private fun initViewModel() {
        fileViewModel.fileListLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is SimpleActionState.Loading -> {
                    binding.stateView.showLoading()
                }

                is SimpleActionState.Fail->{
                    //nothing to do
                }

                is SimpleActionState.Success -> {
                    binding.recyclerView.models = it.bean
                    binding.stateView.showContent()
                }
            }
        }
    }

    private fun getIcon(model: FileBean): Int {
        return when {
            model.size == null -> R.drawable.ic_folder
            model.name.endsWith(".apk") -> R.drawable.ic_android
            model.name.endsWith(".jks") -> R.drawable.ic_key
            model.name.endsWith(".bks") -> R.drawable.ic_key
            else -> R.drawable.ic_file
        }
    }


    private fun nextFragment(path: String) {
        val fragment = create(path)
        parentFragmentManager.commit {
            add(R.id.fragmentFile, fragment)
            addToBackStack(path)
        }
    }

    private fun onBack(): Boolean {
        if (!isResumed) {
            return false
        }

        if (parentFragmentManager.backStackEntryCount == 1) {
            requireActivity().finish()
        } else {
            parentFragmentManager.popBackStack()
        }
        return true
    }


    companion object {

        val FLAG_PATH = "flag_path"

        fun create(path: String?): FileChildFragment {
            val fragment = FileChildFragment()
            fragment.arguments = bundleOf(FLAG_PATH to path)
            return fragment
        }
    }
}
