package com.kraven.ui.home.fragment

import android.content.pm.PackageInfo
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.kraven.R
import com.kraven.application.KravenCustomer
import com.kraven.core.StatusCode
import com.kraven.coreadapter.ItemOffsetDecoration
import com.kraven.coreadapter.OnRecycleItemClick
import com.kraven.di.component.FragmentComponent
import com.kraven.extensions.getViewModel
import com.kraven.extensions.viewShow
import com.kraven.ui.activity.HomeActivity
import com.kraven.ui.activity.IsolatedActivity
import com.kraven.ui.authentication.fragement.SelectIslandFragment
import com.kraven.ui.authentication.viewmodel.AuthViewModel
import com.kraven.ui.cart.fragment.CartFragment
import com.kraven.ui.cart.fragment.CompleteOrderCartFragment
import com.kraven.ui.cart.viewModel.CartViewModel
import com.kraven.ui.edit.profile.fragment.EditProfileFragment
import com.kraven.ui.home.adapter.AdapterAllCuisine
import com.kraven.ui.home.adapter.ServiceAdapter
import com.kraven.ui.home.model.AllCuisine
import com.kraven.ui.home.model.Service
import com.kraven.ui.home.viewmodel.HomeViewModel
import com.kraven.ui.my.offer.fragment.MyOfferFragment
import com.kraven.ui.my.order.fragment.MyOrderHistoryFragment
import com.kraven.ui.order.beverage.SpecialBeverageViewModel
import com.kraven.ui.order.food.fragment.OrderFoodFragment
import com.kraven.utils.ConstantApp
import com.kraven.utils.LocationManager
import kotlinx.android.synthetic.main.home_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject


class HomeNewFragment : HomeSliderFragment() {
    private val authViewModel by getViewModel<AuthViewModel>()

    @Inject
    lateinit var locationManager: LocationManager

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var viewModelCart: CartViewModel
    private lateinit var specialBeverageViewModel: SpecialBeverageViewModel


    private var serviceAdapter: ServiceAdapter? = null
    private var adapterAllCuisine: AdapterAllCuisine? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var currentLatLng: LatLng? = null
    var textViewCartCount: AppCompatTextView? = null

    override fun createLayout(): Int = R.layout.home_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        homeViewModel = ViewModelProviders.of(this, viewModelFactory)[HomeViewModel::class.java]
        viewModelCart = ViewModelProviders.of(this, viewModelFactory)[CartViewModel::class.java]
        specialBeverageViewModel = ViewModelProviders.of(this, viewModelFactory)[SpecialBeverageViewModel::class.java]

        homeViewModel.getNewCuisineListLiveData.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    recyclerViewRestaurant.viewShow()
                    val arrayList = ArrayList<AllCuisine.Cuisine>()
                    arrayList.addAll(it.data?.customList!!)
                    arrayList.addAll(it.data.cuisineList!!)

