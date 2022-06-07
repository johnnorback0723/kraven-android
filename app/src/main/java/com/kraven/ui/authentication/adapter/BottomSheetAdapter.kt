package com.kraven.ui.authentication.adapter

import android.content.Context

import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kraven.R
import com.kraven.ui.authentication.model.Country
import com.kraven.ui.authentication.model.Countrys
import kotlinx.android.synthetic.main.raw_filter_dropdown.view.*


import java.util.ArrayList


/**
 * Created by hlink56 on 3/10/16.
 */

class BottomSheetAdapter(private val context: Context, private var eList: List<Country>?, private val strings: List<String>,
                         private val stringItemEventListener: ItemEventListener) : RecyclerView.Adapter<BottomSheetAdapter.ViewHolder>(), Filterable {
    private val mainList: List<Country> = this.eList!!
    private val view: View? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = View.inflate(context, R.layout.raw_filter_dropdown, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.filterText.text = eList!![position].dial.toString() + "-" + eList!![position].name
    }

    override fun getItemCount(): Int {
        return eList!!.size
    }

    override fun getFilter(): Filter {

        return object : Filter() {

            override fun publishResults(constraint: CharSequence, results: FilterResults) {

                eList = results.values as List<Country>
                notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence): FilterResults {
                var constraint = constraint

                val results = FilterResults()
                val FilteredArrayNames = ArrayList<Country>()

                // perform your search here using the searchConstraint String.

                constraint = constraint.toString().toLowerCase()
                for (i in mainList.indices) {
                    val dataNames = mainList[i]
                    if (dataNames.name?.toLowerCase()!!.contains(constraint.toString())) {
                        FilteredArrayNames.add(dataNames)
                    }
                }

                results.count = FilteredArrayNames.size
                results.values = FilteredArrayNames

                return results
            }
        }
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val filterText = view.filterText!!

        init {

            view.setOnClickListener { stringItemEventListener.onItemEventFired(eList!![adapterPosition]) }
        }
    }

    interface ItemEventListener {

        fun onItemEventFired(t: Country)
    }

}
