package com.kraven.ui.order.beverage.fragment


import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.kraven.R
import com.kraven.di.component.FragmentComponent
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.order.beverage.adapter.BeverageStoreAdapter
import com.kraven.ui.order.beverage.model.BeverageStore
import kotlinx.android.synthetic.main.beverage_store_list_fragment.*

class BeverageStoreListFragment : BaseFragment() {

    private var linearLayoutManager: LinearLayoutManager? = null
    var beverageStoreAdapter: BeverageStoreAdapter? = null

    override fun createLayout(): Int = R.layout.beverage_store_list_fragment

    override fun inject(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.order_beverage))
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        setUpRecyclerView()
    }


    private fun setUpRecyclerView() {
        linearLayoutManager = LinearLayoutManager(activity)
        recyclerViewBeverageStoreList.layoutManager = linearLayoutManager
        val dividerItemDecoration = DividerItemDecoration(recyclerViewBeverageStoreList.context, linearLayoutManager!!.orientation)
        recyclerViewBeverageStoreList.addItemDecoration(dividerItemDecoration)
        beverageStoreAdapter = BeverageStoreAdapter(object : BeverageStoreAdapter.ItemClickListener {
            override fun onItemClick() {
                navigator.load(BeverageListFragment::class.java).replace(true)
            }
        })
        recyclerViewBeverageStoreList.adapter = beverageStoreAdapter

        beverageStoreAdapter!!.items = menuList()
    }

    fun menuList(): ArrayList<BeverageStore> {
        val menuLists = ArrayList<BeverageStore>()
        menuLists.add(BeverageStore("Beverage Store Name"))
        menuLists.add(BeverageStore("Beverage Store Name"))
        menuLists.add(BeverageStore("Beverage Store Name"))
        menuLists.add(BeverageStore("Beverage Store Name"))
        menuLists.add(BeverageStore("Beverage Store Name"))
        menuLists.add(BeverageStore("Beverage Store Name"))
        menuLists.add(BeverageStore("Beverage Store Name"))
        menuLists.add(BeverageStore("Beverage Store Name"))
        menuLists.add(BeverageStore("Beverage Store Name"))
        return menuLists
    }
}