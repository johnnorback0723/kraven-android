package com.kraven.ui.cart.adapter

import android.app.Activity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.kraven.R
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.extensions.convertTwoDigit
import com.kraven.extensions.convertTwoDigitFlot
import com.kraven.extensions.getText
import com.kraven.extensions.twoDigit
import com.kraven.ui.cart.CartSubTotalEvent
import com.kraven.ui.home.model.DishesItem

import kotlinx.android.synthetic.main.cart_raw.view.*


class CartAdapter(private var itemClickListener: ItemClickListener) :
        AdvanceRecycleViewAdapter<CartAdapter.AdapterMenuViewHolder, DishesItem>() {

    var subTotal = 0F

    override fun createDataHolder(parent: ViewGroup?, viewType: Int): AdapterMenuViewHolder {
        return AdapterMenuViewHolder(makeView(parent, R.layout.cart_raw))
    }

    override fun onBindDataHolder(holder: AdapterMenuViewHolder, position: Int, item: DishesItem?) {
        holder.onBindView(item!!)
    }

    inner class AdapterMenuViewHolder(view: View) : BaseHolder<DishesItem>(view) {

        fun onBindView(item: DishesItem) = with(itemView) {

            var toppingPrice = 0F
            imageViewVegNonVeg?.setBackgroundResource(if (item.food == "Veg") R.drawable.ic_veg else R.drawable.ic_nonveg)
            textViewFoodName.text = item.name
            textViewFoodType.text = item.menu
            textVieFoodCount.text = item.qty.toString()

            item.toppings?.forEach { topping ->
                topping.toppingList?.forEach { toppingList ->
                    if (toppingList.isCheckItem) {
                        toppingPrice += toppingList.price!!
                    }
                }
            }

            textViewFoodPrice?.text = convertTwoDigit(item.dishPriceTopping!!.times(item.qty!!.toInt()))
            editTextAddNote.setText(item.specialNote)
            item.vat  = item.vat
            item.calculateVatPrice = twoDigit(item.vatPrice?.toFloat()?.times(item.qty!!)!!)

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

            editTextAddNote.setText(item.specialNote)

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
                textViewFoodPrice?.text = convertTwoDigit(item.dishPriceTopping!!.times(item.qty!!.toInt()))
                textVieFoodCount.text = item.qty.toString()
                item.totalPrice = convertTwoDigitFlot(item.totalPrice!! + item.dishPriceTopping!!)
                item.vat  = item.vat
                //item.calculateVatPrice = twoDigit(item.vatPrice?.toFloat()?.times(item.qty!!)!!)

                item.calculateVatPrice = item.vatPrice?.toFloat()?.times(item.qty!!).toString()!!

                itemClickListener.onAdd(adapterPosition, item, CartSubTotalEvent(item.dishPriceTopping?.times(item.qty!!)!!,
                        textVieFoodCount.text.toString(),
                        item.id.toString(),
                        textViewFoodPrice?.text.toString().replace("$", ""),
                        textVieFoodCount.text.toString().toInt().toString()))

            }

            imageViewSubtract.setOnClickListener {
                if (textVieFoodCount.text.toString().toInt() > 1) {
                    item.qty = item.qty?.minus(1)
                    textViewFoodPrice?.text = convertTwoDigit(item.dishPriceTopping!!.times(item.qty!!.toInt()))
                    textVieFoodCount.text = item.qty.toString()
                    item.totalPrice = convertTwoDigitFlot(item.totalPrice!! + item.dishPriceTopping!!)
                    item.vat  = item.vat
                    //item.calculateVatPrice = twoDigit(item.vatPrice?.toFloat()?.times(item.qty!!)!!)
                    item.calculateVatPrice = item.vatPrice?.toFloat()?.times(item.qty!!).toString()
                    itemClickListener.onSubtract(adapterPosition, item,
                            CartSubTotalEvent(item.dishPriceTopping?.times(item.qty!!)!!,
                                    textVieFoodCount.text.toString(), item.id.toString(),
                                    textViewFoodPrice?.text.toString().replace("$", ""), textVieFoodCount.text.toString().toInt().toString()))


                } else {
                    itemClickListener.onDelete(item, adapterPosition)
                }

            }

            imageViewDelete.setOnClickListener {
                itemClickListener.onDelete(item, adapterPosition)
            }

            setOnClickListener {
                itemClickListener.onMenusClick(adapterPosition, item)
            }
            viewClickable.setOnClickListener {
                itemClickListener.onMenusClick(adapterPosition, item)
            }

        }
    }

    interface ItemClickListener {
        fun onAdd(position: Int, selectedCartList: DishesItem?, cartSubTotalEvent: CartSubTotalEvent)
        fun onSubtract(position: Int, selectedCartList: DishesItem?, cartSubTotalEvent: CartSubTotalEvent)
        fun onClickTopping(position: Int)
        fun onItemClick(position: Int)
        fun onDelete(item: DishesItem?, position: Int)
        fun onMenusClick(position: Int, item: DishesItem?)
        fun onSpecialNote(specialNote: String, id: String)
    }

}


