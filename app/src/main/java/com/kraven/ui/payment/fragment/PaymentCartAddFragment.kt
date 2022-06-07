package com.kraven.ui.payment.fragment

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.kraven.R
import com.kraven.core.StatusCode
import com.kraven.core.Validator
import com.kraven.data.pojo.Parameters
import com.kraven.di.component.FragmentComponent
import com.kraven.exception.ApplicationException
import com.kraven.extensions.convertTwoDigit
import com.kraven.extensions.transactionIdWallet
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.payment.adapter.CardListAdapter
import com.kraven.ui.payment.setting.model.Card
import com.kraven.ui.payment.viewmodel.PaymentViewModel
import kotlinx.android.synthetic.main.fragment_payment.*
import javax.inject.Inject


class PaymentCartAddFragment : BaseFragment() {

    var cardListAdapter: CardListAdapter? = null
    var transactionId: String? = null
    private lateinit var paymentViewModel: PaymentViewModel
    private var lastPosition: Int = -1

    private var cardCVV: String? = null
    private var dialog: Dialog? = null

    @Inject
    lateinit var validator: Validator
    private var editTextCVV: TextInputEditText? = null

    override fun createLayout(): Int = R.layout.fragment_payment

    private val amount: Float by lazy {
        val args = arguments ?: throw IllegalArgumentException("Missing Arguments")
        args.getFloat("amount")
    }

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        paymentViewModel = ViewModelProviders.of(this, viewModelFactory)[PaymentViewModel::class.java]
        paymentViewModel.getCardList.observe(this, { responseBody ->
            showLoader(false)
            when (responseBody.code) {
                StatusCode.CODE_SUCCESS -> {
                    textViewTotalAmount.text = convertTwoDigit(amount)
                    cardListAdapter!!.items = responseBody.data

                }
                StatusCode.CODE_NO_DATA -> {
                    cardListAdapter!!.errorMessage = responseBody.message


                }
                else -> showMessage(responseBody.message)
            }
        }) { true }


        paymentViewModel.hostedPagePayment.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    navigator.load(PaymentWebPageFragment::class.java).setBundle(Bundle().apply {
                        putString("transaction_id",transactionId)
                        putParcelable("card", it.data)
                        putBoolean("isWallet", true)
                        putFloat("amount", amount)
                    }).replace(true)

                }
                else -> {
                    showMessage(it.message)
                }
            }
        })

        paymentViewModel.cardPayment.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    navigator.load(WalletCardPaymentWebView::class.java)
                            .setBundle(Bundle().apply {
                                putString("transaction_id", transactionId)
                                putString("card", it.data?.htmlPage)
                                putString("amount", amount.toString())
                            }).replace(true)
                }
                else -> {
                    showMessage(it.message)
                }
            }
        })

        paymentViewModel.addWalletMoneyFacLiveData.observe(this, {
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    paymentViewModel.hostedPagePayment(hashMapOf(
                            Pair("transaction_id", transactionId!!),
                            Pair("amount", amount.toString()),
                            Pair("capture", "0")
                    ))
                }
                else -> {
                    showLoader(false)
                    showMessage(it.message)
                }
            }
        })
        paymentViewModel.addWalletMoneyFacLiveDataPay.observe(this, {
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    val parameters = HashMap<String, Any>()
                    parameters["card_token"] = cardListAdapter?.items?.get(lastPosition)?.cardToken.toString()
                    parameters["transaction_id"] = transactionId!!
                    parameters["amount"] = amount.toString()
                    parameters["order_id"] = System.currentTimeMillis().toString().takeLast(4)
                    parameters["cvv"] = cardCVV!!
                    paymentViewModel.cardPayment(parameters)
                }
                else -> {
                    showLoader(false)
                    showMessage(it.message)
                }
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

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.payment))
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        setUpRecyclerView()
        paymentViewModel.getCardList()


        buttonPay.setOnClickListener {
            if (lastPosition != -1) {
                showLoader(true)

                /*val intent = Intent()
                intent.putExtra("cardtoken", cardListAdapter?.items?.get(lastPosition)?.cardToken.toString())
                intent.putExtra("amount", amount.toString())
                intent.putExtra("directcard", true)
                activity!!.setResult(Activity.RESULT_OK, intent)
                navigator.finish()*/
                //paymentViewModel.addWalletMoneyFacPay(Parameters(transactionId = transactionId, amount = amount.toString()))

                //CVV Feature
                dialog = Dialog(activity!!, android.R.style.Theme_DeviceDefault_Dialog_MinWidth)
                dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog!!.setContentView(R.layout.dialog_input_cvv)
                dialog!!.setCanceledOnTouchOutside(true)

                val buttonOk = dialog!!.findViewById(R.id.btnPay) as MaterialButton
                editTextCVV = dialog!!.findViewById(R.id.tieCvv) as TextInputEditText
                val inputLayoutCVV = dialog!!.findViewById(R.id.inputLayoutCVV) as TextInputLayout
                buttonOk.setOnClickListener {
                    try {
                        validator.submit(editTextCVV!!, inputLayoutCVV)
                            .checkEmpty().errorMessage(getString(R.string.please_enter_cvv))
                            .check()

                        validator.submit(editTextCVV!!, inputLayoutCVV)
                            .checkMinDigits(3).errorMessage(getString(R.string.please_enter_valid_cvv))
                            .check()

                        val imm = activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
                        imm!!.hideSoftInputFromWindow(editTextCVV!!.windowToken, 0)

                        transactionId = transactionIdWallet(session.user?.id!!)
                        cardCVV = editTextCVV!!.text.toString()
                        if(!cardCVV.isNullOrEmpty()) {
                            showLoader(true)
                            transactionId = transactionIdWallet(session.user?.id!!)
                            paymentViewModel.addWalletMoneyFacPay(Parameters(transactionId = transactionId, amount = amount.toString()))
                        }
                    } catch (a: ApplicationException) {
                        a.message
                    }
                }

                if (dialog != null)
                    dialog!!.show()
            } else {
                showMessage(getString(R.string.please_select_card))
            }
        }
        buttonPayWithNewCard.setOnClickListener {
            showLoader(true)
            transactionId = transactionIdWallet(session.user?.id!!)
           /* paymentViewModel.hostedPagePayment(hashMapOf(
                    Pair("transaction_id", transactionId!!),
                    Pair("amount", amount.toString()),
                    Pair("capture", "1")
            ))*/
            paymentViewModel.addWalletMoneyFac(Parameters(transactionId = transactionId, amount = amount.toString()))
        }
    }

    private fun setUpRecyclerView() {
        recyclerViewCardList.layoutManager = LinearLayoutManager(activity)
        cardListAdapter = CardListAdapter(
            object : CardListAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                lastPosition = position
            }},
            object : CardListAdapter.OnItemDeleteClickListener {
                override fun onItemDeleteClick(card: Card) {
                    commanDialogYesNoNew(getString(R.string.app_name),"Are you sure want to delete card?","Yes","No",object :DialogInterfaceYesNo{
                        override fun onClickYes() {
                            showLoader(true)
                            paymentViewModel.deleteSaveCards(Parameters(userType = "User", cardToken = card.cardToken))
                        }
                        override fun onClickNo() {
                        }
                    })
                }}
        )
        recyclerViewCardList.adapter = cardListAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        if (dialog != null)
            dialog!!.dismiss()
    }
}