package com.kraven.ui.cart.adapter

import android.app.Activity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.kraven.R
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.extensions.convertTwoDigit
import com.kraven.extensions.convertTwoDigitFlot
import com.kraven.extensions.getText
import com.kraven.extensions.twoDigit
import com.kraven.ui.cart.CartSubTotalEvent
import com.kraven.ui.home.model.DishesItem
import com.kraven.utils.GlideApp

import kotlinx.android.synthetic.main.raw_beverage_cart.view.*

class BeverageCartAdapter(private var itemClickListener: ItemClickListener) :
        AdvanceRecycleViewAdapter<BeverageCartAdapter.ViewHolder, DishesItem>() {

    override fun createDataHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(makeView(parent, R.layout.raw_beverage_cart))
    }

    override fun onBindDataHolder(holder: ViewHolder, position: Int, item: DishesItem) {
        holder.onBindView(item)
    }


    inner class ViewHolder(view: View) : BaseHolder<DishesItem>(view) {
        fun onBindView(item: DishesItem) = with(itemView) {
            var toppingPrice = 0F
            GlideApp.with(context)
                    .load(item.image)
                    .transform(FitCenter(), RoundedCorners(30))
                    .into(imageViewBeverage)


            textViewFoodName.text = item.name
            textViewFoodType.text = item.menu
            item.toppings?.forEach { topping ->
                topping.toppingList?.forEach { toppingList ->
                    if (toppingList.isCheckItem) {
                        toppingPrice += toppingList.price!!
                    }
                }
            }
            item.vat  = item.vat
            item.calculateVatPrice = twoDigit(item.vatPrice?.toFloat()?.times(item.qty!!)!!)

            //val dishWithTopingPrice = if (toppingPrice != 0f) toppingPrice else specialOfferPrice(item.price!!, item.specialOffer!!).toFloat()
            val dishWithTopingPrice = convertTwoDigitFlot(if (toppingPrice != 0f) toppingPrice else item.price!!)
            item.dishPriceTopping = dishWithTopingPrice
            item.totalPrice = item.qty!! * dishWithTopingPrice
            textViewFoodPrice?.text = convertTwoDigit(item.dishPriceTopping!!.times(item.qty!!.toInt()))
            textVieFoodCount.text = item.qty.toString()



            if (item.toppings?.size != 0) {
                recyclerViewToopingList.visibility = View.VISIBLE
                recyclerViewToopingList.layoutManager = LinearLayoutManager(context)
                val toppingCartAdapter = ToppingCartAdapter()
                recyclerViewToopingList.adapter = toppingCartAdapter
                toppingCartAdapter.items = item.toppings
                recyclerViewToopingList.isClickable = false

            } else {
                recyclerViewToopingList.visibility = View.GONE
            }

            editTextAddNote.setOnEditorActionListener(object : TextView.OnEditorActionListener {
                override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        editTextAddNote.isCursorVisible = false
                        item.specialNote = getText(editTextAddNote)
                        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(itemView.windowToken, 0)
                        return true
                    }
                    return false
                }
            })
            imageViewAdd.setOnClickListener {
                item.qty = item.qty?.plus(1)
                textViewFoodPrice.text = convertTwoDigit(item.dishPriceTopping!!.times(item.qty!!.toInt()))
                textVieFoodCount.text = item.qty.toString()
                item.totalPrice = convertTwoDigitFlot(item.totalPrice!! + item.dishPriceTopping!!)
                item.vat = item.vat
                item.calculateVatPrice = twoDigit(item.vatPrice?.toFloat()?.times(item.qty!!)!!)

                itemClickListener.onAdd(adapterPosition, item, CartSubTotalEvent(item.totalPrice?.times(item.qty!!)!!,
                        textVieFoodCount.text.toString(),
                        item.id.toString(),
                        textViewFoodPrice?.text.toString().replace("$", ""),
                        textVieFoodCount.text.toString().toInt().toString()))

            }

            imageViewSubtract.setOnClickListener {
                if (textVieFoodCount.text.toString().toInt() > 1) {
                    item.qty = item.qty?.minus(1)
                    textViewFoodPrice.text = convertTwoDigit(item.dishPriceTopping!!.times(item.qty!!.toInt()))
                    textVieFoodCount.text = item.qty.toString()
                    item.totalPrice = convertTwoDigitFlot(item.totalPrice!! - item.dishPriceTopping!!)
                    item.vat = item.vat
                    item.calculateVatPrice = twoDigit(item.vatPrice?.toFloat()?.times(item.qty!!)!!)
                    itemClickListener.onSubtract(adapterPosition, item,
                            CartSubTotalEvent(item.dishPriceTopping?.times(item.qty!!)!!,
                                    textVieFoodCount.text.toString(), item.id.toString(),
                                    textViewFoodPrice?.text.toString().replace("$", ""), textVieFoodCount.text.toString().toInt().toString()))


                }

            }

            imageViewDelete.setOnClickListener {
                itemClickListener.onDelete(item, adapterPosition)
            }

            setOnClickListener {
                itemClickListener.onMenusClick(adapterPosition, item)
            }

            editTextAddNote.setText(item.specialNote)

        }

    }

    interface ItemClickListener {
        fun onAdd(position: Int, selectedCartList: DishesItem?, cartSubTotalEvent: CartSubTotalEvent)
        fun onSubtract(position: Int, selectedCartList: DishesItem?, cartSubTotalEvent: CartSubTotalEvent)
        fun onDelete(item: DishesItem?, position: Int)
        fun onMenusClick(position: Int, item: DishesItem?)
    }

}