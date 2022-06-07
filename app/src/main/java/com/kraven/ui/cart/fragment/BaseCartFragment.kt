package com.kraven.ui.cart.fragment


import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import com.google.android.gms.maps.model.LatLng
import com.kraven.R
import com.kraven.application.KravenCustomer
import com.kraven.core.AppExecutors
import com.kraven.data.pojo.Parameters
import com.kraven.extensions.*
import com.kraven.ui.address.model.Address
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.cart.adapter.BeverageCartAdapter
import com.kraven.ui.cart.adapter.CartAdapter
import com.kraven.ui.cart.adapter.TipAdapter
import com.kraven.ui.cart.model.TollFee
import com.kraven.ui.cart.viewModel.CartViewModel
import com.kraven.ui.home.model.*
import com.kraven.ui.home.viewmodel.HomeViewModel
import com.kraven.ui.order.beverage.model.Beverage
import com.kraven.ui.order.beverage.model.BeverageSend
import com.kraven.utils.ConstantApp
import com.kraven.utils.Formatter
import com.kraven.utils.LocationManager
import io.reactivex.Observable
import kotlinx.android.synthetic.main.cart_fragment.*
import kotlinx.android.synthetic.main.common_cart_layout.*
import kotlinx.android.synthetic.main.layout_apply_code_tip.*
import javax.inject.Inject


abstract class BaseCartFragment : BaseFragment() {

    var cartAdapter: CartAdapter? = null
    var beverageCartAdapter: BeverageCartAdapter? = null
    var walletAmount = 0.00F
    var promoCode: Promocode? = null

    var restaurantDetails: RestaurantDetails? = null
    var address: Address? = null

    var selectTips: String? = null
    var tipAdapter: TipAdapter? = null
    var isEnter: Boolean? = null
    var distance = 0F
    var appExecutors = AppExecutors()

    lateinit var viewModel: CartViewModel
    lateinit var homeViewModel: HomeViewModel

    var cartModel: CartModel? = null

    var tollFee: TollFee? = null

    var addressId = ""
    var dialog: Dialog? = null

    @Inject
    lateinit var locationManager: LocationManager

    fun tipList(): ArrayList<String> {

        val menuLists = ArrayList<String>()
        menuLists.add("5")
        menuLists.add("10")
        menuLists.add("15")
        menuLists.add("20")
        menuLists.add("30")

        return menuLists

    }

    fun setUpLodingView() {
        dialog = Dialog(activity!!, android.R.style.Theme_Dialog)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setContentView(R.layout.loder_view)
        dialog?.setCanceledOnTouchOutside(true)

        //dialog.show()
    }


    fun generateBeverageDishItem(it: Beverage, dishItem: DishesItem): DishesItem {
        var totalPrice = 0F
        val beverage = DishesItem()
        beverage.id = it.id
        beverage.itemId = it.itemId
        beverage.menuId = it.menuId
        beverage.originalPrice = it.originalPrice
        beverage.beverageId = it.beverageId
        beverage.specialNote = dishItem.specialNote
        beverage.price = convertTwoDigitFlot(it.price!!)
        beverage.image = it.image
        beverage.qty = dishItem.qty
        beverage.name = it.name
        beverage.description = it.description
        beverage.menu = it.menu
        //beverage.vat = it.vat
        beverage.vatPrice = it.vatPrice?.toFloat().toString()
        //beverage.vatPrice = (twoDigit(it.vatPrice?.toFloat()!!).toFloat() * dishItem.qty!!).toString()
        beverage.specialOffer = it.specialOffer
        beverage.milliliter = it.milliliter
        beverage.status = it.status
        beverage.calculateVatPrice = if (beverage.vatPrice != null && beverage.vatPrice == "0.0") "0.0"
        else (twoDigit(beverage.vatPrice?.toFloat()!!).toFloat().times(dishItem.qty!!)).toString()
        beverage.totalPrice = it.price?.times(dishItem.qty!!)
        beverage.toppings = generateBeverageTopping(arrayListOf(it.toppings!!), dishItem.toppings!!)


        var toppingPrice = 0f
        beverage.toppings?.forEach { toppingList ->
            toppingList.toppingList?.forEach {
                if (it.isCheckItem) {
                    toppingPrice += it.price!!
                }
            }
        }

        //val dishWithToppingPrice = if (toppingPrice != 0f) toppingPrice else specialOfferPrice(beverage.price!!, beverage.specialOffer!!).toFloat()
        val dishWithToppingPrice = if (toppingPrice != 0f) toppingPrice else it.price!!.toFloat()
        beverage.dishPriceTopping = convertTwoDigitFlot(dishWithToppingPrice)
        beverage.totalPrice = beverage.qty!! * dishWithToppingPrice

        totalPrice += beverage.qty!! * dishWithToppingPrice

        beverage.totalPrice = totalPrice

        return beverage
    }


