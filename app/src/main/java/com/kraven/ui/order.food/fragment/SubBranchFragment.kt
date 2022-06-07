package com.kraven.ui.order.food.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.kraven.R
import com.kraven.core.StatusCode
import com.kraven.coreadapter.EndlessRecyclerViewScrollListener
import com.kraven.data.pojo.Parameters
import com.kraven.di.component.FragmentComponent
import com.kraven.extensions.extraNotNull
import com.kraven.extensions.getCurrentLatLong
import com.kraven.extensions.requirePermission
import com.kraven.extensions.viewShow
import com.kraven.ui.activity.IsolatedActivity
import com.kraven.ui.address.fragment.AddressListFragment
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.cart.fragment.CartFragment
import com.kraven.ui.home.adapter.RestaurantAdapter
import com.kraven.ui.home.fragment.RestaurantDetailsFragment
import com.kraven.ui.home.model.Restaurants
import com.kraven.ui.home.viewmodel.HomeViewModel
import com.kraven.ui.order.beverage.fragment.SpecialOrderFragment
import com.kraven.ui.review.fragment.ReviewFragment
import com.kraven.utils.ConstantApp
import com.kraven.utils.Formatter
import com.kraven.utils.LocationManager
import kotlinx.android.synthetic.main.order_food.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import javax.inject.Inject


class SubBranchFragment : BaseFragment() {
    private val restaurantDetails by extraNotNull<Restaurants>(ConstantApp.PassValue.ITEM)
    private lateinit var homeViewModel: HomeViewModel
    var restaurantAdapter: RestaurantAdapter? = null
    var textViewStatus: AppCompatTextView? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    var textViewCartCount: AppCompatTextView? = null
    private var deliveryTypes: ArrayList<String>? = null
    private var cuisinLists: ArrayList<String>? = null
    //private var page = 1
    private var endlessRecyclerViewScrollListener: EndlessRecyclerViewScrollListener? = null


    @Inject
    lateinit var locationManager: LocationManager


    private val orderPage: String? by lazy {
        val args = arguments ?: throw IllegalStateException("Missing arguments")
        args.getString(ConstantApp.PassValue.ORDER_FOOD)
    }

    private val cuisineId: String? by lazy {
        val args = arguments ?: throw IllegalArgumentException("Missing arguments")
        args.getString(ConstantApp.PassValue.CUISINE_ID)
    }

    private val cuisineName: String? by lazy {
        val args = arguments ?: throw IllegalArgumentException("Missing arguments")
        args.getString(ConstantApp.PassValue.CUISINE_NAME)
    }

    private val query: String? by lazy {
        val args = arguments ?: throw  java.lang.IllegalArgumentException("Missing arguments")
        args.getString("query")
    }

