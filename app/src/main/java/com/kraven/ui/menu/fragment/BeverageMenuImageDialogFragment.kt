package com.kraven.ui.menu.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.kraven.R
import kotlinx.android.synthetic.main.dialog_beverage_menu_image.*


class BeverageMenuImageDialogFragment : DialogFragment() {


    private lateinit var listener: (String) -> Unit
    private var image: String? = null

    companion object {
        val dialog = BeverageMenuImageDialogFragment()
        fun showDialog(fragmentManager: FragmentManager, image: String, callback: (String) -> Unit) {
            dialog.listener = callback
            dialog.image = image
            dialog.isCancelable = true
            if (!dialog.isAdded) {
                dialog.show(fragmentManager, BeverageMenuImageDialogFragment::class.java.simpleName)
            }
        }
    }


    /*override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.let {
            //it.requestFeature(Window.FEATURE_NO_TITLE)
            it.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        }
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let {
            it.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            it.setBackgroundDrawableResource(android.R.color.transparent)
        }
        dialog?.setCanceledOnTouchOutside(true)
        dialog?.setCancelable(true)
    }*/

    override fun onActivityCreated(arg0: Bundle?) {
        super.onActivityCreated(arg0)
        dialog?.window!!.attributes.windowAnimations = R.style.DialogAnimation
    }

    override fun getTheme(): Int {
        return R.style.FullScreenDialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_beverage_menu_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(context!!)
                .load(image)
                .into(imageViewBeverage)
        buttonDismiss.setOnClickListener { dismissAllowingStateLoss() }
    }

}