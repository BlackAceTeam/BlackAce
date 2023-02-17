package com.blackace.app.base

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.blackace.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 *
 * @author: magicHeimdall
 * @create: 10/2/2023 2:49 PM
 */
open class BaseBottomDialogFragment(layoutID: Int) : BottomSheetDialogFragment(layoutID) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        expandedSheet()
    }

    private fun expandedSheet() {
        val dialog = (dialog as? BottomSheetDialog) ?: return
        val behavior = dialog.behavior
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true

        val bottomSheet = dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.backgroundTintMode = PorterDuff.Mode.CLEAR
        bottomSheet?.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
    }


}
