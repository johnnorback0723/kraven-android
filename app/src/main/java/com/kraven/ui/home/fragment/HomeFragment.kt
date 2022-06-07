package com.kraven.ui.home.fragment

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.kraven.R
import com.kraven.application.KravenCustomer
import com.kraven.core.StatusCode
import com.kraven.data.pojo.Parameters
import com.kraven.di.component.FragmentComponent
import com.kraven.extensions.requirePermission
import com.kraven.extensions.viewShow
import com.kraven.ui.activity.HomeActivity
import com.kraven.ui.activity.IsolatedActivity
import com.kraven.ui.address.fragment.AddressListFragment
import com.kraven.ui.cart.fragment.CartFragment
import com.kraven.ui.cart.fragment.CompleteOrderCartFragment
import com.kraven.ui.cart.viewModel.CartViewModel
import com.kraven.ui.home.adapter.RestaurantAdapter
import com.kraven.ui.home.adapter.ServiceAdapter
import com.kraven.ui.home.model.Restaurants
import com.kraven.ui.home.model.Service
import com.kraven.ui.home.viewmodel.HomeViewModel
import com.kraven.ui.my.offer.fragment.MyOfferFragment
import com.kraven.ui.my.order.fragment.MyOrderHistoryFragment
import com.kraven.ui.order.beverage.SpecialBeverageViewModel
import com.kraven.ui.order.food.fragment.OrderFoodFragment
import com.kraven.ui.rating.RatingFragment
import com.kraven.ui.review.fragment.ReviewFragment
import com.kraven.utils.ConstantApp
import com.kraven.utils.Formatter
import com.kraven.utils.Formatter.YYYY_MM_DD_T_HH_MM_SSS_Z
import com.kraven.utils.LocationManager
import kotlinx.android.synthetic.main.home_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import javax.inject.Inject


class HomeFragment : HomeSliderFragment() {

    @Inject
    lateinit var locationManager: LocationManager

    private lateinit var viewModel: HomeViewModel
    private lateinit var viewModelCart: CartViewModel
    private lateinit var specialBeverageViewModel: SpecialBeverageViewModel

    private var serviceAdapter: ServiceAdapter? = null
    private var restaurantAdapter: RestaurantAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var currentLatLng: LatLng? = null
    var textViewCartCount: AppCompatTextView? = null

    override fun createLayout(): Int = R.layout.home_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[HomeViewModel::class.java]
        viewModelCart = ViewModelProviders.of(this, viewModelFactory)[CartViewModel::class.java]
        specialBeverageViewModel = ViewModelProviders.of(this, viewModelFactory)[SpecialBeverageViewModel::class.java]
        viewModel.getRestaurantList.observe(this, { responseBody ->
            when (responseBody.code) {
                StatusCode.CODE_SUCCESS -> {
                    textViewNearBy.text = getString(R.string.nearby, responseBody.data?.size)
                    val sortedList = responseBody.data?.sortedWith(compareBy({ it.availabilityStatus == ConstantApp.RestaurantStatus.CLOSED },
                            { it.availabilityStatus == ConstantApp.RestaurantStatus.CURRENTLY_UNAVAILABLE },
                            { it.availabilityStatus == ConstantApp.RestaurantStatus.AVAILABLE }))
                    restaurantAdapter?.items = sortedList

                    //getRestaurantFutureLists(Formatter.format(Date().toString(), YYYY_MM_DD_T_HH_MM_SSS_Z, "yyyy-MM-dd hh:mm aa", false)!!, responseBody.data!!)
                }
                StatusCode.CODE_NO_DATA -> {
                    restaurantAdapter?.errorMessage = responseBody.message
                }
                else -> showMessage(responseBody.message)
            }

        }) { true }

