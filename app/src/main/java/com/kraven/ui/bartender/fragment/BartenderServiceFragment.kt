package com.kraven.ui.bartender.fragment


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.view.Gravity
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.kraven.R
import com.kraven.core.Validator
import com.kraven.di.component.FragmentComponent
import com.kraven.exception.ApplicationException
import com.kraven.extensions.viewGone
import com.kraven.extensions.viewShow
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.cart.adapter.TipAdapter
import com.kraven.utils.ConstantApp
import kotlinx.android.synthetic.main.bartender_service_fragment.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class BartenderServiceFragment : BaseFragment(), View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private var aTime: String? = null
    @Inject
    lateinit var validator: Validator

    var selectTips: String? = null
    private var tipAdapter: TipAdapter? = null

    override fun createLayout(): Int = R.layout.bartender_service_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.bartender_service))
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        textViewTipLabel.visibility = View.GONE
        textViewTip.visibility = View.GONE
        editTextSelectDate.setOnClickListener(this)
        editTextSelectTime.setOnClickListener(this)
        editTextHours.setOnClickListener(this)
        editTextEvent.setOnClickListener(this)
        editTextDressCode.setOnClickListener(this)
        llTip.setOnClickListener(this)
        buttonTip.setOnClickListener(this)
        imageViewCloseTip.setOnClickListener(this)
        buttonSend.setOnClickListener(this)

        radioButtonDebitCard.setOnCheckedChangeListener(this)
        radioButtonCashOn.setOnCheckedChangeListener(this)


        recyclerViewTip.layoutManager = ChipsLayoutManager.newBuilder(context)
                .setOrientation(ChipsLayoutManager.HORIZONTAL)
                .setChildGravity(Gravity.CENTER)
                .setRowStrategy(ChipsLayoutManager.STRATEGY_FILL_VIEW)
                .build()

        tipAdapter = TipAdapter(tipList(), object : TipAdapter.OnSelectTip {
            override fun onSelectTip(selectTip: String, isEnter: Boolean) {
                selectTips = selectTip
            }
        })
        recyclerViewTip.adapter = tipAdapter
    }

    private fun tipList(): ArrayList<String> {

        val menuLists = ArrayList<String>()
        menuLists.add("5")
        menuLists.add("10")
        menuLists.add("15")
        menuLists.add("20")
        menuLists.add("30")

        return menuLists

    }


    private fun isValidation(): Boolean {
        try {

            validator.submit(editTextSelectDate, inputLayoutSelectDate)
                    .checkEmpty().errorMessage(getString(R.string.please_select_date))
                    .check()

            validator.submit(editTextSelectTime, inputLayoutSelectTime)
                    .checkEmpty().errorMessage(getString(R.string.please_select_time))
                    .check()

            validator.submit(editTextHours, inputLayoutHours)
                    .checkEmpty().errorMessage(getString(R.string.please_select_hours))
                    .check()

            validator.submit(editTextEvent, inputLayoutEvent)
                    .checkEmpty().errorMessage(getString(R.string.please_select_events))
                    .check()

            validator.submit(editTextDressCode, inputLayoutDressCode)
                    .checkEmpty().errorMessage(getString(R.string.please_select_dress_code))
                    .check()

            return true
        } catch (e: ApplicationException) {
            e.message
        }
        return false
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView!!.id) {
            R.id.radioButtonCashOn -> {
                radioButtonDebitCard.isChecked = false
                radioButtonCashOn.isChecked = isChecked
                linearLayoutTip.viewGone()
            }
            R.id.radioButtonDebitCard -> {
                radioButtonCashOn.isChecked = false
                radioButtonDebitCard.isChecked = isChecked
                linearLayoutTip.viewShow()
            }
        }
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.llTip -> {
                tipAdapter!!.setPosition()
                selectTips = ""
                llTip.visibility = View.GONE
                llEnterTip.visibility = View.VISIBLE
            }

            R.id.buttonTip -> {
                tipAdapter!!.setPosition()
                selectTips = ""
                llTip.visibility = View.GONE
                llEnterTip.visibility = View.VISIBLE
            }
            R.id.buttonSend -> {
                if (isValidation()) {
                    navigator.load(BartenderServiceAcceptedFragment::class.java).replace(true)
                }
            }
            R.id.editTextSelectDate -> {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)

                val dpd = DatePickerDialog(activity!!, DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    val calendar = Calendar.getInstance()
                    calendar.set(year, month, day)
                    val format = SimpleDateFormat(ConstantApp.INPUT_DATE_FORMAT, Locale.ENGLISH)
                    editTextSelectDate.setText(format.format(calendar.time))
                }, year, month, day)
                dpd.datePicker.minDate = System.currentTimeMillis() - 1000
                dpd.show()
            }
            R.id.editTextSelectTime -> {
                val mcurrentTime = Calendar.getInstance()
                val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
                val minute = mcurrentTime.get(Calendar.MINUTE)
                val mTimePicker: TimePickerDialog
                mTimePicker = TimePickerDialog(activity!!, TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                    val datetime = Calendar.getInstance()
                    val c = Calendar.getInstance()
                    datetime.set(Calendar.HOUR_OF_DAY, selectedHour)
                    datetime.set(Calendar.MINUTE, selectedMinute)
                    c.add(Calendar.HOUR, 1)
                    val todayDate = SimpleDateFormat(ConstantApp.INPUT_DATE_FORMAT, Locale.getDefault()).format(Date())
                    val tme = selectedHour.toString() + ":" + selectedMinute.toString()
                    if (editTextSelectDate.text.toString().trim() == todayDate) {
                        if (datetime.timeInMillis > c.timeInMillis) {
                            aTime = getTimeFormat(tme)
                        } else {
                            aTime = ""
                            Toast.makeText(activity, getString(R.string.invalid_time), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        aTime = getTimeFormat(tme)
                    }
                    editTextSelectTime.setText(aTime)
                }, hour, minute, false)//Yes 24 hour time
                mTimePicker.show()
            }
            R.id.editTextEvent -> {
                val items = resources.getStringArray(R.array.event_array)

                val builder = AlertDialog.Builder(activity!!)

                builder.setItems(items) { dialog, item ->
                    editTextEvent.setText(items[item])
                    dialog.dismiss()
                }
                builder.show()
            }
            R.id.editTextHours -> {
                val builder = AlertDialog.Builder(context)
                builder.setTitle(getString(R.string.select_hours))

                val array = Array(8) { i -> i.toString() }
                builder.setItems(array.copyOfRange(1, 8)) { dialog_, which ->
                    dialog_.dismiss()
                    editTextHours.setText(array[which])
                }

                val dialog = builder.create()
                dialog.show()
            }
            R.id.editTextDressCode -> {
                val items = resources.getStringArray(R.array.dress_code)
                val builder = AlertDialog.Builder(activity!!)
                builder.setItems(items) { dialog, item ->
                    editTextDressCode.setText(items[item])
                    dialog.dismiss()
                }
                builder.show()

            }
        }
    }

    private fun getTimeFormat(time: String): String {
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
}