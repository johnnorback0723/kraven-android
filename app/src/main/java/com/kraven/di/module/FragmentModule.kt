package com.kraven.di.module

import com.kraven.data.pojo.Parameters
import com.kraven.di.PerFragment
import com.kraven.ui.base.BaseFragment
import dagger.Module
import dagger.Provides

/**
 * Created by hlink21 on 31/5/16.
 */
@Module
class FragmentModule(private val baseFragment: BaseFragment) {

    @Provides
    @PerFragment
    internal fun provideBaseFragment(): BaseFragment {
        return baseFragment
    }

    @Provides
    internal fun provideParameters(): Parameters {
        return Parameters()
    }

}
