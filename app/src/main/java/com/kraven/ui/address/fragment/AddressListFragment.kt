package com.kraven.ui.address.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.kraven.R
import com.kraven.core.AppPreferences
import com.kraven.core.StatusCode
import com.kraven.coreadapter.EndlessRecyclerViewScrollListener
import com.kraven.data.pojo.Parameters
import com.kraven.di.component.FragmentComponent
import com.kraven.extensions.viewGone
import com.kraven.ui.activity.IsolatedActivity
import com.kraven.ui.address.adapter.AddressListAdapter
import com.kraven.ui.address.model.Address
import com.kraven.ui.authentication.viewmodel.AuthViewModel
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.home.fragment.RestaurantDetailsFragment
import com.kraven.ui.home.model.Service
import com.kraven.ui.home.viewmodel.HomeViewModel
import com.kraven.utils.ConstantApp
import kotlinx.android.synthetic.main.edit_address_fragment.*
import javax.inject.Inject

class AddressListFragment : BaseFragment() {

    private lateinit var authViewModel: AuthViewModel
    private lateinit var viewModel: HomeViewModel

    var addressListAdapter: AddressListAdapter? = null

    private var linearLayoutManager: LinearLayoutManager? = null

    @Inject
    lateinit var parameters: Parameters

    @Inject
    lateinit var preferences: AppPreferences

    var addressDelete: Address? = null
    var position: Int? = -1

    var details: Service.Restaurant? = null
    var selectAddress = -1
    var orderPage: String? = null
    private var address: Address? = null
    private var restaurantDistance: String? = null
    private var page = 1

    private val currentLatLng: LatLng by lazy {
        val args = arguments ?: throw  IllegalStateException("Missing arguments")
        args.getParcelable(ConstantApp.PassValue.CURRENT_LAT_LONG)!!
    }

    private val restaurantId: String? by lazy {
        val args = arguments ?: throw IllegalStateException("Missing arguments")
        args.getString(ConstantApp.PassValue.RESTAURANT_ID)
    }


    private val selectedDate: String? by lazy {
        val args = arguments ?: throw IllegalStateException("Missing arguments")
        args.getString(ConstantApp.PassValue.FUTUREDATE)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            selectAddress = arguments!!.getInt("select_address")
            position = arguments?.getInt("Position")
            details = arguments?.getParcelable("values")
            orderPage = arguments?.getString(ConstantApp.PassValue.ORDER_FOOD)
            restaurantDistance = arguments?.getString(ConstantApp.PassValue.RESTAURANT_DISTANCE)
        }
        authViewModel = ViewModelProviders.of(this, viewModelFactory)[AuthViewModel::class.java]
        viewModel = ViewModelProviders.of(this, viewModelFactory)[HomeViewModel::class.java]

