package com.kraven.ui.portable.bar.rental.adapter

import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.kraven.R
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.ui.portable.bar.rental.model.PortableBar
import kotlinx.android.synthetic.main.portable_bar_raw.view.*

class PortableBarListAdapter(private var selectAddressInterface: PortableBarListAdapter.AdapterPortableInterface) : AdvanceRecycleViewAdapter<PortableBarListAdapter.AdapterPortableBarListViewHolder, PortableBar>() {

    private var lastSelectedPosition = 0

    override fun createDataHolder(parent: ViewGroup?, viewType: Int): AdapterPortableBarListViewHolder {
        return AdapterPortableBarListViewHolder(makeView(parent, R.layout.portable_bar_raw))
    }

    override fun onBindDataHolder(holder: AdapterPortableBarListViewHolder?, position: Int, item: PortableBar?) {

        Glide.with(holder?.itemView?.context!!)
                .load(item?.barImage)
               /* .apply(RequestOptions().placeholder(R.drawable.ic_user))*/
                .apply(RequestOptions.bitmapTransform(MultiTransformation(CenterCrop(), RoundedCorners(15))))
                .into(holder.imageViewBar)

        holder.itemView.setOnClickListener {
            //selectAddressInterface.onItemClick(item.address!!, item.addressType!!)
            lastSelectedPosition = position
            notifyDataSetChanged()
        }
        holder.imageViewSelection.visibility = if (lastSelectedPosition == position) View.VISIBLE else View.GONE
    }


    class AdapterPortableBarListViewHolder(view: View) : BaseHolder<PortableBar>(view) {
        var imageViewBar = view.imageViewBar!!
        var imageViewSelection = view.imageViewSelection!!
    }

    interface AdapterPortableInterface {
        fun onItemClick(displayAddress: String, addressType: String)
    }
}