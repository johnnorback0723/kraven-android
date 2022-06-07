package com.kraven.ui.order.beverage.adapter


import android.graphics.Paint
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.kraven.R
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.extensions.convertTwoDigit
import com.kraven.ui.order.beverage.model.Beverage
import kotlinx.android.synthetic.main.beverage_raw.view.*
import java.util.*


class BeverageAdapter(var itemClickListener: ItemClickListener) :
        AdvanceRecycleViewAdapter<BeverageAdapter.AdapterBeverageViewHolder, Beverage>() {


    override fun createDataHolder(parent: ViewGroup?, viewType: Int): AdapterBeverageViewHolder {
        return AdapterBeverageViewHolder(makeView(parent, R.layout.beverage_raw))
    }

    override fun onBindDataHolder(holder: AdapterBeverageViewHolder, position: Int, item: Beverage) {
        holder.onBindView(item)
    }

    inner class AdapterBeverageViewHolder(view: View) : BaseHolder<Beverage>(view) {
        fun onBindView(item: Beverage) = with(itemView) {
            Glide.with(context!!)
                    .load(item.image)
                    /*.apply(RequestOptions().placeholder(R.drawable.ic_user))*/
                    .apply(RequestOptions.bitmapTransform(MultiTransformation(FitCenter(), RoundedCorners(15))))
                    .into(imageViewBeverage)


            if (item.toppings?.toppingList?.size != 0) {
                item.toppings?.minQty = 1
            }
            textViewBeverageName.text = item.name
            textViewBeverageItem.text = item.description
            textViewItemMl.text = getFillingQuantity(item.milliliter!!.toFloat())
            textViewItemNotAvailable.visibility = if (item.status == "Available") View.GONE else View.VISIBLE
            imageViewSpecialOffer?.visibility = if (item.specialOffer != 0F) View.VISIBLE else View.GONE

            //textViewBeveragePrice?.text = convertTwoDigit(specialOfferPrice(item.price!!, item.specialOffer!!).toFloat())
            textViewBeveragePrice?.text = item.price?.let { convertTwoDigit(it) }
            textViewOffPrice.visibility = if (item.specialOffer != 0F) View.VISIBLE else View.GONE
            if (textViewOffPrice.visibility == View.VISIBLE) {
                textViewOffPrice.paintFlags = textViewOffPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                textViewOffPrice.text = convertTwoDigit(item.originalPrice!!.toFloat())
            }

            if (item.status != "Available") {
                textViewBeverageName.paintFlags = textViewBeverageName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }

            if (item.status == "Available") {
                setOnClickListener {
                    itemClickListener.onItemClick(item)
                }
            }

            imageViewBeverage.setOnClickListener {
                item.image?.let { it1 -> itemClickListener.onClickImage(it1, imageViewBeverage) }
            }
        }

    }

    interface ItemClickListener {
        fun onItemClick(beverage: Beverage)
        fun onClickImage(image: String, imageView: AppCompatImageView)
    }

    fun getFillingQuantity(milliliter: Float): String {
        return if (milliliter >= 1000) {
            "(" + String.format(Locale.US, "%.2f", (milliliter / 1000)) + " lt)"
        } else {
            "(" + String.format(Locale.US, "%.2f", (milliliter)) + " ml)"

        }

    }


}