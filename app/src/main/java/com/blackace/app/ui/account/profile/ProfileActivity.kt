package com.blackace.app.ui.account.profile

import android.os.Bundle
import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.afollestad.materialdialogs.MaterialDialog
import com.blackace.R
import com.blackace.app.base.BaseActivity
import com.blackace.app.contract.ProfileActivityContract
import com.blackace.app.ui.account.AccountViewModel
import com.blackace.data.entity.UserBean
import com.blackace.data.state.ChangePassState
import com.blackace.data.state.UserState
import com.blackace.databinding.ActivityProfileBinding
import com.blackace.databinding.ItemProfileSimpleBinding
import com.blackace.databinding.ItemProfileTitleBinding
import com.blackace.util.ext.date
import com.blackace.util.ext.showConfirmDialog
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup

/**
 *
 * @author: magicHeimdall
 * @create: 6/2/2023 9:01 PM
 */
class ProfileActivity : BaseActivity() {

    private val viewModel by viewModels<ProfileViewModel>()

    private val accountViewModel by viewModels<AccountViewModel>()

    private val binding by viewBinding(ActivityProfileBinding::bind)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setupToolbar(R.string.user_info)
        initViewModel()
        initRecyclerView()
        initView()
    }

    private fun initRecyclerView() {
        binding.recyclerView.linear().setup {
            addType<Int>(R.layout.item_profile_title)
            addType<Pair<Int, String>>(R.layout.item_profile_simple)
            onBind {
                when (val model = getModel<Any>()) {
                    is Int -> {
                        val binding = getBinding<ItemProfileTitleBinding>()
                        binding.tvTitle.setText(model)
                    }

                    is Pair<*, *> -> {
                        val binding = getBinding<ItemProfileSimpleBinding>()
                        binding.tvTitle.setText((model.first as Int))
                        binding.tvMsg.text = model.second.toString()
                    }
                }
            }

            onClick(R.id.tvMsg) {
                val model = getModel<Pair<Int, String>>()
                if (model.first == R.string.password) {
                    //????????????
                    PasswordChangeFragment().show(supportFragmentManager, "PasswordChange")
                }
            }

        }
    }

    private fun initViewModel() {
        viewModel.userState.observe(this) {
            when (it) {
                is UserState.NoLogin -> {
                    showSnackBar(R.string.no_login)
                    finish()
                }

                is UserState.Login -> {
                    binding.recyclerView.models = generateTask(it.bean)
                }
            }
        }

        accountViewModel.actionState.observe(this) {
            when (it) {
                is ChangePassState.Loading -> {
                    showLoadingDialog()
                }

                is ChangePassState.Success -> {
                    dismissLoadingDialog()
                    accountViewModel.logout()
                    MaterialDialog(this).show {
                        cancelable(false)
                        cancelOnTouchOutside(false)
                        title(R.string.change_password)
                        message(text = getString(R.string.password_change_success_with_pass, it.password))
                        positiveButton(R.string.done) {
                            setResult(ProfileActivityContract.UPDATE_CONFIG)
                            finish()
                        }
                    }
                }

                is ChangePassState.Fail -> {
                    dismissLoadingDialog()
                    showSnackBar(it.msg)
                }
            }
        }
    }

    private fun initView() {
        binding.btnLogout.setOnClickListener {
            showConfirmDialog(R.string.login_out, R.string.login_out_hint) {
                accountViewModel.logout()
                setResult(ProfileActivityContract.UPDATE_CONFIG)
                finish()
            }
        }
    }

    private fun generateTask(bean: UserBean): List<Any> {
        return listOf(
            R.string.account_setting,
            R.string.username to bean.name,
            R.string.email to bean.email,
            R.string.register_time to bean.registerTime.date("yyyy-MM-dd"),
            R.string.password to getString(R.string.change_password_now),
        )
    }

}
