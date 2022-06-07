package com.kraven.ui.order.beverage.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.kraven.R
import com.kraven.core.StatusCode
import com.kraven.di.component.FragmentComponent
import com.kraven.extensions.getBeverageList
import com.kraven.extensions.getQuantityList
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.order.beverage.SpecialBeverageViewModel
import com.kraven.ui.setting.adapter.SelectTitleAdapter
import kotlinx.android.synthetic.main.fragment_select_title.*
import java.lang.IllegalStateException

class SelectBeverageFragment : BaseFragment() {

    private var selectTitleAdapter: SelectTitleAdapter? = null

    lateinit var specialBeverageViewModel: SpecialBeverageViewModel

    private val beveragge: Boolean by lazy {
        val args = arguments ?: throw  IllegalStateException("Missing Arguments")
        args.getBoolean("beveragge")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        specialBeverageViewModel = ViewModelProviders.of(this, viewModelFactory)[SpecialBeverageViewModel::class.java]
        registerObserver()
    }

    private fun registerObserver() {
        specialBeverageViewModel.specialBeverageDropDownList.observe(this, { it ->
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    val keyList: List<String>?
                    if (beveragge.not()) {
                        keyList = ArrayList(it.data?.quantityTypeList?.values)
                        //keyList = sortedList.sortedWith(compareBy { it })
                    } else {
                        val sortedList = ArrayList(it.data?.beverageList?.values)
                        keyList = sortedList.sortedWith(compareBy { it })
                    }

                    selectTitleAdapter = SelectTitleAdapter(keyList, object : SelectTitleAdapter.OnItemClickListener {
                        override fun onItemClick(title: String) {
                            val resultIntent = Intent()
                            resultIntent.putExtra("title", title)
                            activity!!.setResult(Activity.RESULT_OK, resultIntent)
                            navigator.finish()
                            val mView = editTextSearch
                            if (mView!!.isFocused) {
                                val inputManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                inputManager.hideSoftInputFromWindow(mView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                            }
                        }
                    })
                    recyclerViewTitle.adapter = selectTitleAdapter
                }
                StatusCode.CODE_NO_DATA -> {

                }
                else -> showMessage(it.message)
            }

        })
    }

    override fun createLayout(): Int {
        return R.layout.fragment_select_title
    }

    override fun inject(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(if (beveragge) "Select Beverage" else "Quantity Type")
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)


        editTextSearch!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                selectTitleAdapter?.filter?.filter(s)
            }

            override fun afterTextChanged(s: Editable) {

            }
        })


        // if (beveragge.not()) {
        showLoader(true)
        specialBeverageViewModel.specialBeverageDropDownList()

        /* } else {
             selectTitleAdapter = SelectTitleAdapter(getBeverageList(), object : SelectTitleAdapter.OnItemClickListener {
                 override fun onItemClick(title: String) {
                     val resultIntent = Intent()
                     resultIntent.putExtra("title", title)
                     activity!!.setResult(Activity.RESULT_OK, resultIntent)
                     navigator.finish()
                     val mView = editTextSearch
                     if (mView!!.isFocused) {
                         val inputManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                         inputManager.hideSoftInputFromWindow(mView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                     }
                 }
             })
             recyclerViewTitle.adapter = selectTitleAdapter
         }*/


    }

}