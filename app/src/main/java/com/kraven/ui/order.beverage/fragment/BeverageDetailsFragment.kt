package com.kraven.ui.order.beverage.fragment


import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.kraven.R
import com.kraven.di.component.FragmentComponent
import com.kraven.ui.future.food.order.fragment.FutureFoodOrderFragment
import com.kraven.ui.home.fragment.RestaurantSliderFragment
import com.kraven.ui.home.model.MenuModel
import com.kraven.ui.home.model.Service
import com.kraven.ui.home.viewmodel.HomeViewModel
import com.kraven.ui.menu.fragment.FoodMenuFragment
import com.kraven.ui.order.beverage.adapter.BeverageViewPagerAdapter
import com.kraven.ui.order.beverage.model.Beverage
import com.kraven.ui.restaurant.reservation.fragement.RestaurantBookTableFragment
import com.kraven.utils.ConstantApp
import kotlinx.android.synthetic.main.beverage_details.*
import kotlinx.android.synthetic.main.restaurant_toolbar_layout.*


class BeverageDetailsFragment : RestaurantSliderFragment(), View.OnClickListener {

    var finalCount = 0
    var finalPrice = 0
    private var selectedItems = ArrayList<Beverage>()
    var position: Int? = -1
    private lateinit var viewModel: HomeViewModel
    var details: Service.Restaurant? = null
    var isBack = -1
    var orderPage :String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[HomeViewModel::class.java]
        viewModel.getBeverageList.observe(this,
                { responseBody ->
                    //beverageAdapter?.items = responseBody.data
                }) { true }

