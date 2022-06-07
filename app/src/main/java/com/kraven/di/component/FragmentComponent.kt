package com.kraven.di.component


import com.kraven.di.PerFragment
import com.kraven.di.module.FragmentModule
import com.kraven.ui.address.fragment.AddressFragment
import com.kraven.ui.address.fragment.AddressListFragment
import com.kraven.ui.authentication.fragement.*
import com.kraven.ui.bartender.fragment.BartenderOrderDetailAcceptedFragment
import com.kraven.ui.bartender.fragment.BartenderOrderDetailPendingFragment
import com.kraven.ui.bartender.fragment.BartenderServiceAcceptedFragment
import com.kraven.ui.bartender.fragment.BartenderServiceFragment
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.cart.fragment.BaseCartFragment
import com.kraven.ui.cart.fragment.CartFragment
import com.kraven.ui.cart.fragment.CompleteOrderCartFragment
import com.kraven.ui.cart.fragment.SelectAddressFragment
import com.kraven.ui.comman.fragment.DrawerFragment
import com.kraven.ui.edit.profile.fragment.ChangePasswordFragment
import com.kraven.ui.edit.profile.fragment.EditProfileFragment
import com.kraven.ui.edit.profile.fragment.SavedCardsFragment
import com.kraven.ui.favorite.fragment.FavoriteBeverageFragment
import com.kraven.ui.favorite.fragment.FavoriteFragment
import com.kraven.ui.favorite.fragment.FavoriteRestaurantFragment
import com.kraven.ui.future.food.order.fragment.FutureFoodOrderFragment
import com.kraven.ui.future.food.order.fragment.SelectVendorFragment
import com.kraven.ui.home.fragment.BeverageSearchScreen
import com.kraven.ui.home.fragment.HomeFragment
import com.kraven.ui.home.fragment.HomeNewFragment
import com.kraven.ui.home.fragment.RestaurantDetailsFragment
import com.kraven.ui.menu.fragment.FoodMenuFragment
import com.kraven.ui.menu.fragment.MenuListFragment
import com.kraven.ui.my.offer.fragment.MyOfferFragment
import com.kraven.ui.my.order.fragment.*
import com.kraven.ui.notification.fragment.NotificationFragment
import com.kraven.ui.order.beverage.fragment.*
import com.kraven.ui.order.food.fragment.OrderFoodFilterFragment
import com.kraven.ui.order.food.fragment.OrderFoodFragment
import com.kraven.ui.order.food.fragment.SubBranchFragment
import com.kraven.ui.payment.fragment.*
import com.kraven.ui.portable.bar.rental.fragment.PortableBarRentalFragment
import com.kraven.ui.portable.bar.rental.fragment.PortableOrderDetailsFragment
import com.kraven.ui.rating.RatingFragment
import com.kraven.ui.rating.RatingOrderBeverageFragment
import com.kraven.ui.rating.RatingOrderHistoryFragment
import com.kraven.ui.refer.friend.ReferFriendsFragment
import com.kraven.ui.restaurant.reservation.fragement.RestaurantBookTableFragment
import com.kraven.ui.restaurant.reservation.fragement.RestaurantOrderDetails
import com.kraven.ui.restaurant.reservation.fragement.RestaurantReservationDetailsFragment
import com.kraven.ui.restaurant.reservation.fragement.RestaurantReservationFragment
import com.kraven.ui.review.fragment.ReviewFragment
import com.kraven.ui.setting.fragment.SelectTitleFragment
import com.kraven.ui.setting.fragment.SettingFragment
import com.kraven.ui.setting.fragment.SupportUsFragment
import com.kraven.ui.track.fragment.TrackGoogleMapBeverageFragment
import com.kraven.ui.track.fragment.TrackGoogleMapFragment
import com.kraven.ui.track.fragment.TrackOrderBeverageFragment
import com.kraven.ui.track.fragment.TrackOrderFragment
import com.kraven.ui.wallet.fragment.MyWalletFragment
import dagger.Subcomponent

/**
 * Created by hlink21 on 31/5/16.
 */