        viewModel.addFavouriteRestaurant.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {

                }
                else -> showMessage(it.message)
            }

        }) { true }

        viewModel.getPromoCodeList.observe(this, {
            //showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    groupPromoCode.viewShow()
                    setUpViewPager(it.data)
                }
                StatusCode.CODE_NO_DATA -> {

                }
                else -> showMessage(it.message)
            }
        }) { true }

        viewModel.getSetting.observe(this, {
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {

                }

                else -> showMessage(it.message)
            }
        }) { true }

        viewModelCart.placeOrder.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    session.cartModel = null
                    session.cartCount = "0"

                    navigator.load(CompleteOrderCartFragment::class.java).setBundle(Bundle().apply {
                        putString("name", session.saveRestaurantName)
                    }).replace(false)
                }
                StatusCode.CODE_INVALID_REQUEST -> {
                    showLoader(true)
                    //showMessage(it.message)
                    commandDialogYesNo(getString(R.string.app_name), it.message, object : DialogInterfaceYesNo {
                        override fun onClickYes() {

                        }

                        override fun onClickNo() {

                        }

                    })
                    //viewModelCart.placeOrder(session.saveTempParameters!!)
                }
                else -> showMessage(it.message)
            }
        }) { true }

        specialBeverageViewModel.paySpecialBeverageOrder.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    navigator.load(CompleteOrderCartFragment::class.java).setBundle(Bundle().apply {
                        putString("name", session.saveRestaurantName)
                    }).replace(false)
                }
                StatusCode.CODE_INVALID_REQUEST -> {
                    showLoader(true)
                    showMessage(it.message)
                    viewModelCart.placeOrder(session.saveTempParameters!!)
                }
                else -> showMessage(it.message)
            }
        })

        viewModel.addMoneyToWallet.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    session.saveTempWalletParameters = null
                }
                StatusCode.CODE_INVALID_REQUEST -> {
                    showLoader(true)
                    showMessage(it.message)
                    viewModelCart.placeOrder(session.saveTempParameters!!)
                }
                else -> {
                    showMessage(it.message)
                }
            }
        }) { true }

        viewModel.anyLiveData.observe(this, {

        })
    }

    override fun onStart() {
        super.onStart()
        requirePermission(activity!!, "To see a list of vendors turn on your location services.") {
            locationManager.checkLocationEnableAndStartUpdate(true)
            locationManager.getLocationUpdate(object : LocationManager.LocationListener {
                override fun onLocationAvailable(latLng: LatLng) {

                }

                override fun onFail(status: LocationManager.LocationListener.Status) {

                }

            })

            locationManager.locationUpdateLiveData.observe(this, Observer<LatLng> { latLng ->
                currentLatLng = latLng
                val parameters = Parameters()
                parameters.latitude = latLng.latitude.toString()
                parameters.longitude = latLng.longitude.toString()
                parameters.futureDate = Formatter.format(Date().toString(), YYYY_MM_DD_T_HH_MM_SSS_Z, "yyyy-MM-dd", false).toString()
                viewModel.getRestaurantList(parameters)
                locationManager.locationUpdateLiveData.removeObservers(this)
            })
        }

    }

    /*override fun onStart() {
        super.onStart()
        if (isMyServiceRunning(LocationUpdateService::class.java).not()) {
            startLocationService()
        }
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = context!!.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    private fun startLocationService() {
        requirePermission(activity!!, "To see a list of vendors turn on your location services.") {
            val locationServiceIntent = Intent(context!!, LocationUpdateService::class.java)
            locationServiceIntent.action = Common.Actions.START_SERVICE
            if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.O){
                context!!.startForegroundService(locationServiceIntent)
            }else{
                context!!.startService(locationServiceIntent)
            }

        }
    }*/

    override fun bindData() {

        try {
            (activity as HomeActivity).updateApplication()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        requirePermission(activity!!, "To see a list of vendors turn on your location services.") {
            locationManager.checkLocationEnableAndStartUpdate(true)
            locationManager.locationUpdateLiveData.observe(this, Observer<LatLng> { latLng ->
                currentLatLng = latLng
                val parameters = Parameters()
                parameters.latitude = latLng.latitude.toString()
                parameters.longitude = latLng.longitude.toString()
                parameters.futureDate = Formatter.format(Date().toString(), YYYY_MM_DD_T_HH_MM_SSS_Z, "yyyy-MM-dd", false).toString()
                viewModel.getRestaurantList(parameters)
                locationManager.locationUpdateLiveData.removeObservers(this)
            })
        }

        /*if (session.saveTempParameters != null && session.saveOrderPage != null && session.saveOrderPage!!.isNotEmpty()) {
            showLoader(true)
            if (session.saveOrderPage == ConstantApp.PassValue.ORDER_FOOD || session.saveOrderPage == ConstantApp.PassValue.ORDER_FOOD_FUTURE) {
                viewModelCart.placeOrder(session.saveTempParameters!!)
            } else if (session.saveOrderPage == ConstantApp.PassValue.ORDER_BEVERAGE_SPECIAL) {
                specialBeverageViewModel.paySpecialBeverageOrder(session.saveTempParameters!!)
            } else {
                viewModelCart.placeOrderBeverage(session.saveTempParameters!!)
            }
        } else if (session.saveTempWalletParameters != null) {
            viewModel.addMoneyToWallet(session.saveTempWalletParameters!!)
        }*/

        KravenCustomer.tempPromoCode = null
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.home))
        toolbar.setToolbarTextColorWhite(false)
        toolbar.setButtonTextVisibility(false)


        viewModel.getPromoCodeList("1", session.user?.islandId)
        setUpServiceRecyclerView()
        buttonSeeAll.setOnClickListener {
            navigator.load(MyOfferFragment::class.java).replace(false)
        }
        buttonTrackOrder.setOnClickListener {
            navigator.load(MyOrderHistoryFragment::class.java).replace(false)
        }
        viewModel.getSetting()

        viewModel.updateDeviceInfo()
        //viewModel.checkLiveTracking()
    }

    private fun setUpServiceRecyclerView() {
        recyclerViewServices.layoutManager = GridLayoutManager(activity, 2)
        recyclerViewServices.addItemDecoration(com.kraven.coreadapter.GridDividerItemDecoration(activity!!,
                R.drawable.line_divider, R.drawable.line_divider_transparent, 3, R.dimen._15sdp))
        serviceAdapter = ServiceAdapter(object : ServiceAdapter.ItemClickListener {
            override fun OnItemClick(position: Int) {
                when (position) {
                    0 -> {
                        val bundle = Bundle()
                        bundle.putString(ConstantApp.PassValue.ORDER_FOOD, ConstantApp.PassValue.ORDER_FOOD)
                        navigator.loadActivity(IsolatedActivity::class.java,
                                OrderFoodFragment::class.java).addBundle(bundle).start()
                    }
                    //1 -> navigator.loadActivity(IsolatedActivity::class.java, FutureFoodOrderFragment::class.java).start()
                    1 -> {
                        val bundle = Bundle()
                        bundle.putString(ConstantApp.PassValue.ORDER_FOOD, ConstantApp.PassValue.ORDER_BEVERAGE)
                        navigator.loadActivity(IsolatedActivity::class.java, OrderFoodFragment::class.java).addBundle(bundle).start()
                    }
                }
            }
        })

        linearLayoutManager = LinearLayoutManager(activity)
        recyclerViewServices.adapter = serviceAdapter
        serviceAdapter?.items = serviceList()

        val dividerItemDecoration = com.kraven.coreadapter.DividerItemDecoration(recyclerViewRestaurant.context, R.drawable.divider)
        recyclerViewRestaurant.addItemDecoration(dividerItemDecoration)
        recyclerViewRestaurant.layoutManager = linearLayoutManager
        restaurantAdapter = RestaurantAdapter(false,object : RestaurantAdapter.ItemClickListener {
            override fun onClickFavourite(item: Restaurants) {
                val parameters = Parameters()
                parameters.restaurantId = item.id.toString()
                viewModel.addFavouriteRestaurant(parameters)
            }

            override fun onItemClick(item: Restaurants) {
                if (item.distanceRadius!! > 0) {
                    val bundle = Bundle()
                    bundle.putString(ConstantApp.PassValue.RESTAURANT_ID, item.id.toString())
                    bundle.putString(ConstantApp.PassValue.ORDER_FOOD, ConstantApp.PassValue.ORDER_FOOD)
                    bundle.putString(ConstantApp.PassValue.RESTAURANT_DISTANCE, item.distance.toString())
                    bundle.putParcelable(ConstantApp.PassValue.CURRENT_LAT_LONG, currentLatLng)
                    bundle.putInt("select_address", 1)
                    navigator.loadActivity(IsolatedActivity::class.java, AddressListFragment::class.java).addBundle(bundle).start()
                } else {
                    val bundle = Bundle()
                    bundle.putString(ConstantApp.PassValue.RESTAURANT_ID, item.id.toString())
                    bundle.putParcelable(ConstantApp.PassValue.CURRENT_LAT_LONG, currentLatLng)
                    bundle.putString(ConstantApp.PassValue.RESTAURANT_DISTANCE, item.distance.toString())
                    bundle.putString(ConstantApp.PassValue.ORDER_FOOD, ConstantApp.PassValue.ORDER_FOOD)
                    navigator.loadActivity(IsolatedActivity::class.java).addBundle(bundle).setPage(RestaurantDetailsFragment::class.java).start()
                }
            }

            override fun onClickRatingBar() {
                navigator.loadActivity(IsolatedActivity::class.java, ReviewFragment::class.java).start()
            }
        })
        recyclerViewRestaurant.adapter = restaurantAdapter
    }


    private fun checkWriteExternalPermission(): Boolean {
        val permission = android.Manifest.permission.ACCESS_FINE_LOCATION
        val res = context!!.checkCallingOrSelfPermission(permission)
        return res == PackageManager.PERMISSION_GRANTED
    }

    override fun onResume() {
        super.onResume()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu_icon, menu)
        val mSearch = menu.findItem(R.id.action_search)
        val mSearchView = mSearch.actionView as SearchView
        mSearchView.maxWidth = Integer.MAX_VALUE
        val view = menu.findItem(R.id.ic_cart).actionView
        textViewCartCount = view.findViewById(R.id.textViewCartCount)
        val iconClose = mSearchView.findViewById(androidx.appcompat.R.id.search_close_btn) as ImageView
        iconClose.setColorFilter(Color.BLACK)
        if (session.cartCount?.isNotEmpty()!! && session.cartCount!!.toInt() > 0) textViewCartCount!!.visibility = View.VISIBLE else textViewCartCount!!.visibility = View.GONE
        textViewCartCount!!.text = session.cartCount
        view.setOnClickListener {
            navigator.loadActivity(IsolatedActivity::class.java).setPage(CartFragment::class.java).start()
        }

        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                val bundle = Bundle()
                bundle.putString(ConstantApp.PassValue.ORDER_FOOD, ConstantApp.PassValue.ORDER_FOOD)
                bundle.putString("query", query)
                navigator.loadActivity(IsolatedActivity::class.java, OrderFoodFragment::class.java).addBundle(bundle).start()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun serviceList(): List<Service.Services> {
        val serviceList = mutableListOf<Service.Services>()
        serviceList.add(Service.Services(src = R.drawable.order_food, name = "Order Food"))
        //serviceList.add(Service.Services(src = R.drawable.future_food_order, name = "Future Food Order"))
        serviceList.add(Service.Services(src = R.drawable.order_beverage, name = "Order Beverage"))
        return serviceList
    }


    override fun onDestroy() {
        super.onDestroy()
        if (timer != null) {
            timer.cancel()
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(itemAddedToCartEvent: String) {
        Log.d("Hlink", "Home = itemAddedToCartEvent ${session.cartCount}")
        if (session.cartCount?.isNotEmpty()!! && session.cartCount!!.toInt() > 0) {
            textViewCartCount?.visibility = View.VISIBLE
            textViewCartCount?.text = session.cartCount
        } else {
            textViewCartCount?.visibility = View.GONE
        }
    }


    private fun openRatingScreen(orderId: String, tag: String) {
        navigator.loadActivity(IsolatedActivity::class.java).setPage(RatingFragment::class.java).addBundle(Bundle().apply {
            putString("OrderId", orderId)
            putString("tag", tag)
        }).start()
    }

}