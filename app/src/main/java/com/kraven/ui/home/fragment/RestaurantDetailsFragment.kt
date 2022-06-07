package com.kraven.ui.home.fragment


import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import com.kraven.R
import com.kraven.core.AppExecutors
import com.kraven.core.AppPreferences
import com.kraven.core.Common
import com.kraven.core.StatusCode
import com.kraven.data.pojo.Parameters
import com.kraven.di.component.FragmentComponent
import com.kraven.extensions.getList
import com.kraven.extensions.getText
import com.kraven.extensions.viewGone
import com.kraven.extensions.viewShow
import com.kraven.ui.activity.IsolatedActivity
import com.kraven.ui.address.model.Address
import com.kraven.ui.cart.fragment.CartFragment
import com.kraven.ui.future.food.order.fragment.FutureFoodOrderFragment
import com.kraven.ui.home.model.*
import com.kraven.ui.home.viewmodel.HomeViewModel
import com.kraven.ui.menu.adapter.MenuViewPagerAdapter
import com.kraven.ui.order.beverage.model.Beverage
import com.kraven.ui.review.fragment.ReviewFragment
import com.kraven.utils.ConstantApp
import com.kraven.utils.Formatter
import com.kraven.utils.Formatter.HH_mm_ss
import com.kraven.utils.Formatter.YYYY_MM_DD_T_HH_MM_SSS_Z
import com.kraven.utils.Formatter.hh_mm_aa
import com.kraven.utils.LocationManager

