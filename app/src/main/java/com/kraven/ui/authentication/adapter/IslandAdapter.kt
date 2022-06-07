package com.kraven.ui.authentication.adapter

import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.kraven.R
import com.kraven.coreadapter.AdvanceRecycleViewAdapter
import com.kraven.coreadapter.BaseHolder
import com.kraven.coreadapter.OnRecycleItemClick
import com.kraven.ui.authentication.model.Island
import kotlinx.android.synthetic.main.bartender_order_details_accepted_fragment.*
import kotlinx.android.synthetic.main.raw_select_island.view.*

class IslandAdapter(var newIsland: Boolean, var islandId: String, val onRecycleItemClick: OnRecycleItemClick<Island>) : AdvanceRecycleViewAdapter<IslandAdapter.ViewHolder, Island>() {

    private var lastPositionSelected = -1

    override fun createDataHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(makeView(parent, R.layout.raw_select_island))
    }

    override fun onBindDataHolder(holder: ViewHolder, position: Int, item: Island) {
        holder.onBindView(item)
    }

    inner class ViewHolder(view: View) : BaseHolder<Island>(view) {
        fun onBindView(item: Island) = with(itemView) {
            item.apply {
                Glide.with(itemView.context)
                        .load(image)
                        .apply(RequestOptions.bitmapTransform(MultiTransformation(CenterCrop(), RoundedCorners(30))))
                        .into(imageViewIsland)

                Glide.with(itemView.context)
                        .load(R.drawable.bg_island_gradient)
                        .apply(RequestOptions.bitmapTransform(MultiTransformation(CenterCrop(), RoundedCorners(30))))
                        .into(imageViewGradiant)
                textViewTopIslandName.text = name
                textViewIslandName.text = name
                imageViewSelection.isSelected = lastPositionSelected == adapterPosition
                if (newIsland.not()) {
                    imageViewSelection.isSelected = islandId == id.toString()
                }

                imageViewSelection.setOnClickListener {
                    newIsland = true
                    lastPositionSelected = adapterPosition
                    imageViewSelection.isSelected = imageViewSelection.isSelected.not()
                    notifyDataSetChanged()
                    onRecycleItemClick.onClick(item, it)
                }
            }

        }
    }

}