    private fun generateBeverageTopping(serverToppings: ArrayList<ToppingsItems>, localToppings: ArrayList<ToppingsItems>): java.util.ArrayList<ToppingsItems>? {

        val resultToppingItems = ArrayList<ToppingsItems>()

        serverToppings.forEach { serverToppingItem ->

            val toppingItem = ToppingsItems()
            toppingItem.id = serverToppingItem.id
            toppingItem.type = serverToppingItem.type
            if (serverToppingItem.toppingList?.isNotEmpty()!!) {
                toppingItem.minQty = 1
            }

            val localToppingsItems = localToppings.firstOrNull { localToppings ->
                localToppings.id == serverToppingItem.id
            }

            if (localToppingsItems?.minCount == null) {
                toppingItem.minCount = 0
            } else {

                localToppingsItems.toppingList?.filter { it.isCheckItem }?.forEach { _ -> toppingItem.minCount += 1 }
                serverToppingItem.toppingList?.forEach {
                    if (it.status == ConstantApp.RestaurantStatus.AVAILABLE) {
                        toppingItem.avialbeCount += 1
                    }
                }
            }

            val toppingItemToppingList = ArrayList<ToppingListItem>()
            if (localToppingsItems != null) {

                serverToppingItem.toppingList?.forEach { serverToppingItemToppingListItem ->

                    val localToppingItem =
                            localToppingsItems.toppingList?.firstOrNull { localToppingItemToppingListItem ->
                                localToppingItemToppingListItem.id == serverToppingItemToppingListItem.id
                            }

                    if (localToppingItem != null) {
                        val toppingListItem = ToppingListItem()
                        toppingListItem.isCheckItem = if (serverToppingItemToppingListItem.status == ConstantApp.RestaurantStatus.AVAILABLE) localToppingItem.isCheckItem else false
                        toppingListItem.id = serverToppingItemToppingListItem.id
                        toppingListItem.price = serverToppingItemToppingListItem.price
                        toppingListItem.name = serverToppingItemToppingListItem.name
                        toppingListItem.status = serverToppingItemToppingListItem.status
//                        val convertTwoDigit = testTwoDigit(serverToppingItemToppingListItem.vatPrice!!.toFloat())
//                        toppingListItem.vatPrice = convertTwoDigit
                        toppingListItem.vatPrice = serverToppingItemToppingListItem.vatPrice!!
                        toppingItemToppingList.add(toppingListItem)
                    } else {
                        val toppingListItem = ToppingListItem()
                        toppingListItem.isCheckItem = false
                        toppingListItem.id = serverToppingItemToppingListItem.id
                        toppingListItem.price = serverToppingItemToppingListItem.price
                        toppingListItem.name = serverToppingItemToppingListItem.name
                        toppingListItem.status = serverToppingItemToppingListItem.status
//                        val convertTwoDigit = testTwoDigit(serverToppingItemToppingListItem.vatPrice!!.toFloat())
//                        toppingListItem.vatPrice = convertTwoDigit
                        toppingListItem.vatPrice = serverToppingItemToppingListItem.vatPrice!!
                        toppingItemToppingList.add(toppingListItem)
                    }
                }

            } else {
                var localToppingsItemsList: ToppingListItem?

                serverToppingItem.toppingList?.forEach { serverToppingItemToppingListItem ->
                    localToppings.forEach {

                        localToppingsItemsList = it.toppingList?.firstOrNull { localToppingItemToppingListItem ->
                            localToppingItemToppingListItem.id == serverToppingItemToppingListItem.id
                        }

                        if (localToppingsItemsList != null) {
                            val toppingListItem = ToppingListItem()
                            toppingListItem.isCheckItem = if (serverToppingItemToppingListItem.status == ConstantApp.RestaurantStatus.AVAILABLE) localToppingsItemsList!!.isCheckItem else false
                            toppingListItem.id = serverToppingItemToppingListItem.id
                            toppingListItem.price = serverToppingItemToppingListItem.price
                            toppingListItem.name = serverToppingItemToppingListItem.name
                            toppingListItem.status = serverToppingItemToppingListItem.status
//                            val convertTwoDigit = testTwoDigit(serverToppingItemToppingListItem.vatPrice!!.toFloat())
//                            toppingListItem.vatPrice = convertTwoDigit
                            toppingListItem.vatPrice = serverToppingItemToppingListItem.vatPrice!!
                            toppingItemToppingList.add(toppingListItem)
                        } else {
                            val toppingListItem = ToppingListItem()
                            toppingListItem.isCheckItem = false
                            toppingListItem.id = serverToppingItemToppingListItem.id
                            toppingListItem.price = serverToppingItemToppingListItem.price
                            toppingListItem.name = serverToppingItemToppingListItem.name
                            toppingListItem.status = serverToppingItemToppingListItem.status
//                            val convertTwoDigit = testTwoDigit(serverToppingItemToppingListItem.vatPrice!!.toFloat())
//                            toppingListItem.vatPrice = convertTwoDigit
                            toppingListItem.vatPrice = serverToppingItemToppingListItem.vatPrice!!
                            toppingItemToppingList.add(toppingListItem)
                        }
                    }
                }
            }
            toppingItem.toppingList = toppingItemToppingList
            resultToppingItems.add(toppingItem)
        }


        return resultToppingItems
    }

