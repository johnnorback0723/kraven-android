package com.kraven.ui.bartender.fragment



import com.kraven.R
import com.kraven.di.component.FragmentComponent
import com.kraven.ui.activity.HomeActivity
import com.kraven.ui.base.BaseFragment
import com.kraven.utils.TextDecorator
import kotlinx.android.synthetic.main.bartender_service_accepted_fragment.*

class BartenderServiceAcceptedFragment : BaseFragment() {

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun createLayout(): Int = R.layout.bartender_service_accepted_fragment

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle("")
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        textViewContinue.setOnClickListener {
            navigator.loadActivity(HomeActivity::class.java).byFinishingAll().start()
        }
        TextDecorator.decorate(textViewOrderPlaced, resources.getString(R.string.your_order_was_successfully_placed))
                .apply {
                    setAbsoluteSize(resources.getDimensionPixelSize(R.dimen._15ssp), "A KRAVEN representative will contact you.")
                }
                .build()

    }
}