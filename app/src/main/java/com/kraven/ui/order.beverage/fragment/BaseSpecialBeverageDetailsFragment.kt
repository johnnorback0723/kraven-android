package com.kraven.ui.order.beverage.fragment

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.kraven.R
import com.kraven.extensions.*
import com.kraven.ui.address.model.Address
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.cart.adapter.TipAdapter
import com.kraven.ui.cart.model.TollFee
import com.kraven.ui.cart.viewModel.CartViewModel
import com.kraven.ui.home.model.Promocode
import com.kraven.ui.order.beverage.SpecialBeverageViewModel
import com.kraven.ui.order.beverage.adapter.AdapterSpecialBeverageDetails
import com.kraven.ui.order.beverage.model.SpecialBeverageDetails
import com.kraven.utils.ConstantApp
import com.kraven.utils.Formatter
import kotlinx.android.synthetic.main.common_cart_layout.*

abstract class BaseSpecialBeverageDetailsFragment : BaseFragment() {

    lateinit var specialBeverageViewModel: SpecialBeverageViewModel
    lateinit var cartViewModel: CartViewModel
    var adapterSpecialBeverageDetails: AdapterSpecialBeverageDetails? = null
    var details: SpecialBeverageDetails? = null
    var address: Address? = null
    var textViewStatus: AppCompatTextView? = null
    var walletAmount = 0.00F
    var selectTips: String? = null
    var isEnter: Boolean? = null
    var promoCode: Promocode? = null
    var tipAdapter: TipAdapter? = null

    var tollFee: TollFee? = null

    val orderId: String? by lazyFast {
        val args = arguments ?: throw IllegalArgumentException("Missing Arguments")
        args.getString(ConstantApp.PassValue.ORDER_ID)
    }

    private val pushTag: String? by lazyFast {
        val args = arguments ?: throw IllegalArgumentException("Missing Arguments")
        args.getString("tag")
    }


    fun tipList(): List<String> {
        val menuLists = mutableListOf<String>()
        menuLists.add("5")
        menuLists.add("10")
        menuLists.add("15")
        menuLists.add("20")
        menuLists.add("30")
        return menuLists
    }

    fun setCalculatedData() {
        textViewSubTotalValue.text = details?.subTotal?.toFloat()?.let { convertTwoDigit(it) }
        textViewVatValue.text = details?.totalVat?.toFloat()?.let { convertTwoDigit(it) }
        textViewDeliveryChargeValue.text = details?.deliveryCharge?.toFloat()?.let { convertTwoDigit(it) }

        textViewDiscountValue.text = convertTwoDigit(calculateDiscount())
        textViewWalletValue.text = convertTwoDigit(calculateWallet())
        textViewTipValue.text = convertTwoDigit(Formatter.round(calculateTip().toDouble()).toFloat())
        textViewTotal.text = convertTwoDigit(calculateTotalWithWallet())

        if (tollFee?.tollfee != null) {
            groupTollFee.viewShow()
            textViewTollFeeValue.text = convertTwoDigit(tollFee?.tollfee?.toFloat() ?: 0.0f)
        }else{
            groupTollFee.viewGone()
        }

    }


    private fun calculateDiscount(): Float {
        if (promoCode != null) {
            return if (promoCode?.type == getString(R.string.Percentage)) {
                val totalAmount = details?.total?.toFloat()!!
                totalAmount * promoCode?.amount?.toFloat()!! / 100
            } else {
                val totalAmount = details?.total?.toFloat()!!
                val promocodeAmount = promoCode?.amount?.toFloat()
                if (totalAmount > promocodeAmount!!) {
                    promoCode?.amount?.toFloat()!!
                } else {
                    totalAmount
                }
            }
        } else if (details!!.discount != null && details!!.discount!!.isNotEmpty()) {
            return details!!.discount!!.toFloat()
        }
        return 0.00F
    }

    private fun calculateWallet(): Float {
        return if (walletAmount > Formatter.round(calculateTotal().toDouble()).toFloat()) {
            Formatter.round(calculateTotal().toDouble()).toFloat()
        } else {
            walletAmount
        }
    }

    private fun calculateTip(): Float {
        if (selectTips != null) {

            imageViewCloseTip.visibility = View.VISIBLE
            buttonTip.isEnabled = false
            llTip.isEnabled = false

            return if (isEnter!!) {
                textViewTip.text = getString(R.string.tip, "0.00%")
                buttonTip.text = convertTwoDigit(selectTips?.toFloat()!!)

                selectTips!!.toFloat()
            } else {
                buttonTip.text = selectTips.plus("%")
                textViewTip.text = getString(R.string.tip, selectTips.plus("%"))
                ((details?.subTotal?.toFloat()!! * selectTips!!.toInt()) / 100)
            }
        } else if (details!!.tip != null && details!!.tip!!.isNotEmpty()) {
            return details!!.tip!!.toFloat()
        }
        return 0.00F
    }

    private fun calculateTotalWithWallet(): Float {

        if (walletAmount > Formatter.round(calculateTotal().toDouble()).toFloat()) {
            return 0.0F
        } else {
            return if (selectTips != null && getTextReplace(textViewTotal) == 0.0F) {

                if (isEnter!!) {
                    textViewTip.text = getString(R.string.tip, "0.00%")
                    buttonTip.text = convertTwoDigit(selectTips?.toFloat()!!)

                    selectTips!!.toFloat()
                } else {
                    buttonTip.text = selectTips.plus("%")
                    textViewTip.text = getString(R.string.tip, selectTips.plus("%"))
                    ((details?.subTotal?.toFloat()!! * selectTips!!.toInt()) / 100)
                }
            } else {
                return Formatter.round(calculateTotal().toDouble()).toFloat() - walletAmount
            }
        }
    }

    fun calculateTotal(): Float {

        val subTotal = details?.subTotal?.toFloat()!!
        val vat = details?.totalVat?.toFloat()!!
        val tip = Formatter.round(calculateTip().toDouble()).toFloat()
        val discount = calculateDiscount()

        return if (tollFee?.tollfee != null) {
            subTotal + vat + getTextReplace(textViewDeliveryChargeValue) + tip + tollFee?.tollfee?.toFloat()!! - discount
        } else {
            subTotal + vat + getTextReplace(textViewDeliveryChargeValue) + tip + -discount
        }

    }

}