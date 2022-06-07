package com.kraven.ui.cart.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.kraven.R
import com.kraven.core.AppExecutors
import com.kraven.core.AppPreferences
import com.kraven.core.StatusCode
import com.kraven.data.pojo.Parameters
import com.kraven.di.component.FragmentComponent
import com.kraven.ui.address.fragment.AddressFragment
import com.kraven.ui.address.model.Address
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.cart.adapter.SelectAddressAdapter
import com.kraven.ui.home.viewmodel.HomeViewModel
import com.kraven.utils.ConstantApp
import kotlinx.android.synthetic.main.select_address_fragment.*
import javax.inject.Inject

class SelectAddressFragment : BaseFragment() {
    private lateinit var selectAddressAdapter: SelectAddressAdapter
    private lateinit var viewModel: HomeViewModel
    @Inject
    lateinit var appPreferences: AppPreferences
    val appExecutors = AppExecutors()
    private var address: Address? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)[HomeViewModel::class.java]
        viewModel.checkRestaurantDeliveryRadius.observe(this, {
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    ConstantApp.SaveValue.address = address!!
                    val resultIntent = Intent()
                    resultIntent.putExtra("address", address!!)
                    activity!!.setResult(Activity.RESULT_OK, resultIntent)
                    navigator.finish()
                }
                else -> {
                    //showMessage(it.message)
                    commanDialogYesNoNew("Would you like to pickup your order?", it.message, "PickUp Order", "Cancel", object : DialogInterfaceYesNo {
                        override fun onClickYes() {
                            //ConstantApp.SaveValue.isPickUpOrder = true
                            val resultIntent = Intent()
                            activity!!.setResult(Activity.RESULT_OK, resultIntent)
                            navigator.finish()
                        }

                        override fun onClickNo() {

                        }

                    })
                }
            }
        })

        viewModel.checkBeverageDeliveryRadius.observe(this, {
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    ConstantApp.SaveValue.address = address!!
                    val resultIntent = Intent()
                    resultIntent.putExtra("address", address!!)
                    activity!!.setResult(Activity.RESULT_OK, resultIntent)
                    navigator.finish()
                }
                else -> {
                    //showMessage(it.message)
                    commanDialogYesNoNew("Would you like to pickup your order?", it.message, "PickUp Order", "Cancel", object : DialogInterfaceYesNo {
                        override fun onClickYes() {
                            val resultIntent = Intent()
                            activity!!.setResult(Activity.RESULT_OK, resultIntent)
                            navigator.finish()
                        }

                        override fun onClickNo() {

                        }

                    })
                }
            }
        })

        viewModel.getAddressList.observe(this,
            { responseBody ->
                selectAddressAdapter.items = responseBody.data

                selectAddressAdapter.items.forEachIndexed { index, address ->
                    if (address?.cayId == null) address?.cayId = ""
                    if (address?.cayFee == null) address?.cayFee = "0"
                    if (address?.isCay == null) address?.isCay = "0"
                }
            }) { true }
    }

    override fun createLayout(): Int = R.layout.select_address_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    private val restaurantId: String? by lazy {
        val args = arguments ?: throw IllegalStateException("Missing arguments")
        args.getString(ConstantApp.PassValue.RESTAURANT_ID)
    }

    private val orderPage: String? by lazy {
        val args = arguments ?: throw IllegalStateException("Missing arguments")
        args.getString(ConstantApp.PassValue.ORDER_FOOD)
    }

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.select_address))
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        setUpRecyclerView()

        buttonAddAddress.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean(ConstantApp.PassValue.IS_CART, true)
            navigator.load(AddressFragment::class.java).setBundle(bundle).replace(true)
        }
    }

    private fun setUpRecyclerView() {
        recyclerViewAddress.layoutManager = LinearLayoutManager(activity)
        selectAddressAdapter = SelectAddressAdapter(object : SelectAddressAdapter.AddressAdapterInterface {
            override fun onItemClick(displayAddress: String, addressType: String, item: Address?) {
                address = item
                if (orderPage == ConstantApp.PassValue.ORDER_BEVERAGE) {
                    restaurantId?.let { viewModel.checkBeverageDeliveryRadius(it, item?.latitude.toString(), item?.longitude.toString()) }
                } else if (orderPage == ConstantApp.PassValue.ORDER_FOOD || orderPage == ConstantApp.PassValue.ORDER_FOOD_FUTURE) {
                    restaurantId?.let { viewModel.checkRestaurantDeliveryRadius(it, item?.latitude.toString(), item?.longitude.toString()) }
                } else if (orderPage == ConstantApp.PassValue.ORDER_BEVERAGE_SPECIAL) {
                    val resultIntent = Intent()
                    resultIntent.putExtra("address", address!!)
                    activity!!.setResult(Activity.RESULT_OK, resultIntent)
                    navigator.finish()
                }
            }
        })
        recyclerViewAddress.adapter = selectAddressAdapter

        val parameters = Parameters()
        parameters.page = "1"
        viewModel.getAddressList(parameters)
    }


}