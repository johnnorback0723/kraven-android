package com.kraven.ui.base

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModelProvider

import com.google.android.material.button.MaterialButton
import com.kraven.R

import com.kraven.core.Session
import com.kraven.di.HasComponent
import com.kraven.di.component.ActivityComponent
import com.kraven.di.component.FragmentComponent
import com.kraven.di.module.FragmentModule


import com.kraven.ui.manager.Navigator
import javax.inject.Inject


/**
 * Created by hlink21 on 25/4/16.
 */
abstract class BaseFragment : androidx.fragment.app.Fragment(), HasComponent<FragmentComponent> {

    protected lateinit var toolbar: HasToolbar

    @Inject
    lateinit var session: Session

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var navigator: Navigator

    private val dialogInterface: DialogInterface? = null
    private val dialogInterfaceYesNo: DialogInterfaceYesNo? = null

    override val component: FragmentComponent
        get() {
            return getComponent(ActivityComponent::class.java).plus(FragmentModule(this))
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(createLayout(), container, false)
    }

    /*override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindData()
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindData()
    }

    private fun <C> getComponent(componentType: Class<C>): C {
        return componentType.cast((activity!! as HasComponent<*>).component)
    }

    override fun onAttach(context: Context) {
        inject(component)
        super.onAttach(context)

        if (activity!! is HasToolbar) {
            toolbar = activity!! as HasToolbar
        }
    }

    interface OnCompated {
        fun finished(resutl: String)
    }

    fun commandDialog(title: String, message: String, dialogInterface: DialogInterface) {
        val dialog = Dialog(activity!!, android.R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_info)
        dialog.setCanceledOnTouchOutside(false)

        val textViewDialogTitle = dialog.findViewById(R.id.textViewDialogTitle) as AppCompatTextView
        textViewDialogTitle.text = title
        val textViewInfo = dialog.findViewById(R.id.textViewInfo) as AppCompatTextView
        textViewInfo.text = message
        val buttonOk = dialog.findViewById(R.id.buttonOk) as MaterialButton
        buttonOk.setOnClickListener {
            dialog.dismiss()
            dialogInterface.onClickOk()
        }
        if (dialog.isShowing.not())
            dialog.show()
    }
    fun commandDialogApply(title: String, message: String, dialogInterface: DialogInterface) {
        val dialog = Dialog(activity!!, android.R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_info)
        dialog.setCanceledOnTouchOutside(false)

        val textViewDialogTitle = dialog.findViewById(R.id.textViewDialogTitle) as AppCompatTextView
        textViewDialogTitle.text = title
        val textViewInfo = dialog.findViewById(R.id.textViewInfo) as AppCompatTextView
        textViewInfo.text = message
        val buttonOk = dialog.findViewById(R.id.buttonOk) as MaterialButton
        buttonOk.setOnClickListener {
            dialog.dismiss()
            dialogInterface.onClickOk()
        }
        if (dialog.isShowing.not())
            dialog.show()
    }

    fun commandDialogYesNo(title: String, message: String, dialogInterfaceYesNo: DialogInterfaceYesNo) {
        val dialog = Dialog(activity!!, android.R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_info_yes_no)
        dialog.setCanceledOnTouchOutside(true)

        val textViewDialogTitle = dialog.findViewById(R.id.textViewDialogTitle) as AppCompatTextView
        textViewDialogTitle.text = title
        val textViewInfo = dialog.findViewById(R.id.textViewInfo) as AppCompatTextView
        textViewInfo.text = message
        val buttonYes = dialog.findViewById(R.id.buttonYes) as MaterialButton
        buttonYes.setOnClickListener {
            dialog.dismiss()
            dialogInterfaceYesNo.onClickYes()
        }

        val buttonNo = dialog.findViewById(R.id.buttonNo) as MaterialButton
        buttonNo.setOnClickListener {
            dialog.dismiss()
            //dialogInterfaceYesNo.onClickNo()
        }
        if (dialog.isShowing.not())
        dialog.show()
    }

    fun commanDialogYesNoNew(title: String, message: String, buttonYesText: String, buttonNoText: String, dialogInterfaceYesNo: DialogInterfaceYesNo) {
        val dialog = Dialog(activity!!, android.R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_info_yes_no_new)
        dialog.setCanceledOnTouchOutside(false)

        val textViewDialogTitle = dialog.findViewById(R.id.textViewDialogTitle) as AppCompatTextView
        textViewDialogTitle.text = title
        val textViewInfo = dialog.findViewById(R.id.textViewInfo) as AppCompatTextView
        textViewInfo.text = message
        val buttonYes = dialog.findViewById(R.id.buttonYes) as MaterialButton
        buttonYes.text = buttonYesText
        buttonYes.setOnClickListener {
            dialog.dismiss()
            dialogInterfaceYesNo.onClickYes()
        }

        val buttonNo = dialog.findViewById(R.id.buttonNo) as MaterialButton
        buttonNo.text = buttonNoText

        buttonNo.setOnClickListener {
            dialog.dismiss()
            dialogInterfaceYesNo.onClickNo()
        }
        dialog.show()
    }

    interface DialogInterfaceYesNo {
        fun onClickYes()
        fun onClickNo()
    }

    interface DialogInterface {
        fun onClickOk()
    }


    fun hideKeyBoard() {
        if (activity!! is BaseActivity) {
            (activity!! as BaseActivity).hideKeyboard()
        }
    }

    fun showKeyBoard() {
        if (activity!! is BaseActivity) {
            (activity!! as BaseActivity).showKeyboard()
        }
    }

    fun showMessage(message: String) {
        if (activity!! is BaseActivity) {
            (activity!! as BaseActivity).showErrorMessage(message)
        }
    }

    fun showLoader(isShow: Boolean,message:String ="Please wait...") {
        if (activity!! is BaseActivity) {
            (activity!! as BaseActivity).toggleLoader(isShow,message)
        }
    }

    fun <T : BaseFragment> getParentFragment(targetFragment: Class<T>): T? {

        if (parentFragment == null) return null
        try {
            return targetFragment.cast(parentFragment)
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }

        return null
    }

    open fun onShow() {

    }

    open fun onBackActionPerform(): Boolean {
        return true
    }

    open fun onViewClick(view: View) {

    }

    fun onError(throwable: Throwable) {
        if (activity!! is BaseActivity) {
            (activity!! as BaseActivity).onError(throwable)
        }
    }

    protected abstract fun createLayout(): Int
    protected abstract fun inject(fragmentComponent: FragmentComponent)
    protected abstract fun bindData()

}
