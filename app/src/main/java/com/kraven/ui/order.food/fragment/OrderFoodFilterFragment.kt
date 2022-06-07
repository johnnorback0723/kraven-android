package com.kraven.ui.order.food.fragment


import android.app.Activity
import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.kraven.R
import com.kraven.di.component.FragmentComponent
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.home.viewmodel.HomeViewModel
import com.kraven.ui.order.food.adapter.FilterAdapter
import com.kraven.ui.order.food.model.Filters
import com.kraven.utils.ConstantApp
import kotlinx.android.synthetic.main.order_food_filter_fragment.*

class OrderFoodFilterFragment : BaseFragment() {

    var textViewStatus: AppCompatTextView? = null
    var filterAdapter: FilterAdapter? = null
    private lateinit var viewModel: HomeViewModel

    var deliveryTypes: ArrayList<String>? = null
    private var cuisinLists: ArrayList<String>? = null

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun createLayout(): Int = R.layout.order_food_filter_fragment


    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.filter))
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        setHasOptionsMenu(true)
        setUpRecyclerView()

        viewModel = ViewModelProviders.of(this, viewModelFactory)[HomeViewModel::class.java]
        viewModel.getFilterList()

        viewModel.getFilterList.observe(this,
                { responseBody ->
                    filterAdapter?.items = getFilterList(responseBody.data)
                }) { true }
    }

    private fun setUpRecyclerView() {
        recyclerViewFilter.layoutManager = LinearLayoutManager(activity)
        filterAdapter = FilterAdapter(object : FilterAdapter.OnItemClickListener {
            override fun passData(deliveryType: ArrayList<String>, cuisinList: ArrayList<String>) {
                deliveryTypes = deliveryType
                cuisinLists = cuisinList
            }
        })
        recyclerViewFilter.adapter = filterAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_status, menu)

        textViewStatus = menu.findItem(R.id.notification).actionView.findViewById(R.id.textViewDetailsStatus) as AppCompatTextView
        textViewStatus!!.text = getString(R.string.done)
        textViewStatus!!.setOnClickListener {
            if (deliveryTypes == null && cuisinLists == null) {
                val resultIntent = Intent()
                activity!!.setResult(Activity.RESULT_OK, resultIntent)
                navigator.finish()
            } else {
                val resultIntent = Intent()
                resultIntent.putExtra(ConstantApp.PassValue.deliveryTypes, deliveryTypes)
                resultIntent.putExtra(ConstantApp.PassValue.cuisinLists, cuisinLists)
                activity!!.setResult(Activity.RESULT_OK, resultIntent)
                navigator.finish()
            }
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    fun getFilterList(data: List<Filters.FilterName>?): List<Filters> {

        val filterList = mutableListOf<Filters>()
        filterList.add(Filters("Sort", getFilterListSort(0)))
        filterList.add(Filters("Order Type", getFilterListSort(1)))
        filterList.add(Filters("Cuisine", data!!))

        return filterList
    }

    fun getFilterListSort(position: Int): List<Filters.FilterName> {

        val filterList = mutableListOf<Filters.FilterName>()
        if (position == 0) {
            filterList.add(Filters.FilterName(0, "Rating", 0))
        } else if (position == 1) {
            filterList.add(Filters.FilterName(0, "Delivery", 0))
            filterList.add(Filters.FilterName(0, "Pickup", 0))
        }

        return filterList
    }
}