package com.kraven.ui.order.beverage.fragment

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kraven.R
import com.kraven.core.StatusCode
import com.kraven.coreadapter.EndlessRecyclerViewScrollListener
import com.kraven.data.pojo.Parameters
import com.kraven.di.component.FragmentComponent
import com.kraven.ui.activity.IsolatedActivity
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.order.beverage.SpecialBeverageViewModel
import com.kraven.ui.order.beverage.adapter.AdapterSpecialBeverage
import com.kraven.ui.order.beverage.model.SpecialBeverage
import kotlinx.android.synthetic.main.special_beverage_fragment.*

class SpecialBeverageListFragment : BaseFragment() {

    lateinit var specialBeverageViewModel: SpecialBeverageViewModel
    private var adapterSpecialBeverage: AdapterSpecialBeverage? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var pages = 1

    override fun createLayout(): Int = R.layout.special_beverage_fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        specialBeverageViewModel = ViewModelProviders.of(this, viewModelFactory)[SpecialBeverageViewModel::class.java]
        registerObserver()
    }

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    companion object {
        fun newInstance() = SpecialBeverageListFragment()
    }

    override fun onResume() {
        super.onResume()
        pages = 1
        setUpRecyclerView()
        specialBeverageViewModel.getSpecialBeverageList(Parameters(page = pages.toString()))
    }

    override fun bindData() {

    }

    private fun registerObserver() {
        specialBeverageViewModel.getSpecialBeverageList.observe(this, {
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    adapterSpecialBeverage?.setItems(it.data, pages)
                }
                StatusCode.CODE_NO_DATA -> {
                    if(pages==1) {
                        adapterSpecialBeverage?.errorMessage = it.message
                    }else {
                        adapterSpecialBeverage?.stopLoader()
                    }
                }
                else -> showMessage(it.message)
            }
        }) { true }
    }

    private fun setUpRecyclerView() {

        linearLayoutManager = LinearLayoutManager(activity)
        recyclerViewOrderList.layoutManager = linearLayoutManager

        adapterSpecialBeverage = AdapterSpecialBeverage(object : AdapterSpecialBeverage.ItemClickListener {
            override fun onItemClick(specialBeverage: SpecialBeverage) {
                val bundle = Bundle()
                bundle.putString("order_id", specialBeverage.id.toString())
                navigator.loadActivity(IsolatedActivity::class.java).setPage(SpecialBeverageDetailsFragment::class.java).addBundle(bundle).start()
                //navigator.load(SpecialBeverageDetailsFragment::class.java).setBundle(Bundle().apply { putString("order_id",specialBeverage.id.toString()) }).replace(true)
            }

            override fun onClickRatingBar() {}
        })

        recyclerViewOrderList.addOnScrollListener(object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                pages = page
                specialBeverageViewModel.getSpecialBeverageList(Parameters(page = pages.toString()))
            }

        })
        recyclerViewOrderList.adapter = adapterSpecialBeverage
    }
}