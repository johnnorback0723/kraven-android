package com.kraven.ui.authentication.viewmodel

import com.kraven.data.pojo.Parameters
import com.kraven.data.pojo.User
import com.kraven.data.repository.AuthenticationRepository
import com.kraven.data.repository.UserRepository
import com.kraven.ui.address.model.Address
import com.kraven.ui.address.model.Cay
import com.kraven.ui.authentication.model.AppVersion
import com.kraven.ui.authentication.model.Island
import com.kraven.ui.base.APILiveData
import com.kraven.ui.base.BaseViewModel
import javax.inject.Inject


class AuthViewModel @Inject constructor(private val authenticationRepository: AuthenticationRepository,private val userRepository: UserRepository) : BaseViewModel() {

    val verifyEmailLiveData = APILiveData<User>()
    val verifyOtpLiveData = APILiveData<User>()
    val signUpLogInLiveData = APILiveData<User>()
    val addAddressLiveData = APILiveData<Any>()
    val editUserLiveData = APILiveData<User>()
    val getAddressLiveData = APILiveData<List<Address>>()
    val deleteAddress = APILiveData<Any>()
    val editAddress = APILiveData<Address>()
    val getUser = APILiveData<User>()
    val getIslandListLiveData = APILiveData<List<Island>>()
    val setIslandLiveData = APILiveData<User>()
    val liveData = APILiveData<Any>()
    val getCays = APILiveData<List<Cay>>()

    fun verifyEmail(parameters: Parameters) {
        authenticationRepository.verifyEmail(parameters)
                .subscribe(withLiveData(verifyEmailLiveData))
    }

    fun verifyOtp(parameters: Parameters) {
        authenticationRepository.verifyOtp(parameters)
                .subscribe(withLiveData(verifyOtpLiveData))
    }

    fun signUp(parameters: Parameters) {
        authenticationRepository.signUp(parameters)
                .subscribe(withLiveData(signUpLogInLiveData))
    }

    fun logIn(parameters: Parameters) {
        authenticationRepository.login(parameters)
                .subscribe(withLiveData(signUpLogInLiveData))
    }

    fun addAddress(parameters: Parameters) {
        authenticationRepository.addAddress(parameters)
                .subscribe(withLiveData(addAddressLiveData))
    }

    fun logout() {
        authenticationRepository.logout()
                .subscribe(withLiveData(liveData))
    }

    fun forgotPassword(parameters: Parameters) {
        authenticationRepository.forgotPassword(parameters)
                .subscribe(withLiveData(liveData))
    }

    fun changePassword(parameters: Parameters) {
        authenticationRepository.changePassword(parameters)
                .subscribe(withLiveData(liveData))
    }

    fun contactUs(parameters: Parameters) {
        authenticationRepository.contactUs(parameters)
                .subscribe(withLiveData(liveData))
    }

    fun editUser(parameters: Parameters) {
        authenticationRepository.editUser(parameters)
                .subscribe(withLiveData(editUserLiveData))
    }

    fun getUser(parameters: Parameters) {
        authenticationRepository.getUser(parameters)
                .subscribe(withLiveData(getUser))
    }

    fun getAddressList(parameters: Parameters) {
        authenticationRepository.getAddressList(parameters)
                .subscribe(withLiveData(getAddressLiveData))
    }

    fun deleteAddress(parameters: Parameters) {
        authenticationRepository.deleteAddress(parameters)
                .subscribe(withLiveData(deleteAddress))
    }

    fun editAddress(parameters: Parameters) {
        authenticationRepository.editAddress(parameters)
                .subscribe(withLiveData(editAddress))
    }

    fun getIslandList(){
        userRepository.getIslandList().subscribe(withLiveData(getIslandListLiveData))
    }

    fun setIsland(parameters: Parameters){
        userRepository.setIsland(parameters).subscribe(withLiveData(setIslandLiveData))
    }

    fun getCayList(parameters: Parameters) {
        authenticationRepository.getCayList(parameters)
                .subscribe(withLiveData(getCays))
    }
}
