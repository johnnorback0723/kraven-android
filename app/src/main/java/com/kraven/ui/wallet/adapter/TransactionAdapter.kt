package com.kraven.ui.wallet.adapter

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.kraven.R
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.extensions.convertTwoDigit
import com.kraven.ui.wallet.model.Transaction
import com.kraven.utils.Formatter
import kotlinx.android.synthetic.main.transaction_raw.view.*
import java.util.*

class TransactionAdapter : AdvanceRecycleViewAdapter<TransactionAdapter.ViewHolder, Transaction>() {
    override fun createDataHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(makeView(parent, R.layout.transaction_raw))
    }

    override fun onBindDataHolder(holder: ViewHolder?, position: Int, item: Transaction?) {

        holder!!.textViewBookingType.text = item!!.description
        val amount = item.amount
        holder.textViewAmount.text = convertTwoDigit(item.amount)
        holder.textViewTransactionStatusValue.text = item.status
        when (item.status) {
            "Debited", "Credited" -> {
                holder.textViewTransactionStatusValue.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.green))
            }
            "Payment Processing" -> {
                holder.textViewTransactionStatusValue.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.colorAccent))
            }
            else -> {
                holder.textViewTransactionStatusValue.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
            }
        }

        holder.textViewDateTime.text = Formatter.format(item.insertdate, Formatter.SERVER_TIME_FORMATE,
                Formatter.DD_MMM_YYYY_hh_mm_aa, false)


        if (item.transactionId.isNullOrEmpty()) {
            holder.textViewTransactionId.visibility = View.GONE
        } else {
            holder.textViewTransactionId.visibility = View.VISIBLE
            holder.textViewTransactionId.text = String.format(Locale.US, "%s  %s", holder.itemView.context.getString(R.string.transaction_id), item.transactionId)
        }

    }


    class ViewHolder(itemView: View) : BaseHolder<Transaction>(itemView) {
        var textViewBookingType = itemView.textViewBookingType!!
        var textViewAmount = itemView.textViewAmount!!
        var textViewDateTime = itemView.textViewDateTime!!
        var textViewTransactionId = itemView.textViewTransactionId!!
        var textViewTransactionStatusValue = itemView.textViewTransactionStatusValue!!
    }
}