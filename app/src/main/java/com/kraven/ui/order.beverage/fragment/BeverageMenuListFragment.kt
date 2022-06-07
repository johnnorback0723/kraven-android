package com.kraven.ui.order.beverage.fragment

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders

import androidx.recyclerview.widget.LinearLayoutManager
import com.kraven.R
import com.kraven.coreadapter.DividerItemDecoration
import com.kraven.di.component.FragmentComponent
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.home.viewmodel.HomeViewModel
import com.kraven.ui.order.beverage.adapter.BeverageAdapter
import com.kraven.ui.order.beverage.model.Beverage
import com.kraven.utils.ConstantApp
import kotlinx.android.synthetic.main.beverage_menu_list_fragment.*


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.ArrayList


class BeverageMenuListFragment : BaseFragment() {

    private var beverageAdapter: BeverageAdapter? = null
    private lateinit var viewModel: HomeViewModel
    private var linearLayoutManager: LinearLayoutManager? = null


    companion object {
        fun newInstance(position: Int): BeverageMenuListFragment {
            val args = Bundle()
            args.putInt(ConstantApp.PassValue.MENU_POSITION, position)
            val fragment = BeverageMenuListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)[HomeViewModel::class.java]
        viewModel.getBeverageList.observe(this,
                { responseBody ->
                    //beverageAdapter?.items = responseBody.data
                }) { true }
    }

    override fun createLayout(): Int = R.layout.beverage_menu_list_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        setUpRecyclerView()

        GlobalScope.launch(Dispatchers.Main) {
            delay(500L)
            //viewModel.getBeverageList()
        }

    }

    private fun setUpRecyclerView() {
        linearLayoutManager = LinearLayoutManager(activity)
        recyclerViewBeverageList.layoutManager = linearLayoutManager
        val dividerItemDecoration = DividerItemDecoration(requireContext())
        recyclerViewBeverageList.addItemDecoration(dividerItemDecoration)
        /*beverageAdapter = BeverageAdapter(object : BeverageAdapter.ItemClickListener {
            override fun onAdd(position: Int, selectedCartList: ArrayList<Beverage>) {
                selectedItems = selectedCartList
                finalCount += 1
                finalPrice += beverageAdapter?.getItem(position)?.beveragePrice!!

            }

            override fun onSubtract(position: Int, selectedCartList: ArrayList<Beverage>) {
                selectedItems = selectedCartList
                finalCount -= 1
                finalPrice -= beverageAdapter?.getItem(position)?.beveragePrice!!

            }

            override fun onClickItem(position: Int) {
                val bottomSheetFragment = ToppingBottomSheetFragment()
                val args = Bundle()
                args.putParcelable(ConstantApp.PassValue.TOPPING_POSITION, beverageAdapter!!.items[position])
                args.putInt(ConstantApp.PassValue.FRAGMENT, 2)
                bottomSheetFragment.arguments = args
                bottomSheetFragment.show(fragmentManager, bottomSheetFragment.tag)
            }

        })*/

        recyclerViewBeverageList.adapter = beverageAdapter
    }

}