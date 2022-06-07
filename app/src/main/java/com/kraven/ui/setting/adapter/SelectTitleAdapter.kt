package com.kraven.ui.setting.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.kraven.R
import com.kraven.coreadapter.BaseHolder
import kotlinx.android.synthetic.main.raw_select_titile.view.*


class SelectTitleAdapter(var list: List<String>, var onItemClickListener: OnItemClickListener) :
        RecyclerView.Adapter<SelectTitleAdapter.ViewHolder>(), Filterable {

    private val mainList: List<String> = this.list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = View.inflate(parent.context, R.layout.raw_select_titile, null)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewTitle.text = list[position]


        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(list[position])
        }
    }

    inner class ViewHolder(view: View) : BaseHolder<String>(view) {

        val textViewTitle = view.textViewTitle!!

    }

    override fun getFilter(): Filter {

        return object : Filter() {

            override fun publishResults(constraint: CharSequence, results: FilterResults) {

                list = (results.values as List<String>).toMutableList()
                notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence): FilterResults {
                var constraint = constraint

                val results = FilterResults()
                val FilteredArrayNames = ArrayList<String>()

                // perform your search here using the searchConstraint String.

                constraint = constraint.toString().toLowerCase()
                for (i in mainList.indices) {
                    val dataNames = mainList[i]
                    if (dataNames.toLowerCase()!!.contains(constraint.toString())) {
                        FilteredArrayNames.add(dataNames)
                    }
                }

                results.count = FilteredArrayNames.size
                results.values = FilteredArrayNames

                return results
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(title: String)
    }
}