package com.kraven.ui.order.beverage.adapter

import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.kraven.R
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.extensions.convertTwoDigit
import com.kraven.ui.order.beverage.model.SpecialBeverageDetails
import kotlinx.android.synthetic.main.raw_special_order_details.view.*

class AdapterSpecialBeverageDetails : AdvanceRecycleViewAdapter<AdapterSpecialBeverageDetails.ViewHolder, SpecialBeverageDetails.Item>() {


    override fun createDataHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(makeView(parent, R.layout.raw_special_order_details))
    }

    override fun onBindDataHolder(holder: ViewHolder, position: Int, item: SpecialBeverageDetails.Item) {
        holder.onBindView(item)
    }


    inner class ViewHolder(view: View) : BaseHolder<SpecialBeverageDetails.Item>(view) {

        fun onBindView(item: SpecialBeverageDetails.Item) = with(itemView) {

            Glide.with(context!!)
                    .load(item.image)
                    /*.apply(RequestOptions().placeholder(R.drawable.ic_user))*/
                    .apply(RequestOptions.bitmapTransform(MultiTransformation(FitCenter(), RoundedCorners(15))))
                    .into(imageViewBeverage)

            textViewStatus.text = item.status
            textViewBrand.text = item.brand
            textViewName.text = item.beverage
            textViewPrice.text = item.price?.let { convertTwoDigit(it.toFloat()) }
            textViewQuantity.text =itemView.context.getString(R.string.quantity_types,item.quantity,item.quantityType,item.sizeOfBottle)
            editTextNote.setText(item.specialNote)


        }
    }
}