    fun generateDishItem(restaurantMenu: RestaurantMenu, dishItem: DishesItem): DishesItem {
        var totalPrice = 0F
        val resultDishItem = DishesItem()
        resultDishItem.menuId = restaurantMenu.menuId.toString()
        resultDishItem.id = restaurantMenu.id.toString()
        resultDishItem.restaurantMenu = restaurantMenu
        resultDishItem.price = convertTwoDigitFlot(restaurantMenu.price?.toFloat()!!)
        resultDishItem.specialNote = dishItem.specialNote
        resultDishItem.status = restaurantMenu.status
        resultDishItem.qty = dishItem.qty
        resultDishItem.food = restaurantMenu.food
        resultDishItem.originalPrice = restaurantMenu.originalPrice
        resultDishItem.menu = dishItem.menu
        resultDishItem.dishId = dishItem.dishId
        //resultDishItem.vat = (restaurantMenu.vat?.toFloat()).toString()
        resultDishItem.vatPrice = restaurantMenu.vatPrice?.toFloat().toString()
        resultDishItem.calculateVatPrice = restaurantMenu.vatPrice?.toFloat()!!.toFloat().times(dishItem.qty!!).toString()
//        resultDishItem.calculateVatPrice = if (restaurantMenu.vatPrice != null && restaurantMenu.vatPrice == "0.0") "0.0"
//        else (twoDigit(restaurantMenu.vatPrice?.toFloat()!!).toFloat().times(dishItem.qty!!)).toString()
        resultDishItem.name = restaurantMenu.name
        resultDishItem.specialOffer = restaurantMenu.specialOffer?.toFloat()

        resultDishItem.toppings = generateToppings(restaurantMenu.toppings, dishItem.toppings)
       /* totalPrice += if (restaurantMenu.specialOffer != null && restaurantMenu.specialOffer == "0") {
            restaurantMenu.price?.toFloat()!!
        } else {
            restaurantMenu.originalPrice?.toFloat()!!
        }*/
        totalPrice += restaurantMenu.price?.toFloat()!!


        var toppingPrice = 0f
        resultDishItem.toppings?.forEach { toppingList ->
            toppingList.toppingList?.forEach {
                if (it.isCheckItem) {
                    toppingPrice += it.price!!
                    totalPrice += it.price!!
                }
            }
        }
        resultDishItem.totalPrice = totalPrice



       // if (restaurantMenu.specialOffer != null && restaurantMenu.specialOffer == "0") {
            val dishWithToppingPrice = restaurantMenu.price!!.toFloat() + toppingPrice
            resultDishItem.dishPriceTopping = convertTwoDigitFlot(dishWithToppingPrice)
       /* } else {
            val dishWithToppingPrice = resultDishItem.originalPrice!!.toFloat() + toppingPrice
            resultDishItem.dishPriceTopping = dishWithToppingPrice
        }*/


        return resultDishItem
    }

