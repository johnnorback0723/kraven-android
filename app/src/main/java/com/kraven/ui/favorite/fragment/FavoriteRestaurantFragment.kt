package com.kraven.ui.favorite.fragment


import android.graphics.Color
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.kraven.R
import com.kraven.core.StatusCode
import com.kraven.coreadapter.EndlessRecyclerViewScrollListener
import com.kraven.data.pojo.Parameters
import com.kraven.di.component.FragmentComponent
import com.kraven.extensions.getCurrentLatLong
import com.kraven.extensions.requirePermission
import com.kraven.ui.activity.IsolatedActivity
import com.kraven.ui.address.fragment.AddressListFragment
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.favorite.viewmodel.FavoriteViewModel
import com.kraven.ui.home.adapter.RestaurantAdapter
import com.kraven.ui.home.fragment.RestaurantDetailsFragment
import com.kraven.ui.home.model.Restaurants
import com.kraven.ui.home.viewmodel.HomeViewModel
import com.kraven.utils.ConstantApp
import com.kraven.utils.LocationManager
import kotlinx.android.synthetic.main.favorite_restaurant_fragment.*
import javax.inject.Inject


class FavoriteRestaurantFragment : BaseFragment() {
    lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var viewModel: FavoriteViewModel
    private lateinit var homeViewModel: HomeViewModel
    //var favoriteRestaurantAdapter: FavoriteRestaurantAdapter? = null
    var recyclerViewRestaurant: RestaurantAdapter? = null
    //private var filterList = ArrayList<Restaurants>()
    private var endlessRecyclerViewScrollListener :EndlessRecyclerViewScrollListener?=null
    @Inject
    lateinit var locationManager: LocationManager

    private var cureentLatLng: LatLng? = null