    private val selectedDate: String? by lazy {
        val args = arguments ?: throw IllegalStateException("Missing arguments")
        args.getString(ConstantApp.PassValue.FUTUREDATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = ViewModelProviders.of(this, viewModelFactory)[HomeViewModel::class.java]

        homeViewModel.restaurantBranchListLiveData.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    setRecyclerViewData(it.data!!)
                }

                StatusCode.CODE_NO_DATA -> {
                  /*  if (page == 1) {
                        restaurantAdapter?.clearAllItem()
                        endlessRecyclerViewScrollListener?.resetState()
                        restaurantAdapter?.errorMessage = it.message
                    } else {*/
                        restaurantAdapter?.stopLoader()
                        restaurantAdapter?.errorMessage = it.message
                  //  }
                }
                else -> showMessage(it.message)
            }

        }) { true }

        homeViewModel.addToFavoriteRestaurant.observe(this, { responseBody ->
            showLoader(false)
            when (responseBody.code) {
                StatusCode.CODE_SUCCESS -> {

                }

                else -> showMessage(responseBody.message)
            }

        }) { true }

        homeViewModel.getBeverageList.observe(this, { it ->
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    restaurantAdapter?.clearAllItem()
                    endlessRecyclerViewScrollListener?.resetState()
                    val sortedList = it.data!!.sortedWith(compareBy({ it.availabilityStatus == ConstantApp.RestaurantStatus.CLOSED },
                            { it.availabilityStatus == ConstantApp.RestaurantStatus.CURRENTLY_UNAVAILABLE },
                            { it.availabilityStatus == ConstantApp.RestaurantStatus.AVAILABLE }))
                    restaurantAdapter?.setItems(sortedList)
                }
                StatusCode.CODE_NO_DATA -> {
                   /* if (page == 1) {
                        restaurantAdapter?.clearAllItem()
                        endlessRecyclerViewScrollListener?.resetState()
                        restaurantAdapter?.errorMessage = it.message
                    } else {*/
                        restaurantAdapter?.stopLoader()
                        restaurantAdapter?.errorMessage = it.message
                   // }
                }
                else -> showMessage(it.message)
            }
        })

    }

    private fun setRecyclerViewData(data: List<Restaurants>) {
        val sortedList = data.sortedWith(compareBy({ it.availabilityStatus == ConstantApp.RestaurantStatus.CLOSED },
                { it.availabilityStatus == ConstantApp.RestaurantStatus.CURRENTLY_UNAVAILABLE },
                { it.availabilityStatus == ConstantApp.RestaurantStatus.AVAILABLE }))
        restaurantAdapter?.setItems(sortedList)
    }

    override fun createLayout(): Int = R.layout.order_food

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {

        toolbar.showToolbar(true)
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        setHasOptionsMenu(true)
        setUpRecyclerView()

        requirePermission(requireActivity(), "To see a list of vendors turn on your location services.") {
            locationManager.checkLocationEnableAndStartUpdate(true)
            locationManager.locationUpdateLiveData.observe(this, { latLng ->
                homeViewModel.currentLatLng = latLng
                when (orderPage) {
                    ConstantApp.PassValue.ORDER_FOOD -> {
                        toolbar.setToolbarTitle(getString(R.string.order_food))
                        callRestaurantList()
                    }
                    ConstantApp.PassValue.ORDER_BEVERAGE -> {
                        toolbar.setToolbarTitle(getString(R.string.order_beverage))
                        callBeverageList()
                    }
                    else -> {
                        toolbar.setToolbarTitle(getString(R.string.order_food))
                        if (cuisineId != null) {
                            homeViewModel.getRestaurantList(Parameters(latitude = latLng.latitude.toString(), longitude = latLng.longitude.toString(),
                                cuisineIds = arrayListOf(cuisineId!!)))
                        }
                    }
                }
                locationManager.locationUpdateLiveData.removeObservers(this)
            })
        }

        buttonSpecialOrder.setOnClickListener {
            navigator.load(SpecialOrderFragment::class.java).replace(true)
        }

    }

    private fun callBeverageList() {
        val parameters = Parameters()
        parameters.mainBranchId = restaurantDetails.id.toString()
        parameters.latitude = homeViewModel.currentLatLng?.latitude.toString()
        parameters.longitude = homeViewModel.currentLatLng?.longitude.toString()
      //  parameters.page = page.toString()

        homeViewModel.beverageBranchList(parameters)
    }

    private fun callRestaurantList() {
        val parameters = Parameters()
        parameters.mainBranchId = restaurantDetails.mainBranchId.toString()
        parameters.latitude = homeViewModel.currentLatLng?.latitude.toString()
        parameters.longitude = homeViewModel.currentLatLng?.longitude.toString()
        parameters.search = query
       // parameters.page = page.toString()
        /*if (selectedDate != null && selectedDate.isNotEmpty()) {
            parameters.futureDate = Formatter.format(selectedDate, "yyyy-MM-dd HH:mm aa", "yyyy-MM-dd")
        } else {
            parameters.futureDate = Formatter.format(Date().toString(), Formatter.YYYY_MM_DD_T_HH_MM_SSS_Z, Formatter.YYYY_MM_DD_HH_MM_SS, false).toString()
        }*/
        if (deliveryTypes != null && cuisinLists != null) {
            if (deliveryTypes?.size != 0) {
                parameters.deliveryType = deliveryTypes?.joinToString(", ", transform = { it })
            }
            if (cuisinLists?.size != 0) {
                parameters.cuisineIds = cuisinLists
            }
        } else if (cuisineId != null && cuisineId!!.isNotEmpty()) {
            parameters.cuisineIds = arrayListOf(cuisineId!!)
        }
        homeViewModel.restaurantBranchList(parameters)
    }

    private fun setUpRecyclerView() {
        linearLayoutManager = LinearLayoutManager(activity)

        val dividerItemDecoration = DividerItemDecoration(recyclerViewOrderFood.context, linearLayoutManager!!.orientation)
        recyclerViewOrderFood.addItemDecoration(dividerItemDecoration)
        recyclerViewOrderFood.layoutManager = linearLayoutManager
        restaurantAdapter = RestaurantAdapter(false,object : RestaurantAdapter.ItemClickListener {
            override fun onClickFavourite(item: Restaurants) {
                val parameters = Parameters()
                if (orderPage == ConstantApp.PassValue.ORDER_FOOD) {
                    parameters.restaurantId = item.id.toString()
                    homeViewModel.addFavouriteRestaurant(parameters)
                } else if (orderPage == ConstantApp.PassValue.ORDER_BEVERAGE) {
                    parameters.beverageId = item.id.toString()
                    homeViewModel.addFavouriteBeverage(parameters)
                }
            }

            override fun onItemClick(item: Restaurants) {

                if (item.distanceRadius!! > 0) {
                    val bundle = Bundle()
                    bundle.putString(ConstantApp.PassValue.RESTAURANT_ID, item.id.toString())
                    bundle.putString(ConstantApp.PassValue.RESTAURANT_DISTANCE, item.distance.toString())
                    bundle.putParcelable(ConstantApp.PassValue.CURRENT_LAT_LONG, homeViewModel.currentLatLng)
                    bundle.putString(ConstantApp.PassValue.ORDER_FOOD, orderPage)
                    if (orderPage == ConstantApp.PassValue.ORDER_FOOD_FUTURE) {
                        bundle.putString(ConstantApp.PassValue.FUTUREDATE, selectedDate)
                    }
                    bundle.putInt("select_address", 1)
                    navigator.loadActivity(IsolatedActivity::class.java, AddressListFragment::class.java).addBundle(bundle).start()
                } else {

                        val bundle = Bundle()
                        bundle.putString(ConstantApp.PassValue.RESTAURANT_ID, item.id.toString())
                        bundle.putParcelable(ConstantApp.PassValue.CURRENT_LAT_LONG, homeViewModel.currentLatLng)
                        bundle.putString(ConstantApp.PassValue.RESTAURANT_DISTANCE, item.distance.toString())
                        bundle.putString(ConstantApp.PassValue.ORDER_FOOD, orderPage)
                        if (orderPage == ConstantApp.PassValue.ORDER_FOOD_FUTURE) {
                            bundle.putString(ConstantApp.PassValue.FUTUREDATE, selectedDate)
                        }
                        navigator.loadActivity(IsolatedActivity::class.java).addBundle(bundle).setPage(RestaurantDetailsFragment::class.java).start()

                }
            }

            override fun onClickRatingBar() {
                navigator.loadActivity(IsolatedActivity::class.java, ReviewFragment::class.java).start()
            }
        })


        recyclerViewOrderFood.adapter = restaurantAdapter



    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.order_food_menu_icon, menu)

        textViewStatus = menu.findItem(R.id.ic_filter).actionView.findViewById(R.id.textViewDetailsStatus) as AppCompatTextView
        textViewCartCount = menu.findItem(R.id.ic_cart).actionView.findViewById(R.id.textViewCartCount) as AppCompatTextView
        if (session.cartCount?.isNotEmpty()!! && session.cartCount!!.toInt() > 0) textViewCartCount!!.visibility = View.VISIBLE else textViewCartCount!!.visibility = View.GONE
        textViewCartCount!!.text = session.cartCount
        textViewStatus!!.setOnClickListener {
            navigator.loadActivity(IsolatedActivity::class.java).setPage(OrderFoodFilterFragment::class.java).forResult(10).start()
        }
        menu.findItem(R.id.ic_cart).actionView.setOnClickListener {
            navigator.loadActivity(IsolatedActivity::class.java, CartFragment::class.java).start()
        }


        if (orderPage == ConstantApp.PassValue.ORDER_BEVERAGE) {
            menu.findItem(R.id.ic_filter).isVisible = false
            buttonSpecialOrder.viewShow()
            textViewSpecificBeverage.viewShow()
        }
        val mSearch = menu.findItem(R.id.action_search)
        val mSearchView = mSearch.actionView as SearchView

        val iconClose = mSearchView.findViewById(androidx.appcompat.R.id.search_close_btn) as ImageView
        iconClose.setColorFilter(Color.BLACK)


        mSearch.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                mSearchView.setQuery("", false)
                mSearchView.clearFocus()

                /*if (orderPage == ConstantApp.PassValue.ORDER_BEVERAGE) {
                    restaurantAdapter?.clearAllItem()
                    endlessRecyclerViewScrollListener?.resetState()
                    callBeverageList(1)
                } else {
                    restaurantAdapter?.clearAllItem()
                    endlessRecyclerViewScrollListener?.resetState()
                    callRestaurantList(1)
                }*/
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                mSearchView.setQuery("", false)
                mSearchView.clearFocus()
                if (orderPage == ConstantApp.PassValue.ORDER_BEVERAGE) {
                    restaurantAdapter?.clearAllItem()
                    endlessRecyclerViewScrollListener?.resetState()
                    callBeverageList()
                } else {
                    restaurantAdapter?.clearAllItem()
                    endlessRecyclerViewScrollListener?.resetState()
                    callRestaurantList()
                }
                return true
            }
        })

        iconClose.setOnClickListener {
            mSearchView.setQuery("", false)
            mSearchView.clearFocus()
            val parameters = Parameters()
            getCurrentLatLong(locationManager) {
                parameters.latitude = it.latitude.toString()
                parameters.longitude = it.longitude.toString()
                if (orderPage == ConstantApp.PassValue.ORDER_BEVERAGE) {
                    restaurantAdapter?.clearAllItem()
                    endlessRecyclerViewScrollListener?.resetState()
                    callBeverageList()
                } else {
                    restaurantAdapter?.clearAllItem()
                    endlessRecyclerViewScrollListener?.resetState()
                    callRestaurantList()
                }
            }
        }

        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                if (homeViewModel.currentLatLng != null) {
                    val parameters = Parameters()
                    parameters.latitude = homeViewModel.currentLatLng!!.latitude.toString()
                    parameters.longitude = homeViewModel.currentLatLng!!.longitude.toString()
                    parameters.search = query
                    parameters.page = "1"
                    if (cuisineId != null && cuisineId!!.isNotEmpty()) {
                        parameters.cuisineIds = arrayListOf(cuisineId!!)
                    }
                    if (orderPage == ConstantApp.PassValue.ORDER_BEVERAGE) {
                        restaurantAdapter?.clearAllItem()
                        endlessRecyclerViewScrollListener?.resetState()
                        homeViewModel.getBeverageList(parameters)
                    } else {
                        restaurantAdapter?.clearAllItem()
                        endlessRecyclerViewScrollListener?.resetState()
                        homeViewModel.getRestaurantList(parameters)
                    }

                    return false
                } else {
                    return false
                }
            }

            override fun onQueryTextChange(newText: String): Boolean {

                return false
            }

        })

        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10) {
            if (data?.getStringArrayListExtra(ConstantApp.PassValue.deliveryTypes) != null
                    && data.getStringArrayListExtra(ConstantApp.PassValue.cuisinLists) != null) {
                deliveryTypes = data.getStringArrayListExtra(ConstantApp.PassValue.deliveryTypes)
                        ?: throw java.lang.IllegalStateException("Missing Argument")
                cuisinLists = data.getStringArrayListExtra(ConstantApp.PassValue.cuisinLists)
                        ?: throw java.lang.IllegalStateException("Missing Argument")

                if (deliveryTypes != null && cuisinLists != null) {
                    val parameters = Parameters()
                    parameters.latitude = homeViewModel.currentLatLng!!.latitude.toString()
                    parameters.longitude = homeViewModel.currentLatLng!!.longitude.toString()
                    if (deliveryTypes?.size != 0) {
                        parameters.deliveryType = deliveryTypes?.joinToString(", ", transform = { it })
                    }
                    if (cuisinLists?.size != 0) {
                        parameters.cuisineIds = cuisinLists
                    }
                    if (selectedDate != null && selectedDate!!.isNotEmpty()) {
                        parameters.futureDate = Formatter.format(selectedDate!!, "yyyy-MM-dd HH:mm aa", "yyyy-MM-dd")
                    }
                    restaurantAdapter?.clearAllItem()
                    endlessRecyclerViewScrollListener?.resetState()
                    homeViewModel.getRestaurantList(parameters)
                }
            } else {
                deliveryTypes = null
                cuisinLists = null
                restaurantAdapter?.clearAllItem()
                endlessRecyclerViewScrollListener?.resetState()
                if (orderPage == ConstantApp.PassValue.ORDER_BEVERAGE) {
                    restaurantAdapter?.clearAllItem()
                    endlessRecyclerViewScrollListener?.resetState()
                    callBeverageList()
                } else {
                    restaurantAdapter?.clearAllItem()
                    endlessRecyclerViewScrollListener?.resetState()
                    callRestaurantList()
                }
            }
        }
    }


    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(itemAddedToCartEvent: String) {
        if (session.cartCount?.isNotEmpty()!! && session.cartCount!!.toInt() > 0) {
            textViewCartCount?.visibility = View.VISIBLE
            textViewCartCount?.text = session.cartCount
        } else {
            textViewCartCount?.visibility = View.GONE
        }
    }
}