import kotlinx.android.synthetic.main.restaurant_details.*
import kotlinx.android.synthetic.main.restaurant_raw.view.*
import org.greenrobot.eventbus.EventBus
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class RestaurantDetailsFragment : RestaurantSliderFragment(), View.OnClickListener,
        ToppingBottomSheetFragment.OnChildFragmentInteractionListeners, ToppingBottomSheetBeverageFragment.OnChildFragmentInteractionListeners {

    @Inject
    lateinit var appPreferences: AppPreferences

    @Inject
    lateinit var locationManager: LocationManager


    var finalPrice = 0F
    private var displayCount = 0

    private lateinit var viewModel: HomeViewModel
    private var restaurantDetails: RestaurantDetails? = null
    private var appExecutors: AppExecutors? = null
    private var address: Address? = null


    var position: Int? = -1


    var textViewCartCount: AppCompatTextView? = null
    var imageViewFavButton: AppCompatImageView? = null

    var cartModel: CartModel? = null

    companion object {
        var selectedDate: String? = null
        var selectedTopping = mutableListOf<DishesItem>()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.requestApplyInsets(coordinatorLayout)
    }

    /*private val currentLatLng: LatLng by lazy {
        val args = arguments ?: throw  IllegalArgumentException("Missing arguments")
        args.getParcelable(ConstantApp.PassValue.CURRENT_LAT_LONG) as LatLng
    }*/

    private val restaurantId: String? by lazy {
        val args = arguments ?: throw IllegalArgumentException("Missing arguments")
        args.getString(ConstantApp.PassValue.RESTAURANT_ID)
    }


    private val orderPage: String? by lazy {
        val args = arguments ?: throw IllegalArgumentException("Missing arguments")
        args.getString(ConstantApp.PassValue.ORDER_FOOD)
    }

    override fun createLayout(): Int = R.layout.restaurant_details

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            selectedDate = arguments!!.getString(ConstantApp.PassValue.FUTUREDATE)
        }

        address = ConstantApp.SaveValue.address
        viewModel = ViewModelProviders.of(this, viewModelFactory)[HomeViewModel::class.java]
        appExecutors = AppExecutors()

        viewModel.getRestaurantDetails.observe(this, {

            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    setRestaurantDetails(it.data)
                }
                else -> showMessage(it.message)
            }
        }) { true }

        viewModel.addFavouriteRestaurant.observe(this, {
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    imageViewFavButton?.isSelected = !imageViewFavButton?.isSelected!!
                }
                else -> showMessage(it.message)
            }
        }) { true }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_restauarnt, menu)

        if (orderPage == ConstantApp.PassValue.ORDER_BEVERAGE) {
            menu.findItem(R.id.action_search)?.isVisible = true
        }

        val view = menu.findItem(R.id.ic_cart)?.actionView
        textViewCartCount = view?.findViewById(R.id.textViewCartCount)

        view?.setOnClickListener {
            openCartFragment()
            //navigator.loadActivity(IsolatedActivity::class.java).setPage(CartFragment::class.java).start()
        }

        if (session.cartCount?.isNotEmpty()!! && session.cartCount!!.toInt() > 0) {
            finalPrice = session.totalPrice!!
            displayCount = session.cartCount!!.toInt()
            textViewCartCount!!.viewShow()
            linearLayoutCart.viewShow()
            textViewFinalCount.text = String.format(Locale.US, session.cartCount + " " + getString(R.string.items))
            textViewFinalPrice.text = String.format(Locale.US, "$%.2f", session.totalPrice)
        } else {
            finalPrice = 0f
            displayCount = 0
            textViewCartCount!!.viewGone()
        }

        textViewCartCount!!.text = session.cartCount

        val imageViewFav = menu.findItem(R.id.menuFav)?.actionView
        imageViewFavButton = imageViewFav?.findViewById(R.id.imageViewFavButton)
        imageViewFavButton!!.isSelected = restaurantDetails!!.isFavourite == 1

        imageViewFavButton?.setOnClickListener {
            if (orderPage == ConstantApp.PassValue.ORDER_FOOD) {
                viewModel.addFavouriteRestaurant(Parameters(restaurantId = restaurantId))
            } else if (orderPage == ConstantApp.PassValue.ORDER_BEVERAGE) {
                viewModel.addFavouriteBeverage(Parameters(beverageId = restaurantId))
            }
        }

        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {

                navigator.loadActivity(IsolatedActivity::class.java).addBundle(Bundle().apply {
                    putString("menu_id", restaurantDetails?.menus?.get(viewPagerFood.currentItem)?.id.toString())
                    putString("restaurant_id", restaurantId)
                }).setPage(BeverageSearchScreen::class.java).forResult(101).start()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setRestaurantDetails(data: RestaurantDetails?) {
        restaurantDetails = data
        setHasOptionsMenu(true)
        activity!!.runOnUiThread {

            textViewRestaurantName.text = data?.name
            if (data?.cuisines != null) {
                textViewRestaurantItem.text = data.cuisines.joinToString(", ", transform = { it.cuisine })
            } else {
                textViewRestaurantItem.viewGone()
            }

            ratingBarRestaurant.rating = data?.rating?.toFloat()!!
            textViewReviewCount.text = getString(R.string.review_restaurant, data.reviews.toString())

            /*textViewDetailsStatus.text = if (selectedDate != null && selectedDate!!.isNotEmpty())
                availableRestaurantFuture(selectedDate!!, getList(data.timing as ArrayList<Timing>))[0].status else availableRestaurant(getList(data.timing as ArrayList<Timing>))[0].status*/


            textViewDetailsStatus.text = data.availabilityStatus



            setUpViewPager(data.banners, getText(textViewDetailsStatus)!!)

            val timing = getList(data.timing as ArrayList<Timing>).joinToString("\n", transform = { getString(R.string.opening_hours, Formatter.format(it.startTime, HH_mm_ss, hh_mm_aa, false), Formatter.format(it.endTime, HH_mm_ss, hh_mm_aa, false)) })

            textViewHoursTime.text = if (timing.isNotEmpty()) timing else "-"

            if (getText(textViewDetailsStatus) == ConstantApp.RestaurantStatus.AVAILABLE) {
                textViewDetailsStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
            } else {
                textViewDetailsStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            }
            textViewMiles.text = getString(R.string.mile_restaurant, String.format("%.2f", data.distance))
            textViewAddressName.text = data.address

            if(data.averagePrepTime!=null){
                textViewAvrPrpTime.text = data.averagePrepTime.toDouble().roundToInt().toString().plus(" mins")
            }

            textViewMinOrderAmount.text = String.format(Locale.US, "$%.2f", data.minOrderAmount)

            when {
                data.deliveryType == "Pickup,Delivery" -> textViewDeliveryType.text = "Delivery & Pickup"
                data.deliveryType == "Pickup" -> textViewDeliveryType.text = "Pickup Only"
                data.deliveryType == "Delivery" -> textViewDeliveryType.text = "Delivery Only"
            }

            val menuViewPagerAdapter =
                restaurantId?.let {
                    orderPage?.let { it1 ->
                        MenuViewPagerAdapter(childFragmentManager, data.menus,
                            it, textViewDetailsStatus.text.toString(), restaurantDetails, it1
                        )
                    }
                }
            viewPagerFood.adapter = menuViewPagerAdapter
            viewPagerFood.offscreenPageLimit =0
            tabLayoutFood.setupWithViewPager(viewPagerFood)

            for (i in 0 until tabLayoutFood.tabCount) {
                val tab = tabLayoutFood.getTabAt(i)
                tab?.setCustomView(R.layout.tablayout_custome_view)
                val view = tab?.customView
                val textView = view?.findViewById<AppCompatTextView>(R.id.custom_text)
                textView?.text = data.menus?.get(i)!!.name
            }

            tabLayoutFood.getTabAt(0)?.customView?.findViewById<AppCompatTextView>(R.id.custom_text)?.setTextColor(ContextCompat.getColor(context!!, R.color.light_blue))

            tabLayoutFood.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tabPosition: TabLayout.Tab) {
                }

                override fun onTabUnselected(tabPosition: TabLayout.Tab) {
                    tabLayoutFood.getTabAt(tabPosition.position)?.customView?.findViewById<AppCompatTextView>(R.id.custom_text)?.setTextColor(ContextCompat.getColor(context!!, R.color.black))
                }

                override fun onTabSelected(tabPosition: TabLayout.Tab) {

                    tabLayoutFood.getTabAt(tabPosition.position)?.customView?.findViewById<AppCompatTextView>(R.id.custom_text)?.setTextColor(ContextCompat.getColor(context!!, R.color.light_blue))
                }

            })

        }

        showLoader(false)
    }


    override fun bindData() {
        showLoader(true)
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle("")
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)


        imageViewRestaurantReservation.setOnClickListener(this)
        imageViewFutureFoodOrder.setOnClickListener(this)
        imageViewNavigate.setOnClickListener(this)
        textViewReviewCount.setOnClickListener(this)

        /*requirePermission(activity!!,"To see a vendor turn on your location services") {
            Log.d("Hlink","requirePermission :")
            locationManager.triggerLocation(object : LocationManager.LocationListener {
                override fun onLocationAvailable(latLng: LatLng) {
                    Log.d("Hlink","onLocationAvailable :${latLng}")
                    if (orderPage == ConstantApp.PassValue.ORDER_FOOD
                            || orderPage == ConstantApp.PassValue.ORDER_FOOD_FUTURE) {
                        viewModel.getRestaurantDetails(restaurantId,
                                latLng, (if (selectedDate != null && selectedDate!!.isNotEmpty()) selectedDate else Formatter.format(Date().toString(), YYYY_MM_DD_T_HH_MM_SSS_Z,
                                Formatter.YYYY_MM_DD_HH_MM_SS, false).toString())!!, if (orderPage == ConstantApp.PassValue.ORDER_FOOD) "Food" else "Future Food")
                    } else if (orderPage == ConstantApp.PassValue.ORDER_BEVERAGE) {
                        imageViewFutureFoodOrder.viewGone()
                        textViewFutureFoodOrder.viewGone()
                        viewModel.getBeverageDetails(restaurantId, latLng)
                    }

                }
                override fun onFail(status: LocationManager.LocationListener.Status) {
Log.d("Hlink","status :${status}")
                }
            })*/

        locationManager.locationUpdateLiveData.observe(this, androidx.lifecycle.Observer { latLng ->
            if (orderPage == ConstantApp.PassValue.ORDER_FOOD
                    || orderPage == ConstantApp.PassValue.ORDER_FOOD_FUTURE) {
                restaurantId?.let {
                    viewModel.getRestaurantDetails(
                        it,
                        latLng, (if (selectedDate != null && selectedDate!!.isNotEmpty()) selectedDate else Formatter.format(Date().toString(), YYYY_MM_DD_T_HH_MM_SSS_Z,
                            Formatter.YYYY_MM_DD_HH_MM_SS, false).toString())!!, if (orderPage == ConstantApp.PassValue.ORDER_FOOD) "Food" else "Future Food")
                }
            } else if (orderPage == ConstantApp.PassValue.ORDER_BEVERAGE) {
                imageViewFutureFoodOrder.viewGone()
                textViewFutureFoodOrder.viewGone()
                restaurantId?.let { viewModel.getBeverageDetails(it, latLng) }
            }
            locationManager.locationUpdateLiveData.removeObservers(this)
        })



        textViewViewCart.setOnClickListener {
            openCartFragment()

        }

        cartModel = if (session.cartModel != null) {
            session.cartModel!!
        } else {
            CartModel()
        }

        if (selectedDate != null && selectedDate!!.isNotEmpty()) {
            cartModel?.orderDatetime = selectedDate
            cardViewThree.viewGone()
        }


    }

    override fun onResume() {
        super.onResume()
        if (session.cartModel == null) {
            textViewCartCount?.viewGone()
            linearLayoutCart.viewGone()
        }
    }

    override fun add(finalPrice: Float, items: DishesItem) {

        activity!!.runOnUiThread {
            if (session.cartModel != null && cartModel != null) {
                if (cartModel?.restaurantId?.toInt() == restaurantDetails?.id && cartModel?.orderPage == orderPage) {
                    orderPage?.let { addItemToCarts(finalPrice, items, it) }
                } else {
                    openPopup(items, finalPrice)
                }
            } else {
                orderPage?.let { addItemToCarts(finalPrice, items, it) }
            }
        }
    }

    private fun openPopup(items: DishesItem, finalPrice: Float) {
        commanDialogYesNoNew(getString(R.string.message_restaurant_already_exist_title),
                if (orderPage == ConstantApp.PassValue.ORDER_FOOD_FUTURE) getString(R.string.message_future_food_already_exits) else getString(R.string.message_restaurant_already_exist), "Yes", "No", object : DialogInterfaceYesNo {
            override fun onClickYes() {
                cartModel = null
                session.cartModel = null
                session.cartCount = "0"
                cartModel = if (session.cartModel != null) {
                    session.cartModel!!
                } else {
                    CartModel()
                }
                displayCount = 0
                this@RestaurantDetailsFragment.finalPrice = 0F
                session.totalPrice = 0F
                session.cartCount = "0"
                orderPage?.let { addItemToCarts(finalPrice, items, it) }
            }

            override fun onClickNo() {
            }

        })
    }

    private fun addItemToCarts(finalPrice: Float, items: DishesItem, orderPage: String) {
        this.finalPrice += finalPrice
        displayCount = displayCount.inc()


        textViewCartCount?.visibility = View.VISIBLE
        textViewCartCount?.text = displayCount.toString()
        EventBus.getDefault().post("")

        linearLayoutCart.visibility = View.VISIBLE
        textViewFinalCount.text = String.format(Locale.US, displayCount.toString() + " " + getString(R.string.items))
        textViewFinalPrice.text = String.format(Locale.US, "$%.2f", this.finalPrice)


        if (cartModel?.dishes == null) {
            cartModel?.dishes = ArrayList()
        }

        if (selectedDate != null && selectedDate!!.isNotEmpty()) {
            cartModel?.orderDatetime = selectedDate
        } else {
            cartModel?.orderDatetime = ""
        }

        cartModel?.restaurantId = restaurantId
        cartModel?.addressId = address?.id.toString()
        cartModel?.dishes?.add(items)

        cartModel?.orderPage = orderPage
        session.cartModel = cartModel
    }

    override fun onPause() {
        super.onPause()
        session.cartCount = if (session.cartCount?.isNotEmpty()!! && session.cartCount?.toInt()!! >= 0) displayCount.toString() else "0"
        EventBus.getDefault().post("")
        session.totalPrice = finalPrice
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.toolbarCustom -> {
                navigator.goBack()
            }

            R.id.imageViewRestaurantReservation -> {
                //      navigator.load(RestaurantBookTableFragment::class.java).replace(true)
            }
            R.id.imageViewFutureFoodOrder -> {
                navigator
                        .loadActivity(IsolatedActivity::class.java, FutureFoodOrderFragment::class.java)
                        .addBundle(Bundle().apply {
                            putBoolean("isRestaurantDetails", true)
                            putParcelable(ConstantApp.PassValue.RESTAURANT_MENU, restaurantDetails)
                        })
                        .forResult(101).start()

            }
            R.id.imageViewCart -> {
                openCartFragment()

            }
            R.id.textViewReviewCount -> {
                val b = Bundle()
                b.putString(Common.ID, restaurantId)
                b.putString(Common.VENDORTYPE, orderPage)
                navigator.load(ReviewFragment::class.java).setBundle(b).replace(true)

            }
            R.id.imageViewNavigate -> {
                var gmmIntentUri: Uri? = null
                try {
                    gmmIntentUri = Uri.parse("google.navigation:q=" + restaurantDetails?.latitude + "," + restaurantDetails?.longitude)
                    val mapIntent = Intent(ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    startActivity(mapIntent)
                } catch (e: ActivityNotFoundException) {
                    try {
                        val unrestrictedIntent = Intent(ACTION_VIEW, Uri.parse("google.navigation:q=" + restaurantDetails?.latitude + "," + restaurantDetails?.longitude))
                        startActivity(unrestrictedIntent)

                    } catch (innerEx: ActivityNotFoundException) {
                        Toast.makeText(context, "Please install a maps application", Toast.LENGTH_LONG).show()
                    }

                }
                /* val gmmIntentUri = Uri.parse("google.navigation:q=" + restaurantDetails?.latitude + "," + restaurantDetails?.longitude)
                 val mapIntent = Intent(ACTION_VIEW, gmmIntentUri)
                 mapIntent.setPackage("com.google.android.apps.maps")
                 startActivity(mapIntent)*/
            }
        }
    }

    private fun openCartFragment() {
        /*session.cartCount = displayCount.toString()
        session.totalPrice = finalPrice
        val bundle = Bundle()
        bundle.putString(ConstantApp.PassValue.RESTAURANT_ID, restaurantId)
        bundle.putParcelable(ConstantApp.PassValue.ADDRESS_ID, address)
        bundle.putString(ConstantApp.PassValue.FUTUREDATE, selectedDate)
        navigator.load(CartFragment::class.java).setBundle(bundle).replace(true)*/
        /* cartModel?.restaurantId = restaurantId
         cartModel?.addressId = address?.id.toString()

         session.cartModel?.dishes = selectedItems*/
        session.cartCount = displayCount.toString()
        session.totalPrice = finalPrice

        /* cartModel?.orderPage = orderPage
         session.cartModel = cartModel
 */
        val bundle = Bundle()
        bundle.putString(ConstantApp.PassValue.RESTAURANT_ID, restaurantId)
        bundle.putParcelable(ConstantApp.PassValue.ADDRESS_ID, address)
        bundle.putString(ConstantApp.PassValue.FUTUREDATE, selectedDate)
        //navigator.load(CartFragment::class.java).setBundle(bundle).replace(true)
        navigator.load(CartFragment::class.java).replace(true)
        //navigator.loadActivity(IsolatedActivity::class.java).setPage(CartFragment::class.java).start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == 101) {
                val beverage = data!!.getParcelableExtra<Beverage>("beverage")

                if (getText(textViewDetailsStatus) == "Closed" || getText(textViewDetailsStatus) == "Currently Unavailable") {
                    //if (selectedDate != null && selectedDate!!.isNotEmpty()) {
                    showMessage("Sorry! " + restaurantDetails?.name + " is " + restaurantDetails?.availabilityStatus!!)
                    /*} else {
                        commandDialogYesNo("Restaurant is currently closed", "Do you want to order for the future?", object : DialogInterfaceYesNo {
                            override fun onClickYes() {
                                navigator
                                        .loadActivity(IsolatedActivity::class.java, FutureFoodOrderFragment::class.java)
                                        .addBundle(Bundle().apply {
                                            putBoolean("isRestaurantDetails", true)
                                            putParcelable(ConstantApp.PassValue.RESTAURANT_MENU, restaurantDetails)
                                        }).forResult(101).start()
                            }

                            override fun onClickNo() {

                            }
                        })
                    }*/
                } else {
                    if (beverage != null) {  openToppingListBeverage(beverage)
                    }
                }

            }
            /* if (requestCode == 101) {
                 selectedNewDate = data!!.getStringExtra(ConstantApp.PassValue.FUTUREDATE)
                 cartModel?.orderDatetime = selectedNewDate
                 EventBus.getDefault().post(selectedNewDate)
             }*/
        }
    }

    private fun openToppingListBeverage(restaurantMenu: Beverage) {

        restaurantMenu.toppings?.toppingList?.forEach { toppingsItems: ToppingListItem ->
            toppingsItems.isCheckItem = false
        }

        val bottomSheetTopping = ToppingBottomSheetBeverageFragment()
        val args = Bundle()
        args.putParcelable(ConstantApp.PassValue.RESTAURANT_MENU, restaurantMenu)
        args.putString(ConstantApp.PassValue.TYPE, restaurantDetails?.menus?.get(viewPagerFood.currentItem)?.name)
        args.putString(ConstantApp.PassValue.ORDER_FOOD, orderPage)
        bottomSheetTopping.arguments = args
        bottomSheetTopping.show(childFragmentManager, bottomSheetTopping.tag)
    }


}
