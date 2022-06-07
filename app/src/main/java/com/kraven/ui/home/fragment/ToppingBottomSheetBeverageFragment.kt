package com.kraven.ui.home.fragment


import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kraven.R
import com.kraven.extensions.convertTwoDigit
import com.kraven.extensions.convertTwoDigitFlot
import com.kraven.extensions.getText
//import com.kraven.extensions.specialOfferPrice
import com.kraven.ui.activity.HomeActivity
import com.kraven.ui.activity.IsolatedActivity
import com.kraven.ui.home.adapter.ToppingTitleAdapter
import com.kraven.ui.home.model.DishesItem
import com.kraven.ui.home.model.RestaurantDetails
import com.kraven.ui.home.model.ToppingListItem
import com.kraven.ui.home.model.ToppingsItems
import com.kraven.ui.order.beverage.model.Beverage
import com.kraven.utils.ConstantApp
import kotlinx.android.synthetic.main.bottomsheet_topping_beverage.view.*


class ToppingBottomSheetBeverageFragment : DialogFragment() {
    private var count = 0
    private var beverage: Beverage? = null
    private var type: String? = null
    private var mParentListener: OnChildFragmentInteractionListeners? = null
    private var toppingTitleAdapter: ToppingTitleAdapter? = null
    var finalCount = 0
    var finalPrice = 0F
    private var restaurantDetails: RestaurantDetails? = null
    var menuId = 0


    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is HomeActivity) {
            val mainActivity = context
            val parentFragment = mainActivity.getCurrentFragment<RestaurantDetailsFragment>()
            if (parentFragment != null) {
                mParentListener = parentFragment
            } else {
                throw RuntimeException("The parent fragment must implement OnChildFragmentInteractionListener")
            }
        } else {
            if (context is IsolatedActivity) {
                val mainActivity = context
                val parentFragment = mainActivity.getCurrentFragment<RestaurantDetailsFragment>()
                if (parentFragment != null) {
                    mParentListener = parentFragment
                } else {
                    throw RuntimeException("The parent fragment must implement OnChildFragmentInteractionListener")
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        mParentListener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            beverage = arguments?.getParcelable(ConstantApp.PassValue.RESTAURANT_MENU)
            type = arguments?.getString(ConstantApp.PassValue.TYPE)
            restaurantDetails = arguments?.getParcelable("Status")
        }
        finalCount = 0
        finalPrice = 0F
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottomsheet_topping_beverage, container, false)

        view.textViewFoodName.text = beverage?.name
        view.textViewFoodType.text = beverage?.menu

        view.imageViewBack.setOnClickListener { dismissAllowingStateLoss() }

        //val foodPrice = specialOfferPrice(beverage!!.price!!, beverage!!.specialOffer!!).toFloat()
        val foodPrice =  convertTwoDigitFlot(beverage?.price!!.toFloat())


        //view.textViewFoodPrice.text = if (beverage?.specialOffer != 0F) convertTwoDigit(foodPrice) else convertTwoDigit(beverage?.price!!)
        view.textViewFoodPrice.text =convertTwoDigit(beverage?.price!!)

        //view.textViewTotal.text = if (beverage?.specialOffer != 0F) foodPrice.let { convertTwoDigit(it) } else beverage?.price?.let { convertTwoDigit(it) }
        view.textViewTotal.text = convertTwoDigit(beverage?.price!!)

        view.textViewOffPrice.visibility = if (beverage?.specialOffer != 0F) View.VISIBLE else View.GONE
        if (view.textViewOffPrice.visibility == View.VISIBLE) {
            view.textViewOffPrice.paintFlags = view.textViewOffPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            view.textViewOffPrice.text = convertTwoDigit(beverage?.originalPrice!!.toFloat())
        }

        view.recyclerViewToppingTitle.layoutManager = LinearLayoutManager(activity!!)
        toppingTitleAdapter = ToppingTitleAdapter(object : ToppingTitleAdapter.OnClickListener {
            override fun onItemClick() {

                var toppingPrice = 0f
                toppingTitleAdapter?.items?.forEach { toppingList ->
                    toppingList.toppingList?.forEach {
                        if (it.isCheckItem) {
                            toppingPrice += it.price!!
                        }
                    }
                }
                if (toppingPrice != 0f) {
                    view.textViewTotal.text = convertTwoDigit(toppingPrice)
                } else {
                    //view.textViewTotal.text = if (beverage?.specialOffer != 0F) convertTwoDigit(foodPrice) else beverage?.price?.let { convertTwoDigit(it) }
                    view.textViewTotal.text = foodPrice.toString()
                }
                //getTextReplace(view.textViewFoodPrice)?.plus(toppingPrice)?.let { convertTwoDigit(it) }
            }

        }, true)


        if (beverage?.toppings != null) {
            view.recyclerViewToppingTitle.adapter = toppingTitleAdapter
            toppingTitleAdapter!!.items = arrayListOf(beverage!!.toppings)
        }

        view.imageButtonAddition.setOnClickListener {
            count = count.inc()
            view.textViewCount.text = count.toString()
            view.imageButtonSubtract.background = ContextCompat.getDrawable(activity!!, R.drawable.ic_substact_item)
            view.imageButtonSubtract.isEnabled = true

            finalCount += 1
            //finalPrice += if (beverage?.specialOffer != 0F) specialOfferPrice(beverage!!.price!!, beverage!!.specialOffer!!).toFloat() else beverage?.price!!
            //finalPrice += if (beverage?.specialOffer != 0F) beverage!!.price!! else beverage?.originalPrice!!.toFloat()
            finalPrice += foodPrice.toFloat()

        }

        view.imageButtonSubtract.setOnClickListener {
            count = count.dec()
            view.textViewCount.text = count.toString()
            finalCount -= 1
            //finalPrice -= if (beverage?.specialOffer != 0F) beverage!!.price!! else beverage?.originalPrice!!.toFloat()
            finalPrice += foodPrice.toFloat()
            //finalPrice -= beverage?.price!!
            if (count == 0) {
                view.imageButtonSubtract.background = ContextCompat.getDrawable(activity!!, R.drawable.ic_substact_item_disable)
                view.imageButtonSubtract.isEnabled = false
            }
        }

        view.buttonAddToCart.setOnClickListener {
            if (restaurantDetails?.availabilityStatus == "Closed" || restaurantDetails?.availabilityStatus == "Currently Unavailable") {
                val sb = Snackbar.make(view, "Sorry! " + restaurantDetails?.name + " is " + restaurantDetails?.availabilityStatus!!, Snackbar.LENGTH_LONG)
                sb.show()
            } else {
                val firstCheckIs = toppingTitleAdapter?.items?.firstOrNull { it.minQty != null && it.minCount == 0 && it.minCount <= it.minQty!! && it.minQty!! != 0 }
                if (firstCheckIs != null) {
                    val sb = Snackbar.make(view, "Please choose ${firstCheckIs.type} up to ${firstCheckIs.minQty}", Snackbar.LENGTH_LONG)
                    sb.show()
                } else if (count == 0) {
                    val sb = Snackbar.make(view, "Please add quantity", Snackbar.LENGTH_LONG)
                    sb.show()
                } else {
                    val dishesItem = DishesItem()
                    dishesItem.menu = type!!
                    dishesItem.specialNote = getText(view.textViewSpecialNote).toString()
                    dishesItem.price = beverage?.price
                    dishesItem.qty = finalCount
                    dishesItem.status = beverage?.status
                    dishesItem.dish = beverage?.name.toString()
                    dishesItem.menuId = beverage?.menuId.toString()
                    dishesItem.id = beverage?.id.toString()
                    dishesItem.beverageId = beverage?.beverageId.toString()
                    dishesItem.itemId = beverage?.id.toString()
                    dishesItem.calculateVatPrice = if (beverage!!.vatPrice != null && beverage!!.vatPrice == "0.0") "0.0" else (beverage!!.vatPrice?.toFloat()?.times(finalCount)).toString()
                    //dishesItem.toppings = toppingTitleAdapter?.items as ArrayList<ToppingsItems>?
                    val toppings = ArrayList<ToppingsItems>()



                    toppingTitleAdapter?.items?.forEach { toppingList ->
                        val toppingLists = ArrayList<ToppingListItem>()
                        val testToppingsItems = ToppingsItems()
                        toppingList.toppingList?.forEach { tList ->
                            val toppingListItem = ToppingListItem()
                            toppingListItem.isCheckItem = tList.isCheckItem
                            toppingListItem.inserDateTime = tList.inserDateTime
                            toppingListItem.price = tList.price
                            toppingListItem.name = tList.name
                            toppingListItem.id = tList.id
                            toppingListItem.status = tList.status
                            toppingListItem.vatPrice = tList.vatPrice
                            toppingLists.add(toppingListItem)
                        }
                        testToppingsItems.avialbeCount = toppingList.avialbeCount
                        testToppingsItems.id = toppingList.id
                        testToppingsItems.type = toppingList.type
                        testToppingsItems.category = toppingList.category
                        testToppingsItems.minQty = toppingList.minQty
                        testToppingsItems.minCount = toppingList.minCount

                        testToppingsItems.toppingList = toppingLists

                        toppings.add(testToppingsItems)

                    }
                    dishesItem.toppings = toppings

                    var toppingPrice = 0f
                    toppingTitleAdapter?.items?.forEach { toppingList ->
                        toppingList.toppingList?.forEach {
                            if (it.isCheckItem) {
                                toppingPrice += it.price!!
                            }
                        }
                    }
                    //val foodPrices = specialOfferPrice(beverage!!.price!!, beverage!!.specialOffer!!).toFloat()
                    val foodPrices =  convertTwoDigitFlot(beverage!!.price!!)
                    val dishWithTopingPrice = if (toppingPrice != 0f) toppingPrice else foodPrices!!
                    dishesItem.dishPriceTopping = dishWithTopingPrice
                    dishesItem.totalPrice = dishesItem.qty!! * dishWithTopingPrice


                    mParentListener?.add(dishesItem.totalPrice!!, dishesItem)
                    dismiss()
                }
            }
        }

        return view
    }


    interface OnChildFragmentInteractionListeners {
        fun add(finalPrice: Float, items: DishesItem)
    }

}