    private fun generateToppings(serverToppings: ArrayList<ToppingsItems>?, localToppings: ArrayList<ToppingsItems>?): ArrayList<ToppingsItems>? {
        val resultToppingItems = ArrayList<ToppingsItems>()

        serverToppings?.forEach { serverToppingItem ->

            val toppingItem = ToppingsItems()
            toppingItem.id = serverToppingItem.id
            toppingItem.type = serverToppingItem.type
            toppingItem.category = serverToppingItem.category
            toppingItem.minQty = serverToppingItem.minQty


            val localToppingsItems = localToppings?.firstOrNull { localToppings ->
                localToppings.id == serverToppingItem.id
            }

            if (localToppingsItems?.minCount == null) {
                toppingItem.minCount = 0
            } else {
                localToppingsItems.toppingList?.filter { it.isCheckItem }?.forEach { _ -> toppingItem.minCount += 1 }

                serverToppingItem.toppingList?.forEach {
                    if (it.status == ConstantApp.RestaurantStatus.AVAILABLE) {
                        toppingItem.avialbeCount += 1
                    }
                }
            }

            val toppingItemToppingList = ArrayList<ToppingListItem>()
            if (localToppingsItems != null) {
                serverToppingItem.toppingList?.forEach { serverToppingItemToppingListItem ->

                    val localToppingItem =
                            localToppingsItems.toppingList?.firstOrNull { localToppingItemToppingListItem ->
                                localToppingItemToppingListItem.id == serverToppingItemToppingListItem.id
                            }

                    if (localToppingItem != null) {
                        val toppingListItem = ToppingListItem()

                        toppingListItem.isCheckItem = if (serverToppingItemToppingListItem.status == ConstantApp.RestaurantStatus.AVAILABLE) localToppingItem.isCheckItem else false
                        toppingListItem.id = serverToppingItemToppingListItem.id
                        toppingListItem.price = serverToppingItemToppingListItem.price
                        toppingListItem.name = serverToppingItemToppingListItem.name
                        toppingListItem.status = serverToppingItemToppingListItem.status
//                        val convertTwoDigit = testTwoDigit(serverToppingItemToppingListItem.vatPrice!!.toFloat())
//                        toppingListItem.vatPrice = convertTwoDigit
                        toppingListItem.vatPrice = serverToppingItemToppingListItem.vatPrice!!

                        toppingItemToppingList.add(toppingListItem)
                    } else {
                        val toppingListItem = ToppingListItem()
                        toppingListItem.isCheckItem = false
                        toppingListItem.id = serverToppingItemToppingListItem.id
                        toppingListItem.price = serverToppingItemToppingListItem.price
                        toppingListItem.name = serverToppingItemToppingListItem.name
                        toppingListItem.status = serverToppingItemToppingListItem.status
//                        val convertTwoDigit = testTwoDigit(serverToppingItemToppingListItem.vatPrice!!.toFloat())
//                        toppingListItem.vatPrice = convertTwoDigit
                        toppingListItem.vatPrice = serverToppingItemToppingListItem.vatPrice!!
                        toppingItemToppingList.add(toppingListItem)
                    }

                }

            } else {
                serverToppingItem.toppingList?.forEach { serverToppingItemToppingListItem ->
                    val toppingListItem = ToppingListItem()
                    toppingListItem.isCheckItem = false
                    toppingListItem.id = serverToppingItemToppingListItem.id
                    toppingListItem.price = serverToppingItemToppingListItem.price
                    toppingListItem.name = serverToppingItemToppingListItem.name
                    toppingListItem.status = serverToppingItemToppingListItem.status
//                    val convertTwoDigit = testTwoDigit(serverToppingItemToppingListItem.vatPrice!!.toFloat())
//                    toppingListItem.vatPrice = convertTwoDigit
                    toppingListItem.vatPrice = serverToppingItemToppingListItem.vatPrice!!
                    toppingItemToppingList.add(toppingListItem)
                }
            }
            toppingItem.toppingList = toppingItemToppingList
            resultToppingItems.add(toppingItem)
        }

        return resultToppingItems
    }

