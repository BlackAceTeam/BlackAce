package com.blackace.app.ui.account

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blackace.R
import com.blackace.app.base.BaseFragment
import com.blackace.app.ui.file.FileChildFragment
import com.blackace.data.state.LoginState
import com.blackace.databinding.FragmentLoginBinding
import com.blackace.util.ext.*

/**
 *
 * @author: magicHeimdall
 * @create: 18/12/2022 9:40 下午
 */
class LoginFragment : BaseFragment(R.layout.fragment_login) {

    private val binding by viewBinding(FragmentLoginBinding::bind)

    private val viewModel by activityViewModels<AccountViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initEdit()
        initButton()
        initViewModel()
    }

    private fun initViewModel() {
        viewModel.loginState.observe(viewLifecycleOwner) {
            when (it) {
                is LoginState.Loading -> {
                    showLoadingDialog()
                }

                is LoginState.Fail -> {
                    dismissLoadingDialog()
                    binding.layoutPassword.error = it.msg
                }

                is LoginState.Success -> {
                    dismissLoadingDialog()
                    attachActivity<AccountActivity>().loginSuccess()
                }
            }
        }
    }

    private fun initEdit() {
        binding.editPassword.addTextChangedListener {
            binding.layoutPassword.error = ""
            binding.layoutPassword.isErrorEnabled = false
        }
        binding.editUsername.addTextChangedListener {
            binding.layoutUsername.error = ""
            binding.layoutPassword.isErrorEnabled = false
        }
    }

    private fun initButton() {
        binding.btnLogin.setOnClickListener {
            if (checkUserName() && checkPassword()) {
                viewModel.login(binding.editUsername.text.toString(), binding.editPassword.text.toString())
            }
        }

        binding.btnRegister.setOnClickListener {
            parentFragmentManager.commit {
                hide(this@LoginFragment)
                add(R.id.fragmentContainer, RegisterFragment(), "Register")
                addToBackStack("Register")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        baseActivity().setToolbarTitle(R.string.login)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            baseActivity().setToolbarTitle(R.string.login)
        }
    }

    private fun checkUserName(): Boolean {
        val text = binding.editUsername.text.toString()
        return if (text.isEmpty()) {
            binding.layoutPassword.isErrorEnabled = true
            binding.layoutUsername.error = getString(R.string.username_must_no_empty)
            false
        } else {
            true
        }
    }

    private fun checkPassword(): Boolean {
        val text = binding.editPassword.text.toString()
        if (text.length < 6) {
            binding.layoutPassword.isErrorEnabled = true
            binding.layoutPassword.error = getString(R.string.password_must_6)
            return false
        }
        return true
    }


}
