package com.kraven.ui.authentication.fragement

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent

import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.kraven.R
import com.kraven.di.component.FragmentComponent
import com.kraven.ui.authentication.adapter.BottomSheetAdapter
import com.kraven.ui.authentication.model.CountriesFetcher
import com.kraven.ui.base.BaseFragment
import com.kraven.utils.ConstantApp
import com.kraven.ui.authentication.model.Country
import com.kraven.ui.authentication.model.Countrys
import kotlinx.android.synthetic.main.contry_code_fragment.*


import java.util.ArrayList


class CountryCodeFragment : BaseFragment() {


    override fun createLayout(): Int = R.layout.contry_code_fragment

    override fun inject(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.country_code))
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        setHasOptionsMenu(true)
        recyclerViewMultiple!!.setHasFixedSize(true)
        recyclerViewMultiple!!.layoutManager = LinearLayoutManager(activity!!)


        val bottomSheetAdapter = BottomSheetAdapter(activity!!, Country.readJsonOfCountries(activity!!).sortedWith(compareBy {it.name}), listOf(context!!.getString(R.string.country_code)), object : BottomSheetAdapter.ItemEventListener {
            override fun onItemEventFired(t: Country) {
                val resultIntent = Intent()
                resultIntent.putExtra(ConstantApp.PassValue.COUNTRY_CODE, t)
                activity!!.setResult(Activity.RESULT_OK, resultIntent)
                navigator.finish()
                val mView = editTextSearch
                if (mView!!.isFocused) {
                    val inputManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManager.hideSoftInputFromWindow(mView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                }
            }
        })
        editTextSearch!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                bottomSheetAdapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
        recyclerViewMultiple!!.adapter = bottomSheetAdapter

    }

    private fun getCountriesCode(): List<Country> {
        val listCountry = Country.readJsonOfCountries(activity!!)
        val countries = ArrayList<Country>()
        countries.addAll(listCountry)
        /*for (locale in listCountry) {
            countries.a
            *//*val country = locale.dial + " - " + locale.name
            if (country.trim { it <= ' ' }.isNotEmpty() && !countries.contains(country)) {
                countries.addBeverage(country)
            }*//*
        }*/
        //countries.sort()

        return countries
    }

    @SuppressLint("PrivateResource")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val editItem = menu.add(0, 0, 0, "")
        editItem.setIcon(R.drawable.abc_ic_search_api_material)
        editItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            0 -> search_bar!!.visibility = if (search_bar!!.visibility == View.GONE) View.VISIBLE else View.GONE
        }
        return super.onOptionsItemSelected(item)
    }


}