    fun radioButtonPickupVisibility(restaurantDetails: RestaurantDetails) {
        this@BaseCartFragment.restaurantDetails = restaurantDetails

        if (restaurantDetails.deliveryType == ConstantApp.DELIVERY) {
            radioButtonPickup.visibility = View.GONE
        } else if (restaurantDetails.deliveryType == ConstantApp.PICKUP) {
            radioButtonDelivery.visibility = View.GONE
        }

        if (address != null) {
            homeViewModel.checkToolFeeLocation(Parameters(
                    pickupLatitude = restaurantDetails.latitude,
                    pickupLongitude = restaurantDetails.longitude,
                    latitude = address?.latitude.toString(), longitude = address?.longitude.toString()))
        }
    }

    private fun calculateSubTotal(): Float {
        var subTotal = 0.00F

        if (cartAdapter != null) {
            cartAdapter?.items?.forEach { dishes ->
                subTotal += (dishes.dishPriceTopping!! * dishes.qty!!)
            }
        } else {
            beverageCartAdapter?.items?.forEach { dishes ->
                subTotal += dishes.totalPrice!!
            }
        }

        return subTotal
    }

    private fun calculateVat(): Float {
        var vatPrice = 0.00F
        var toppingsVatPrice = 0.00F

        if (cartAdapter != null) {
            cartAdapter?.items?.forEach { dishes ->
                if (dishes.calculateVatPrice != null) {
                    val convertTwoDigit = dishes.calculateVatPrice?.toFloat()!!
                    vatPrice += convertTwoDigit
                }

                if (dishes.toppings != null) {
                    dishes.toppings?.forEach { it ->
                        it.toppingList?.filter { it.isCheckItem }?.forEach { toppingItems ->
                            val convertTwoDigit = toppingItems.vatPrice?.toFloat()!!.times(dishes.qty!!)
                            toppingsVatPrice += convertTwoDigit
                        }
                    }
                }
            }
        } else {
            beverageCartAdapter?.items?.forEach { dishes ->
                if (dishes.calculateVatPrice != null) {

                    if (dishes.toppings != null && dishes.toppings!!.isNotEmpty()) {
                        dishes.toppings?.forEach { it ->
                            val filterList = it.toppingList?.filter { it.isCheckItem }
                            if (filterList?.isEmpty()!!) {
                                vatPrice += dishes.calculateVatPrice?.toFloat() ?: 0.0f
                            } else {
                                filterList.forEach { toppingItems ->
                                    val convertTwoDigit = toppingItems.vatPrice?.toFloat()!!.times(dishes.qty!!)
                                    toppingsVatPrice += convertTwoDigit
                                }
                            }

                        }
                    } else {
                        vatPrice += dishes.calculateVatPrice?.toFloat() ?: 0.0f
                    }
                }
            }
        }

        return vatPrice.plus(toppingsVatPrice)
        /*return if (restaurantDetails?.vatOption != null && restaurantDetails?.vatOption == "VAT") {
            val data = session.userSettings?.vat
            val total = calculateSubTotal()
            total * data!!.toFloat() / 100
        } else {
            0.00F
        }*/
    }

