package com.kraven.ui.menu.fragment

import android.os.Bundle
import com.kraven.R
import com.kraven.di.component.FragmentComponent
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.cart.fragment.CartFragment
import com.kraven.ui.home.fragment.ToppingBottomSheetFragment
import com.kraven.ui.home.model.DishesItem
import com.kraven.ui.home.model.MenuModel
import com.kraven.utils.ConstantApp
import kotlinx.android.synthetic.main.fragment_food_menu.*


class FoodMenuFragment : BaseFragment() {


    private var finalPrice = 0
    private var finalCount = 0
    private var selectedItems = ArrayList<MenuModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun createLayout(): Int = R.layout.fragment_food_menu

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.menu))
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        //val menuViewPagerAdapter = MenuViewPagerAdapter(childFragmentManager, data.menus)
        //viewPagerFood.adapter = menuViewPagerAdapter
        //tabLayoutFood.setupWithViewPager(viewPagerFood)

        textViewViewCart.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelableArrayList(ConstantApp.PassValue.passItems, selectedItems)
            navigator.load(CartFragment::class.java).setBundle(bundle).replace(true)
        }
    }

    override fun onResume() {
        super.onResume()
        if (selectedItems.size != 0) {
            selectedItems.clear()
            finalCount = 0
            finalPrice = 0
        }
    }


    /*override fun addBeverage(finalPrice:
                     Float, finalCount: Int, items: DishesItem, id: Int, itemPrice: Float, toppingPrice: Float, id1: Int) {
      *//*  selectedItems = items
        finalCount += 1
        this.finalPrice += finalPrice
        textViewFinalCount.text = String.format(finalCount.toString() + " " + getString(R.string.items))
        textViewFinalPrice.text = String.format("$" + this.finalPrice.toString())

        if (finalCount == 1) {
            linearLayoutCart.visibility = View.VISIBLE
        }*//*
    }
*/
   /* override fun subtract(finalPrice: Int, items: RestaurantMenu) {
       *//* selectedItems = items
        finalCount -= 1
        this.finalPrice -= finalPrice
        textViewFinalCount.text = String.format(finalCount.toString() + " " + getString(R.string.items))
        textViewFinalPrice.text = String.format("$" + this.finalPrice.toString())
        if (finalCount == 0) {
            linearLayoutCart.visibility = View.GONE
        }*//*
    }
*/
}