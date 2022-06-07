package com.kraven.ui.home.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.ViewModelProviders
import com.kraven.R
import com.kraven.core.StatusCode
import com.kraven.di.component.FragmentComponent
import com.kraven.extensions.lazyFast
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.home.viewmodel.HomeViewModel
import com.kraven.ui.order.beverage.adapter.BeverageAdapter
import com.kraven.ui.order.beverage.model.Beverage
import kotlinx.android.synthetic.main.beverage_search_screen.*

class BeverageSearchScreen : BaseFragment() {


    private lateinit var viewModel: HomeViewModel
    private var beverageAdapter: BeverageAdapter? = null

    private val menuId: String? by lazyFast {
        val args = arguments ?: throw IllegalArgumentException("Missing argument")
        args.getString("menu_id")
    }

    private val restaurnatId: String? by lazyFast {
        val args = arguments ?: throw IllegalArgumentException("Missing argument")
        args.getString("restaurant_id")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)[HomeViewModel::class.java]
        registerObserver()
    }

    private fun registerObserver() {
        viewModel.getBeverageMenuList.observe(this, {

            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    setUpRecyclerView()
                    beverageAdapter?.clearAllItem()
                    beverageAdapter?.items = it.data
                }
                StatusCode.CODE_NO_DATA -> {
                    beverageAdapter?.errorMessage = it.message
                }
                else -> showMessage(it.message)
            }
        })
    }

    override fun createLayout(): Int = R.layout.beverage_search_screen

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        toolbar.showToolbar(false)

        imageViewClose.setOnClickListener {
            hideKeyBoard()
            navigator.finish()

        }

        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }





            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(s.toString().isNotEmpty()){
                    restaurnatId?.let { viewModel.getBeverageMenuListSearch(s.toString(), it) }
                }

            }

        })
    }

    private fun setUpRecyclerView() {
        beverageAdapter = BeverageAdapter(object : BeverageAdapter.ItemClickListener {
            override fun onClickImage(image: String, imageView: AppCompatImageView) {

            }

            override fun onItemClick(beverage: Beverage) {
                val resultIntent = Intent()
                resultIntent.putExtra("beverage", beverage)
                activity!!.setResult(Activity.RESULT_OK, resultIntent)
                navigator.finish()
            }
        })
        recyclerViewMenuList.adapter = beverageAdapter
    }
}