    @SuppressLint("CheckResult")
    fun calculateTotal(): Float {

        val subTotal = calculateSubTotal()
        val vat = calculateVat()
        val tip = Formatter.round(calculateTip().toDouble()).toFloat()

        val discount = calculateDiscount()

        return if (tollFee?.tollfee != null && radioButtonDelivery.isChecked) {
            subTotal + vat + (if (getTextReplaceString(textViewDeliveryChargeValue).isNotEmpty()
                    && getTextReplaceString(textViewDeliveryChargeValue) != "0.0F") getTextReplace(textViewDeliveryChargeValue) else 0.0F) + tip + tollFee?.tollfee?.toFloat()!! - discount
        } else {
            subTotal + vat + (if (getTextReplaceString(textViewDeliveryChargeValue).isNotEmpty()
                    && getTextReplaceString(textViewDeliveryChargeValue) != "0.0F") getTextReplace(textViewDeliveryChargeValue) else 0.0F) + tip - discount
        }
    }

    @SuppressLint("CheckResult")
    fun calculateTotalDiscount(): Float {

        val subTotal = calculateSubTotal()
        val vat = calculateVat()
        val tip = Formatter.round(calculateTip().toDouble()).toFloat()


        return if (tollFee?.tollfee != null && radioButtonDelivery.isChecked) {
            subTotal + vat + (if (getTextReplaceString(textViewDeliveryChargeValue).isNotEmpty()
                    && getTextReplaceString(textViewDeliveryChargeValue) != "0.0F") getTextReplace(textViewDeliveryChargeValue) else 0.0F) + tip + tollFee?.tollfee?.toFloat()!!
        } else {
            subTotal + vat + (if (getTextReplaceString(textViewDeliveryChargeValue).isNotEmpty()
                    && getTextReplaceString(textViewDeliveryChargeValue) != "0.0F") getTextReplace(textViewDeliveryChargeValue) else 0.0F) + tip
        }
    }

    private fun calculateWallet(): Float {
        return if (walletAmount > Formatter.round(calculateTotal().toDouble()).toFloat()) {
            Formatter.round(calculateTotal().toDouble()).toFloat()
        } else {
            walletAmount
        }
    }

    private fun calculateTotalWithWallet(): Float {

        if (walletAmount > Formatter.round(calculateTotal().toDouble()).toFloat()) {
            return 0.0F
        } else {
            return Formatter.round(calculateTotal().toDouble()).toFloat() - walletAmount

            /*if (selectTips != null && selectTips?.isNotEmpty()!! && textViewTotal.text.isNotEmpty() && getTextReplace(textViewTotal) == 0.0F) {
               if (isEnter!!) {
                   textViewTip.text = getString(R.string.tip, "0.00%")
                   buttonTip.text = convertTwoDigit(selectTips?.toFloat()!!)
                   return  selectTips!!.toFloat()
               } else {
                   buttonTip.text = selectTips.plus("%")
                   textViewTip.text = getString(R.string.tip, selectTips.plus("%"))
                   return ((calculateSubTotal() * selectTips!!.toInt()) / 100)
               }
           } else {
               return Formatter.round(calculateTotal().toDouble()).toFloat() - walletAmount
           }*/
        }
    }


    private fun calculateDiscount(): Float {
        if (KravenCustomer.tempPromoCode != null && KravenCustomer.tempPromoCode!!.minimumAmount!! <= calculateSubTotal()) {
            promoCode = KravenCustomer.tempPromoCode
            imageViewCancelPromocode.visibility = View.VISIBLE
            editTextPromoCode.setText(promoCode!!.promocode)
            editTextPromoCode.clearFocus()
        }
        if (promoCode != null) {
            return if (promoCode?.type == getString(R.string.Percentage)) {
                val totalAmount = calculateTotalDiscount()
                totalAmount * promoCode?.amount?.toFloat()!! / 100
            } else {
                val totalAmount = calculateTotalDiscount()
                val promoCodeAmount = promoCode?.amount?.toFloat()
                if (totalAmount > promoCodeAmount!!) {
                    promoCode?.amount?.toFloat()!!
                } else {
                    totalAmount
                }
            }
        }
        return 0.00F
    }

    fun calculateCharges(): Observable<Float> {
        return if (radioButtonDelivery != null && radioButtonDelivery.isChecked && address != null) {
            //showLoader(true)
            dialog?.show()
            viewModel.calculateCharges(
                    LatLng(restaurantDetails!!.latitude.toDouble(), restaurantDetails!!.longitude.toDouble()),
                    LatLng(address!!.latitude.toDouble(), address!!.longitude.toDouble()), getString(R.string.google_maps_broswer_key))
        } else {
            Observable.just(0.0F)
        }
    }

