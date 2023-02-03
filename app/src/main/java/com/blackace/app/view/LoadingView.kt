package com.blackace.app.view

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import com.blackace.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 *
 * @author: magicHeimdall
 * @create: 19/12/2022 10:59 AM
 */
class LoadingDialog : BottomSheetDialogFragment() {

    private var mainDialog: Dialog? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mainDialog = Dialog(requireActivity()).apply {
            setContentView(R.layout.dialog_loading)
            window?.setGravity(Gravity.CENTER)

            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }

        return mainDialog!!
    }


}
