package com.kraven.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kraven.di.ViewModelKey
import com.kraven.ui.authentication.viewmodel.AuthViewModel
import com.kraven.ui.cart.viewModel.CartViewModel
import com.kraven.ui.favorite.viewmodel.FavoriteViewModel
import com.kraven.ui.home.viewmodel.HomeViewModel
import com.kraven.ui.my.order.viewModel.OrderViewModel
import com.kraven.ui.order.beverage.SpecialBeverageViewModel
import com.kraven.ui.payment.viewmodel.PaymentViewModel
import com.kraven.ui.review.viewmodel.RatingViewModel
import com.kraven.ui.setting.SettingViewModel
import com.kraven.ui.track.viewmodel.TrackViewModel
import com.kraven.ui.viewmodel.MapLocationViewModel
import com.kraven.ui.viewmodel.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(SettingViewModel::class)
    abstract fun bindSettingViewModel(settingViewModel: SettingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun bindLoginViewModel(authViewModel: AuthViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(homeViewModel: HomeViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(CartViewModel::class)
    abstract fun bindCartViewModel(cartViewModel: CartViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TrackViewModel::class)
    abstract fun bindTrackViewModel(trackViewModel: TrackViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FavoriteViewModel::class)
    abstract fun bindFavoriteViewModel(favoriteViewModel: FavoriteViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(RatingViewModel::class)
    abstract fun bindRatingViewModel(ratingViewModel: RatingViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(OrderViewModel::class)
    abstract fun bindOrderViewModel(cartViewModel: OrderViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MapLocationViewModel::class)
    abstract fun bindMapLocationViewModel(mapLocationViewModel: MapLocationViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(PaymentViewModel::class)
    abstract fun bindPaymentViewModel(paymentViewModel: PaymentViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(SpecialBeverageViewModel::class)
    abstract fun bindSpecialBeverageViewModel(specialBeverageViewModel: SpecialBeverageViewModel):ViewModel


    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory



}