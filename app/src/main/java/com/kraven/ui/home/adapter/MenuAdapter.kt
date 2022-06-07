package com.kraven.ui.home.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kraven.R
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.extensions.convertTwoDigit
import com.kraven.extensions.viewGone
import com.kraven.extensions.viewShow
import com.kraven.ui.home.model.MenuModel
import com.kraven.ui.home.model.RestaurantMenu
import kotlinx.android.synthetic.main.menu_raw.view.*


class MenuAdapter(private var itemClickListener: ItemClickListener) :
        AdvanceRecycleViewAdapter<MenuAdapter.AdapterMenuViewHolder, RestaurantMenu>() {

    interface ItemClickListener {
        fun onItemClick(restaurantMenu: RestaurantMenu)
    }

    override fun createDataHolder(parent: ViewGroup?, viewType: Int): AdapterMenuViewHolder {
        return AdapterMenuViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.menu_raw, parent, false))
    }

    override fun onBindDataHolder(holder: AdapterMenuViewHolder, position: Int, item: RestaurantMenu) {
        holder.bindView(item)
    }

    inner class AdapterMenuViewHolder(view: View) : BaseHolder<MenuModel>(view) {
        fun bindView(item: RestaurantMenu) = with(itemView) {

            item.apply {
                imageViewVegNonVeg?.setBackgroundResource(if (food == "Veg") R.drawable.ic_veg else R.drawable.ic_nonveg)
                textViewFoodName.text = name
                if(status == "Unavailable"){
                    textViewItemNotAvailable.viewShow()
                    textViewFoodName.paintFlags = textViewFoodName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }else{
                    textViewItemNotAvailable.viewGone()
                    textViewFoodName.paintFlags = 0
                }
                if (description?.isNotEmpty()!!) {
                    textViewDescription.viewShow()
                    textViewDescription.text = description
                }
                //textViewFoodPrice?.text = convertTwoDigit(if (specialOffer?.toFloat() != 0F) specialOfferPrice(price?.toFloat()!!, specialOffer?.toFloat()!!).toFloat() else price?.toFloat()!!)
                //textViewFoodPrice?.text = convertTwoDigit(if (specialOffer?.toFloat() == 0F) originalPrice?.toFloat()!! else  price?.toFloat()!!)
                textViewFoodPrice?.text = convertTwoDigit(price?.toFloat()!!)
                textViewOffPrice.visibility = if (specialOffer?.toFloat() != 0F) View.VISIBLE else View.GONE
                imageViewSpecialOffer?.visibility = if (item.specialOffer?.toFloat() != 0F) View.VISIBLE else View.GONE
                if (textViewOffPrice.visibility == View.VISIBLE) {
                    textViewOffPrice.paintFlags = textViewOffPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    textViewOffPrice.text = convertTwoDigit(originalPrice?.toFloat()!!)
                }else{
                    textViewOffPrice.paintFlags = 0
                }

                setOnClickListener {
                    itemClickListener.onItemClick(item)
                }
//
//                setOnClickListener {
//                    itemClickListener.onItemClick(item)
//                }
            }

            /*imageViewSpecialOffer?.visibility = if (item.specialOffer?.toFloat() != 0F) View.VISIBLE else View.GONE
            textViewFoodName.text = item.name
            if (item.description?.isNotEmpty()!!) {
                textViewDescription.viewShow()
                textViewDescription.text = item.description
            }
            textViewItemNotAvailable.visibility = if (item.status == "Available") View.GONE else View.VISIBLE
            if (textViewItemNotAvailable.visibility == View.VISIBLE) {
                textViewFoodName.paintFlags = textViewFoodName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
            textViewFoodPrice?.text = convertTwoDigit(if (item.specialOffer?.toFloat() != 0F) specialOfferPrice(item.price?.toFloat()!!, item.specialOffer?.toFloat()!!).toFloat() else item.price?.toFloat()!!)

            textViewOffPrice.visibility = if (item.specialOffer?.toFloat() != 0F) View.VISIBLE else View.GONE
            if (textViewOffPrice.visibility == View.VISIBLE) {
                textViewOffPrice.paintFlags = textViewOffPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                textViewOffPrice.text = convertTwoDigit(item.price?.toFloat()!!)
            }
            //if (item.status == "Available") {
                setOnClickListener {
                    itemClickListener.onItemClick(item)
                }
            //}*/
        }
    }
}


