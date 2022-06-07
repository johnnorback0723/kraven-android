package com.kraven.ui.setting.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProviders
import com.kraven.R
import com.kraven.core.StatusCode
import com.kraven.di.component.FragmentComponent
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.setting.SettingViewModel
import com.kraven.ui.setting.adapter.SelectTitleAdapter
import com.kraven.ui.setting.model.Title
import com.kraven.utils.ConstantApp
import kotlinx.android.synthetic.main.fragment_select_title.*

class SelectTitleFragment : BaseFragment() {


    private var selectTitleAdapter: SelectTitleAdapter? = null

    private lateinit var settingViewModel: SettingViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settingViewModel = ViewModelProviders.of(this, viewModelFactory)[SettingViewModel::class.java]

        settingViewModel.titleList.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    selectTitleAdapter = SelectTitleAdapter(getArrayList(it.data?.userDropdownlist), object : SelectTitleAdapter.OnItemClickListener {
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
            }
        })

    }

    private fun getArrayList(userDropdownlist: Title.UserDropdownlist?): MutableList<String> {
        val list = mutableListOf<String>()

        list.add(userDropdownlist?.x0!!)
        list.add(userDropdownlist.x1!!)
        list.add(userDropdownlist.x2!!)
        list.add(userDropdownlist.x3!!)
        list.add(userDropdownlist.x4!!)

        return list
    }


    override fun createLayout(): Int {
        return R.layout.fragment_select_title
    }

    override fun inject(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle("Select Title")
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        showLoader(true)
        settingViewModel.getTitleList()


        editTextSearch!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                selectTitleAdapter?.filter?.filter(s)
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

    }

    private fun setUpRecyclerView() {
        /* selectTitleAdapter = SelectTitleAdapter(object : SelectTitleAdapter.OnItemClickListener {
             override fun onItemClick(title: String) {

             }
         })

         recyclerViewTitle.adapter = selectTitleAdapter*/

    }
}