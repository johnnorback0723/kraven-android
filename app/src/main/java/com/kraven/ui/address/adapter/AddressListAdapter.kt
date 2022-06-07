package com.kraven.ui.address.adapter

import android.view.View
import android.view.ViewGroup
import com.kraven.R
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.extensions.viewGone
import com.kraven.ui.address.model.Address
import kotlinx.android.synthetic.main.address_list_raw.view.*

class AddressListAdapter(var itemClickListener: AddressAdapterInterface, var selectAddress: Int) : AdvanceRecycleViewAdapter<AddressListAdapter.ViewHolder, Address>() {

    override fun createDataHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(makeView(parent, R.layout.address_list_raw))
    }

    override fun onBindDataHolder(holder: ViewHolder?, position: Int, item: Address?) {
        holder?.textViewAddressType?.text = item?.addressType
        holder?.textViewAddress?.text = item?.address
        holder?.textViewEditAddress?.setOnClickListener {
            itemClickListener.onEdit(item)
        }
        holder?.textViewDeleteAddress?.setOnClickListener {
            itemClickListener.onDelete(item)
        }

        if(selectAddress==1){
            holder?.textViewEditAddress?.viewGone()
            holder?.textViewDeleteAddress?.viewGone()
            holder?.itemView?.setOnClickListener{
                itemClickListener.goBack(item!!)
            }
        }
    }


    class ViewHolder(itemView: View) : BaseHolder<Address>(itemView) {
        var textViewAddressType = itemView.textViewAddressType!!
        var textViewAddress = itemView.textViewAddress!!
        var textViewEditAddress = itemView.textViewEditAddress!!
        var textViewDeleteAddress = itemView.textViewDeleteAddress!!


    }


    interface AddressAdapterInterface {
        fun onEdit(item: Address?)
        fun onDelete(item: Address?)
        fun goBack(item: Address)
    }
}