@PerFragment
@Subcomponent(modules = [(FragmentModule::class)])
interface FragmentComponent {
    fun baseFragment(): BaseFragment
    fun inject(loginFragment: LoginFragment)
    fun inject(loginFragment: SplashFragment)
    fun inject(forgotPasswordFragment: ForgotPasswordFragment)
    fun inject(signUpFragment: SignUpFragment)
    fun inject(countryCodeFragment: CountryCodeFragment)
    fun inject(addressFragment: AddressFragment)
    fun inject(verificationFragment: VerificationFragment)
    fun inject(homeFragment: HomeFragment)
    fun inject(drawerFragment: DrawerFragment)
    fun inject(restaurantDetailsFragment: RestaurantDetailsFragment)
    fun inject(foodMenu: FoodMenuFragment)
    fun inject(menuListFragment: MenuListFragment)
    fun inject(reviewFragment: ReviewFragment)
    fun inject(cartFragment: CartFragment)
    fun inject(selectAddressFragment: SelectAddressFragment)
    fun inject(paymentFragment: PaymentFragment)
    fun inject(waitingScreenFragment: WaitingScreenFragment)
    fun inject(trackOrderFragment: TrackOrderFragment)
    fun inject(trackGoogleMapFragment: TrackGoogleMapFragment)
    fun inject(ratingFragment: RatingFragment)
    fun inject(myOrderHistoryFragment: MyOrderHistoryFragment)
    fun inject(myOrderDetailsFragment: MyOrderDetailsFragment)
    fun inject(orderFoodFragment: OrderFoodFragment)
    fun inject(futureFoodOrderListFragment: FutureFoodOrderListFragment)
    fun inject(selectVendorFragment: SelectVendorFragment)
    fun inject(beverageListFragment: BeverageListFragment)
    fun inject(beverageCartFragment: BeverageCartFragment)
    fun inject(specialOrderFragment: SpecialOrderFragment)
    fun inject(orderDetailsNoramlFragment: OrderDetailsNoramlFragment)
    fun inject(orderDetailsPendingFragment: OrderDetailsPendingFragment)
    fun inject(orderDetailsConfirmedFragment: OrderDetailsConfirmedFragment)
    fun inject(restaurantReservationFragment: RestaurantReservationFragment)
    fun inject(restaurantReservationDetailsFragment: RestaurantReservationDetailsFragment)
    fun inject(restaurantBookTableFragment: RestaurantBookTableFragment)
    fun inject(restaurantOrderDetails: RestaurantOrderDetails)
    fun inject(portableBarRentalFragment: PortableBarRentalFragment)
    fun inject(portableOrderDetailsFragment: PortableOrderDetailsFragment)
    fun inject(bartenderServiceFragment: BartenderServiceFragment)
    fun inject(bartenderOrderDetailPendingFragment: BartenderOrderDetailPendingFragment)
    fun inject(bartenderOrderDetailAcceptedFragment: BartenderOrderDetailAcceptedFragment)
    fun inject(myWalletFragment: MyWalletFragment)
    fun inject(myOfferFragment: MyOfferFragment)
    fun inject(referFriends: ReferFriendsFragment)
    fun inject(notificationFragment: NotificationFragment)
    fun inject(settingFragment: SettingFragment)
    fun inject(supportUsFragment: SupportUsFragment)
    fun inject(editProfileFragment: EditProfileFragment)
    fun inject(changePasswordFragment: ChangePasswordFragment)
    fun inject(favoriteRestaurantFragment: FavoriteRestaurantFragment)
    fun inject(orderFoodFilterFragment: OrderFoodFilterFragment)
    fun inject(bartenderServiceAcceptedFragment: BartenderServiceAcceptedFragment)
    fun inject(addressListFragment: AddressListFragment)
    fun inject(beverageStoreListFragment: BeverageStoreListFragment)
    fun inject(termsAndConditionsFragment: TermsAndConditionsFragment)
    fun inject(beverageDetailsFragment: BeverageDetailsFragment)
    fun inject(beverageMenuListFragment: BeverageMenuListFragment)
    fun inject(completeOrderCartFragment: CompleteOrderCartFragment)
    fun inject(orderFoodListFragment: OrderFoodListFragment)
    fun inject(bartenderServiceListFragment: BartenderServiceListFragment)
    fun inject(orderBeverageListFragment: OrderBeverageListFragment)
    fun inject(portableBarRentalListFragment: PortableBarRentalListFragment)
    fun inject(restaurantReservationListFragment: RestaurantReservationListFragment)
    fun inject(futureFoodOrderFragment: FutureFoodOrderFragment)
    fun inject(orderDetailsBeverageFragment: OrderDetailsBeverageFragment)
    fun inject(favoriteBeverageFragment: FavoriteBeverageFragment)
    fun inject(favoriteFragment: FavoriteFragment)
    fun inject(ratingOrderHistoryFragment: RatingOrderHistoryFragment)
    fun inject(trackOrderBeverageFragment: TrackOrderBeverageFragment)
    fun inject(trackGoogleMapBeverageFragment: TrackGoogleMapBeverageFragment)
    fun inject(ratingOrderBeverageFragment: RatingOrderBeverageFragment)
    fun inject(baseCartFragment: BaseCartFragment)
    fun inject(verificationFragmentEdit: VerificationFragmentEdit)
    fun inject(selectTitleFragment: SelectTitleFragment)
    fun inject(selectBeverageFragment: SelectBeverageFragment)
    fun inject(paymentWebPageFragment: PaymentWebPageFragment)
    fun inject(paymentWalletFragment: PaymentWalletFragment)
    fun inject(paymentCartAddFragment: PaymentCartAddFragment)
    fun inject(specialBeverageListFragment: SpecialBeverageListFragment)
    fun inject(specialBeverageDetailsFragment: SpecialBeverageDetailsFragment)
    fun inject(beverageSearchScreen: BeverageSearchScreen)
    fun inject(cardPaymentWebView: CardPaymentWebView)
    fun inject(homeNewFragment: HomeNewFragment)
    fun inject(savedCardsFragment: SavedCardsFragment)
    fun inject(walletCardPaymentWebView: WalletCardPaymentWebView)
    fun inject(selectIslandFragment: SelectIslandFragment)
    fun inject(subBranchFragment: SubBranchFragment)
}
