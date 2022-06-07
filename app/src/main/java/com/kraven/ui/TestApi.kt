package com.kraven.ui

import com.kraven.R
import com.kraven.ui.address.model.Address
import com.kraven.ui.address.model.Cay
import com.kraven.ui.bartender.model.BartenderOrderList
import com.kraven.ui.cart.model.SelectAddress
import com.kraven.ui.home.model.MenuModel
import com.kraven.ui.home.model.Service
import com.kraven.ui.home.model.Topping
import com.kraven.ui.my.order.model.OrderList
import com.kraven.ui.my_offer.model.MyOffer
import com.kraven.ui.notification.model.Notifications
import com.kraven.ui.order.beverage.model.Beverage
import com.kraven.ui.payment.model.Payment
import com.kraven.ui.portable.bar.rental.model.PortableBar
import com.kraven.ui.portable.bar.rental.model.PortableOrderList
import com.kraven.ui.restaurant.reservation.model.Restaurant


class TestApi {

    companion object {


        fun getAddressList(): List<Address> {

            val addressList = mutableListOf<Address>()
            addressList.add(Address("Thaltej", "India", "10,Jay ambe nagar,near udgam school", "Home", "1", "Ahmedabad", "72.5050", "72.5050", "1"))
            addressList.add(Address("Thaltej", "India", "10,Jay ambe nagar,near udgam school", "Home", "1", "Ahmedabad", "72.5050", "72.5050", "2"))
            return addressList
        }

       /* fun beverageList(): List<Beverage> {

            val beverageList = mutableListOf<Beverage>()

            beverageList.addBeverage(Beverage("https://i1.wp.com/www.wallpapersbyte.com/wp-content/uploads/2015/06/Jack-Daniels-Whiskey-Alcohol-Bottle-and-Glass-Red-Brown-WallpapersByte-com-1366x768.jpg", "Jack Daniels", "Scotch Whisky", 55, "(250 ml)", "1", 0))
            beverageList.addBeverage(Beverage("https://images6.alphacoders.com/409/409417.jpg", "Glefiddich", "Whisky", 120, "(750 ml)", "0", 0))
            beverageList.addBeverage(Beverage("https://i.pinimg.com/originals/cc/45/15/cc451527492814a09dd6744a227e7568.jpg", "MaDowell's", "Reserve Whisky", 40, "(750 ml)", "0", 0))
            beverageList.addBeverage(Beverage("https://images.finedrams.com/image/27675-mediumlarge-1483300583/teachers-origin-blended-whisky.jpg", "Teacher's", "Blended Whisky", 140, " (750 ml)", "0", 0))
            beverageList.addBeverage(Beverage("https://d1lfury6i2wx5t.cloudfront.net/images/1f9a80bc040c1f5f6dadef42394a566d.jpeg", "Bacardi", "White Rum", 140, " (250 ml)", "0", 0))


            beverageList.addBeverage(Beverage("https://i1.wp.com/www.wallpapersbyte.com/wp-content/uploads/2015/06/Jack-Daniels-Whiskey-Alcohol-Bottle-and-Glass-Red-Brown-WallpapersByte-com-1366x768.jpg", "Jack Daniels", "Scotch Whisky", 55, "(250 ml)", "0", 0))
            beverageList.addBeverage(Beverage("https://images6.alphacoders.com/409/409417.jpg", "Glefiddich", "Whisky", 120, "(750 ml)", "0", 0))
            beverageList.addBeverage(Beverage("https://i.pinimg.com/originals/cc/45/15/cc451527492814a09dd6744a227e7568.jpg", "MaDowell's", "Reserve Whisky", 40, "(750 ml)", "0", 0))
            beverageList.addBeverage(Beverage("https://images.finedrams.com/image/27675-mediumlarge-1483300583/teachers-origin-blended-whisky.jpg", "Teacher's", "Blended Whisky", 140, " (750 ml)", "0", 0))
            beverageList.addBeverage(Beverage("https://d1lfury6i2wx5t.cloudfront.net/images/1f9a80bc040c1f5f6dadef42394a566d.jpeg", "Bacardi", "White Rum", 140, " (250 ml)", "0", 0))



            return beverageList
        }*/

        fun getRestaurantOrderList(): List<Restaurant> {

            val restaurantOrderList = mutableListOf<Restaurant>()

            restaurantOrderList.add(Restaurant("123456", "14 Aug 2018, 12:40 AM", "Pending", "https://cdn.guidingtech.com/imager/media/assets/HD-Mouth-Watering-Food-Wallpapers-for-Desktop-12_acdb3e4bb37d0e3bcc26c97591d3dd6b.jpg", "Basic Kneads Pizza", "Appetizers", 3, "0", "(500 Reviews)", 1))
            restaurantOrderList.add(Restaurant("123456", "14 Aug 2018, 12:40 AM", "Accepted", "https://images.all-free-download.com/images/graphicthumb/food_picture_05_hd_picture_167519.jpg", "Jassi De Parathe", "Appetizers", 3, "1", "(500 Reviews)", 0))
            restaurantOrderList.add(Restaurant("123456", "14 Aug 2018, 12:40 AM", "Finished", "https://images.pexels.com/photos/461198/pexels-photo-461198.jpeg", "Iscon Ganthiya", "Appetizers", 3, "0", "(485 Reviews)", 0))

            restaurantOrderList.add(Restaurant("123456", "14 Aug 2018, 12:40 AM", "Pending", "https://cdn.guidingtech.com/imager/media/assets/HD-Mouth-Watering-Food-Wallpapers-for-Desktop-12_acdb3e4bb37d0e3bcc26c97591d3dd6b.jpg", "Basic Kneads Pizza", "Appetizers", 3, "0", "(500 Reviews)", 0))
            restaurantOrderList.add(Restaurant("123456", "14 Aug 2018, 12:40 AM", "Accepted", "https://images.all-free-download.com/images/graphicthumb/food_picture_05_hd_picture_167519.jpg", "Jassi De Parathe", "Appetizers", 3, "1", "(500 Reviews)", 0))
            restaurantOrderList.add(Restaurant("123456", "14 Aug 2018, 12:40 AM", "Finished", "https://images.pexels.com/photos/461198/pexels-photo-461198.jpeg", "Iscon Ganthiya", "Appetizers", 3, "0", "(485 Reviews)", 0))

            return restaurantOrderList
        }

       /* fun orderBeverageDetails(): List<Beverage> {

            val beverageList = mutableListOf<Beverage>()

            beverageList.addBeverage(Beverage("https://i1.wp.com/www.wallpapersbyte.com/wp-content/uploads/2015/06/Jack-Daniels-Whiskey-Alcohol-Bottle-and-Glass-Red-Brown-WallpapersByte-com-1366x768.jpg", "Jack Daniels", "Scotch Whisky", 55, "(250 ml)", "1", 0))
            beverageList.addBeverage(Beverage("https://images6.alphacoders.com/409/409417.jpg", "Glefiddich", "Whisky", 120, "(750 ml)", "0", 0))
            beverageList.addBeverage(Beverage("https://drinks-dvq6ncf.netdna-ssl.com/wordpress/wp-content/uploads/2016/04/mcdowells_number_1_whiskey_set.jpg", "MaDowell's", "Reserve Whisky", 40, "(750 ml)", "0", 0))

            return beverageList
        }
*/

        fun cardList(): List<Payment> {

            val cardList = mutableListOf<Payment>()
            cardList.add(Payment("xxxx xxxx xxxx 1523", "0"))
            cardList.add(Payment("xxxx xxxx xxxx 1523", "1"))
            cardList.add(Payment("xxxx xxxx xxxx 1523", "0"))
            cardList.add(Payment("xxxx xxxx xxxx 1523", "1"))

            return cardList
        }

        fun orderList(): List<OrderList> {
            val orderList = mutableListOf<OrderList>()

            // orderList.addBeverage(OrderList("123456", "Placed", "", "", myOrderList()))
            // orderList.addBeverage(OrderList("123458", "Delivered", "", "", myOrderList()))

            return orderList
        }

        fun beverageOrderList(): List<OrderList> {

            val orderList = mutableListOf<OrderList>()

            /* orderList.addBeverage(OrderList("123456", "Pending", "14 Aug 2018,", "Johny Walker,Chivas Regal", null))
             orderList.addBeverage(OrderList("123458", "Confirmed", "26 Aug 2018", "Jack Daniels", null))
             orderList.addBeverage(OrderList("123455", "Finished", "16 Aug 2018", "Jack Daniels", null))
             orderList.addBeverage(OrderList("123455", "Placed", "16 Aug 2018", "Jack Daniels", null))
             orderList.addBeverage(OrderList("123455", "Canceled", "16 Aug 2018", "Jack Daniels", null))*/

            return orderList
        }

        fun futureOrderList(): List<OrderList> {
            val orderList = mutableListOf<OrderList>()

            //orderList.addBeverage(OrderList("123456", "Placed", "14 Aug 2018, 12:40 AM", "", myOrderList()))
            //orderList.addBeverage(OrderList("123458", "Delivered", "14 Aug 2018, 12:40 AM", "", myOrderList()))

            return orderList
        }

        fun myOrderList(): List<OrderList> {
            val serviceList = mutableListOf<OrderList>()
//            serviceList.addBeverage(OrderList.MyOrderList("1", "Pizza Marina", "2", "Italian", "$200", "1", "Onion"))
//            serviceList.addBeverage(OrderList.MyOrderList("1", "Pineapple Juice", "5", "Beverages", "$100", "0", ""))
//            serviceList.addBeverage(OrderList.MyOrderList("0", "Maxican Aloo Wrap", "6", "Burger", "$500", "0", ""))
//            serviceList.addBeverage(OrderList.MyOrderList("0", "Aloo Tikki", "2", "Burger", "$300", "0", ""))
//            serviceList.addBeverage(OrderList.MyOrderList("0", "Chicken Bagle Sandwich", "8", "Sandwich", "$50", "0", ""))
//            serviceList.addBeverage(OrderList.MyOrderList("1", "Margherita Pizza", "10", "Pizza", "$80", "0", ""))
            return serviceList
        }

        fun myOrderListChild(): List<OrderList> {
            val serviceList = mutableListOf<OrderList>()
//            serviceList.addBeverage(OrderList.MyOrderList("1", "Chicken Bagle Sandwich", "8", "Sandwich", "$50", "1", ""))
//            serviceList.addBeverage(OrderList.MyOrderList("0", "Margherita Pizza", "10", "Pizza", "$80", "0", ""))
            return serviceList
        }

        fun menuList(): List<MenuModel> {
            val menuLists = mutableListOf<MenuModel>()

            menuLists.add(MenuModel("1", "Pizza Marinara", "Italian", "55", "0", 0, "0"))
            menuLists.add(MenuModel("1", "Pineapple Juice(50% off)", "Beverages", "50", "1", 0, "25"))
            menuLists.add(MenuModel("0", "Maxican Aloo Wrap", "Burger", "55", "0", 0, "0"))
            menuLists.add(MenuModel("0", "Aloo Tikki", "Burger", "60", "0", 0, "0"))
            menuLists.add(MenuModel("0", "The Chicken Bagle Sandwich", "Sandwich", "55", "0", 0, "0"))
            menuLists.add(MenuModel("1", "Margherita Pizza(50% off)", "Pizza", "55", "1", 0, "0"))

            menuLists.add(MenuModel("1", "Pizza Marinara", "Italian", "55", "0", 0, "0"))
            menuLists.add(MenuModel("1", "Pineapple Juice(50% off)", "Beverages", "50", "1", 0, "25"))
            menuLists.add(MenuModel("0", "Maxican Aloo Wrap", "Burger", "55", "0", 0, "0"))
            menuLists.add(MenuModel("0", "Aloo Tikki", "Burger", "60", "0", 0, "0"))
            menuLists.add(MenuModel("0", "The Chicken Bagle Sandwich", "Sandwich", "55", "0", 0, "0"))
            menuLists.add(MenuModel("1", "Margherita Pizza(50% off)", "Pizza", "55", "1", 0, "0"))

            return menuLists
        }

//        fun reviewList(): List<Review> {
//            val reviewList = mutableListOf<Review>()
//
//            reviewList.addBeverage(Review("https://www.alvinailey.org/sites/default/files/styles/slideshow_image/public/melanie-person.jpg", "Greg Lewis", 4, "12 Aug, 2018", KravenCustomer.appContext.getString(R.string.long_text)))
//            reviewList.addBeverage(Review("http://cdn01.cdn.justjaredjr.com/wp-content/uploads/2011/01/callan-tv/callan-mcauliffe-tv-person-03.jpg", "Greg Lewis", 4, "12 Aug, 2018", KravenCustomer.appContext.getString(R.string.long_text)))
//            reviewList.addBeverage(Review("http://cdn.collider.com/wp-content/uploads/person-of-interest-taraji-p-henson-4.jpg", "Greg Lewis", 4, "12 Aug, 2018", KravenCustomer.appContext.getString(R.string.long_text)))
//            reviewList.addBeverage(Review("https://tribzap2it.files.wordpress.com/2015/07/person-of-interest-season-4-sarah-shahi-cbs.jpg", "Greg Lewis", 4, "12 Aug, 2018", KravenCustomer.appContext.getString(R.string.long_text)))
//            reviewList.addBeverage(Review("https://timedotcom.files.wordpress.com/2017/12/rose-mcgowan-person-of-year-2017-time-magazine-facebook-1.jpg", "Greg Lewis", 4, "12 Aug, 2018", KravenCustomer.appContext.getString(R.string.long_text)))
//
//
//            return reviewList
//
//        }


        fun addressList(): List<SelectAddress> {
            val addressList = mutableListOf<SelectAddress>()

            addressList.add(SelectAddress("Home", "51 Butternut Lane Harrisburg, IL 629546"))
            addressList.add(SelectAddress("Work", "51 Butternut Lane Harrisburg, IL 629546"))
            addressList.add(SelectAddress("Office", "51 Butternut Lane Harrisburg, IL 629546"))
            return addressList

        }


        fun service(): Service {
            return Service(serviceList(), getResturantList())
        }


        private fun serviceList(): List<Service.Services> {
            val serviceList = mutableListOf<Service.Services>()
            serviceList.add(Service.Services(src = R.drawable.order_food, name = "Order Food"))
            serviceList.add(Service.Services(src = R.drawable.future_food_order, name = "Future Food Order"))
            serviceList.add(Service.Services(src = R.drawable.order_beverage, name = "Order Beverage"))
            serviceList.add(Service.Services(src = R.drawable.restaurant_reservation, name = "Restaurant Reservation"))
            serviceList.add(Service.Services(src = R.drawable.portable_bar_rental, name = "Portable Bar Rental"))
            serviceList.add(Service.Services(src = R.drawable.bartender_service, name = "Bartender Service"))

            return serviceList
        }

        private fun getResturantList(): List<Service.Restaurant> {
            val serviceList = mutableListOf<Service.Restaurant>()
            serviceList.add(Service.Restaurant("https://cdn.guidingtech.com/imager/media/assets/HD-Mouth-Watering-Food-Wallpapers-for-Desktop-12_acdb3e4bb37d0e3bcc26c97591d3dd6b.jpg", "Basic Kneads Pizza", "Appetizers", 3, "0", "(500 Reviews)", 1, "Available", "Delivery & Pickup"))
            serviceList.add(Service.Restaurant("https://images.all-free-download.com/images/graphicthumb/food_picture_05_hd_picture_167519.jpg", "Jassi De Parathe", "Appetizers", 3, "1", "(500 Reviews)", 0, "Available", "Delivery Only"))
            serviceList.add(Service.Restaurant("https://images.pexels.com/photos/461198/pexels-photo-461198.jpeg", "Iscon Ganthiya", "Appetizers", 3, "0", "(485 Reviews)", 0, "Available", "Pickup Only"))
            serviceList.add(Service.Restaurant("https://wallpaper.wiki/wp-content/uploads/2017/04/wallpaper.wiki-HD-delicious-food-photos-PIC-WPD009107.jpg", "Magic Chicken", "Appetizers", 3, "1", "(285 Reviews)", 0, "Available", "Delivery & Pickup"))
            serviceList.add(Service.Restaurant("https://www.pixelstalk.net/wp-content/uploads/2016/08/Desktop-Food-HD-Wallpapers-Free-Download-620x388.jpg", "Basic Kneads Pizza", "Appetizers", 3, "0", "(555 Reviews)", 0, "Currently Unavailable", "Delivery & Pickup"))
            serviceList.add(Service.Restaurant("https://images.alphacoders.com/862/862638.jpg", "Basic Kneads Pizza", "Appetizers", 3, "0", "(500 Reviews)", 0, "Closed", "Delivery & Pickup"))


            return serviceList
        }

        fun getPortableBarList(): List<PortableBar> {
            val getPortableBarList = mutableListOf<PortableBar>()
            getPortableBarList.add(PortableBar("https://www.badgercase.com/wp-content/uploads/2018/05/Portable-bar-ATA-case.jpg"))
            getPortableBarList.add(PortableBar("https://cdn.shopify.com/s/files/1/1190/7174/products/Bamboo_White_Back_2048x.jpg"))

            return getPortableBarList
        }

        fun getPortableBarOrderList(): List<PortableOrderList> {
            val getPortableBarOrderList = mutableListOf<PortableOrderList>()
            getPortableBarOrderList.add(PortableOrderList("https://www.badgercase.com/wp-content/uploads/2018/05/Portable-bar-ATA-case.jpg", "123456", "Pending", "16 Aug 2018, 12:00 AM", "215"))
            getPortableBarOrderList.add(PortableOrderList("https://cdn.shopify.com/s/files/1/1190/7174/products/Bamboo_White_Back_2048x.jpg", "123455", "Confirmed", "14 Aug 2018, 10:00 AM", "205"))


            return getPortableBarOrderList
        }

        fun getBartenderOrderList(): List<BartenderOrderList> {
            val getBartenderOrderList = mutableListOf<BartenderOrderList>()

            getBartenderOrderList.add(BartenderOrderList("123456", "Pending", "16 Aug 2018, 12:00 AM", "215"))
            getBartenderOrderList.add(BartenderOrderList("123455", "Accepted", "14 Aug 2018, 10:00 AM", "205"))
            return getBartenderOrderList
        }


        fun getOfferList(): List<MyOffer> {
            val getOfferList = mutableListOf<MyOffer>()
            getOfferList.add(MyOffer("https://images.all-free-download.com/images/graphicthumb/food_picture_03_hd_pictures_167556.jpg", "20 % OFF ON ALL FOOD ITEMS Valid for over $50"))
            getOfferList.add(MyOffer("https://images.pexels.com/photos/958545/pexels-photo-958545.jpeg?auto=compress&cs=tinysrgb&h=350", "20 % OFF ON ALL FOOD ITEMS Valid for over 50"))
            getOfferList.add(MyOffer("https://cdn.guidingtech.com/imager/media/assets/HD-Mouth-Watering-Food-Wallpapers-for-Desktop-12_acdb3e4bb37d0e3bcc26c97591d3dd6b.jpg", "20 % OFF ON ALL FOOD ITEMS Valid for over 50"))
            getOfferList.add(MyOffer("https://images.all-free-download.com/images/graphicthumb/food_picture_01_hd_pictures_167558.jpg", "20 % OFF ON ALL FOOD ITEMS Valid for over 50"))

            getOfferList.add(MyOffer("https://images.all-free-download.com/images/graphicthumb/food_picture_03_hd_pictures_167556.jpg", "20 % OFF ON ALL FOOD ITEMS Valid for over $50"))
            getOfferList.add(MyOffer("https://images.pexels.com/photos/958545/pexels-photo-958545.jpeg?auto=compress&cs=tinysrgb&h=350", "20 % OFF ON ALL FOOD ITEMS Valid for over 50"))
            getOfferList.add(MyOffer("https://cdn.guidingtech.com/imager/media/assets/HD-Mouth-Watering-Food-Wallpapers-for-Desktop-12_acdb3e4bb37d0e3bcc26c97591d3dd6b.jpg", "20 % OFF ON ALL FOOD ITEMS Valid for over 50"))
            getOfferList.add(MyOffer("https://images.all-free-download.com/images/graphicthumb/food_picture_01_hd_pictures_167558.jpg", "20 % OFF ON ALL FOOD ITEMS Valid for over 50"))

            return getOfferList
        }

       /* fun getNotificationList(): List<Notifications> {
            val getNotificationList = mutableListOf<Notifications>()

            getNotificationList.addBeverage(Notifications("123456", "for table booking request accepted.", "14 Aug 2018, 12:00 AM"))
            getNotificationList.addBeverage(Notifications("123456", "order is on the way.", "14 Aug 2018, 12:00 AM"))
            getNotificationList.addBeverage(Notifications("123456", "for Beverage is rejected.", "14 Aug 2018, 12:00 AM"))
            getNotificationList.addBeverage(Notifications("123456", "$50 refunded for Beverage cancel order.", "14 Aug 2018, 12:00 AM"))

            getNotificationList.addBeverage(Notifications("123456", "for table booking request accepted.", "14 Aug 2018, 12:00 AM"))
            getNotificationList.addBeverage(Notifications("123456", "order is on the way.", "14 Aug 2018, 12:00 AM"))
            getNotificationList.addBeverage(Notifications("123456", "for Beverage is rejected.", "14 Aug 2018, 12:00 AM"))
            getNotificationList.addBeverage(Notifications("123456", "$50 refunded for Beverage cancel order.", "14 Aug 2018, 12:00 AM"))

            return getNotificationList
        }*/


        fun getToppingTitleList(): List<Topping> {

            val filterList = mutableListOf<Topping>()
            filterList.add(Topping("Extra"))
            filterList.add(Topping("Topping"))
            filterList.add(Topping("Dips"))

            return filterList
        }

        /*fun getToppingTitleList(): List<Topping> {

            val filterList = mutableListOf<Topping>()
            filterList.addBeverage(Topping("Extra")
            filterList.addBeverage(Topping("Topping")
            filterList.addBeverage(Topping("Dips")

            return filterList
        }

        fun toppingList(position: Int): List<Topping.ToppingName> {
            val toppingList = mutableListOf<Topping.ToppingName>()
            when (position) {
                0 -> toppingList.addBeverage(Topping.ToppingName("Extra Cheese", "40", false))
                1 -> {
                    toppingList.addBeverage(Topping.ToppingName("Onion", "30", false))
                    toppingList.addBeverage(Topping.ToppingName("Extra Butter", "25", false))
                    toppingList.addBeverage(Topping.ToppingName("Tomato", "10", false))
                }
                2 -> {
                    toppingList.addBeverage(Topping.ToppingName("Cabbage", "20", false))
                    toppingList.addBeverage(Topping.ToppingName("Onion", "22", false))
                    toppingList.addBeverage(Topping.ToppingName("Extra Cheese", "35", false))
                    toppingList.addBeverage(Topping.ToppingName("Extra Butter", "18", false))
                    toppingList.addBeverage(Topping.ToppingName("Tomato", "27", false))
                }
                3 -> {
                    toppingList.addBeverage(Topping.ToppingName("Cabbage", "34", false))
                    toppingList.addBeverage(Topping.ToppingName("Onion", "27", false))
                    toppingList.addBeverage(Topping.ToppingName("Extra Cheese", "08", false))
                }
            }




            return toppingList
        }*/

        fun getCayList(): List<Cay> {

            val cayList = mutableListOf<Cay>()
            cayList.add(Cay("1", "Exuma, The Bahamas  ", "30", "23.61925980", "-75.96954650", "Active", "2021-06-22T04:22:09.000Z"))
            cayList.add(Cay("2", "W Bay St, Nassau, The Bahamas", "42.23", "25.07827580", "-77.34340500", "Active", "2021-06-22T04:26:26.000Z"))
            return cayList
        }
    }


}