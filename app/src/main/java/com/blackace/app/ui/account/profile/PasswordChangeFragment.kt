package com.blackace.app.ui.account.profile

import android.app.Dialog
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.blackace.R
import com.blackace.databinding.DialogPasswordChangeBinding
import com.blackace.databinding.FragmentPackageConfigBinding
import com.blackace.util.ext.autoClearError
import com.blackace.util.ext.hide
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 *
 * @author: magicHeimdall
 * @create: 7/2/2023 3:41 PM
 */
class PasswordChangeFragment : DialogFragment() {

    private lateinit var binding: DialogPasswordChangeBinding

    private val viewModel by activityViewModels<ProfileViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog =
            MaterialDialog(requireContext())
                .title(R.string.change_password)
                .customView(R.layout.dialog_password_change)
        binding = DialogPasswordChangeBinding.bind(dialog.getCustomView())
        initView()
        return dialog
    }

    private fun initView() {
        binding.passLayout.autoClearError()
        binding.verifyLayout.autoClearError()

        binding.btnVerify.setOnClickListener {
            val password = binding.pass.text.toString()
            if (password.length < 6) {
                binding.passLayout.isErrorEnabled = true
                binding.passLayout.error = getString(R.string.password_must_6)
                return@setOnClickListener
            }

            val verify = binding.verify.text.toString()
            if (verify.length != 6) {
                binding.verifyLayout.isErrorEnabled = true
                binding.verifyLayout.error = getString(R.string.verify_must_6)
                binding.btnSendVerify.hide()
                return@setOnClickListener
            }

            viewModel.changePassword(verify, password)
            dismiss()
        }

        binding.btnSendVerify.setOnClickListener {
            viewModel.sendEmailVerify()
        }
    }

}
