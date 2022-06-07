package com.kraven.ui.menu.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.kraven.R
import com.kraven.core.StatusCode
import com.kraven.di.component.FragmentComponent
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.home.adapter.MenuAdapter
import com.kraven.ui.home.fragment.ToppingBottomSheetBeverageFragment
import com.kraven.ui.home.fragment.ToppingBottomSheetFragment
import com.kraven.ui.home.model.*
import com.kraven.ui.home.viewmodel.HomeViewModel
import com.kraven.ui.menu.LargeImageActivity
import com.kraven.ui.order.beverage.adapter.BeverageAdapter
import com.kraven.ui.order.beverage.model.Beverage
import com.kraven.utils.ConstantApp
import kotlinx.android.synthetic.main.menu_list_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


class MenuListFragment : BaseFragment() {

    private var menuAdapter: MenuAdapter? = null
    private var beverageAdapter: BeverageAdapter? = null
    private lateinit var viewModel: HomeViewModel

    private var menus: List<MenusItem>? = null
    private var restaurantId: String? = null
    private var position: Int? = null
    private var status: String? = null
    private var orderPage: String? = null
    lateinit var cartModel: CartModel
    private var selectedNewDate: String? = null
    private var restaurantDetails: RestaurantDetails? = null
    private var _hasLoadedOnce = false

