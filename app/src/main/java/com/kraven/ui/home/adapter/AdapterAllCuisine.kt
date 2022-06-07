package com.kraven.ui.home.adapter

import android.view.View
import android.view.ViewGroup
import com.kraven.R
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.coreadapter.OnRecycleItemClick
import com.kraven.ui.home.model.AllCuisine
import com.kraven.utils.GlideApp
import kotlinx.android.synthetic.main.raw_all_cuisine.view.*

class AdapterAllCuisine(private val onRecycleItemClick: OnRecycleItemClick<AllCuisine.Cuisine>) : AdvanceRecycleViewAdapter<AdapterAllCuisine.ViewHolder, AllCuisine.Cuisine>() {


    override fun createDataHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(makeView(parent, R.layout.raw_all_cuisine))
    }

    override fun onBindDataHolder(holder: ViewHolder, position: Int, item: AllCuisine.Cuisine) {
        holder.onBindView(item)
    }


    inner class ViewHolder(view: View) : BaseHolder<AllCuisine.Cuisine>(view) {
        fun onBindView(item: AllCuisine.Cuisine) = with(itemView) {
            GlideApp.with(itemView.context)
                    .load(item.image!!)
                    .centerCrop()
                    .into(imageViewCuisine)
           // imageViewCuisine.loadCornerImage(item.image!!, 10)
            textViewCuisineName.text = item.name
            setOnClickListener {
                onRecycleItemClick.onClick(item,it)
            }
        }
    }
}