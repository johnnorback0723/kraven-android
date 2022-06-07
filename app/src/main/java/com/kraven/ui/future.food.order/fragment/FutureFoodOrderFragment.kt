package com.kraven.ui.future.food.order.fragment

import android.app.Activity


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders

import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException
import com.google.android.gms.maps.model.LatLng
import com.kraven.R
import com.kraven.core.StatusCode
import com.kraven.core.Validator
import com.kraven.di.component.FragmentComponent
import com.kraven.exception.ApplicationException
import com.kraven.extensions.availableRestaurantFuture
import com.kraven.extensions.getList
import com.kraven.ui.activity.IsolatedActivity

import com.kraven.ui.base.BaseFragment
import com.kraven.ui.home.model.RestaurantDetails
import com.kraven.ui.home.model.Timing
import com.kraven.ui.home.viewmodel.HomeViewModel
import com.kraven.ui.order.food.fragment.OrderFoodFragment
import com.kraven.utils.ConstantApp

import com.kraven.utils.DateUtils.isSameDay

import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.future_food_order_fragment.*
import org.greenrobot.eventbus.EventBus
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class FutureFoodOrderFragment : BaseFragment(), View.OnClickListener {

    private var aTime: String? = null

    @Inject
    lateinit var validator: Validator

    var date: String? = null
    var time: String? = null
    var sdf: SimpleDateFormat? = null
    var selectedDate: Date? = null
    var selectedDateTime: String? = null
    var outputDateFormat: SimpleDateFormat? = null
    private lateinit var viewModel: HomeViewModel
    private var restaurantDetails: RestaurantDetails? = null

    private val isRestaurantDetails: Boolean by lazy {
        val args = arguments ?: throw IllegalStateException("Missing arguments")
        args.getBoolean("isRestaurantDetails")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            restaurantDetails = arguments!!.getParcelable(ConstantApp.PassValue.RESTAURANT_MENU)
        }

        viewModel = ViewModelProviders.of(this, viewModelFactory)[HomeViewModel::class.java]

        viewModel.getRestaurantDetails.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    restaurantDetails = it.data

                    if (availableRestaurantFuture(selectedDateTime!!, getList(restaurantDetails!!.timing as ArrayList<Timing>))[0].status == ConstantApp.RestaurantStatus.AVAILABLE) {
                        EventBus.getDefault().post(selectedDateTime)
                        val intent = Intent()
                        intent.putExtra(ConstantApp.PassValue.FUTUREDATE, selectedDateTime)
                        intent.putExtra(ConstantApp.PassValue.ORDER_FOOD, ConstantApp.PassValue.ORDER_FOOD_FUTURE)
                        activity!!.setResult(Activity.RESULT_OK, intent)
                        navigator.finish()
                    } else {
                        commandDialog(getString(R.string.app_name), "Restaurant doesn't serve outside the working hours",
                                object : DialogInterface {
                                    override fun onClickOk() {

                                    }
                                })
                    }
                }
                else -> showMessage(it.message)
            }
        }) { true }
    }

    override fun createLayout(): Int = R.layout.future_food_order_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.future_food_order))
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        setupCalendarView()
        editTextSelectTime.setOnClickListener(this)
        buttonNext.setOnClickListener(this)
    }


    private fun setupCalendarView() {
        try {
            val calendar = Calendar.getInstance()
            calendarView.setDate(calendar)
            calendarView.setMinimumDate(calendar)


            val calendarMaxDate = Calendar.getInstance()
            calendarMaxDate.add(Calendar.DATE, 7)
            calendarView.setMaximumDate(calendarMaxDate)

        } catch (e: OutOfDateRangeException) {
            e.printStackTrace()
        }
        sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
        outputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

        calendarView.setOnDayClickListener {
            val currentTime = Calendar.getInstance()
            val hour = currentTime.get(Calendar.HOUR_OF_DAY)
            val minute = currentTime.get(Calendar.MINUTE)
            try {
                val newDate = sdf!!.parse(it.calendar.time.toString())
                val outputPattern = "yyyy-MM-dd"
                val outputFormat = SimpleDateFormat(outputPattern, Locale.ENGLISH)

                selectedDate = newDate

                date = outputFormat.format(newDate)

            } catch (e: ParseException) {
                e.printStackTrace()
            }

        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.editTextSelectTime -> {
                val mCurrentTime = Calendar.getInstance()
                mCurrentTime.add(Calendar.HOUR_OF_DAY, 3)
                val hour = mCurrentTime.get(Calendar.HOUR_OF_DAY)
                val minutes = mCurrentTime.get(Calendar.MINUTE)
                val dpd = TimePickerDialog.newInstance(
                        { view, hourOfDay, minute, second ->
                            val tme = "$hourOfDay:$minute"
                            aTime = getTimeFormat(tme)
                            time = aTime

                            editTextSelectTime.setText(aTime)
                        }, hour, minutes,
                        false
                )
                if (selectedDate != null) {
                    if (isSameDay(selectedDate, Date())) {
                        dpd.setMinTime(mCurrentTime.get(Calendar.HOUR_OF_DAY), mCurrentTime.get(Calendar.MINUTE), mCurrentTime.get(Calendar.SECOND))
                    }
                } else {
                    date = outputDateFormat?.format(Date()).toString()
                    dpd.setMinTime(mCurrentTime.get(Calendar.HOUR_OF_DAY), mCurrentTime.get(Calendar.MINUTE), mCurrentTime.get(Calendar.SECOND))
                }
                fragmentManager?.let { dpd.show(it, "Timepickerdialog") }
            }
            R.id.buttonNext -> {
                try {
                    validator.submit(editTextSelectTime, inputLayoutSelectTime)
                            .checkEmpty().errorMessage("Please select time")
                            .check()

                    if (isRestaurantDetails) {

                        if (date == null) {
                            date = outputDateFormat?.format(Date()).toString()
                        }
                        selectedDateTime = "$date $time"

                        showLoader(true)

                        viewModel.getRestaurantDetails(restaurantDetails?.id.toString(),
                                LatLng(restaurantDetails?.latitude!!.toDouble(), restaurantDetails!!.longitude.toDouble()),
                                (if (selectedDateTime != null && selectedDateTime!!.isNotEmpty()) selectedDateTime else "")!!, "Future Food")

                    } else {
                        if (date == null) {
                            date = outputDateFormat?.format(Date()).toString()
                        }
                        val selectedDate = "$date $time"
                        EventBus.getDefault().post(selectedDate)

                        navigator.loadActivity(IsolatedActivity::class.java).setPage(OrderFoodFragment::class.java).addBundle(Bundle().apply {
                            putString(ConstantApp.PassValue.FUTUREDATE, selectedDate)
                            putString(ConstantApp.PassValue.ORDER_FOOD, ConstantApp.PassValue.ORDER_FOOD_FUTURE)
                        }).start()

                    }

                } catch (e: ApplicationException) {
                    e.message
                }
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