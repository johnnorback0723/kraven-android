package com.kraven.ui.future.food.order.fragment

import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.kraven.R
import com.kraven.di.component.FragmentComponent
import com.kraven.ui.activity.IsolatedActivity
import com.kraven.ui.address.fragment.AddressListFragment
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.cart.fragment.CartFragment
import com.kraven.ui.home.adapter.RestaurantAdapter
import com.kraven.ui.home.fragment.RestaurantDetailsFragment
import com.kraven.ui.home.model.MenuModel
import com.kraven.ui.home.model.Restaurants
import com.kraven.ui.home.model.Service
import com.kraven.ui.home.viewmodel.HomeViewModel
import com.kraven.ui.review.fragment.ReviewFragment
import com.kraven.utils.ConstantApp

import kotlinx.android.synthetic.main.select_vendor_fragment.*
import java.util.ArrayList


class SelectVendorFragment : BaseFragment() {
    private lateinit var viewModel: HomeViewModel
    var restaurantAdapter: RestaurantAdapter? = null

    private var linearLayoutManager: LinearLayoutManager? = null
    override fun createLayout(): Int = R.layout.select_vendor_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

   /* override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = Bundle()
        bundle.putInt("select_address", 1)
        navigator.loadActivity(IsolatedActivity::class.java).setPage(AddressListFragment::class.java).addBundle(bundle).start()
    }
*/

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.future_food_order))
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        setHasOptionsMenu(true)
        setUpRecyclerView()

        viewModel = ViewModelProviders.of(this, viewModelFactory)[HomeViewModel::class.java]
        viewModel.getServiceList()

        viewModel.getList.observe(this,
                { responseBody ->
                  //  restaurantAdapter?.items = responseBody.data?.restaurantList
                }) { true }

    }

    private fun setUpRecyclerView() {

        recyclerViewVendorList.layoutManager = GridLayoutManager(activity, 1)
        linearLayoutManager = LinearLayoutManager(activity)
        val dividerItemDecoration = DividerItemDecoration(recyclerViewVendorList.context, linearLayoutManager!!.orientation)
        recyclerViewVendorList.addItemDecoration(dividerItemDecoration)
        recyclerViewVendorList.layoutManager = linearLayoutManager
        /*restaurantAdapter = RestaurantAdapter(true, object : RestaurantAdapter.ItemClickListener {
            override fun onClickFavourite(item: Restaurants) {

            }

            override fun onItemClick( item: Restaurants) {
                val bundle = Bundle()

                bundle.putString("Vendor", "Vendor")
                bundle.putParcelable("values", item)
                bundle.putInt("select_address", 1)
                navigator.load(AddressListFragment::class.java).setBundle(bundle).replace(true)
                //navigator.load(RestaurantDetailsFragment::class.java).setBundle(bundle).replace(true)
            }

            override fun onClickRatingBar() {
                navigator.load(ReviewFragment::class.java).replace(true)
            }
        })*/
        recyclerViewVendorList.adapter = restaurantAdapter
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater!!.inflate(R.menu.home_menu_icon, menu)
        val mSearch = menu!!.findItem(R.id.action_search)
        val mSearchView = mSearch.actionView as SearchView

        val iconClose = mSearchView.findViewById(androidx.appcompat.R.id.search_close_btn) as ImageView
        iconClose.setColorFilter(Color.BLACK)


        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                //restaurantAdapter?.filter?.filter(query)

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
               // restaurantAdapter?.filter?.filter(newText)

                return false
            }

        })

        mSearch.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                toolbar.setToolbarTitle(getString(R.string.select_vendor))
                return true
            }

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                toolbar.setToolbarTitle("")
                return true
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
            }
            R.id.ic_cart -> {
                val bundle = Bundle()
                bundle.putParcelableArrayList(ConstantApp.PassValue.passItems, menuList())
                navigator.loadActivity(IsolatedActivity::class.java, CartFragment::class.java).addBundle(bundle).start()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun menuList(): ArrayList<out Parcelable>? {
        val menuLists = ArrayList<MenuModel>()

        menuLists.add(MenuModel("1", "Pizza Marinara", "Italian", "55(With Toppings)", "0", 1, "25"))
        menuLists.add(MenuModel("1", "Pineapple Juice(50% off)", "Beverages", "50", "1", 1, "0"))
        menuLists.add(MenuModel("0", "Maxican Aloo Wrap", "Burger", "55", "0", 1, "0"))


        return menuLists
    }
}