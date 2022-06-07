package com.kraven.ui.my.order.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.kraven.R
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.extensions.convertTwoDigit
import com.kraven.extensions.viewGone
import com.kraven.extensions.viewShow
import com.kraven.ui.home.model.*
import com.kraven.utils.TextDecorator
import kotlinx.android.synthetic.main.order_list_raw.view.*
import java.util.*

class OrderDetailsAdapter(private var itemClickListener: ItemClickListenerChild) :
        AdvanceRecycleViewAdapter<OrderDetailsAdapter.OrderListChildHolder, DishesItem>() {
    override fun createDataHolder(parent: ViewGroup?, viewType: Int): OrderListChildHolder {
        return OrderListChildHolder(makeView(parent, R.layout.order_list_raw))
    }

    override fun onBindDataHolder(holder: OrderListChildHolder?, position: Int, item: DishesItem) {
        holder?.onBindView(item)
    }

    inner class OrderListChildHolder(itemView: View) : BaseHolder<DishesItem>(itemView) {
        fun onBindView(item: DishesItem) = with(itemView) {
            imageViewVegNonVeg?.setBackgroundResource(if (item.food == "Veg") R.drawable.ic_veg else R.drawable.ic_nonveg)

            textViewQty.text = itemView.context.getString(R.string.qty, item.qty.toString())
            textViewFoodType.text = item.menu


            /*if (item.status == "Unavailable") {
                //textViewFoodName.paintFlags = textViewFoodName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                textViewFoodName.text = item.dish + " (" + item.status + ")"
            } else {
                textViewFoodName.text = item.dish
            }*/

            if (item.status == "Unavailable") {
                //textViewFoodName.text = item.item + " (" + item.status + ")"
                TextDecorator.decorate(textViewFoodName, item.dish + " (" + item.status + ")")
                        .apply {
                            setTextColor(R.color.red, " (" + item.status + ")")
                        }
                        .build()
            } else {
                textViewFoodName.text = item.dish
            }
            if (item.specialNote?.isNotEmpty()!!) {
                textViewSpecialNoteLabel.viewShow()
                textViewSpecialNote.viewShow()
                textViewSpecialNote.text = item.specialNote
            } else {
                textViewSpecialNoteLabel.viewGone()
                textViewSpecialNote.viewGone()
            }

            if (item.toppings != null && item.toppings!!.isNotEmpty()) {
                val foodPriceWithTopping = item.price!! + getToppingPrice(item.toppings!!)
                textViewFoodPrice.text = convertTwoDigit(foodPriceWithTopping)
                recyclerViewToopingList.viewShow()
                recyclerViewToopingList.layoutManager = LinearLayoutManager(context)
                val toppingCartAdapter = ToppingDetailsAdapter()
                recyclerViewToopingList.adapter = toppingCartAdapter
                toppingCartAdapter.items = item.toppings!!
            } else {
                recyclerViewToopingList.viewGone()
                textViewFoodPrice.text = String.format(Locale.US,"$%.2f", item.price)
            }
        }

        private fun getToppingPrice(sendToppings: ArrayList<ToppingsItems>): Float {
            var toppingPrice = 0F
            sendToppings.forEach { sentTopping ->
                sentTopping.toppingList?.forEach { sendToppingItems ->
                    toppingPrice += sendToppingItems.price!!

                }
            }
            return toppingPrice
        }

    }

    interface ItemClickListenerChild {
        fun onItemChildClick()
    }
}