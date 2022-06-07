package com.kraven.ui.edit.profile.fragment

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.kraven.R
import com.kraven.core.StatusCode
import com.kraven.coreadapter.OnRecycleItemClick
import com.kraven.data.pojo.Parameters
import com.kraven.di.component.FragmentComponent
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.edit.profile.adapter.AdapterSavedCards
import com.kraven.ui.payment.viewmodel.PaymentViewModel
import kotlinx.android.synthetic.main.saved_cards_fragment.*

class SavedCardsFragment : BaseFragment() {

    private var adapterSavedCards: AdapterSavedCards? = null

    private val paymentViewModel by lazy { ViewModelProviders.of(this, viewModelFactory)[PaymentViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerObserver()
    }

    override fun createLayout(): Int = R.layout.saved_cards_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        initView()
        setAdapter()
    }

    private fun registerObserver() {
        paymentViewModel.getCardList.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    adapterSavedCards?.items = it.data
                }
                StatusCode.CODE_NO_DATA->{
                    adapterSavedCards?.clearAllItem()
                    adapterSavedCards?.errorMessage = it.message
                }
                else -> showMessage(it.message)
            }
        })
        paymentViewModel.deleteSaveCardsLiveData.observe(this, {
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    paymentViewModel.getCardList()
                }
                else -> {
                    showLoader(false)
                    showMessage(it.message)
                }
            }
        })
    }

    private fun initView() {
        toolbar.apply {
            showToolbar(true)
            setToolbarTitle("Saved Cards")
            setToolbarButton(true)
        }
        showLoader(true)
        paymentViewModel.getCardList()
    }

    private fun setAdapter() {
        adapterSavedCards = AdapterSavedCards(OnRecycleItemClick { t, _ ->
            commanDialogYesNoNew(getString(R.string.app_name),"Are you sure want to delete card?","Yes","No",object :DialogInterfaceYesNo{
                override fun onClickYes() {
                    showLoader(true)
                    paymentViewModel.deleteSaveCards(Parameters(userType = "User", cardToken = t.cardToken))
                }

                override fun onClickNo() {

                }
            })
        })
        recyclerViewSavedCards.adapter = adapterSavedCards
    }
}