    private fun calculateTip(): Float {
        if (selectTips != null && selectTips?.isNotEmpty()!!) {

            buttonTip.isEnabled = false
            llTip.isEnabled = false

            return if (isEnter!!) {
                try {
                    imageViewCloseTip.visibility = View.VISIBLE
                    textViewTip.text = getString(R.string.tip, "0.00%")
                    buttonTip.text = convertTwoDigit(selectTips?.toFloat()!!)
                    selectTips!!.toFloat()
                } catch (e: NumberFormatException) {
                    0.00F
                }
            } else {
                imageViewCloseTip.visibility = View.VISIBLE
                buttonTip.text = selectTips.plus("%")
                textViewTip.text = getString(R.string.tip, selectTips.plus("%"))
                ((calculateSubTotal() * selectTips!!.toInt()) / 100)
            }
        }
        return 0.00F
    }

    @SuppressLint("CheckResult")
    fun setCalculatedData() {
        textViewSubTotalValue.text = convertTwoDigit(calculateSubTotal())
        textViewVatValue.text = convertTwoDigit(calculateVat())

        if (radioButtonDelivery.isChecked && address != null) {
            textViewWalletValue.text = convertTwoDigit(calculateWallet())
            textViewDiscountValue.text = convertTwoDigit(calculateDiscount())
            textViewTipValue.text = convertTwoDigit(Formatter.round(calculateTip().toDouble()).toFloat())
            textViewTotal.text = convertTwoDigit(calculateTotalWithWallet())
        } else {
            textViewDeliveryChargeValue.text = convertTwoDigit(0.0F)
            textViewWalletValue.text = convertTwoDigit(calculateWallet())
            textViewDiscountValue.text = convertTwoDigit(calculateDiscount())
            textViewTipValue.text = convertTwoDigit(Formatter.round(calculateTip().toDouble()).toFloat())
            textViewTotal.text = convertTwoDigit(calculateTotalWithWallet())
        }

        if (tollFee?.tollfee != null && radioButtonDelivery.isChecked) {
            groupTollFee.viewShow()
            textViewTollFeeValue.text = convertTwoDigit(tollFee?.tollfee?.toFloat() ?: 0.0f)
        } else {
            groupTollFee.viewGone()
        }
    }

    fun getTotalQuantity(): Int {
        var totalQty = 0
        if (cartAdapter != null && cartAdapter!!.items.size != 0) {
            cartAdapter?.items?.forEach { cartItem ->
                totalQty += cartItem.qty!!
            }
        } else {
            beverageCartAdapter?.items?.forEach { cartItem ->
                totalQty += cartItem.qty!!
            }
        }

        return totalQty
    }


    fun getFinalPrice(): Float? {
        var totalPrice = 0f
        if (cartAdapter != null) {
            cartAdapter?.items?.forEach { cartItem ->
                totalPrice += cartItem.dishPriceTopping!! * cartItem.qty!!
            }
        } else {
            beverageCartAdapter?.items?.forEach { cartItem ->
                totalPrice += cartItem.dishPriceTopping!! * cartItem.qty!!
            }
        }
        return totalPrice
    }

    fun getFinalPriceBeverage(): Float? {
        var totalPrice = 0f
        beverageCartAdapter?.items?.forEach { cartItem ->
            totalPrice += cartItem.dishPriceTopping!! * cartItem.qty!!
        }
        return totalPrice
    }


