package com.blackace.app.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import com.afollestad.materialdialogs.MaterialDialog
import com.blackace.R
import com.blackace.app.base.BaseActivity
import com.blackace.app.contract.ResultCodes
import com.blackace.app.ui.account.AccountActivity
import com.blackace.app.ui.local.LocalActivity
import com.blackace.app.ui.sign.SignManagerActivity
import com.blackace.data.config.AceConfig
import com.blackace.data.entity.http.TaskBean
import com.blackace.data.state.PackageState
import com.blackace.data.state.TaskListState
import com.blackace.data.state.UserState
import com.blackace.databinding.ActivityMainBinding
import com.blackace.databinding.ItemMainTaskBinding
import com.blackace.databinding.ViewMainNavigationHeaderBinding
import com.blackace.util.ext.hide
import com.blackace.util.ext.log
import com.blackace.util.ext.show
import com.blackace.util.ext.showConfirmDialog
import com.drake.brv.listener.ItemDifferCallback
import com.drake.brv.utils.*


/**
 *
 * @author: magicHeimdall
 * @create: 2022/12/16 下午9:41
 */
class MainActivity : BaseActivity() {

    private val binding by viewBinding(ActivityMainBinding::bind)

    private val viewModel by viewModels<MainViewModel>()

    private var isLogin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupToolbar(R.string.app_name, null)
        initFab()
        initDrawer()
        initRecyclerView()
        initViewModel()
    }

    private fun initFab() {
        binding.fab.setOnClickListener {
            LocalActivity.start(this)
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
        }.refreshing()

        binding.pageRefresh.onLoadMore {
            viewModel.loadTaskList()
        }

    }

    private fun initDrawer() {
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val mDrawerToggle = ActionBarDrawerToggle(this, binding.root, toolbar, R.string.open, R.string.close)
        mDrawerToggle.syncState()

        binding.root.addDrawerListener(mDrawerToggle)

        val headerBinding = ViewMainNavigationHeaderBinding.bind(binding.navigation.getHeaderView(0))
        headerBinding.root.setOnClickListener {
            if (isLogin) {
                AceConfig.saveUser(null)
                viewModel.loadUserState()
            } else {
                AccountActivity.start(this)
            }
        }
        binding.navigation.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.main_app_manager -> {

                }
                R.id.main_sign_manager -> {
                    SignManagerActivity.start(this)
                }
            }
            true
        }
        observeUserState(headerBinding)
    }

    private fun observeUserState(headerBinding: ViewMainNavigationHeaderBinding) {
        viewModel.userState.observe(this) {
            when (it) {
                is UserState.NoLogin -> {
                    headerBinding.tvEmail.setText(R.string.click_login)
                    headerBinding.tvUsername.setText(R.string.no_login)
                    isLogin = false
                    binding.pageRefresh.refresh()
                }

                is UserState.Login -> {
                    headerBinding.tvUsername.text = it.bean.name
                    headerBinding.tvEmail.text = it.bean.email
                    isLogin = true
                    binding.pageRefresh.refresh()
                }
            }
        }
    }

    private fun initViewModel() {
        viewModel.taskState.observe(this) {
            when (it) {
                is TaskListState.FirstSuccess -> {
                    if (it.list.isEmpty()) {
                        binding.pageRefresh.showEmpty(getString(R.string.no_task))
                        binding.pageRefresh.finish(false,false)
                    } else {
                        binding.recyclerView.models = it.list
                        viewModel.updateState(binding.recyclerView.models)
                        binding.pageRefresh.showContent(it.list.size >= 20)
                        binding.pageRefresh.finish(true,it.list.size >= 20)

                    }
                }

                is TaskListState.FirstFail -> {
                    binding.pageRefresh.showError(it.msg,true)
                    binding.pageRefresh.finish(false,false)
                }

                is TaskListState.MoreSuccess -> {
                    binding.recyclerView.addModels(it.list)
                    viewModel.updateState(binding.recyclerView.models)
                    binding.pageRefresh.showContent(it.list.size >= 20)
                    binding.pageRefresh.finish(true,it.list.size >= 20)
                }

                is TaskListState.MoreFail -> {
                    binding.pageRefresh.showContent(true)
                    showSnackBar(it.msg)
                    binding.pageRefresh.finish(false,true)
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
        viewModel.packageState.observe(this) {
            when (it) {
                is PackageState.Loading -> {
                    showLoadingDialog()
                }

                is PackageState.LoadSignSuccess -> {
                    dismissLoadingDialog()
                    PackageFragment().show(supportFragmentManager,"PackageConfig")
                }

                is PackageState.Fail -> {
                    dismissLoadingDialog()
                    showSnackBar(it.msg)
                }

                is PackageState.Success -> {
                    dismissLoadingDialog()
                    MaterialDialog(this).show {
                        title(R.string.package_success)
                        message(text = getString(R.string.package_success_hint,it.path))
                        positiveButton(R.string.done)
                    }
                }
            }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == ResultCodes.LOGIN_SUCCESS) {
            viewModel.loadUserState()
        } else if (resultCode == ResultCodes.CREATE_TASK) {
            binding.pageRefresh.refresh()
        }
    }
}
