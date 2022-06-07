package com.kraven.ui.order.beverage.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.kraven.R
import com.kraven.core.StatusCode
import com.kraven.core.Validator
import com.kraven.coreadapter.DividerItemDecoration
import com.kraven.di.component.FragmentComponent
import com.kraven.exception.ApplicationException
import com.kraven.extensions.*
import com.kraven.ui.activity.HomeActivity
import com.kraven.ui.activity.IsolatedActivity
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.order.beverage.SpecialBeverageViewModel
import com.kraven.ui.order.beverage.adapter.BeverageAddAdapter
import com.kraven.ui.order.beverage.model.BeverageAdd

import com.kraven.utils.ConstantApp
import com.kraven.utils.Formatter
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog


import kotlinx.android.synthetic.main.special_order_fragment.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class SpecialOrderFragment : BaseFragment(), View.OnClickListener {

    @Inject
    lateinit var validator: Validator

    var position = -1

    var linearLayoutManager: LinearLayoutManager? = null
    var beverageAddAdapter: BeverageAddAdapter? = null

    lateinit var specialBeverageViewModel: SpecialBeverageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        specialBeverageViewModel = ViewModelProviders.of(this, viewModelFactory)[SpecialBeverageViewModel::class.java]
        registerObserver()
    }


    private fun registerObserver() {
        specialBeverageViewModel.placeSpecialBeverageOrder.observe(this, {
            showLoader(false)
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    commandDialog(getString(R.string.app_name), "Your order was Successfully Placed! A KRAVEN representative will contact you.",
                            object : DialogInterface {
                                override fun onClickOk() {
                                    navigator.loadActivity(HomeActivity::class.java).byFinishingAll().start()
                                }
                            })
                }
                else -> showMessage(it.message)
            }
        })
    }

    override fun createLayout(): Int = R.layout.special_order_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        setHasOptionsMenu(true)
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.special_order))
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        initView()
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        linearLayoutManager = LinearLayoutManager(activity)
        recyclerViewBeverage.layoutManager = linearLayoutManager
        recyclerViewBeverage.addItemDecoration(DividerItemDecoration(requireContext()))
        beverageAddAdapter = BeverageAddAdapter(object : BeverageAddAdapter.OnItemClickListner {
            override fun onEdit(item: BeverageAdd?, position: Int) {
                this@SpecialOrderFragment.position = position
                buttonAdd.text = getString(R.string.edit)
                editTextBeverageDate.setText(item?.deliveryDate)
                editTextBeverage.setText(item?.beverageName)
                editTextBrand.setText(item?.beverageBrand)
                editTextQuantityType.setText(item?.quantityType)
                editTextQuantity.setText(item?.quantity)
                editTextSpecialNote.setText(item?.specialNote)
            }

            override fun onDelete(item: BeverageAdd?) {
                beverageAddAdapter?.removeItem(item)
                if (beverageAddAdapter?.items?.size == 0) {
                    buttonPay.viewGone()
                    editTextBeverageDate.clearText()
                    editTextBeverageDate.isEnabled = true
                    editTextBeverageDate.isClickable = true
                }
                editTextBeverage.clearText()
                editTextBrand.clearText()
                editTextQuantityType.clearText()
                editTextQuantity.clearText()
                editTextSpecialNote.clearText()
            }
        })
        recyclerViewBeverage.adapter = beverageAddAdapter

    }

    private fun initView() {
        editTextBeverageDate.setOnClickListener(this)
        editTextBeverage.setOnClickListener(this)
        editTextQuantityType.setOnClickListener(this)
        buttonAdd.setOnClickListener(this)
        buttonPay.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_info, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.ic_info -> {
                commandDialog(getString(R.string.delivery_charge_info), getString(R.string.beverage_deliver_info), object : DialogInterface {
                    override fun onClickOk() {

                    }
                })
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.editTextBeverage -> {
                navigator.loadActivity(IsolatedActivity::class.java).setPage(SelectBeverageFragment::class.java)
                        .addBundle(Bundle().apply {
                            putBoolean("beveragge", true)
                        })
                        .forResult(101).start()
            }
            R.id.editTextQuantityType -> {
                navigator.loadActivity(IsolatedActivity::class.java).setPage(SelectBeverageFragment::class.java)
                        .addBundle(Bundle().apply {
                            putBoolean("beveragge", false)
                        })
                        .forResult(102).start()
            }
            R.id.editTextBeverageDate -> {

                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)

                val dpd = DatePickerDialog.newInstance({ _, year, month, day ->
                    editTextBeverageDate.clearText()
                    val calendar = Calendar.getInstance()
                    calendar.set(year, month, day)
                    val format = SimpleDateFormat(ConstantApp.INPUT_DATE_FORMAT, Locale.ENGLISH)

                    val timiPicker = TimePickerDialog.newInstance({ view, hourOfDay, minute, second ->
                        val tme = "$hourOfDay:$minute"
                        editTextBeverageDate.setText(format.format(calendar.time) + ", " + getTimeFormat(tme))
                    }, true)



                    fragmentManager?.let { timiPicker.show(it, "TimePicker") }
                }, year, month, day)

                /* val maxDateC = Calendar.getInstance()
                 maxDateC.add(Calendar.YEAR, 16)
                 dpd.maxDate = maxDateC

                 val cNew = Calendar.getInstance()
                 var loopDate = Calendar.getInstance()
                 while (cNew.before(maxDateC)) {
                     val dayOfWeek = loopDate.get(Calendar.DAY_OF_WEEK)
                     if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY) {
                         val disabledDays = arrayOfNulls<Calendar>(1)
                         disabledDays[0] = loopDate
                         dpd.disabledDays = disabledDays
                     }
                     cNew.add(Calendar.DATE, 1)
                     loopDate = cNew
                 }
 */
                c.add(Calendar.DAY_OF_MONTH, 6)
                dpd.minDate = c
                fragmentManager?.let { dpd.show(it, "DatePicker") }
            }
            R.id.buttonAdd -> {
                if (validation()) {
                    if (buttonAdd.text == getString(R.string.edit)) {
                        beverageAddAdapter?.updateItem(BeverageAdd(getText(editTextBeverageDate), getText(editTextBeverage), getText(editTextBrand), getText(editTextQuantityType), getText(editTextQuantity), getText(editTextSpecialNote)), position)
                        buttonAdd.text = getString(R.string.add)
                    } else {
                        recyclerViewBeverage.viewShow()
                        if (beverageAddAdapter?.items!!.size < 30) {
                            beverageAddAdapter?.addItem(BeverageAdd(getText(editTextBeverageDate), getText(editTextBeverage), getText(editTextBrand), getText(editTextQuantityType), getText(editTextQuantity), getText(editTextSpecialNote)))
                            editTextBeverage.clearText()
                            editTextBrand.clearText()
                            editTextQuantityType.clearText()
                            editTextQuantity.clearText()
                            editTextSpecialNote.clearText()
                        } else {
                            showMessage("You can order a maximum of 30 beverages at a time.")
                        }
                    }

                    if (beverageAddAdapter?.items?.size!! >= 1) {
                        buttonPay.viewShow()
                        editTextBeverageDate.isEnabled = false
                        editTextBeverageDate.isClickable = false
                    }


                }
            }
            R.id.buttonPay -> {
                if (beverageAddAdapter?.items!!.size == 0) {
                    showMessage("Please add at least 1 item")
                } else {
                    showLoader(true)
                    val parameters = HashMap<String, Any>()
                    parameters["delivery_datetime"] = Formatter.format(getText(editTextBeverageDate)!!, Formatter.DD_MMM_YYYY_hh_mm_aa, Formatter.YYYY_MM_DD_HH_MM_SS, true).toString()
                    parameters["items"] = sendSpecialBeverageItems(beverageAddAdapter!!.items)
                    specialBeverageViewModel.placeSpecialBeverageOrder(parameters)

                }
            }
        }
    }


    private fun sendSpecialBeverageItems(items: List<BeverageAdd>): List<BeverageAdd> {
        val sendBeverageItems = ArrayList<BeverageAdd>()
        items.forEach {
            sendBeverageItems.add(BeverageAdd(beverageName = it.beverageName, beverageBrand = it.beverageBrand, quantityType = it.quantityType, quantity = it.quantity, specialNote = it.specialNote))
        }
        return sendBeverageItems

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 101) {
                editTextBeverage.setText(data?.getStringExtra("title"))
            } else if (requestCode == 102) {
                editTextQuantityType.setText(data?.getStringExtra("title"))
            }
        }
    }

    private fun validation(): Boolean {
        try {
            validator.submit(editTextBeverageDate, inputLayoutDeliveryDate)
                    .checkEmpty().errorMessage(R.string.please_select_beverage_date)
                    .check()

            validator.submit(editTextBeverage, inputLayoutBeverage)
                    .checkEmpty().errorMessage(R.string.please_enter_beverage)
                    .check()

            validator.submit(editTextBrand, inputLayoutBrand)
                    .checkEmpty().errorMessage(R.string.please_enter_brand)
                    .check()

            validator.submit(editTextQuantityType, inputLayoutQuantityType)
                    .checkEmpty().errorMessage(R.string.please_enter_quantity_type)
                    .check()

            validator.submit(editTextQuantity, inputLayoutQuantity)
                    .checkEmpty().errorMessage(R.string.please_enter_quantity)
                    .check()

            /* validator.submit(editTextSpecialNote, inputLayoutSpecialNote)
                     .checkEmpty().errorMessage(R.string.please_enter_special_note)
                     .check()*/
            return true

        } catch (e: ApplicationException) {
            e.message
        }
        return false
    }
}