        if(arguments!=null){
            isBack = arguments?.getInt("isBack")!!
            orderPage = arguments?.getString(ConstantApp.PassValue.ORDER_FOOD)
            Log.d("Hlink", "OrderPage =$orderPage")
            /*if (isBack != 1) {
                val bundle = Bundle()
                bundle.putInt("select_address", 1)
                bundle.putInt("Position", position!!)
                bundle.putParcelable("values", details)
                bundle.putString(ConstantApp.PassValue.ORDER_FOOD, orderPage)
                navigator.loadActivity(IsolatedActivity::class.java).setPage(AddressListFragment::class.java).addBundle(bundle).start()
            }*/
        }



    }

    override fun createLayout(): Int = R.layout.beverage_details

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        /* val dialogFragment = TooltipDialogFragment()
         dialogFragment.setTargetFragment(this@BeverageDetailsFragment, 101)
         dialogFragment.show(fragmentManager, TooltipDialogFragment::class.java.simpleName)
         imageViewSpecialOrder.visibility = View.VISIBLE
         imageViewSpecialOrder.setBackgroundDrawable(ContextCompat.getDrawable(activity!!, R.drawable.ic_special_order_add_orange))*/

        //viewModel.getBeverageList()

        toolbar.showToolbar(false)


        //nestedScrollView.isFillViewport = true
        //setUpViewPager(data.banners.joinToString(",",transform = {it.banner}))
        if (details != null) {
            textViewDetailsStatus.text = details?.isClose
            textViewDeliveryType.text = details?.deliveryType
            if (details?.isClose.equals("Currently Unavailable") || details?.isClose.equals("Closed")) {
                textViewDetailsStatus.setTextColor(ContextCompat.getColor(this.activity!!, R.color.red))
                val matrix = ColorMatrix()
                matrix.setSaturation(0F)

                val filter = ColorMatrixColorFilter(matrix)
                //imageViewRestaurant.colorFilter = filter
            }
        }


        toolbarCustom.setNavigationOnClickListener { activity?.onBackPressed() }
        imageViewFav.setOnClickListener(this)
        imageViewRestaurantReservation.setOnClickListener(this)
        imageViewFutureFoodOrder.setOnClickListener(this)
        buttonMenu.setOnClickListener(this)
        imageViewCart.setOnClickListener(this)
        //imageViewSpecialOrder.setOnClickListener(this)

        setUpRecyclerView()
        textViewViewCart.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelableArrayList(ConstantApp.PassValue.passItems, selectedItems)
            navigator.load(BeverageCartFragment::class.java).setBundle(bundle).replace(true)
        }

        finalCount = 0
        finalPrice = 0

        val beverageViewPagerAdapter = BeverageViewPagerAdapter(fragmentManager!!)
        viewPagerBeverageList.adapter = beverageViewPagerAdapter
        tabLayoutFood.setupWithViewPager(viewPagerBeverageList)
    }


    private fun setUpRecyclerView() {

        /* linearLayoutManager = LinearLayoutManager(activity)
         recyclerViewBeverageList.layoutManager = linearLayoutManager
         val dividerItemDecoration = DividerItemDecoration(recyclerViewBeverageList.context, linearLayoutManager!!.orientation)
         recyclerViewBeverageList.addItemDecoration(dividerItemDecoration)
         beverageAdapter = BeverageAdapter(object : BeverageAdapter.ItemClickListener {
             override fun onAdd(position: Int, selectedCartList: ArrayList<Beverage>) {
                 selectedItems = selectedCartList
                 finalCount += 1
                 finalPrice += beverageAdapter?.getItem(position)?.beveragePrice!!
                 textViewFinalCount.text = String.format(finalCount.toString() + " " + getString(R.string.items))
                 textViewFinalPrice.text = String.format("$" + finalPrice.toString())

                 if (finalCount == 1) {
                     linearLayoutCart.visibility = View.VISIBLE
                 }
             }

             override fun onSubtract(position: Int, selectedCartList: ArrayList<Beverage>) {
                 selectedItems = selectedCartList
                 finalCount -= 1
                 finalPrice -= beverageAdapter?.getItem(position)?.beveragePrice!!
                 textViewFinalCount.text = String.format(finalCount.toString() + " " + getString(R.string.items))
                 textViewFinalPrice.text = String.format("$" + finalPrice.toString())
                 if (finalCount == 0) {
                     linearLayoutCart.visibility = View.GONE
                 }
             }

             override fun onClickItem(position: Int) {
                 val bottomSheetFragment = ToppingBottomSheetFragment()
                 val args = Bundle()
                 args.putParcelable(ConstantApp.PassValue.TOPPING_POSITION, beverageAdapter!!.items[position])
                 args.putInt(ConstantApp.PassValue.FRAGMENT,2)
                 bottomSheetFragment.arguments = args
                 bottomSheetFragment.show(fragmentManager, bottomSheetFragment.tag)
             }

         })

         recyclerViewBeverageList.adapter = beverageAdapter*/
    }
    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101) {
            imageViewSpecialOrder.setBackgroundDrawable(ContextCompat.getDrawable(activity!!, R.drawable.ic_special_order_add))
        }
    }*/


    fun menuList(): List<MenuModel> {
        val menuLists = mutableListOf<MenuModel>()

        menuLists.add(MenuModel("1", "Pizza Marinara", "Italian", "55(With Toppings)", "0", 0))
        menuLists.add(MenuModel("1", "Pineapple Juice(50% off)", "Beverages", "50", "1", 0))
        menuLists.add(MenuModel("0", "Maxican Aloo Wrap", "Burger", "55", "0", 0))
        menuLists.add(MenuModel("0", "Aloo Tikki", "Burger", "60", "0", 0))
        menuLists.add(MenuModel("0", "The Chicken Bagle Sandwich", "Sandwich", "55", "0", 0))
        menuLists.add(MenuModel("1", "Margherita Pizza(50% off)", "Pizza", "55", "1", 0))

        return menuLists
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.toolbarCustom -> {
                activity?.onBackPressed()
            }
            R.id.buttonMenu -> {
                navigator.load(FoodMenuFragment::class.java).replace(true)
            }
            R.id.imageViewFav -> {
                imageViewFav.isSelected = !imageViewFav.isSelected

            }
            R.id.imageViewRestaurantReservation -> {
                navigator.load(RestaurantBookTableFragment::class.java).replace(true)
            }
            R.id.imageViewFutureFoodOrder -> {
                navigator.load(FutureFoodOrderFragment::class.java).replace(true)
            }
            R.id.imageViewCart -> {
               // val bundle = Bundle()
                //bundle.putParcelableArrayList(ConstantApp.PassValue.passItems, menuLists())
                //navigator.load(BeverageCartFragment::class.java).setBundle(bundle).replace(true)
            }
            R.id.imageViewSpecialOrder -> {
                navigator.load(SpecialOrderFragment::class.java).replace(true)
            }
        }
    }

    /*fun menuLists(): ArrayList<out Parcelable>? {
        val beverageList = ArrayList<Beverage>()

        beverageList.addBeverage(Beverage("https://i1.wp.com/www.wallpapersbyte.com/wp-content/uploads/2015/06/Jack-Daniels-Whiskey-Alcohol-Bottle-and-Glass-Red-Brown-WallpapersByte-com-1366x768.jpg", "Jack Daniels", "Scotch Whisky", 55, "(250 ml)", "1", 1))
        beverageList.addBeverage(Beverage("https://images6.alphacoders.com/409/409417.jpg", "Glefiddich", "Whisky", 120, "(750 ml)", "0", 1))
        beverageList.addBeverage(Beverage("https://i.pinimg.com/originals/cc/45/15/cc451527492814a09dd6744a227e7568.jpg", "MaDowell's", "Reserve Whisky", 40, "(750 ml)", "0", 1))


        return beverageList
    }*/


}
