package com.kraven.ui.restaurant.reservation.fragement

import android.graphics.Color
import android.os.Bundle
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
import com.kraven.ui.home.adapter.RestaurantAdapter
import com.kraven.ui.home.model.Restaurants
import com.kraven.ui.home.model.Service
import com.kraven.ui.home.viewmodel.HomeViewModel
import com.kraven.ui.review.fragment.ReviewFragment
import kotlinx.android.synthetic.main.restaurant_reservation_fragment.*


class RestaurantReservationFragment : BaseFragment() {

    private lateinit var viewModel: HomeViewModel
    var restaurantAdapter: RestaurantAdapter? = null

    private var linearLayoutManager: LinearLayoutManager? = null

    override fun createLayout(): Int = R.layout.restaurant_reservation_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.restaurant_reservation))
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        setHasOptionsMenu(true)
        setUpRecyclerView()

        viewModel = ViewModelProviders.of(this, viewModelFactory)[HomeViewModel::class.java]
        viewModel.getServiceList()

        viewModel.getList.observe(this,
                { responseBody ->
                    //restaurantAdapter?.items = responseBody.data?.restaurantList
                }) { true }
    }

    private fun setUpRecyclerView() {
        recyclerViewRestaurantReservation.layoutManager = GridLayoutManager(activity, 1)
        linearLayoutManager = LinearLayoutManager(activity)
        val dividerItemDecoration = DividerItemDecoration(recyclerViewRestaurantReservation.context, linearLayoutManager!!.orientation)
        recyclerViewRestaurantReservation.addItemDecoration(dividerItemDecoration)
        recyclerViewRestaurantReservation.layoutManager = linearLayoutManager
        restaurantAdapter = RestaurantAdapter( false,object : RestaurantAdapter.ItemClickListener {
            override fun onClickFavourite(item: Restaurants) {

            }

            override fun onItemClick(item: Restaurants) {
                val bundle = Bundle()

                bundle.putParcelable("values", item)
                navigator.load(RestaurantReservationDetailsFragment::class.java).setBundle(bundle).replace(true)
            }

            override fun onClickRatingBar() {
                navigator.load(ReviewFragment::class.java).replace(true)
            }
        })
        recyclerViewRestaurantReservation.adapter = restaurantAdapter
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu_icon, menu)
        val mSearch = menu.findItem(R.id.action_search)
        val icCart = menu.findItem(R.id.ic_cart)
        icCart.isVisible = false
        val mSearchView = mSearch.actionView as SearchView

        val iconClose = mSearchView.findViewById(androidx.appcompat.R.id.search_close_btn) as ImageView
        iconClose.setColorFilter(Color.BLACK)


        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                // restaurantAdapter?.filter?.filter(query)

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                //restaurantAdapter?.filter?.filter(newText)

                return false
            }

        })

        mSearch.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                toolbar.setToolbarTitle(getString(R.string.restaurant_reservation))
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
            }
        }
        return super.onOptionsItemSelected(item)
    }
}