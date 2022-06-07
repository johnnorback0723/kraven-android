package com.kraven.ui.order.beverage

import com.kraven.data.pojo.Parameters
import com.kraven.data.repository.AuthenticationRepository
import com.kraven.data.repository.PlaceOrderRepository
import com.kraven.data.repository.UserRepository
import com.kraven.ui.base.APILiveData
import com.kraven.ui.base.BaseViewModel
import com.kraven.ui.order.beverage.model.QuantityTypeList
import com.kraven.ui.order.beverage.model.SpecialBeverage
import com.kraven.ui.order.beverage.model.SpecialBeverageDetails
import java.util.HashMap
import javax.inject.Inject

class SpecialBeverageViewModel @Inject constructor(private val userRepository: UserRepository,private val placeOrderRepository: PlaceOrderRepository) : BaseViewModel() {


    var specialBeverageDropDownList = APILiveData<QuantityTypeList>()
    var placeSpecialBeverageOrder = APILiveData<Any>()
    var getSpecialBeverageList = APILiveData<List<SpecialBeverage>>()
    var getSpecialBeverageOrderDetails = APILiveData<SpecialBeverageDetails>()
    var cancelSpecialBeverageOrder = APILiveData<Any>()
    var paySpecialBeverageOrder = APILiveData<Any>()


    fun specialBeverageDropDownList() {
        userRepository.specialBeverageDropDownList().subscribe(withLiveData(specialBeverageDropDownList))
    }

    fun placeSpecialBeverageOrder(parameters: HashMap<String, Any>) {
        placeOrderRepository.placeSpecialBeverageOrder(parameters).subscribe(withLiveData(placeSpecialBeverageOrder))
    }

    fun getSpecialBeverageList(parameters: Parameters) {
        userRepository.getSpecialBeverageList(parameters).subscribe(withLiveData(getSpecialBeverageList))
    }

    fun getSpecialBeverageOrderDetails(parameters: Parameters) {
        userRepository.getSpecialBeverageOrderDetails(parameters).subscribe(withLiveData(getSpecialBeverageOrderDetails))
    }

    fun cancelSpecialBeverageOrder(parameters: Parameters) {
        userRepository.cancelSpecialBeverageOrder(parameters).subscribe(withLiveData(cancelSpecialBeverageOrder))
    }

    fun paySpecialBeverageOrder(parameters: HashMap<String, Any>) {
        placeOrderRepository.paySpecialBeverageOrder(parameters).subscribe(withLiveData(paySpecialBeverageOrder))
    }

}