package com.kraven.di.component


import com.kraven.di.PerActivity
import com.kraven.di.module.ActivityModule
import com.kraven.di.module.FragmentModule
import com.kraven.ui.activity.*
import com.kraven.ui.base.BaseActivity
import com.kraven.ui.manager.Navigator

import dagger.BindsInstance
import dagger.Component

/**
 * Created by hlink21 on 9/5/16.
 */
@PerActivity
@Component(dependencies = [ApplicationComponent::class], modules = [ActivityModule::class])
interface ActivityComponent {

    fun activity(): BaseActivity

    fun navigator(): Navigator


    operator fun plus(fragmentModule: FragmentModule): FragmentComponent

    fun inject(mainActivity: AuthenticationActivity)
    fun inject(mainActivity: IsolatedActivity)
    fun inject(homeActivity: HomeActivity)
    fun inject(mapLocationActivity: MapLocationActivity)


    @Component.Builder
    interface Builder {

        fun bindApplicationComponent(applicationComponent: ApplicationComponent): Builder

        @BindsInstance
        fun bindActivity(baseActivity: BaseActivity): Builder

        fun build(): ActivityComponent
    }
}
