package com.blackace.app.ui.main

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import com.afollestad.materialdialogs.MaterialDialog
import com.blackace.R
import com.blackace.app.base.BaseFragment
import com.blackace.app.contract.TaskCreateContract
import com.blackace.data.entity.http.TaskBean
import com.blackace.data.state.TaskListState
import com.blackace.databinding.FragmentTaskListBinding
import com.blackace.databinding.ItemMainTaskBinding
import com.blackace.util.ext.*
import com.drake.brv.BindingAdapter
import com.drake.brv.listener.ItemDifferCallback
import com.drake.brv.utils.*
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress

/**
 *
 * @author: magicHeimdall
 * @create: 6/2/2023 11:09 AM
 */
class TaskListFragment : BaseFragment(R.layout.fragment_task_list) {

    private val binding by viewBinding(FragmentTaskListBinding::bind)

    private val viewModel by activityViewModels<MainViewModel>()

    private lateinit var mAdapter: BindingAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initRecyclerView()
        initFab()
        baseActivity().addBackCallback(lifecycle, callback = this::onBack)
    }

    private fun initViewModel() {
        viewModel.userState.observe(viewLifecycleOwner) {
            lifecycleScope.launchWhenResumed {
                binding.pageRefresh.refreshing()
            }
        }

        viewModel.taskState.observe(viewLifecycleOwner) {
            when (it) {
                is TaskListState.FirstSuccess -> {
                    if (it.list.isEmpty()) {
                        binding.pageRefresh.showEmpty(getString(R.string.no_task))
                        binding.pageRefresh.finishRefreshWithNoMoreData()
                    } else {
                        binding.recyclerView.models = it.list
                        viewModel.updateState(binding.recyclerView.models)
                        binding.pageRefresh.showContent(it.list.size >= 20)
                        binding.pageRefresh.finishRefresh(0, true, it.hasMore)

                    }
                }

                is TaskListState.FirstFail -> {
                    binding.pageRefresh.finishRefresh(false)
                    binding.pageRefresh.showError(it.msg, true)
                }

                is TaskListState.MoreSuccess -> {
                    binding.recyclerView.addModels(it.list)
                    viewModel.updateState(binding.recyclerView.models)
                    binding.pageRefresh.showContent(it.list.size >= 20)
                    binding.pageRefresh.finishLoadMore(0, true, it.hasMore)
                }

                is TaskListState.MoreFail -> {
                    showSnackBar(it.msg)
                    binding.pageRefresh.showContent()
                    binding.pageRefresh.finishLoadMore(false)
                }

                is TaskListState.StateUpdate -> {
                    binding.recyclerView.setDifferModels(it.list, false) {
                        viewModel.updateState(binding.recyclerView.models)
                    }
                }

                is TaskListState.SimpleFail -> {
                    dismissLoadingDialog()
                    showSnackBar(it.msg)
                }

                is TaskListState.Delete -> {
                    dismissLoadingDialog()
                    exitToggleMode()
                    viewModel.loadTaskList("")
                }
            }
        }
    }

    private fun initFab() {
        binding.fab.setOnClickListener {
            if (mAdapter.toggleMode) {
                MaterialDialog(requireContext()).show {
                    title(R.string.delete_task)
                    message(R.string.delete_task_hint)
                    negativeButton(R.string.cancel)
                    positiveButton(R.string.done) {
                        viewModel.taskDelete(mAdapter.getCheckedModels())
                    }
                }
            } else {
                createTaskContract.launch(Unit)
            }
        }
    }

    private fun initRecyclerView() {
        mAdapter = binding.recyclerView.linear().setup {
            addType<TaskBean>(R.layout.item_main_task)
            onCreate {
                val binding = getBinding<ItemMainTaskBinding>()
                bindProgressButton(binding.progressButton)
            }

            onBind {
                val binding = getBinding<ItemMainTaskBinding>()
                val model = getModel<TaskBean>()
                binding.ivIcon.load(model.icon)
                binding.tvTitle.text = model.apkName
                binding.tvPkg.text = model.apkPkg
                binding.tvInfo.text = model.versionName
                if (toggleMode) {
                    bindToggleRecyclerViewItem(binding, model)
                } else {
                    bindRecyclerViewItem(binding, model)
                }
            }

            onClick(R.id.progressButton) {
                val model = getModel<TaskBean>()
                packageApk(model)
            }

            onClick(R.id.taskParent) {
                if (toggleMode) {
                    val model = getModel<TaskBean>()
                    setChecked(modelPosition, !model.isCheck)
                }
            }

            onLongClick(R.id.taskParent) {
                if (!toggleMode) {
                    toggle()
                    setChecked(layoutPosition, true)
                    binding.fab.setImageResource(R.drawable.ic_delete)
                }
            }

            onToggle { position, _, _ ->
                notifyItemChanged(position)
            }

            onChecked { position, checked, _ ->
                val model = getModel<TaskBean>(position)
                model.isCheck = checked
                notifyItemChanged(position)
            }

            itemDifferCallback = this@TaskListFragment.itemDifferCallback
        }

        binding.pageRefresh.onRefresh {
            viewModel.loadTaskList("")
        }

        binding.pageRefresh.onLoadMore {
            viewModel.loadTaskList()
        }

    }

    private fun bindRecyclerViewItem(binding: ItemMainTaskBinding, model: TaskBean) {
        binding.checkbox.hide()
        if (model.status == TaskBean.STATE_APK_DELETE) {
            binding.progressButton.hide()
        } else {
            binding.progressButton.show()
            when (model.status) {
                TaskBean.STATE_SUCCESS -> {
                    binding.progressButton.setBackgroundColor(getColor(R.color.progress_btn_green))
                    binding.progressButton.hideProgress(R.string.task_success)
                }
                TaskBean.STATE_WAIT -> {
                    binding.progressButton.setBackgroundColor(getColor( R.color.progress_btn_blue))
                    binding.progressButton.hideProgress(R.string.task_wait)
                }
                TaskBean.STATE_FAIL -> {
                    binding.progressButton.setBackgroundColor(getColor( R.color.progress_btn_red))
                    binding.progressButton.hideProgress(R.string.task_error)
                }
                TaskBean.STATE_LOADING -> {
                    binding.progressButton.setBackgroundColor(getColor( R.color.progress_btn_blue))
                    binding.progressButton.showProgress {
                        progressColor = Color.WHITE
                    }
                }
            }
        }
    }

    private fun bindToggleRecyclerViewItem(binding: ItemMainTaskBinding, model: TaskBean) {
        binding.progressButton.hide()
        binding.checkbox.show()
        binding.checkbox.isChecked = model.isCheck
    }

    private val itemDifferCallback by lazy {
        object : ItemDifferCallback {
            override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
                if (oldItem !is TaskBean || newItem !is TaskBean) {
                    return false
                }
                return oldItem.taskNo == newItem.taskNo
            }

            override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
                return oldItem == newItem
            }

            override fun getChangePayload(oldItem: Any, newItem: Any): Any {
                return 1
            }
        }
    }

    private fun packageApk(model: TaskBean) {
        when (model.status) {
            TaskBean.STATE_LOADING, TaskBean.STATE_WAIT -> {
                showSnackBar(R.string.task_processing)
            }

            TaskBean.STATE_FAIL -> {
                showSnackBar(R.string.task_process_fail)
            }

            TaskBean.STATE_SUCCESS -> {
                viewModel.loadSignList(model)
            }
        }
    }

    private fun exitToggleMode() {
        mAdapter.toggle(false)
        mAdapter.checkedAll(false)
        binding.fab.setImageResource(R.drawable.ic_add)
    }

    private val createTaskContract = registerForActivityResult(TaskCreateContract()) {
        if (it) {
            binding.pageRefresh.refresh()
        }
    }

    private fun onBack(): Boolean {
        if (mAdapter.toggleMode) {
            exitToggleMode()
            return true
        }
        return false
    }


}
