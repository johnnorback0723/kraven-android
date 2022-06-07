package com.kraven.ui.cart.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kraven.R
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.ui.address.model.Address
import kotlinx.android.synthetic.main.address_raw.view.*

class SelectAddressAdapter(private var selectAddressInterface: AddressAdapterInterface) : AdvanceRecycleViewAdapter<SelectAddressAdapter.AdapterSelectAddressHolder, Address>() {

    private var lastSelectedPosition = -1

    override fun createDataHolder(parent: ViewGroup?, viewType: Int): AdapterSelectAddressHolder {
        return AdapterSelectAddressHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.address_raw, parent, false))
    }

    override fun onBindDataHolder(holder: AdapterSelectAddressHolder?, position: Int, item: Address?) {
        holder!!.textViewAddressType.text = item!!.addressType
        holder.textViewAddress.text = item.address
        holder.itemView.setOnClickListener {
            selectAddressInterface.onItemClick(item.address, item.addressType,item)
        }

        holder.imageViewSelectedAddress.visibility = if (lastSelectedPosition == position) View.VISIBLE else View.GONE
    }

    class AdapterSelectAddressHolder(itemView: View) : BaseHolder<Address>(itemView) {
        var textViewAddressType = itemView.textViewAddressType!!
        var imageViewSelectedAddress = itemView.imageViewSelectedAddress!!
        var textViewAddress = itemView.textViewAddress!!

    }
    interface AddressAdapterInterface {
        fun onItemClick(displayAddress: String,addressType:String,item: Address?)
    }

}

