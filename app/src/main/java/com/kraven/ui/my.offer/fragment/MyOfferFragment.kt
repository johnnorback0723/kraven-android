package com.kraven.ui.my.offer.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.kraven.R
import com.kraven.application.KravenCustomer
import com.kraven.core.StatusCode
import com.kraven.coreadapter.EndlessRecyclerViewScrollListener
import com.kraven.di.component.FragmentComponent
import com.kraven.extensions.requirePermission
import com.kraven.ui.activity.IsolatedActivity
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.home.fragment.HomeNewFragment
import com.kraven.ui.home.fragment.RestaurantDetailsFragment
import com.kraven.ui.home.model.Promocode
import com.kraven.ui.home.viewmodel.HomeViewModel
import com.kraven.ui.my.offer.adapter.MyOfferAdapter
import com.kraven.utils.ConstantApp
import com.kraven.utils.LocationManager
import kotlinx.android.synthetic.main.my_offer_fragment.*
import javax.inject.Inject

class MyOfferFragment : BaseFragment() {

    @Inject
    lateinit var locationManager: LocationManager
    private var currentLatLng: LatLng? = null
    private lateinit var linearLayoutManager: LinearLayoutManager

    private lateinit var viewModel: HomeViewModel
    private var pages = 1

    var myOfferAdapter: MyOfferAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)[HomeViewModel::class.java]
        registerObserver()
    }

    private fun registerObserver() {
        viewModel.getPromoCodeList.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    myOfferAdapter!!.setItems(it.data!!, pages)
                }
                StatusCode.CODE_NO_DATA -> {
                    myOfferAdapter!!.errorMessage = it.message
                }
                else -> showMessage(it.message)
            }
        }) { true }
    }

    override fun createLayout(): Int = R.layout.my_offer_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.promos))
        toolbar.setToolbarTextColorWhite(false)
        toolbar.setButtonTextVisibility(false)

        requirePermission(activity!!, "This app need permissions to use this feature.You can grant them in app settings.") {
            locationManager.checkLocationEnableAndStartUpdate(true)
            locationManager.locationUpdateLiveData.observe(this, {
                currentLatLng = it
                locationManager.locationUpdateLiveData.removeObservers(this)
            })
        }

        setUpRecyclerView()
        viewModel.getPromoCodeList(pages.toString(),session.user?.islandId)

    }

    override fun onBackActionPerform(): Boolean {
        navigator.load(HomeNewFragment::class.java).clearHistory(null).replace(false)
        return false
    }

    private fun setUpRecyclerView() {
        recyclerViewOfferList.layoutManager = LinearLayoutManager(activity!!)
        linearLayoutManager = LinearLayoutManager(requireContext())

        myOfferAdapter = MyOfferAdapter(object : MyOfferAdapter.OnItemClickListener {
            override fun onItemClick(item: Promocode) {
                KravenCustomer.tempPromoCode = item
                val bundle = Bundle()
                bundle.putParcelable(ConstantApp.PassValue.CURRENT_LAT_LONG, currentLatLng)
                bundle.putString(ConstantApp.PassValue.RESTAURANT_ID, if (item.restaurantId != 0) item.restaurantId.toString() else item.beverageId.toString())
                bundle.putString(ConstantApp.PassValue.ORDER_FOOD, if (item.restaurantId != 0) ConstantApp.PassValue.ORDER_FOOD else ConstantApp.PassValue.ORDER_BEVERAGE)
                navigator.loadActivity(IsolatedActivity::class.java, RestaurantDetailsFragment::class.java).addBundle(bundle).start()
            }

        })
        recyclerViewOfferList.adapter = myOfferAdapter

        recyclerViewOfferList.addOnScrollListener(object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                pages = page
                viewModel.getPromoCodeList(page.toString(), session.user?.islandId)
            }

        })
    }
}