package com.kraven.ui.my.order.adapter

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.kraven.R
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.extensions.getText
import com.kraven.ui.home.model.ToppingListItem
import com.kraven.utils.TextDecorator
import kotlinx.android.synthetic.main.topping_inner_list.view.*

class ToppingInnerListAdapter : AdvanceRecycleViewAdapter<ToppingInnerListAdapter.ViewHolder, ToppingListItem>() {

    override fun createDataHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(makeView(parent, R.layout.topping_inner_list))
    }

    override fun onBindDataHolder(holder: ViewHolder, position: Int, item: ToppingListItem) {
        holder.onBindView(item)
    }


    inner class ViewHolder(view: View) : BaseHolder<ToppingListItem>(view) {
        fun onBindView(item: ToppingListItem) = with(itemView) {
            if (item.status != null && item.status?.isNotEmpty()!!) {
                textViewToppingsInnerName.text = item.name!!.plus(" (" + item.status + ")")
                setRedColor(textViewToppingsInnerName)
            } else {
                textViewToppingsInnerName.text = item.name!!
            }

        }
    }

    private fun setRedColor(textViewToppingsName: AppCompatTextView) {
        getText(textViewToppingsName)?.let {
            TextDecorator.decorate(textViewToppingsName, it)
                    .apply {
                        setTextColor(R.color.red, " (" + "Unavailable" + ")")
                    }
                    .build()
        }
    }

}