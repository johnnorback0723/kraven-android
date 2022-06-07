package com.kraven.ui.restaurant.reservation.fragement

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.kraven.R
import com.kraven.di.component.FragmentComponent
import com.kraven.ui.activity.HomeActivity
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.restaurant.reservation.model.Restaurant
import com.kraven.utils.ConstantApp
import kotlinx.android.synthetic.main.restaurant_order_details.*
import java.util.*


class RestaurantOrderDetails : BaseFragment(), View.OnClickListener {

    var textViewStatus: AppCompatTextView? = null
    var restaurant: Restaurant? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            restaurant = arguments!!.getParcelable(ConstantApp.PassValue.ORDER_DETAIL)
        }
    }

    override fun createLayout(): Int = R.layout.restaurant_order_details

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        setHasOptionsMenu(true)
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.my_reservation_detail))
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        buttonOk.setOnClickListener(this)
        buttonCancel.setOnClickListener(this)

        textViewOrderId.text = String.format(Locale.US,"%s %s", getString(R.string.order_id), "123456")

        imageViewFav.setOnClickListener {
            imageViewFav.isSelected = !imageViewFav.isSelected
        }


        buttonCancel.setOnClickListener {
            activity?.onBackPressed()
        }

        Glide.with(activity!!)
                .load("https://cdn.guidingtech.com/imager/media/assets/HD-Mouth-Watering-Food-Wallpapers-for-Desktop-12_acdb3e4bb37d0e3bcc26c97591d3dd6b.jpg")
                /*.apply(RequestOptions().placeholder(R.drawable.ic_user))*/
                .apply(RequestOptions.bitmapTransform(MultiTransformation(CenterCrop(), RoundedCorners(15))))
                .into(imageViewRestaurant)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater!!.inflate(R.menu.menu_status, menu)

        textViewStatus = menu!!.findItem(R.id.notification).actionView.findViewById(R.id.textViewDetailsStatus) as AppCompatTextView
        when {
            restaurant?.orderStatus.equals(getString(R.string.status_pending)) -> textViewStatus!!.text = getString(R.string.status_pending)
            restaurant?.orderStatus.equals(getString(R.string.status_accepted)) -> textViewStatus!!.text = getString(R.string.status_accepted)
            restaurant?.orderStatus.equals(getString(R.string.status_finished)) -> textViewStatus!!.text = getString(R.string.status_finished)
        }

        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.buttonOk -> {
                navigator.loadActivity(HomeActivity::class.java).byFinishingAll().start()
            }
            R.id.buttonCancel -> {
                navigator.goBack()
            }
        }
    }


}