    fun sendBeverageCart(items: List<DishesItem>?): ArrayList<BeverageSend> {
        val sendBeverage = ArrayList<BeverageSend>()
        items?.forEach { beverageItems ->
            val beverage = BeverageSend()
            beverage.id = beverageItems.id
            beverage.menuId = beverageItems.menuId
            beverage.itemId = beverageItems.id
            beverage.menu = beverageItems.menu
            beverage.vat = beverageItems.vat

            beverage.item = beverageItems.name
            beverage.milliliter = beverageItems.milliliter
            beverage.image = beverageItems.image?.substring(beverageItems.image?.lastIndexOf("/")!! + 1)
            beverage.qty = beverageItems.qty
            beverageItems.toppings?.forEach { toppingItem ->
                val selectedList = toppingItem.toppingList?.filter { it.isCheckItem }
                if (selectedList != null && selectedList.isNotEmpty()) {
                    beverage.price = 0.00F
                    selectedList.forEach {
                        beverage.vatPrice = it.vatPrice
                    }
                } else {
                    //beverage.price = specialOfferPrice(beverageItems.price!!, beverageItems.specialOffer!!).toFloat()
                    if (beverageItems.specialOffer != null && beverageItems.specialOffer == 0F) {
                        beverage.price = beverageItems.price
                    } else {
                        beverage.price = beverageItems.originalPrice!!.toFloat()
                    }

                    beverage.vatPrice = beverageItems.vatPrice
                }
            }

            beverage.specialNotes = beverageItems.specialNote
            if (sendBeverageTopping(beverageItems.toppings)!!.isNotEmpty()) {
                beverage.sendToppings = sendBeverageTopping(beverageItems.toppings)
            }
            sendBeverage.add(beverage)
        }
        return sendBeverage
    }

    private fun sendBeverageTopping(toppings: ArrayList<ToppingsItems>?): ArrayList<SendToppingListItem>? {
        val toppingItems = ArrayList<SendToppingListItem>()
        toppings?.forEach { toppingItem ->
            toppingItem.toppingList?.forEach {
                if (it.isCheckItem) {
                    val sendToppingList = SendToppingListItem()
                    sendToppingList.id = it.id
                    sendToppingList.name = it.name
                    sendToppingList.price = it.price
                    toppingItems.add(sendToppingList)
                }
            }
        }
        return toppingItems
    }

    fun sendDishItem(items: List<DishesItem>?): List<SendItems>? {

        val sendList = ArrayList<SendItems>()

        items?.forEach { dishItems ->
            val sendDishItem = SendItems()
            sendDishItem.menuId = dishItems.menuId
            sendDishItem.dishId = dishItems.dishId
            sendDishItem.menu = dishItems.menu
            sendDishItem.vat = dishItems.vat
            sendDishItem.vatPrice = dishItems.vatPrice
            sendDishItem.dish = dishItems.name
            sendDishItem.specialOffer = dishItems.specialOffer.toString()
            sendDishItem.food = dishItems.food
            sendDishItem.qty = dishItems.qty
            //sendDishItem.price = specialOfferPrice(dishItems.price!!, dishItems.specialOffer!!).toFloat()
            if (dishItems.specialOffer != null && dishItems.specialOffer == 0F) {
                sendDishItem.price = dishItems.price!!.toFloat()
            } else {
                sendDishItem.price = dishItems.originalPrice!!.toFloat()
            }

            sendDishItem.specialNote = dishItems.specialNote

            if (sendToppings(dishItems.toppings).isNotEmpty()) {
                sendDishItem.sendToppings = sendToppings(dishItems.toppings)
            }
            sendList.add(sendDishItem)
        }
        return sendList
    }

    private fun sendToppings(toppings: ArrayList<ToppingsItems>?): ArrayList<SendToppingsItems> {
        val topping = ArrayList<SendToppingsItems>()
        toppings?.forEach { toppingItem ->
            if (sendToppingList(toppingItem.toppingList).size != 0) {
                val sendItems = SendToppingsItems()
                sendItems.id = toppingItem.id
                sendItems.type = toppingItem.type
                sendItems.category = toppingItem.category
                sendItems.toppingList = sendToppingList(toppingItem.toppingList)
                topping.add(sendItems)
            }
        }
        return topping
    }

    private fun sendToppingList(toppingList: ArrayList<ToppingListItem>?): ArrayList<SendToppingListItem> {
        val sendToppingListItem = ArrayList<SendToppingListItem>()
        toppingList?.forEach { toppingListItems ->
            if (toppingListItems.isCheckItem) {
                val sendToppingList = SendToppingListItem()
                sendToppingList.id = toppingListItems.id
                sendToppingList.name = toppingListItems.name
                sendToppingList.price = toppingListItems.price
                sendToppingListItem.add(sendToppingList)
            }
        }
        return sendToppingListItem
    }


}