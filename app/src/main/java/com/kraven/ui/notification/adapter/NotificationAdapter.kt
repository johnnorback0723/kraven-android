package com.kraven.ui.notification.adapter

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils

import android.view.View
import android.view.ViewGroup

import androidx.core.content.res.ResourcesCompat
import com.kraven.R
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.ui.notification.model.Notifications
import com.kraven.utils.CustomTypefaceSpan
import com.kraven.utils.Formatter
import com.kraven.utils.Formatter.DD_MMM_YYYY_hh_mm_aa
import kotlinx.android.synthetic.main.notificaiton_raw.view.*


class NotificationAdapter(var itemClickListener: ItemClickListener) : AdvanceRecycleViewAdapter<NotificationAdapter.ViewHolder, Notifications>() {

    override fun createDataHolder(parent: ViewGroup?, viewType: Int): ViewHolder = ViewHolder(makeView(parent, R.layout.notificaiton_raw))

    override fun onBindDataHolder(holder: ViewHolder?, position: Int, item: Notifications?) {

        if (item?.orderId != "0") {
            val stringBuilderId = SpannableStringBuilder(holder!!.itemView.context.getString(R.string.order_id_small, item!!.orderId))
            stringBuilderId.setSpan(
                CustomTypefaceSpan(ResourcesCompat.getFont(holder.itemView.context!!, R.font.work_sans_bold)!!),
                    0, stringBuilderId.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            val stringBuilderStatus = SpannableStringBuilder(item.message)
            stringBuilderStatus.setSpan(CustomTypefaceSpan(ResourcesCompat.getFont(holder.itemView.context!!, R.font.work_sans_regular)!!),
                    0, stringBuilderStatus.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            holder.textViewOrderId.text = TextUtils.concat(stringBuilderId, " ", stringBuilderStatus)


            holder.itemView.setOnClickListener {
                itemClickListener.onItemClick(item)
            }
        } else {
            holder?.textViewOrderId?.text = item.message
        }
        holder?.textViewDateTime?.text = Formatter.format(item.insertdate!!,
                Formatter.SERVER_TIME_FORMATE, DD_MMM_YYYY_hh_mm_aa, false)
    }

    class ViewHolder(itemView: View) : BaseHolder<Notifications>(itemView) {
        var textViewOrderId = itemView.textViewOrderId!!
        var textViewDateTime = itemView.textViewDateTime!!
    }

    interface ItemClickListener {
        fun onItemClick(item: Notifications)
    }
}