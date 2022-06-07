package com.kraven.utils

import android.widget.Filter

import com.kraven.ui.home.adapter.RestaurantAdapter
import com.kraven.ui.home.model.Service

import java.util.ArrayList

class FilterHelper : Filter() {

    /*
    - Perform actual filtering.
     */
    override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
        var constraint = constraint
        val filterResults = Filter.FilterResults()

        if (constraint != null && constraint.length > 0) {
            //CHANGE TO UPPER
            constraint = constraint.toString().toUpperCase()

            //HOLD FILTERS WE FIND
            val foundFilters = ArrayList<String>()

            var galaxy: String?

            //ITERATE CURRENT LIST
            for (i in currentList.indices) {
                galaxy = currentList[i].restaurantName

                //SEARCH
                if (galaxy!!.toUpperCase().contains(constraint)) {
                    //ADD IF FOUND
                    foundFilters.add(galaxy)
                }
            }

            //SET RESULTS TO FILTER LIST
            filterResults.count = foundFilters.size
            filterResults.values = foundFilters
        } else {
            //NO ITEM FOUND.LIST REMAINS INTACT
            filterResults.count = currentList.size
            filterResults.values = currentList
        }

        //RETURN RESULTS
        return filterResults
    }

    override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {

       // adapter.setGalaxies(filterResults.values as MutableList<Service.Restaurant>)
        adapter.notifyDataSetChanged()
    }

    companion object {
        internal lateinit var currentList: MutableList<Service.Restaurant>
        internal lateinit var adapter: RestaurantAdapter

        fun newInstance(currentList: MutableList<Service.Restaurant>, adapter: RestaurantAdapter): FilterHelper {
            FilterHelper.adapter = adapter
            FilterHelper.currentList = currentList
            return FilterHelper()
        }
    }
}