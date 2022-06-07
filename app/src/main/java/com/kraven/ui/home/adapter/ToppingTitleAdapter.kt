package com.kraven.ui.home.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kraven.R
import com.kraven.core.AppExecutors
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.extensions.viewGone
import com.kraven.extensions.viewShow
import com.kraven.ui.home.model.ToppingListItem
import com.kraven.ui.home.model.ToppingsItems
import com.kraven.utils.ConstantApp
import kotlinx.android.synthetic.main.toping_title_raw.view.*


class ToppingTitleAdapter(var onItemClickListener: OnClickListener, var isBeverage: Boolean) :
        AdvanceRecycleViewAdapter<ToppingTitleAdapter.AdapterToppingViewHolder, ToppingsItems>() {

    var appExecutors = AppExecutors()

    override fun createDataHolder(parent: ViewGroup?, viewType: Int): AdapterToppingViewHolder {
        return AdapterToppingViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.toping_title_raw, parent, false))
    }

    override fun onBindDataHolder(holder: AdapterToppingViewHolder, position: Int, item: ToppingsItems) {
        var toppingAdapter: ToppingAdapter? = null
        holder.recyclerViewTopping.layoutManager = LinearLayoutManager(holder.itemView.context)

        toppingAdapter = ToppingAdapter(object : ToppingAdapter.OnClickListener {
            override fun onItemClick(toppingListItem: ToppingListItem?, adapterPosition: Int,
                                     toppinInnerList: ArrayList<ToppingListItem>) {

                if (item.minQty != 0) {
                    if (item.minQty == item.minCount) {
                        if (!toppingAdapter!!.items[adapterPosition].isCheckItem) {
                            val sb = Snackbar.make(holder.itemView, "Please choose up to ${item.minQty}", Snackbar.LENGTH_LONG)
                            sb.show()
                        } else {
                            item.minCount = item.minCount - 1
                            toppingAdapter?.setCheckTrue(adapterPosition)
                        }
                    } else {
                        if (toppingAdapter!!.items[adapterPosition].isCheckItem) {
                            item.minCount = item.minCount - 1
                        } else {
                            item.minCount = item.minCount + 1
                        }
                        toppingAdapter?.setCheckTrue(adapterPosition)

                    }
                } else {
                    toppingAdapter?.setCheckTrue(adapterPosition)
                }

                onItemClickListener.onItemClick()
            }
        }, isBeverage)
        holder.textViewName.text = item.type
        holder.recyclerViewTopping.post {
            holder.recyclerViewTopping.adapter = toppingAdapter

            if (item.toppingList?.size == 0) {
                holder.textViewName.visibility = View.GONE
                holder.recyclerViewTopping.visibility = View.GONE
            } else {
                toppingAdapter.items = item.toppingList
            }
        }

        item.toppingList?.forEach {
            if (it.status == ConstantApp.RestaurantStatus.AVAILABLE) {
                item.avialbeCount = item.avialbeCount.plus(1)
            }

        }

        if (item.minQty != null) {
            if (item.minQty!! > 0 && item.toppingList?.size != 0) {
                item.minCount = 0
                holder.textViewChoose.viewShow()
                holder.textViewChoose.text = "(Choose up to " + item.minQty + ")"
            } else {
                holder.textViewChoose.viewGone()
            }
        } else {
            holder.textViewChoose.viewGone()
        }

    }

    fun getSelectedItemCount() {

    }

    class AdapterToppingViewHolder(view: View) : BaseHolder<ToppingsItems>(view) {
        var textViewName = view.textViewName!!
        var textViewChoose = view.textViewChoose!!
        var recyclerViewTopping = view.recyclerViewTopping!!

    }

    interface OnClickListener {
        fun onItemClick()
    }
}