                    adapterAllCuisine?.items = arrayList
                }
                else -> showMessage(it.message)
            }
        })

        homeViewModel.addFavouriteRestaurant.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {

                }
                else -> showMessage(it.message)
            }

        }) { true }

        homeViewModel.getPromoCodeList.observe(this, {
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

        homeViewModel.getSetting.observe(this, {
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
                    // showMessage(it.message)
                    //viewModelCart.placeOrder(session.saveTempParameters!!)
                    commandDialogYesNo(getString(R.string.app_name), it.message, object : DialogInterfaceYesNo {
                        override fun onClickYes() {

                        }

                        override fun onClickNo() {

                        }

                    })
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
                    commandDialogYesNo(getString(R.string.app_name), it.message, object : DialogInterfaceYesNo {
                        override fun onClickYes() {

                        }

                        override fun onClickNo() {

                        }
                    })
                    //showMessage(it.message)
                    //viewModelCart.placeOrder(session.saveTempParameters!!)
                }
                else -> showMessage(it.message)
            }
        })

        homeViewModel.addMoneyToWallet.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    session.saveTempWalletParameters = null
                }
                StatusCode.CODE_INVALID_REQUEST -> {
                    showLoader(true)
                    commandDialogYesNo(getString(R.string.app_name), it.message, object : DialogInterfaceYesNo {
                        override fun onClickYes() {

                        }

                        override fun onClickNo() {

                        }

                    })
                    //showMessage(it.message)
                    //viewModelCart.placeOrder(session.saveTempParameters!!)
                }
                else -> {
                    showMessage(it.message)
                }
            }
        }) { true }

        /*homeViewModel.getUserLiveData.observe(this, {
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    textViewIslandName.text=  it.data?.island
                }
            }
        })*/

        homeViewModel.getAppVersionLiveData.observe(this, {
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    val pInfo: PackageInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
                    if (it.data?.androidUserAppVersion?.toInt()!! > pInfo.versionCode) {
                        (activity as HomeActivity).updateApplication()
                    }
                }
            }
        })

        authViewModel.getIslandListLiveData.observe(this, {
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    it.data?.forEach { island ->
                        if (island.id == session.user?.islandId?.toInt())
                            textViewCountryName.text = island.name + ", The Bahamas"
                    }
                }
                else -> {
                    showMessage(it.message)
                }
            }
        })
    }

    override fun bindData() {
        /*try {
            (activity as HomeActivity).updateApplication()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
*/

        if (session.user?.gender == null || session.user?.gender == "" || session.user?.dob == null || session.user?.dob == "0000-00-00") {
            navigator.load(EditProfileFragment::class.java).replace(false)
        }

        locationManager.getLocationUpdate(object : LocationManager.LocationListener {
            override fun onLocationAvailable(latLng: LatLng) {
                homeViewModel.currentLatLng = latLng
            }

            override fun onFail(status: LocationManager.LocationListener.Status) {

            }
        })

        /* if (session.saveTempParameters != null && session.saveOrderPage != null && session.saveOrderPage!!.isNotEmpty()) {
             showLoader(true)
             if (session.saveOrderPage == ConstantApp.PassValue.ORDER_FOOD || session.saveOrderPage == ConstantApp.PassValue.ORDER_FOOD_FUTURE) {
                 viewModelCart.placeOrder(session.saveTempParameters!!)
             } else if (session.saveOrderPage == ConstantApp.PassValue.ORDER_BEVERAGE_SPECIAL) {
                 specialBeverageViewModel.paySpecialBeverageOrder(session.saveTempParameters!!)
             } else {
                 viewModelCart.placeOrderBeverage(session.saveTempParameters!!)
             }
         } else if (session.saveTempWalletParameters != null) {
             homeViewModel.addMoneyToWallet(session.saveTempWalletParameters!!)
         }*/
        KravenCustomer.tempPromoCode = null
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.home))
        toolbar.setToolbarTextColorWhite(false)
        toolbar.setButtonTextVisibility(false)

        showLoader(true)
        //homeViewModel.getUser(session.user?.id.toString())
        homeViewModel.getPromoCodeList("1", session.user?.islandId)
        homeViewModel.getNewCuisineList()
        setUpServiceRecyclerView()
        buttonSeeAll.setOnClickListener {
            navigator.load(MyOfferFragment::class.java).replace(false)
        }
        buttonTrackOrder.setOnClickListener {
            navigator.load(MyOrderHistoryFragment::class.java).replace(false)
        }
        homeViewModel.getSetting()
        homeViewModel.updateDeviceInfo()

        authViewModel.getIslandList()

        textViewCountryName.setOnClickListener {
            navigator.load(SelectIslandFragment::class.java).setBundle(bundleOf(ConstantApp.PassValue.NEW_ISLAND to false)).replace(false)
        }
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
                        navigator.loadActivity(IsolatedActivity::class.java, OrderFoodFragment::class.java).addBundle(bundle).start()
                    }
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

        adapterAllCuisine = AdapterAllCuisine(OnRecycleItemClick { t, _ ->

            when (t.name) {
                "All Restuarant" -> {
                    navigator.loadActivity(IsolatedActivity::class.java,
                            OrderFoodFragment::class.java).addBundle(bundleOf(ConstantApp.PassValue.ORDER_FOOD to ConstantApp.PassValue.ORDER_FOOD)).start()
                }
                "All Beverage" -> {
                    navigator.loadActivity(IsolatedActivity::class.java,
                            OrderFoodFragment::class.java).addBundle(bundleOf(ConstantApp.PassValue.ORDER_FOOD to ConstantApp.PassValue.ORDER_BEVERAGE)).start()
                }
                else -> {
                    navigator.loadActivity(IsolatedActivity::class.java,
                            OrderFoodFragment::class.java).addBundle(bundleOf(ConstantApp.PassValue.ORDER_FOOD to ConstantApp.PassValue.ORDER_FOOD,
                            ConstantApp.PassValue.CUISINE_NAME to t.name,
                            ConstantApp.PassValue.CUISINE_ID to t.id)).start()
                }
            }
        })
        recyclerViewRestaurant.adapter = adapterAllCuisine
        recyclerViewRestaurant.addItemDecoration(ItemOffsetDecoration(requireContext(), R.dimen._5sdp))
    }


    override fun onResume() {
        super.onResume()
        //(activity as HomeActivity).updateApplication()
        homeViewModel.getAppVersion()
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
        if (session.cartCount?.isNotEmpty()!! && session.cartCount!!.toInt() > 0) {
            textViewCartCount?.visibility = View.VISIBLE
            textViewCartCount?.text = session.cartCount
        } else {
            textViewCartCount?.visibility = View.GONE
        }
    }


}