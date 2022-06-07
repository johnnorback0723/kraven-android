package com.kraven.ui.payment.viewmodel

import com.kraven.data.pojo.Parameters
import com.kraven.data.repository.PlaceOrderRepository
import com.kraven.data.repository.UserRepository
import com.kraven.ui.base.APILiveData
import com.kraven.ui.base.BaseViewModel
import com.kraven.ui.payment.model.CardResponse
import com.kraven.ui.payment.model.HostedPageModel
import com.kraven.ui.payment.model.TransactionDetails
import com.kraven.ui.payment.setting.model.Card
import java.util.*
import javax.inject.Inject

class PaymentViewModel @Inject constructor(private val userRepository: UserRepository, private val paymentRepository: PlaceOrderRepository) : BaseViewModel() {

    val getCardList = APILiveData<List<Card>>()
    val hostedPagePayment = APILiveData<HostedPageModel>()
    val cardPayment = APILiveData<CardResponse>()

    val getTransactionStatusLiveData = APILiveData<TransactionDetails>()
    val deleteSaveCardsLiveData = APILiveData<Any>()
    val addWalletMoneyFacLiveData = APILiveData<Any>()
    val addWalletMoneyFacLiveDataPay = APILiveData<Any>()

    fun addWalletMoneyFac(parameters: Parameters){
        userRepository.addWalletMoneyFac(parameters).subscribe(withLiveData(addWalletMoneyFacLiveData))
    }

    fun addWalletMoneyFacPay(parameters: Parameters){
        userRepository.addWalletMoneyFac(parameters).subscribe(withLiveData(addWalletMoneyFacLiveDataPay))
    }


    fun getCardList() {
        userRepository.cardList(Parameters(userType = "User"))
                .subscribe(withLiveData(getCardList))
    }


    fun hostedPagePayment(hashMapOf: HashMap<String, String>) {
        paymentRepository.hostedPagePayment(hashMapOf)
                .subscribe(withLiveData(hostedPagePayment))
    }

    fun cardPayment(hashMapOf: HashMap<String, Any>) {
        paymentRepository.cardPayment(hashMapOf)
                .subscribe(withLiveData(cardPayment))
    }

    fun getTransactionStatus(hashMapOf: HashMap<String, Any>) {
        paymentRepository.getTransactionStatus(hashMapOf).subscribe(withLiveData(getTransactionStatusLiveData))
    }

    fun deleteSaveCards(parameters: Parameters) {
        userRepository.deleteSavedCards(parameters).subscribe(withLiveData(deleteSaveCardsLiveData))
    }

}