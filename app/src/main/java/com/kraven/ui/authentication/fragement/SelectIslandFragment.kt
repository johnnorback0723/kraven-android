package com.kraven.ui.authentication.fragement

import android.os.Bundle
import com.kraven.R
import com.kraven.core.StatusCode
import com.kraven.coreadapter.OnRecycleItemClick
import com.kraven.data.pojo.Parameters
import com.kraven.di.component.FragmentComponent
import com.kraven.extensions.extraNotNull
import com.kraven.extensions.getViewModel
import com.kraven.ui.activity.HomeActivity
import com.kraven.ui.authentication.adapter.IslandAdapter
import com.kraven.ui.authentication.model.Island
import com.kraven.ui.authentication.viewmodel.AuthViewModel
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.home.fragment.HomeNewFragment
import com.kraven.utils.ConstantApp
import kotlinx.android.synthetic.main.fragment_select_island.*

class SelectIslandFragment : BaseFragment() {

    private val authViewModel by getViewModel<AuthViewModel>()
    private val newIsland by extraNotNull<Boolean>(ConstantApp.PassValue.NEW_ISLAND)
    private var islandAdapter: IslandAdapter? = null
    private var island: Island? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerObserver()
    }

    override fun createLayout(): Int = R.layout.fragment_select_island

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        initView()
        setOnClickListener()
    }

    private fun registerObserver() {
        authViewModel.getIslandListLiveData.observe(this, {
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    islandAdapter?.items = it.data
                    island = it.data?.find { il -> il.id.toString() == session.user?.islandId }
                }
                else -> {
                    showMessage(it.message)
                }
            }
        })

        authViewModel.setIslandLiveData.observe(this, {
            when (it.code) {
                StatusCode.CODE_SUCCESS -> {
                    session.cartModel = null
                    session.cartCount = "0"
                    session.totalPrice = 0F
                    session.saveTempParameters = null
                    session.saveOrderPage = ""
                    session.saveRestaurantName = ""
                    //session.user?.islandId = island?.id.toString()
                    if (newIsland.not()) {
                        navigator.load(HomeNewFragment::class.java).clearHistory(null).replace(false)
                    } else {
                        navigator.loadActivity(HomeActivity::class.java).byFinishingAll().start()
                    }
                }
                else -> {
                    showMessage(it.message)
                }
            }
        })
    }

    private fun initView() {
        toolbar.apply {
            showToolbar(true)
            setToolbarTitle(if (newIsland) "Select Island" else "Edit Island")
            if (newIsland.not()) {
                setToolbarButton(false)
            }else{
                setButtonVisibility(false)
            }
        }
        islandAdapter = IslandAdapter(newIsland, session.user?.islandId!!,OnRecycleItemClick<Island> { island, view ->
            this@SelectIslandFragment.island = island })
        recyclerViewIsland.adapter = islandAdapter
        authViewModel.getIslandList()

    }

    private fun setOnClickListener() {
        buttonSubmit.setOnClickListener {
            if (island == null) {
                showMessage("Please select island")
            } else {
                showLoader(true)
                authViewModel.setIsland(Parameters(islandId = island?.id.toString()))
            }
        }
    }

    override fun onBackActionPerform(): Boolean {
        if (newIsland.not()) {
            navigator.load(HomeNewFragment::class.java).clearHistory(null).replace(false)
        } else {
            requireActivity().finishAffinity()
        }
        return false
    }
}