        authViewModel.getAddressLiveData.observe(this, {
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    addressListAdapter?.setItems(it.data,page)
                }
                StatusCode.CODE_NO_DATA -> {
                    if (page == 1)
                        addressListAdapter?.errorMessage = it.message
                }
                else -> {
                    showMessage(it.message)

                }
            }
        })
        /*  { responseBody ->
              addressListAdapter?.items = responseBody.data
          }
          , { true })*/

        authViewModel.deleteAddress.observe(this,
                { responseBody ->
                    showLoader(false)
                    if (addressListAdapter?.itemCount == 0) {
                        addressListAdapter?.errorMessage = responseBody.message
                    } else {
                        addressListAdapter?.removeItem(addressDelete)
                    }
                }, { true })

        viewModel.checkRestaurantDeliveryRadius.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    ConstantApp.SaveValue.address = address!!
                    ConstantApp.SaveValue.isPickUpOrder = false
                    openFoodRestarurant()
                }
                else -> {
                    commanDialogYesNoNew("Would you like to pickup your order?", it.message, "PickUp Order", "Cancel", object : DialogInterfaceYesNo {
                        override fun onClickYes() {
                            ConstantApp.SaveValue.isPickUpOrder = true
                            openFoodRestarurant()
                        }

                        override fun onClickNo() {

                        }

                    })
                }
            }
        })

        viewModel.checkBeverageDeliveryRadius.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    ConstantApp.SaveValue.address = address!!
                    ConstantApp.SaveValue.isPickUpOrder = false
                    openFoodRestarurant()
                }
                else -> {
                    commanDialogYesNoNew("Would you like to pickup your order?", it.message, "PickUp Order", "Cancel", object : DialogInterfaceYesNo {
                        override fun onClickYes() {
                            ConstantApp.SaveValue.isPickUpOrder = true
                            openFoodRestarurant()
                        }

                        override fun onClickNo() {

                        }
                    })
                }
            }
        })

    }

    private fun openFoodRestarurant() {
        val bundle = Bundle()
        bundle.putString(ConstantApp.PassValue.RESTAURANT_ID, restaurantId)
        bundle.putParcelable(ConstantApp.PassValue.CURRENT_LAT_LONG, currentLatLng)
        bundle.putString(ConstantApp.PassValue.RESTAURANT_DISTANCE, restaurantDistance)
        bundle.putString(ConstantApp.PassValue.ORDER_FOOD, orderPage)
        when {
            orderPage.equals(ConstantApp.PassValue.ORDER_FOOD)
            -> navigator.load(RestaurantDetailsFragment::class.java).setBundle(bundle).replace(false)
            orderPage.equals(ConstantApp.PassValue.ORDER_BEVERAGE)
            -> navigator.load(RestaurantDetailsFragment::class.java).setBundle(bundle).replace(false)
            orderPage.equals(ConstantApp.PassValue.ORDER_FOOD_FUTURE) -> {
                bundle.putString(ConstantApp.PassValue.FUTUREDATE, selectedDate)
                navigator.load(RestaurantDetailsFragment::class.java).setBundle(bundle).replace(false)
            }
            else -> navigator.load(RestaurantDetailsFragment::class.java).setBundle(bundle).replace(false)
        }
    }

    override fun createLayout(): Int = R.layout.edit_address_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.manage_address))
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        if (restaurantDistance == null) {
            textViewMenuPrice.viewGone()
        }

        setUpRecyclerView()
        parameters.page = page.toString()
        authViewModel.getAddressList(parameters)
        buttonAddAddress.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(ConstantApp.PassValue.POSITION, ConstantApp.ADD)
            bundle.putBoolean(ConstantApp.PassValue.IS_CART, false)
            navigator.loadActivity(IsolatedActivity::class.java)
                    .setPage(AddressFragment::class.java)
                    .addBundle(bundle).forResult(101).start()
        }

    }

    private fun setUpRecyclerView() {
        linearLayoutManager = LinearLayoutManager(activity)
        recyclerViewAddress.layoutManager = linearLayoutManager
        addressListAdapter = AddressListAdapter(object : AddressListAdapter.AddressAdapterInterface {
            override fun onEdit(item: Address?) {
                page = 1
                val bundle = Bundle()
                bundle.putString(ConstantApp.PassValue.POSITION, ConstantApp.EDIT)
                bundle.putParcelable(ConstantApp.PassValue.ADDRESS, item)
                navigator.load(AddressFragment::class.java).setBundle(bundle).replace(true)
            }

            override fun onDelete(item: Address?) {
                commandDialogYesNo(getString(R.string.app_name), getString(R.string.sure_delete), object : DialogInterfaceYesNo {
                    override fun onClickYes() {
                        if (session.isProtoType) {

                        } else {
                            showLoader(true)
                            addressDelete = item
                            val parameters = Parameters()
                            parameters.addressId = item?.id
                            authViewModel.deleteAddress(parameters)
                        }
                    }

                    override fun onClickNo() {

                    }

                })
            }

            override fun goBack(item: Address) {
                showLoader(true)
                if (orderPage == ConstantApp.PassValue.ORDER_BEVERAGE) {
                    restaurantId?.let { viewModel.checkBeverageDeliveryRadius(it, item.latitude, item.longitude) }
                } else {
                    restaurantId?.let { viewModel.checkRestaurantDeliveryRadius(it, item.latitude, item.longitude) }
                }
                address = item
            }

        }, selectAddress)
        recyclerViewAddress.adapter = addressListAdapter

        recyclerViewAddress.addOnScrollListener(object:EndlessRecyclerViewScrollListener(linearLayoutManager){
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                this@AddressListFragment.page =page
                authViewModel.getAddressList(Parameters(page = page.toString()))
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 101) {
                parameters.page = "1"
                authViewModel.getAddressList(parameters)
            }
        }
    }
}