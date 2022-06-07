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
import com.kraven.extensions.getTextReplace
//import com.kraven.extensions.specialOfferPrice
import com.kraven.ui.activity.HomeActivity
import com.kraven.ui.activity.IsolatedActivity
import com.kraven.ui.home.adapter.ToppingTitleAdapter
import com.kraven.ui.home.model.*
import com.kraven.utils.ConstantApp
import kotlinx.android.synthetic.main.bottomsheet_topping.view.*


class ToppingBottomSheetFragment : DialogFragment() {

    private var count = 0
    private var toppingTitleAdapter: ToppingTitleAdapter? = null
    private var restaurantMenu: RestaurantMenu? = null
    private var type: String? = null
    private var mParentListener: OnChildFragmentInteractionListeners? = null
    private var finalCount = 0
    private var finalPrice = 0F
    private var toppingPrice = 0F
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
            restaurantMenu = arguments?.getParcelable(ConstantApp.PassValue.RESTAURANT_MENU)
            type = arguments?.getString(ConstantApp.PassValue.TYPE)
            restaurantDetails = arguments?.getParcelable("Status")
        }
        finalCount = 0
        finalPrice = 0F
        toppingPrice = 0F
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
        val view = inflater.inflate(R.layout.bottomsheet_topping, container, false)

        view.textViewFoodName.text = restaurantMenu?.name
        if (type != null && type!!.isNotEmpty()) {
            view.textViewFoodType.text = type
        }
        //val foodPrice = restaurantMenu?.price?.let { restaurantMenu?.specialOffer?.let { it1 -> specialOfferPrice(it.toFloat(), it1.toFloat()).toFloat() } }
        /*val foodPrice = if (restaurantMenu!!.specialOffer != null && restaurantMenu!!.specialOffer == "0") {
            restaurantMenu!!.price!!.toFloat()
        } else {
            restaurantMenu!!.originalPrice!!.toFloat()
        }
        view.textViewFoodPrice.text = if (restaurantMenu?.specialOffer?.toFloat() != 0F) foodPrice.let { convertTwoDigit(it) } else restaurantMenu?.price?.let { convertTwoDigit(it.toFloat()) }*/

        val foodPrice =  convertTwoDigitFlot(restaurantMenu!!.price!!.toFloat())
        view.textViewFoodPrice.text = foodPrice.toString()
        view.textViewOffPrice.visibility = if (restaurantMenu?.specialOffer?.toFloat() != 0F) View.VISIBLE else View.GONE
        if (view.textViewOffPrice.visibility == View.VISIBLE) {
            view.textViewOffPrice.paintFlags = view.textViewOffPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            view.textViewOffPrice.text = convertTwoDigit(restaurantMenu?.originalPrice?.toFloat()!!)
        }

        view.imageViewBack.setOnClickListener {
            dismissAllowingStateLoss()
        }
        view.textViewTotal.text = if (restaurantMenu?.specialOffer?.toFloat() != 0F) foodPrice.let { convertTwoDigit(it) } else restaurantMenu?.price?.let { convertTwoDigit(it.toFloat()) }

        view.recyclerViewToppingTitle.layoutManager = LinearLayoutManager(context)
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
                view.textViewTotal.text = getTextReplace(view.textViewFoodPrice).plus(toppingPrice).let { convertTwoDigit(it) }
            }

        }, false)

        if (restaurantMenu?.toppings != null) {
            view.recyclerViewToppingTitle.adapter = toppingTitleAdapter
            toppingTitleAdapter?.items = restaurantMenu?.toppings
        }

        view.imageButtonAddition.setOnClickListener {

            count = count.inc()
            view.textViewCount.text = count.toString()
            view.imageButtonSubtract.background = ContextCompat.getDrawable(activity!!, R.drawable.ic_substact_item)
            view.imageButtonSubtract.isEnabled = true

            finalCount += 1
            //finalPrice += if (restaurantMenu?.specialOffer?.toFloat() != 0F) specialOfferPrice(restaurantMenu!!.price?.toFloat()!!, restaurantMenu!!.specialOffer?.toFloat()!!).toFloat() else restaurantMenu?.price?.toFloat()!!
          /*  finalPrice += if (restaurantMenu!!.specialOffer != null && restaurantMenu!!.specialOffer == "0") {
                restaurantMenu!!.price!!.toFloat()
            } else {
                restaurantMenu!!.originalPrice!!.toFloat()
            }*/

            finalPrice +=foodPrice

        }
        view.imageButtonSubtract.setOnClickListener {
            count = count.dec()
            view.textViewCount.text = count.toString()
            finalCount -= 1
            //finalPrice -= restaurantMenu?.price?.toFloat()!!
          /*  finalPrice -= if (restaurantMenu!!.specialOffer != null && restaurantMenu!!.specialOffer == "0") {
                restaurantMenu!!.price!!.toFloat()
            } else {
                restaurantMenu!!.originalPrice!!.toFloat()
            }*/

            finalPrice -=foodPrice
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
                val firstCheckIs = toppingTitleAdapter?.items?.firstOrNull { it.minCount < it.minQty!! && it.minQty!! != 0 }
                if (firstCheckIs != null) {
                    if (firstCheckIs.minCount != 0 && firstCheckIs.avialbeCount != 0 && firstCheckIs.avialbeCount >= firstCheckIs.minCount && firstCheckIs.minCount >= firstCheckIs.minQty!!) {
                        if (count == 0) {
                            val sb = Snackbar.make(view, "Please add quantity", Snackbar.LENGTH_LONG)
                            sb.show()
                        } else {
                            goToCart(view)
                        }
                    } else {
                        val sb = Snackbar.make(view, "Please choose ${firstCheckIs.type} up to ${firstCheckIs.minQty}", Snackbar.LENGTH_LONG)
                        sb.show()
                    }
                } else {
                    if (count == 0) {
                        val sb = Snackbar.make(view, "Please add quantity", Snackbar.LENGTH_LONG)
                        sb.show()
                    } else {
                        goToCart(view)
                    }
                }
            }


        }

        return view
    }

    private fun goToCart(view: View) {
        val dishesItem = DishesItem()
        dishesItem.menu = type!!
        dishesItem.specialNote = getText(view.textViewSpecialNote).toString()
        //dishesItem.price = restaurantMenu?.price?.toFloat()
        dishesItem.price = if (restaurantMenu!!.specialOffer != null && restaurantMenu!!.specialOffer == "0") {
            restaurantMenu!!.price!!.toFloat()
        } else {
            restaurantMenu!!.originalPrice!!.toFloat()
        }
        dishesItem.qty = finalCount
        dishesItem.status = restaurantMenu?.status
        dishesItem.calculateVatPrice = if (restaurantMenu!!.vatPrice != null && restaurantMenu!!.vatPrice == "0.0") "0.0" else (restaurantMenu!!.vatPrice?.toFloat()?.times(finalCount)).toString()
        //  dishesItem.toppings = toppingTitleAdapter?.items as ArrayList<ToppingsItems>?


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

        dishesItem.food = restaurantMenu?.food.toString()
        dishesItem.dish = restaurantMenu?.name.toString()
        dishesItem.menuId = restaurantMenu?.menuId.toString()
        dishesItem.id = restaurantMenu?.id.toString()
        dishesItem.dishId = restaurantMenu?.id.toString()

        var toppingPrice = 0f
        toppingTitleAdapter?.items?.forEach { toppingList ->
            toppingList.toppingList?.forEach {
                if (it.isCheckItem) {
                    toppingPrice += it.price!!
                }
            }
        }

        /*val foodPrices = specialOfferPrice(restaurantMenu!!.price?.toFloat()!!, restaurantMenu!!.specialOffer?.toFloat()!!).toFloat()
        val dishWithTopingPrice = foodPrices + toppingPrice*/
       /* val dishWithTopingPrice = if (restaurantMenu!!.specialOffer != null && restaurantMenu!!.specialOffer == "0") {
            restaurantMenu!!.price!!.toFloat() + toppingPrice
        } else {
            restaurantMenu!!.originalPrice!!.toFloat() + toppingPrice
        }*/
        val dishWithTopingPrice = convertTwoDigitFlot(restaurantMenu!!.price!!.toFloat() + toppingPrice)
        dishesItem.dishPriceTopping = dishWithTopingPrice


        dishesItem.totalPrice = dishesItem.qty!! * dishWithTopingPrice


        mParentListener?.add(dishesItem.totalPrice!!, dishesItem)
        dismiss()
    }

    interface OnChildFragmentInteractionListeners {
        fun add(finalPrice: Float, items: DishesItem)
    }

}