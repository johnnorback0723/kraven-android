package com.kraven.ui.wallet.fragment


import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.Window
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.kraven.R
import com.kraven.core.StatusCode
import com.kraven.core.Validator
import com.kraven.coreadapter.EndlessRecyclerViewScrollListener
import com.kraven.data.pojo.Parameters
import com.kraven.di.component.FragmentComponent
import com.kraven.exception.ApplicationException
import com.kraven.extensions.convertTwoDigit
import com.kraven.extensions.getTextReplace
import com.kraven.ui.activity.IsolatedActivity
import com.kraven.ui.authentication.viewmodel.AuthViewModel
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.home.fragment.HomeNewFragment
import com.kraven.ui.home.viewmodel.HomeViewModel
import com.kraven.ui.payment.fragment.PaymentWalletFragment
import com.kraven.ui.wallet.adapter.TransactionAdapter
import kotlinx.android.synthetic.main.my_wallet_fragment.*
import java.util.*
import javax.inject.Inject

class MyWalletFragment : BaseFragment() {
    private lateinit var viewModel: HomeViewModel
    private lateinit var authViewModel: AuthViewModel

    @Inject
    lateinit var validator: Validator
    private lateinit var linearLayoutManager: LinearLayoutManager

    private var pages = 1

    private var editTextAddMoney: TextInputEditText? = null

    private var adapterTransaction: TransactionAdapter? = null

    override fun createLayout(): Int = R.layout.my_wallet_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)[HomeViewModel::class.java]
        authViewModel = ViewModelProviders.of(this, viewModelFactory)[AuthViewModel::class.java]
        registerObserver()

    }

    private fun registerObserver() {

        viewModel.getTransactionList.observe(this, {
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    adapterTransaction!!.setItems(it.data, pages)
                }
                StatusCode.CODE_NO_DATA -> {
                    adapterTransaction?.errorMessage = it.message
                }
            }

        })


        authViewModel.getUser.observe(this, {
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    val finalAdd = it.data?.wallet!!.toFloat()
                    textViewCurrentBalance.text = convertTwoDigit(finalAdd)
                }
            }
        })
    }

    val UPDATE_INTERVAL = 10000L
    val updateWidgetHandler = Handler()
    private var updateWidgetRunnable: Runnable = Runnable {
        run {
            authViewModel.getUser(Parameters(userId = session.user!!.id.toString()))
            updateWidgetHandler.postDelayed(updateWidgetRunnable, UPDATE_INTERVAL)
        }

    }

    override fun onPause() {
        super.onPause()
        updateWidgetHandler.removeCallbacks(updateWidgetRunnable);
    }

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.my_wallet))
        toolbar.setToolbarTextColorWhite(true)
        toolbar.setButtonTextVisibility(false)

        setUpRecyclerView()

        viewModel.getTransactionList(pages.toString())

        //authViewModel.getUser(Parameters(userId = session.user!!.id.toString()))
        updateWidgetHandler.postDelayed(updateWidgetRunnable, 0)

        buttonAddMoney.setOnClickListener {
            val dialog = Dialog(activity!!, android.R.style.Theme_DeviceDefault_Dialog_MinWidth)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.dialog_add_money)
            dialog.setCanceledOnTouchOutside(true)

            val buttonOk = dialog.findViewById(R.id.buttonOk) as MaterialButton
            editTextAddMoney = dialog.findViewById(R.id.editTextAddMoney) as TextInputEditText


            editTextAddMoney?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {

                    if (!editTextAddMoney?.text.toString().trim().startsWith("$") && !editTextAddMoney?.text.toString().contains("$")) {
                        editTextAddMoney?.setText(String.format(Locale.US,"$%s", editTextAddMoney?.text))
                        editTextAddMoney?.setSelection(editTextAddMoney?.text!!.length)
                    } else {
                        if (editTextAddMoney?.text.toString().contains("$")) {
                            if (!editTextAddMoney?.text.toString().trim().startsWith("$")) {
                                editTextAddMoney?.setText(String.format(Locale.US,"$%s", editTextAddMoney?.text.toString().replace("$", "")))
                            }
                        }
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }
            })


            val inputLayoutAddMoney = dialog.findViewById(R.id.inputLayoutAddMoney) as TextInputLayout
            buttonOk.setOnClickListener {
                try {

                    validator.submit(editTextAddMoney!!, inputLayoutAddMoney)
                            .checkEmpty().errorMessage(getString(R.string.please_enter_money))
                            .check()

                    val imm = activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
                    imm!!.hideSoftInputFromWindow(editTextAddMoney!!.windowToken, 0)

                    navigator.loadActivity(IsolatedActivity::class.java).setPage(PaymentWalletFragment::class.java).addBundle(
                            Bundle().apply {
                                putFloat("amount", getTextReplace(editTextAddMoney!!))
                            }
                    ).forResult(101).start()
                    dialog.dismiss()
                } catch (a: ApplicationException) {
                    a.message
                }
            }
            dialog.show()
        }
    }

    override fun onBackActionPerform(): Boolean {
        navigator.load(HomeNewFragment::class.java).clearHistory(null).replace(false)
        return false
    }

    private fun setUpRecyclerView() {
        linearLayoutManager = LinearLayoutManager(requireContext())
        recyclerViewTransactionList.layoutManager = linearLayoutManager
        adapterTransaction = TransactionAdapter()
        recyclerViewTransactionList.adapter = adapterTransaction

        recyclerViewTransactionList.addOnScrollListener(object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                pages = page
                viewModel.getTransactionList(page.toString())
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 101) {
            //val finalAdd = session.user?.wallet!!.toFloat().plus(getTextReplace(editTextAddMoney!!)!!.toFloat())
            //textViewCurrentBalance.text = convertTwoDigit(finalAdd)
            //val user = session.user
            //user!!.wallet = finalAdd
            //session.user = user
            viewModel.getTransactionList("1")
        }
    }
}