    companion object {
        fun newInstance(position: Int, menus: ArrayList<MenusItem>?, restaurantId: String, status: String,
                        restaurantDetails: RestaurantDetails?,
                        orderPage: String): MenuListFragment {
            //Log.d("hlink","selectedDate newInstance$selectedDate")
            val args = Bundle()
            args.putInt(ConstantApp.PassValue.MENU_POSITION, position)
            args.putString(ConstantApp.PassValue.RESTAURANT_ID, restaurantId)
            args.putParcelableArrayList(ConstantApp.PassValue.MENU_LIST, menus)
            args.putParcelable("restaurantDetails", restaurantDetails)
            args.putString("status", status)
            args.putString(ConstantApp.PassValue.ORDER_FOOD, orderPage)
            //args.putString("selectedDate", selectedDate)
            val fragment = MenuListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            position = arguments?.getInt(ConstantApp.PassValue.MENU_POSITION)
            restaurantId = arguments?.getString(ConstantApp.PassValue.RESTAURANT_ID)
            menus = arguments?.getParcelableArrayList(ConstantApp.PassValue.MENU_LIST)
            status = arguments?.getString("status")
            restaurantDetails = arguments?.getParcelable("restaurantDetails")
            orderPage = arguments?.getString(ConstantApp.PassValue.ORDER_FOOD)
            //selectedDate = arguments?.getString("selectedDate")
            // Log.d("hlink","selectedDate onCreate$selectedDate")
        }
        viewModel = ViewModelProviders.of(this, viewModelFactory)[HomeViewModel::class.java]

        viewModel.getMenuList.observe(this,
                {
                    when (it.code) {
                        StatusCode.CODE_SUCCESS -> {
                            menuAdapter?.items = it.data
                        }
                        StatusCode.CODE_NO_DATA -> {
                            menuAdapter?.errorMessage = it.message
                        }
                    }
                }) { true }

        viewModel.getBeverageMenuList.observe(this, {
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    beverageAdapter?.items = it.data
                }
                StatusCode.CODE_NO_DATA -> {
                    beverageAdapter?.errorMessage = it.message
                }
                else -> showMessage(it.message)
            }
        })
    }

    override fun createLayout(): Int = R.layout.menu_list_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        setUpRecyclerView()
        if (orderPage == ConstantApp.PassValue.ORDER_FOOD || orderPage == ConstantApp.PassValue.ORDER_FOOD_FUTURE) {
            viewModel.getMenuList(menus?.get(position!!)?.id.toString(), restaurantId!!)
        } else if (orderPage == ConstantApp.PassValue.ORDER_BEVERAGE) {
            viewModel.getBeverageMenuList(menus?.get(position!!)?.id.toString(), restaurantId!!)
        }

        cartModel = if (session.cartModel != null) {
            session.cartModel!!
        } else {
            CartModel()
        }

    }

    override fun onResume() {
        super.onResume()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    private fun setUpRecyclerView() {
        recyclerViewMenuList.layoutManager = GridLayoutManager(activity, 1)

        if (orderPage == ConstantApp.PassValue.ORDER_FOOD ||
                orderPage == ConstantApp.PassValue.ORDER_FOOD_FUTURE) {
            menuAdapter = MenuAdapter(object : MenuAdapter.ItemClickListener {

                override fun onItemClick(restaurantMenu: RestaurantMenu) {

                    if (restaurantMenu.status == "Available") {
                        openToppingList(restaurantMenu)
                    }
                    /*if (selectedNewDate != null && selectedNewDate!!.isNotEmpty()) {
                        openToppingList(restaurantMenu)
                    } else {
                        openToppingList(restaurantMenu)
                        *//*if (status == "Closed" || status == "Currently Unavailable") {
                            //if (RestaurantDetailsFragment.selectedDate != null && RestaurantDetailsFragment.selectedDate!!.isNotEmpty()) {
                            //showMessage("Sorry! " + restaurantDetails?.name + " is " + availableRestaurantFuture(RestaurantDetailsFragment.selectedDate!!, getList(restaurantDetails!!.timing as ArrayList<Timing>))[0].status?.toLowerCase())
                            showMessage("Sorry! " + restaurantDetails?.name + " is " + restaurantDetails?.availabilityStatus!!)
                            *//**//* } else {
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
                             }*//**//*
                        } else {
                            openToppingList(restaurantMenu)
                        }*//*
                    }*/


                }
            })
            recyclerViewMenuList.addItemDecoration(DividerItemDecoration(recyclerViewMenuList.context, DividerItemDecoration.VERTICAL))
            recyclerViewMenuList.adapter = menuAdapter
        } else if (orderPage == ConstantApp.PassValue.ORDER_BEVERAGE) {
            beverageAdapter = BeverageAdapter(object : BeverageAdapter.ItemClickListener {
                override fun onClickImage(image: String, imageView: AppCompatImageView) {

                    val intent = Intent(activity, LargeImageActivity::class.java)
                    intent.putExtra("image", image)
                    val options = activity?.let { ActivityOptionsCompat.makeSceneTransitionAnimation(it, imageView, "profile") }
                    startActivity(intent, options?.toBundle())
                }

                @SuppressLint("DefaultLocale")
                override fun onItemClick(beverage: Beverage) {
                    if (beverage.status == "Available") {
                        openToppingListBeverage(beverage)
                    }
                    /*if (selectedNewDate != null && selectedNewDate!!.isNotEmpty()) {
                        openToppingListBeverage(beverage)
                    } else {
                        openToppingListBeverage(beverage)
                       *//* if (status == "Closed" || status == "Currently Unavailable") {
                            if (RestaurantDetailsFragment.selectedDate != null && RestaurantDetailsFragment.selectedDate!!.isNotEmpty()) {
                                //showMessage("Sorry! " + restaurantDetails?.name + " is " + availableRestaurantFuture(RestaurantDetailsFragment.selectedDate!!, getList(restaurantDetails!!.timing as ArrayList<Timing>))[0].status.toLowerCase())
                                showMessage("Sorry! " + restaurantDetails?.name + " is " + restaurantDetails?.availabilityStatus!!)
                            } else {
                                *//**//*commandDialogYesNo("Restaurant is currently closed", "Do you want to order for the future?", object : DialogInterfaceYesNo {
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
                                })*//**//*
                                showMessage("Sorry! " + restaurantDetails?.name + " is " + status)
                            }
                        } else {
                            openToppingListBeverage(beverage)
                        }*//*
                    }*/

                }


            })

            recyclerViewMenuList.adapter = beverageAdapter
        }
    }

    private fun openToppingListBeverage(restaurantMenu: Beverage) {

        restaurantMenu.toppings?.toppingList?.forEach { toppingsItems: ToppingListItem ->
            toppingsItems.isCheckItem = false
        }

        val bottomSheetTopping = ToppingBottomSheetBeverageFragment()
        val args = Bundle()
        args.putParcelable(ConstantApp.PassValue.RESTAURANT_MENU, restaurantMenu)
        args.putString(ConstantApp.PassValue.TYPE, menus?.get(position!!)?.name)
        args.putString(ConstantApp.PassValue.ORDER_FOOD, orderPage)
        args.putParcelable("Status", restaurantDetails)
        bottomSheetTopping.arguments = args
        bottomSheetTopping.show(childFragmentManager, bottomSheetTopping.tag)
    }

    private fun openToppingList(restaurantMenu: RestaurantMenu?) {

        restaurantMenu?.toppings?.forEach { toppingsItems: ToppingsItems ->
            toppingsItems.minCount = 0
            toppingsItems.toppingList?.forEach { toppingListItem: ToppingListItem? ->
                toppingListItem?.isCheckItem = false
            }
        }

        val bottomSheetTopping = ToppingBottomSheetFragment()
        val args = Bundle()
        args.putParcelable(ConstantApp.PassValue.RESTAURANT_MENU, restaurantMenu)
        args.putString(ConstantApp.PassValue.STATUS, status)
        args.putParcelable("Status", restaurantDetails)
        args.putString(ConstantApp.PassValue.TYPE, menus?.get(position!!)?.name)
        args.putString(ConstantApp.PassValue.ORDER_FOOD, orderPage)

        bottomSheetTopping.arguments = args
        bottomSheetTopping.show(childFragmentManager, bottomSheetTopping.tag)
    }


    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(selectedNewDate: String) {
        this@MenuListFragment.selectedNewDate = selectedNewDate
        /* if (itemAddedToCartEvent.count.isNotEmpty()) {

             textViewCartCount.visibility = View.VISIBLE
             textViewCartCount.text = session.cartCount
         }*/
    }

}