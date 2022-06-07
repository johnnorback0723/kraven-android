package com.kraven.ui.home.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.kraven.R
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.ui.home.model.Service
import kotlinx.android.synthetic.main.service_raw.view.*


class ServiceAdapter(private var itemClickListener: ItemClickListener) : AdvanceRecycleViewAdapter<ServiceAdapter.AdapterServiceViewHolder, Service.Services>() {


    override fun createDataHolder(parent: ViewGroup?, viewType: Int): AdapterServiceViewHolder {
        return AdapterServiceViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.service_raw, parent, false))

    }

    override fun onBindDataHolder(holder: AdapterServiceViewHolder?, position: Int, item: Service.Services?) {
        holder?.textViewFoodName?.text = item?.name
        Glide.with(holder?.itemView?.context!!)
                .load(item?.src)
               /* .apply(RequestOptions().placeholder(R.drawable.ic_user))*/
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(holder.imageViewFoodImage)


        holder.itemView.setOnClickListener {
            itemClickListener.OnItemClick(position)
        }

    }

    class AdapterServiceViewHolder(view: View) :
            BaseHolder<Service>(view) {
        val imageViewFoodImage = view.imageViewFoodImage!!
        val textViewFoodName = view.textViewFoodName!!
    }

    interface ItemClickListener {
        fun OnItemClick(position:Int)
    }



}