package com.kraven.ui.portable.bar.rental.fragment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.text.Editable
import android.text.TextWatcher

import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.kraven.R
import com.kraven.core.Validator
import com.kraven.di.component.FragmentComponent
import com.kraven.exception.ApplicationException
import com.kraven.extensions.viewGone
import com.kraven.extensions.viewShow
import com.kraven.ui.bartender.fragment.BartenderServiceAcceptedFragment
import com.kraven.ui.base.BaseFragment

import com.kraven.ui.cart.fragment.BaseFragmentVisibility
import com.kraven.ui.cart.fragment.SelectAddressFragment
import com.kraven.ui.home.viewmodel.HomeViewModel
import com.kraven.ui.portable.bar.rental.adapter.PortableBarListAdapter

import com.kraven.utils.ConstantApp
import com.kraven.utils.TextDecorator
import kotlinx.android.synthetic.main.common_cart_layout.*
import kotlinx.android.synthetic.main.layout_apply_code_tip.*
import kotlinx.android.synthetic.main.portable_bar_rental_fragment.*
import java.text.ParseException

import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class PortableBarRentalFragment : BaseFragmentVisibility(), View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private var aTime: String? = null
    private lateinit var viewModel: HomeViewModel
    lateinit var textWatcher: TextWatcher

    @Inject
    lateinit var validator: Validator

    private var portableBarListAdapter: PortableBarListAdapter? = null

    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun createLayout(): Int = R.layout.portable_bar_rental_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.portable_bar_rental))
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        TextDecorator.decorate(textViewPaymentLabel, resources.getString(R.string.select_payment_method_not_cash))
                .apply {
                    setAbsoluteSize(resources.getDimensionPixelSize(R.dimen._12sdp), "(cash not accepted)")
                }
                .build()

        buttonCheckout.text = getString(R.string.next)

        loadLayout()
        setUpRecyclerView()
        imageViewDeliveryInfo.setOnClickListener(this)
        editTextSelectDate.setOnClickListener(this)
        editTextSelectTime.setOnClickListener(this)
        textViewChange.setOnClickListener(this)
        editTextNumberHours.setOnClickListener(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory)[HomeViewModel::class.java]
        viewModel.getPortabalBarList()

        viewModel.getPortableBarList.observe(this,
                { responseBody ->
                    portableBarListAdapter?.items = responseBody.data
                }) { true }

        radioButtonDebitCard.setOnCheckedChangeListener(this)
       // radioButtonCashOn.setOnCheckedChangeListener(this)

        textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                if (!editTextWallet.text.toString().trim().startsWith("$") &&
                        !editTextWallet.text.toString().contains("$")) {
                    editTextWallet.setText(String.format(Locale.US,"$%s", editTextWallet.text))
                    editTextWallet.setSelection(editTextWallet.text!!.length)
                } else {
                    if (editTextWallet.text.toString().contains("$")) {
                        if (!editTextWallet.text.toString().trim().startsWith("$")) {
                            editTextWallet.setText(String.format(Locale.US,"$%s", editTextWallet.text.toString().replace("$", "")))
                        }
                    }
                }

                if (editTextWallet.text.toString().trim().length == 1) {
                    editTextWallet.removeTextChangedListener(textWatcher)
                    editTextWallet.setText("")
                    editTextWallet.addTextChangedListener(textWatcher)
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        }

        editTextWallet.addTextChangedListener(textWatcher)
    }


    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView!!.id) {
            R.id.radioButtonCashOn -> {
                radioButtonCreditCard.isChecked = false
                radioButtonDebitCard.isChecked = false
               // radioButtonCashOn.isChecked = isChecked
                linearLayoutTip.viewGone()
            }
            R.id.radioButtonDebitCard -> {
                //radioButtonCashOn.isChecked = false
                radioButtonCreditCard.isChecked = false
                //radioButtonDebitCard.isChecked = isChecked
                linearLayoutTip.viewShow()
            }
        }
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v!!.id) {
            R.id.textViewChange -> {
                navigator.load(SelectAddressFragment::class.java).replace(true)
            }
            R.id.imageViewDeliveryInfo -> {
                commandDialog(getString(R.string.delivery_charge_info), getString(R.string.portable_bar_delivery_info), object : BaseFragment.DialogInterface {
                    override fun onClickOk() {

                    }
                })
            }
            R.id.editTextNumberHours -> {
                val builder = AlertDialog.Builder(context)
                builder.setTitle(getString(R.string.select_hours))

                val array = Array(8) { i -> "$i Hour" }
                builder.setItems(array.copyOfRange(1, 8)) { dialog_, which ->
                    dialog_.dismiss()
                    editTextNumberHours.setText(array[which])
                }

                val dialog = builder.create()
                dialog.show()
            }

            R.id.editTextSelectDate -> {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                c.add(Calendar.HOUR_OF_DAY, 48)

                val dpd = DatePickerDialog(activity!!, DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    val calendar = Calendar.getInstance()
                    calendar.set(year, month, day)
                    val format = SimpleDateFormat(ConstantApp.INPUT_DATE_FORMAT, Locale.ENGLISH)
                    editTextSelectDate.setText(format.format(calendar.time))
                }, year, month, day)
                dpd.datePicker.minDate = c.timeInMillis
                val calendarMaxDate = Calendar.getInstance()
                calendarMaxDate.add(Calendar.DATE, 7)
                dpd.datePicker.maxDate = calendarMaxDate.timeInMillis
                dpd.show()
            }

            R.id.editTextSelectTime -> {
                val mCurrentTime = Calendar.getInstance()
                val hour = mCurrentTime.get(Calendar.HOUR_OF_DAY)
                val minute = mCurrentTime.get(Calendar.MINUTE)
                val mTimePicker: TimePickerDialog
                mTimePicker = TimePickerDialog(activity!!, TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                    val datetime = Calendar.getInstance()
                    val c = Calendar.getInstance()
                    datetime.set(Calendar.HOUR_OF_DAY, selectedHour)
                    datetime.set(Calendar.MINUTE, selectedMinute)
                    c.add(Calendar.HOUR, 1)
                    val todayDate = SimpleDateFormat(ConstantApp.INPUT_DATE_FORMAT, Locale.getDefault()).format(Date())
                    val tme = "$selectedHour:$selectedMinute"
                    if (editTextSelectDate.text.toString().trim() == todayDate) {
                        if (datetime.timeInMillis > c.timeInMillis) {
                            aTime = getTimeFormate(tme)
                        } else {
                            aTime = ""
                            Toast.makeText(activity, getString(R.string.invalid_time), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        aTime = getTimeFormate(tme)
                    }
                    editTextSelectTime.setText(aTime)
                }, hour, minute, false)//Yes 24 hour time
                mTimePicker.show()
            }
            R.id.buttonCheckout -> {
                if (radioButtonDebitCard.isChecked) {
                    try {
                        validator.submit(editTextSelectDate, inputLayoutSelectDate)
                                .checkEmpty().errorMessage(getString(R.string.please_select_date))
                                .check()

                        validator.submit(editTextSelectTime, inputLayoutSelectTime)
                                .checkEmpty().errorMessage(getString(R.string.please_select_time))
                                .check()

                        validator.submit(editTextNumberHours, inputLayoutNumberHours)
                                .checkEmpty().errorMessage(getString(R.string.please_select_number_of_hours))
                                .check()
                        navigator.load(BartenderServiceAcceptedFragment::class.java).replace(true)
                    } catch (e: ApplicationException) {
                        e.message
                    }

                } else {
                    showMessage(getString(R.string.please_select_payment_method))
                }
            }
        }

    }

    private fun getTimeFormate(time: String): String {
        var getTime = ""
        try {
            val sdf = SimpleDateFormat(ConstantApp.TIME_INPUT_FORMAT, Locale.ENGLISH)
            val dateObj = sdf.parse(time)
            getTime = SimpleDateFormat(ConstantApp.TIME_OUTPUT_FORMAT, Locale.ENGLISH).format(dateObj)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return getTime

    }


    fun setUpRecyclerView() {
        linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewPortableBarList.layoutManager = linearLayoutManager
        portableBarListAdapter = PortableBarListAdapter(object : PortableBarListAdapter.AdapterPortableInterface {
            override fun onItemClick(displayAddress: String, addressType: String) {

            }

        })
        recyclerViewPortableBarList.adapter = portableBarListAdapter
    }
}