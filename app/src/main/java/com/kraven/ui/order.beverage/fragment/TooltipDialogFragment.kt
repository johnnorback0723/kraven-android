package com.kraven.ui.order.beverage.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout

import androidx.fragment.app.DialogFragment
import com.kraven.R

class TooltipDialogFragment : DialogFragment() {


    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val view = inflater.inflate(R.layout.tooltip_dialog_fragment, container, false)
        val constrainLayoutTooltip = view.findViewById<ConstraintLayout>(R.id.constrainLayoutTooltip)
        constrainLayoutTooltip.setOnClickListener {
            targetFragment?.onActivityResult(101, 0, null)
            dismiss()
        }
        return view
    }


}