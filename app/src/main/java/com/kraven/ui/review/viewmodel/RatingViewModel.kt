package com.kraven.ui.review.viewmodel

import com.kraven.data.pojo.Parameters
import com.kraven.data.repository.UserRepository
import com.kraven.ui.base.APILiveData
import com.kraven.ui.base.BaseViewModel
import javax.inject.Inject

class RatingViewModel @Inject constructor(private val userRepository: UserRepository) : BaseViewModel() {


    val giveRating = APILiveData<Any>()
    val giveRatingRestaurant = APILiveData<Any>()

    fun giveRating(orderId: String, vendorId: String?, vendorType: String?, count: Int, comment: String?) {
        userRepository.giveRateReview(Parameters(orderId = orderId, vendorId = vendorId, vendorType = vendorType, rate = count.toString(), comment = comment))
                .subscribe(withLiveData(giveRating))
    }

    fun giveRatingRestaurant(orderId: String, vendorId: String?, vendorType: String?, count: Int, comment: String?) {
        userRepository.giveRateReview(Parameters(orderId = orderId, vendorId = vendorId, vendorType = vendorType, rate = count.toString(), comment = comment))
                .subscribe(withLiveData(giveRatingRestaurant))
    }
}