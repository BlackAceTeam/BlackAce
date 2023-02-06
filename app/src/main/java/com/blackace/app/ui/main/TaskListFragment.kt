package com.blackace.app.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import com.blackace.R
import com.blackace.app.base.BaseFragment
import com.blackace.app.contract.TaskCreateContract
import com.blackace.data.entity.http.TaskBean
import com.blackace.data.state.TaskListState
import com.blackace.databinding.FragmentTaskListBinding
import com.blackace.databinding.ItemMainTaskBinding
import com.blackace.util.ext.*
import com.drake.brv.listener.ItemDifferCallback
import com.drake.brv.utils.*

/**
 *
 * @author: magicHeimdall
 * @create: 6/2/2023 11:09 AM
 */
class TaskListFragment : BaseFragment(R.layout.fragment_task_list) {

    private val binding by viewBinding(FragmentTaskListBinding::bind)

    private val viewModel by activityViewModels<MainViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initRecyclerView()
        initFab()
    }

    private fun initViewModel() {
        viewModel.userState.observe(viewLifecycleOwner) {
            binding.pageRefresh.refreshing()
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
                    binding.recyclerView.mutable.removeAt(it.position)
                    binding.recyclerView.adapter?.notifyItemRemoved(it.position)
                }
            }
        }
    }

    private fun initRecyclerView() {
        binding.recyclerView.linear().setup {
            addType<TaskBean>(R.layout.item_main_task)
            onBind {
                val binding = getBinding<ItemMainTaskBinding>()
                val model = getModel<TaskBean>()
                binding.ivIcon.load(model.icon)
                binding.tvTitle.text = model.apkName
                binding.tvPkg.text = model.apkPkg
                binding.tvInfo.text = model.versionName
                if (model.status == TaskBean.STATE_APK_DELETE) {
                    binding.progressButton.hide()
                } else {
                    binding.progressButton.show()
                    binding.progressButton.isIndeterminateProgressMode = true
                    when (model.status) {
                        TaskBean.STATE_SUCCESS -> {
                            binding.progressButton.progress = 100
                        }
                        TaskBean.STATE_WAIT -> {
                            binding.progressButton.progress = 0
                        }
                        TaskBean.STATE_FAIL -> {
                            binding.progressButton.progress = -1
                        }
                        TaskBean.STATE_LOADING -> {
                            binding.progressButton.progress = 50
                        }
                    }
                }
            }

            onClick(R.id.progressButton) {
                val model = getModel<TaskBean>()
                packageApk(model)
            }

            onLongClick(R.id.taskParent) {
                showConfirmDialog(R.string.delete_task, R.string.delete_task_hint) {
                    val model = getModel<TaskBean>()
                    showLoadingDialog()
                    viewModel.taskDelete(model, bindingAdapterPosition)
                }
            }

            itemDifferCallback = object : ItemDifferCallback {
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

        binding.pageRefresh.onRefresh {
            viewModel.loadTaskList("")
        }

        binding.pageRefresh.onLoadMore {
            viewModel.loadTaskList()
        }

    }

    private fun initFab() {
        binding.fab.setOnClickListener {
            createTaskContract.launch(Unit)
        }
    }

    private fun packageApk(model: TaskBean) {
        when (model.status) {
            TaskBean.STATE_LOADING, TaskBean.STATE_WAIT -> {
                showSnackBar(R.string.task_processing)
//                viewModel.loadSignList(model)
            }

            TaskBean.STATE_FAIL -> {
                showSnackBar(R.string.task_processing)
            }

            TaskBean.STATE_SUCCESS -> {
                viewModel.loadSignList(model)
            }
        }
    }

    private val createTaskContract = registerForActivityResult(TaskCreateContract()) {
        if (it) {
            binding.pageRefresh.refresh()
        }
    }
}