    private var pages = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)[FavoriteViewModel::class.java]
        homeViewModel = ViewModelProviders.of(this, viewModelFactory)[HomeViewModel::class.java]
        registerObserver()
    }

   /* override fun onResume() {
        super.onResume()
        pages = 1
    }*/

    private fun registerObserver() {
        viewModel.getFavoriteList.observe(this,
                { it ->
                    when (it.code) {
                        StatusCode.CODE_SUCCESS -> {
                            recyclerViewRestaurant?.clearAllItem()
                            val sortedList = it.data?.sortedWith(compareBy({ it.availabilityStatus == ConstantApp.RestaurantStatus.CLOSED },
                                    { it.availabilityStatus == ConstantApp.RestaurantStatus.CURRENTLY_UNAVAILABLE },
                                    { it.availabilityStatus == ConstantApp.RestaurantStatus.AVAILABLE }))
                            recyclerViewRestaurant?.setItems(sortedList, pages)
                        }
                        StatusCode.CODE_NO_DATA -> {
                            if(pages==1) {
                                recyclerViewRestaurant?.clearAllItem()
                                endlessRecyclerViewScrollListener?.resetState()
                                recyclerViewRestaurant?.errorMessage = it.message
                            }
                        }
                    }
                })

    }

    override fun createLayout(): Int = R.layout.favorite_restaurant_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        setUpRecyclerView()
        setHasOptionsMenu(true)
        requirePermission(activity!!, "This app need permissions to use this feature.You can grant them in app settings.") {
            locationManager.checkLocationEnableAndStartUpdate(true)
            locationManager.locationUpdateLiveData.observe(this.viewLifecycleOwner, {
                cureentLatLng = it
                //filterList.clear()
                recyclerViewRestaurant?.clearAllItem()
                endlessRecyclerViewScrollListener?.resetState()
                viewModel.getFavoriteList(it, pages.toString(), "")
                locationManager.locationUpdateLiveData.removeObservers(this)
            })
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        setHasOptionsMenu(false)
    }

    fun setUpRecyclerView()
    {
        linearLayoutManager = LinearLayoutManager(activity!!)
        val ATTRS = intArrayOf(android.R.attr.listDivider)

        val a = activity!!.obtainStyledAttributes(ATTRS)
        val divider = a.getDrawable(0)
        val inset = resources.getDimensionPixelSize(R.dimen._10sdp)
        val insetDivider = InsetDrawable(divider, inset, 0, inset, 0)
        a.recycle()

        recyclerViewFavorite.layoutManager = linearLayoutManager
        val dividerItemDecoration = DividerItemDecoration(recyclerViewFavorite.context,
                linearLayoutManager.orientation)
        dividerItemDecoration.setDrawable(insetDivider)
        recyclerViewFavorite.addItemDecoration(dividerItemDecoration)
        recyclerViewRestaurant = RestaurantAdapter(false,object : RestaurantAdapter.ItemClickListener {
            override fun onClickFavourite(item: Restaurants) {
                val parameters = Parameters()
                    parameters.restaurantId = item.id.toString()
                homeViewModel.addFavouriteRestaurant(parameters)


        }

            override fun onItemClick(item: Restaurants) {

                if (item.distanceRadius!! > 0) {
                    val bundle = Bundle()
                    bundle.putString(ConstantApp.PassValue.RESTAURANT_ID, item.id.toString())
                    bundle.putString(ConstantApp.PassValue.RESTAURANT_DISTANCE, item.distance.toString())
                    bundle.putParcelable(ConstantApp.PassValue.CURRENT_LAT_LONG, cureentLatLng)
                    bundle.putString(ConstantApp.PassValue.ORDER_FOOD, ConstantApp.PassValue.ORDER_FOOD)
                    bundle.putInt("select_address", 1)
                    navigator.loadActivity(IsolatedActivity::class.java, AddressListFragment::class.java).addBundle(bundle).start()
                } else {
                    val bundle = Bundle()
                    bundle.putString(ConstantApp.PassValue.RESTAURANT_ID, item.id.toString())
                    bundle.putParcelable(ConstantApp.PassValue.CURRENT_LAT_LONG, cureentLatLng)
                    bundle.putString(ConstantApp.PassValue.RESTAURANT_DISTANCE, item.distance.toString())
                    bundle.putString(ConstantApp.PassValue.ORDER_FOOD, ConstantApp.PassValue.ORDER_FOOD)
                    navigator.loadActivity(IsolatedActivity::class.java).addBundle(bundle).setPage(RestaurantDetailsFragment::class.java).start()
                }
            }

            override fun onClickRatingBar() {
               // navigator.loadActivity(IsolatedActivity::class.java, ReviewFragment::class.java).start()
            }
        })
        recyclerViewFavorite.adapter = recyclerViewRestaurant
        endlessRecyclerViewScrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                pages =page
                viewModel.getFavoriteBeverageList(cureentLatLng!!, page.toString(), "")
            }
        }
        recyclerViewFavorite.addOnScrollListener(endlessRecyclerViewScrollListener!!)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu_icon, menu)
        val mSearch = menu.findItem(R.id.action_search)
        val icCart = menu.findItem(R.id.ic_cart)
        icCart.isVisible = false
        val mSearchView = mSearch.actionView as SearchView

        val iconClose = mSearchView.findViewById(androidx.appcompat.R.id.search_close_btn) as ImageView
        iconClose.setColorFilter(Color.BLACK)


        iconClose.setOnClickListener {
            mSearchView.setQuery("", false)
            mSearchView.clearFocus()
            //filterList.clear()
            recyclerViewRestaurant?.clearAllItem()
            endlessRecyclerViewScrollListener?.resetState()
            getCurrentLatLong(locationManager){
                viewModel.getFavoriteList(it, "1", "")
            }
        }


        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                //filterList.clear()
                recyclerViewRestaurant?.clearAllItem()
                endlessRecyclerViewScrollListener?.resetState()
                pages = 1
                getCurrentLatLong(locationManager){
                    viewModel.getFavoriteList(it, pages.toString(), query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {

                return false
            }

        })

        mSearch.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                mSearchView.setQuery("", false)
                mSearchView.clearFocus()
                //filterList.clear()
                recyclerViewRestaurant?.clearAllItem()
                endlessRecyclerViewScrollListener?.resetState()
                pages = 1
                getCurrentLatLong(locationManager){
                    viewModel.getFavoriteList(it, pages.toString(), "")
                }

                return true
            }

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                mSearchView.setQuery("", false)
                mSearchView.clearFocus()
                //filterList.clear()
                recyclerViewRestaurant?.clearAllItem()
                endlessRecyclerViewScrollListener?.resetState()
                pages = 1
                getCurrentLatLong(locationManager){
                    viewModel.getFavoriteList(it, pages.toString(), "")
                }
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