package com.kraven.ui.order.beverage.fragment

import android.content.Intent
import android.graphics.Color
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.kraven.R
import com.kraven.data.pojo.Parameters
import com.kraven.di.component.FragmentComponent
import com.kraven.extensions.requirePermission
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.home.adapter.RestaurantAdapter
import com.kraven.ui.home.model.Restaurants

import com.kraven.ui.home.viewmodel.HomeViewModel
import com.kraven.utils.LocationManager
import kotlinx.android.synthetic.main.beverage_fragment.*
import javax.inject.Inject


class BeverageListFragment : BaseFragment() {

    @Inject
    lateinit var locationManager: LocationManager

    private var adapterRestaurants: RestaurantAdapter? = null
    private lateinit var viewModel: HomeViewModel
    private var menus: Menu? = null
    private var currentLatLng: LatLng? = null


    override fun createLayout(): Int = R.layout.beverage_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.order_beverage))
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        setHasOptionsMenu(true)
        setUpRecyclerView()
        viewModel = ViewModelProviders.of(this, viewModelFactory)[HomeViewModel::class.java]

        viewModel.getBeverageList.observe(this,
                { responseBody ->
                    adapterRestaurants?.items = responseBody.data
                }) { true }


        requirePermission(activity!!, "This app need permissions to use this feature.You can grant them in app settings.") {
            locationManager.checkLocationEnableAndStartUpdate(true)
            locationManager.locationUpdateLiveData.observe(this, {
                currentLatLng = it
                val parameters = Parameters()
                parameters.latitude = it.latitude.toString()
                parameters.longitude = it.longitude.toString()
                locationManager.locationUpdateLiveData.removeObservers(this)
            })
        }
    }

    private fun setUpRecyclerView() {
        recyclerViewBeverageList.layoutManager = GridLayoutManager(activity, 1)
        adapterRestaurants = RestaurantAdapter(true, object : RestaurantAdapter.ItemClickListener {
            override fun onItemClick(item: Restaurants) {

            }

            override fun onClickRatingBar() {

            }

            override fun onClickFavourite(item: Restaurants) {

            }
        })

        recyclerViewBeverageList.adapter = adapterRestaurants
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menus = menu
        inflater.inflate(R.menu.order_beverage, menu)

        val specialOrder = menu.findItem(R.id.ic_special_order)
        specialOrder.icon = ContextCompat.getDrawable(activity!!, R.drawable.ic_special_order_add_orange)
        val mSearch = menu.findItem(R.id.action_search)
        val mSearchView = mSearch.actionView as SearchView

        val iconClose = mSearchView.findViewById(androidx.appcompat.R.id.search_close_btn) as ImageView
        iconClose.setColorFilter(Color.BLACK)


        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {

                return false
            }

        })

        mSearch.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                toolbar.setToolbarTitle(getString(R.string.order_beverage))
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
            R.id.ic_special_order -> {
                navigator.load(SpecialOrderFragment::class.java).replace(true)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101) {
            menus?.getItem(1)?.icon = ContextCompat.getDrawable(activity!!, R.drawable.ic_special_order_add)
        }
    }
}