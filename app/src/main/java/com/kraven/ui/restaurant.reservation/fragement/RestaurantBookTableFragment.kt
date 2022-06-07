package com.kraven.ui.restaurant.reservation.fragement

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.view.View

import android.widget.Toast
import com.kraven.R
import com.kraven.core.Validator
import com.kraven.di.component.FragmentComponent
import com.kraven.exception.ApplicationException
import com.kraven.extensions.getTimeFormat
import com.kraven.ui.base.BaseFragment
import com.kraven.utils.ConstantApp
import kotlinx.android.synthetic.main.restaurant_book_table_fragment.*

import java.text.SimpleDateFormat
import java.util.*
import java.text.ParseException
import javax.inject.Inject


class RestaurantBookTableFragment : BaseFragment(), View.OnClickListener {

    private var aTime: String? = null
    @Inject
    lateinit var validator: Validator


    override fun createLayout(): Int = R.layout.restaurant_book_table_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.book_table))
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        buttonBook.setOnClickListener(this)
        editTextNumberOfPeople.setOnClickListener(this)
        editTextSelectDate.setOnClickListener(this)
        editTextSelectTime.setOnClickListener(this)
        editTextDiningTime.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.buttonBook -> {
                try {
                    validator.submit(editTextNumberOfPeople, inputLayoutNumberOfPeople)
                            .checkEmpty().errorMessage("Please enter number of people")
                            .check()

                    validator.submit(editTextSelectDate, inputLayoutSelectDate)
                            .checkEmpty().errorMessage("Please select date")
                            .check()

                    validator.submit(editTextSelectTime, inputLayoutSelectTime)
                            .checkEmpty().errorMessage("Please select time")
                            .check()

                    validator.submit(editTextDiningTime, inputLayoutDiningTime)
                            .checkEmpty().errorMessage("Please select dining time")
                            .check()

                    validator.submit(editTextSpecialNote, inputLayoutSpecialNote)
                            .checkEmpty().errorMessage("Please add Beverage special notes")
                            .check()

                    navigator.load(RestaurantOrderDetails::class.java).replace(true)
                } catch (e: ApplicationException) {
                    e.message
                }

            }
            R.id.editTextNumberOfPeople -> {

                val builder = AlertDialog.Builder(context)
                builder.setTitle(getString(R.string.number_of_people))

                val array = Array(31) { i -> i.toString() }
                builder.setItems(array.copyOfRange(1, 31)) { dialog_, which ->
                    dialog_.dismiss()
                    editTextNumberOfPeople.setText(array[which])
                }

                val dialog = builder.create()
                dialog.show()
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

                /* val calendar = Calendar.getInstance()
                 calendar.addBeverage(Calendar.DATE, 1)

                 dpd.datePicker.minDate = calendar.timeInMillis

                 val calendarMaxDate = Calendar.getInstance()
                 calendarMaxDate.addBeverage(Calendar.DATE, 7)

                 dpd.datePicker.maxDate = calendarMaxDate.timeInMillis*/


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
            R.id.editTextDiningTime -> {

                val builder = AlertDialog.Builder(context)
                builder.setTitle(getString(R.string.number_of_people))

                val array = Array(8) { i -> if (i == 1) "$i Hour" else "$i Hours" }
                builder.setItems(array.copyOfRange(1, 7)) { dialog_, which ->
                    dialog_.dismiss()
                    editTextDiningTime.setText(array[which])
                }

                val dialog = builder.create()
                dialog.show()

            }
        }
    }



}