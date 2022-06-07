package com.kraven.ui.restaurant.reservation.fragement

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.kraven.R
import com.kraven.di.component.FragmentComponent
import com.kraven.ui.home.fragment.RestaurantSliderFragment
import com.kraven.ui.home.model.Service

import kotlinx.android.synthetic.main.restaurant_reservation_details.*

import kotlinx.android.synthetic.main.restaurant_toolbar_layout.*

class RestaurantReservationDetailsFragment : RestaurantSliderFragment(), View.OnClickListener {


    var details = Service.Restaurant()
    var position: Int? = -1
    override fun createLayout(): Int = R.layout.restaurant_reservation_details

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            position = arguments?.getInt("Position")
            details = arguments?.getParcelable("values")!!
        }
    }

    override fun bindData() {
        toolbar.showToolbar(false)
        //setUpViewPager(data.banners.joinToString(",",transform = {it.banner}))
        textViewDetailsStatus.text = details.isClose
        textViewDeliveryType.text = details.deliveryType

        if (details.isClose.equals("Currently Unavailable") || details.isClose.equals("Closed")) {
            textViewDetailsStatus.setTextColor(ContextCompat.getColor(this.activity!!, R.color.red))
            val matrix = ColorMatrix()
            matrix.setSaturation(0F)

            val filter = ColorMatrixColorFilter(matrix)
            //imageViewRestaurant.colorFilter = filter
        }
        toolbarCustom.setNavigationOnClickListener { navigator.goBack() }

        imageViewFav.setOnClickListener(this)
        buttonBookTable.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageViewFav -> {
                imageViewFav.isSelected = !imageViewFav.isSelected
            }
            R.id.buttonBookTable -> {
                navigator.load(RestaurantBookTableFragment::class.java).replace(true)

            }
        }
    }
}