package com.kraven.ui.my.order.adapter


import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.kraven.R
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.extensions.convertTwoDigit
import com.kraven.extensions.viewGone
import com.kraven.extensions.viewShow
import com.kraven.ui.home.model.ToppingsItems
import com.kraven.ui.my.order.model.BeverageHistoryDetails
import com.kraven.utils.GlideApp
import com.kraven.utils.TextDecorator
import kotlinx.android.synthetic.main.raw_beverage_details.view.*
import java.util.*


class BeverageDetailsAdapter :
        AdvanceRecycleViewAdapter<BeverageDetailsAdapter.ViewHolder, BeverageHistoryDetails.PassOrderItem>() {

    override fun createDataHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(makeView(parent, R.layout.raw_beverage_details))
    }

    override fun onBindDataHolder(holder: ViewHolder, position: Int, item: BeverageHistoryDetails.PassOrderItem) {
        holder.onBindView(item)
    }


    inner class ViewHolder(view: View) : BaseHolder<BeverageHistoryDetails.PassOrderItem>(view) {
        fun onBindView(item: BeverageHistoryDetails.PassOrderItem) = with(itemView) {

            GlideApp.with(context)
                    .load(item.image)
                    .apply(RequestOptions.bitmapTransform(MultiTransformation(FitCenter(), RoundedCorners(15))))
                    .into(imageViewBeverage)

            if (item.status == "Unavailable") {
                //textViewFoodName.text = item.item + " (" + item.status + ")"
                TextDecorator.decorate(textViewFoodName, item.item + " (" + item.status + ")")
                        .apply {
                            setTextColor(R.color.red, " (" + item.status + ")")
                        }
                        .build()
            } else {
                textViewFoodName.text = item.item
            }

            if (item.toppings != null && item.toppings!!.isNotEmpty()) {
                val foodPriceWithTopping = getToppingPrice(item.toppings!!)
                textViewFoodPrice.text = convertTwoDigit(foodPriceWithTopping)
                recyclerViewToopingList.viewShow()
                recyclerViewToopingList.layoutManager = LinearLayoutManager(context)
                val toppingCartAdapter = ToppingDetailsAdapter()
                recyclerViewToopingList.adapter = toppingCartAdapter
                toppingCartAdapter.items = item.toppings!!
            } else {
                recyclerViewToopingList.viewGone()
                textViewFoodPrice.text = String.format(Locale.US, "$%.2f", item.price)
            }
            textViewItem.text = item.menu

            textViewQty.text = context.getString(R.string.qty, item.qty.toString())
            textViewItemMl.text = getFillingQuantity(item.milliliter!!.toFloat())


            if (item.specialNote!!.isNotEmpty()) {
                textViewAddSpecialNote.viewShow()
                editTextAddNote.viewShow()
                editTextAddNote.setText(item.specialNote)
            } else {
                textViewAddSpecialNote.viewGone()
                editTextAddNote.viewGone()
            }
        }
    }

    private fun getToppingPrice(sendToppings: ArrayList<ToppingsItems>): Float {
        var toppingPrice = 0F
        sendToppings.forEach { sentTopping ->
            sentTopping.toppingList?.forEach { sendToppingItems ->
                sendToppingItems.isCheckItem = true
                toppingPrice += sendToppingItems.price!!

            }
        }
        return toppingPrice
    }


    fun getFillingQuantity(milliliter: Float): String {

        if (milliliter >= 1000) {
            return "(" + String.format(Locale.US, "%.2f", (milliliter / 1000)) + " lt)"
        } else {
            return "(" + String.format(Locale.US, "%.2f", (milliliter)) + " ml)"

        }

    }
}