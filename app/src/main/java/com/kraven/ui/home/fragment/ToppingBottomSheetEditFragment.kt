package com.kraven.ui.home.fragment


//import com.kraven.extensions.specialOfferPrice
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
import com.kraven.ui.activity.IsolatedActivity
import com.kraven.ui.cart.fragment.CartFragment
import com.kraven.ui.home.adapter.ToppingTitleEditAdapter
import com.kraven.ui.home.model.DishesItem
import com.kraven.ui.home.model.RestaurantMenu
import com.kraven.ui.home.model.ToppingListItem
import com.kraven.ui.home.model.ToppingsItems
import com.kraven.utils.ConstantApp
import kotlinx.android.synthetic.main.bottomsheet_topping.view.*


class ToppingBottomSheetEditFragment : DialogFragment() {
    private var count = 0
    private var toppingTitleAdapter: ToppingTitleEditAdapter? = null
    private var newDishList: DishesItem? = null
    private var restaurantMenu: RestaurantMenu? = null
    private var type: String? = null
    private var mParentListener: OnChildFragmentInteractionListener? = null
    var finalCount = 0
    var finalPrice = 0F
    var toppingPrice = 0F
    private var toppingList = ArrayList<ToppingsItems>()

    var menuId = 0

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
        }
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is IsolatedActivity) {
            val parentFragment = context.getCurrentFragment<CartFragment>()
            if (parentFragment != null) {
                mParentListener = parentFragment
            } else {
                throw RuntimeException("The parent fragment must implement OnChildFragmentInteractionListener")
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
            newDishList = arguments?.getParcelable("NewDishList")
            restaurantMenu = arguments?.getParcelable(ConstantApp.PassValue.RESTAURANT_MENU)
            type = arguments?.getString(ConstantApp.PassValue.TYPE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottomsheet_topping, container, false)
        view.textViewFoodName.text = restaurantMenu?.name
        view.textViewFoodType.text = type
        //val foodPrice = specialOfferPrice(restaurantMenu!!.price?.toFloat()!!, restaurantMenu!!.specialOffer?.toFloat()!!).toFloat()

        /*val foodPrice =if(restaurantMenu!!.specialOffer!=null && restaurantMenu!!.specialOffer=="0"){
            restaurantMenu!!.price!!.toFloat()
        }else{
            restaurantMenu!!.originalPrice!!.toFloat()
        }*/
        val foodPrice =  convertTwoDigitFlot(restaurantMenu!!.price!!.toFloat())

        view.textViewFoodPrice.text = foodPrice.toString()

        view.textViewOffPrice.visibility = if (restaurantMenu!!.specialOffer?.toFloat() != 0F) View.VISIBLE else View.GONE
        if (view.textViewOffPrice.visibility == View.VISIBLE) {
            view.textViewOffPrice.paintFlags = view.textViewOffPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            view.textViewOffPrice.text = convertTwoDigit(restaurantMenu?.originalPrice?.toFloat()!!)
        }

        view.textViewCount.text = newDishList?.qty.toString()
        view.textViewSpecialNote.setText(newDishList?.specialNote)

        view.imageViewBack.setOnClickListener {
            dismissAllowingStateLoss()
        }


        view.buttonAddToCart.text = "Update Cart"
        count = newDishList?.qty!!
        finalCount = newDishList?.qty!!
        finalPrice = newDishList?.price!!
        this@ToppingBottomSheetEditFragment.toppingList = newDishList?.toppings!!
        if (count == 0) {
            view.imageButtonSubtract.background = ContextCompat.getDrawable(activity!!, R.drawable.ic_substact_item_disable)
            view.imageButtonSubtract.isEnabled = false
        } else {
            view.imageButtonSubtract.background = ContextCompat.getDrawable(activity!!, R.drawable.ic_substact_item)
            view.imageButtonSubtract.isEnabled = true
        }


        view.recyclerViewToppingTitle.layoutManager = LinearLayoutManager(activity!!)
        toppingTitleAdapter = ToppingTitleEditAdapter(object : ToppingTitleEditAdapter.OnClickListener {
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


        if(restaurantMenu?.toppings!=null) {
            view.recyclerViewToppingTitle.adapter = toppingTitleAdapter
            toppingTitleAdapter!!.items = newDishList?.toppings
        }
        var toppingPrice = 0f
        toppingTitleAdapter?.items?.forEach { toppingList ->
            toppingList.toppingList?.forEach {
                if (it.isCheckItem) {
                    toppingPrice += it.price!!
                }
            }
        }

        view.textViewTotal.text = getTextReplace(view.textViewFoodPrice).plus(toppingPrice).let { convertTwoDigit(it) }

        view.imageButtonAddition.setOnClickListener {
            count = count.inc()
            view.textViewCount.text = count.toString()
            view.imageButtonSubtract.background = ContextCompat.getDrawable(activity!!, R.drawable.ic_substact_item)
            view.imageButtonSubtract.isEnabled = true

            finalCount += 1
            //finalPrice += if (restaurantMenu?.specialOffer?.toFloat() != 0F) specialOfferPrice(restaurantMenu!!.price?.toFloat()!!, restaurantMenu!!.specialOffer?.toFloat()!!).toFloat() else restaurantMenu?.price?.toFloat()!!
           /* finalPrice += if(restaurantMenu!!.specialOffer!=null && restaurantMenu!!.specialOffer=="0"){
                restaurantMenu!!.price!!.toFloat()
            }else{
                restaurantMenu!!.originalPrice!!.toFloat()
            }*/
            finalPrice +=foodPrice

        }
        view.imageButtonSubtract.setOnClickListener {
            count = count.dec()
            view.textViewCount.text = count.toString()
            finalCount -= 1
            //finalPrice -= restaurantMenu?.price?.toFloat()!!
           /* finalPrice -= if(restaurantMenu!!.specialOffer!=null && restaurantMenu!!.specialOffer=="0"){
                restaurantMenu!!.price!!.toFloat()
            }else{
                restaurantMenu!!.originalPrice!!.toFloat()
            }*/

            finalPrice -=foodPrice
            if (count == 0) {
                view.imageButtonSubtract.background = ContextCompat.getDrawable(activity!!, R.drawable.ic_substact_item_disable)
                view.imageButtonSubtract.isEnabled = false
            }
        }
        view.buttonAddToCart.setOnClickListener {

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


        return view
    }


    private fun goToCart(view: View) {
        val dishesItem = DishesItem()
        dishesItem.menu = type!!
        dishesItem.specialNote = getText(view.textViewSpecialNote)
        dishesItem.name = newDishList?.name
        dishesItem.restaurantMenu = restaurantMenu
        dishesItem.originalPrice = restaurantMenu!!.originalPrice
        dishesItem.specialNote = getText(view.textViewSpecialNote).toString()
        dishesItem.price = if(restaurantMenu!!.specialOffer!=null && restaurantMenu!!.specialOffer=="0"){
            restaurantMenu!!.price!!.toFloat()
        }else{
            restaurantMenu!!.originalPrice!!.toFloat()
        }
        //dishesItem.price = specialOfferPrice(restaurantMenu!!.price?.toFloat()!!, restaurantMenu!!.specialOffer?.toFloat()!!).toFloat()
        dishesItem.qty = finalCount
        //dishesItem.vat = restaurantMenu!!.vat
        dishesItem.vatPrice = restaurantMenu?.vatPrice
        dishesItem.calculateVatPrice = if(restaurantMenu!!.vatPrice!=null && restaurantMenu!!.vatPrice=="0.0") "0.0" else (restaurantMenu!!.vatPrice?.toFloat()?.times(finalCount)).toString()
        dishesItem.status = restaurantMenu?.status
        dishesItem.specialOffer = newDishList!!.specialOffer
        //dishesItem.toppings = toppingTitleAdapter!!.items as ArrayList<ToppingsItems>?
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
        dishesItem.food = newDishList?.food.toString()
        dishesItem.dish = restaurantMenu?.name.toString()
        dishesItem.menuId = newDishList?.id.toString()
        dishesItem.dishId = restaurantMenu?.id.toString()

        var toppingPrice = 0f
        toppingList.forEach { toppingList ->
            toppingList.toppingList?.forEach {
                if (it.isCheckItem) {
                    toppingPrice += it.price!!
                }
            }
        }
        //val dishWithTopingPrice = specialOfferPrice(restaurantMenu!!.price?.toFloat()!!, restaurantMenu!!.specialOffer?.toFloat()!!).toFloat() + toppingPrice
       /* val dishWithTopingPrice = if(restaurantMenu!!.specialOffer!=null && restaurantMenu!!.specialOffer=="0"){
            restaurantMenu!!.price!!.toFloat() +  toppingPrice
        }else{
            restaurantMenu!!.originalPrice!!.toFloat() + toppingPrice
        }*/

        val dishWithTopingPrice = convertTwoDigitFlot(restaurantMenu!!.price!!.toFloat() + toppingPrice)

        dishesItem.dishPriceTopping = dishWithTopingPrice

        dishesItem.totalPrice = dishesItem.qty!! * dishWithTopingPrice


        mParentListener?.addFood(finalPrice, finalCount, dishesItem, restaurantMenu?.id!!, finalPrice, toppingPrice)
        dismiss()
    }

    interface OnChildFragmentInteractionListener {
        fun addFood(dishPriceTopping: Float, finalCount: Int, items: DishesItem, id: Int, itemPrice: Float, toppingPrice: Float)
    }

}

