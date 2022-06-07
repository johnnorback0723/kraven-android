package com.kraven.ui.review.fragment

import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kraven.R
import com.kraven.core.Common
import com.kraven.core.StatusCode
import com.kraven.coreadapter.EndlessRecyclerViewScrollListener
import com.kraven.data.pojo.Parameters
import com.kraven.di.component.FragmentComponent
import com.kraven.ui.address.model.Address
import com.kraven.ui.base.BaseFragment
import com.kraven.ui.home.viewmodel.HomeViewModel
import com.kraven.ui.review.adapter.ReviewAdapter
import com.kraven.utils.ConstantApp
import kotlinx.android.synthetic.main.review_fragment.*
import java.lang.IllegalStateException

class ReviewFragment : BaseFragment() {
    var reviewAdapter: ReviewAdapter? = null
    private lateinit var viewModel: HomeViewModel
    private var pages = 1


    private val vendorId: String? by lazy {
        val args = arguments ?: throw  IllegalStateException("Missing arguments")
        args.getString(Common.ID)
    }
    private val vendorType: String? by lazy {
        val args = arguments ?: throw  IllegalStateException("Missing arguments")
        args.getString(Common.VENDORTYPE)
    }

    override fun createLayout(): Int = R.layout.review_fragment

    override fun inject(fragmentComponent: FragmentComponent) = fragmentComponent.inject(this)

    override fun bindData() {
        toolbar.showToolbar(true)
        toolbar.setToolbarTitle(getString(R.string.review))
        toolbar.setToolbarButton(true)
        toolbar.setButtonTextVisibility(false)

        setUpRecyclerView()
        viewModel = ViewModelProviders.of(this, viewModelFactory)[HomeViewModel::class.java]
        val parameters = Parameters()
        parameters.vendorId = vendorId
        parameters.vendorType = if (vendorType == ConstantApp.PassValue.ORDER_FOOD) ConstantApp.RESTAUTANT else ConstantApp.BEVERAGE
        parameters.page = pages.toString()
        viewModel.reviews(parameters)

        viewModel.reviews.observe(this, { responseBody ->
            showLoader(false)
            when (responseBody.code) {
                StatusCode.CODE_SUCCESS -> {
                    reviewAdapter!!.setItems(responseBody.data,pages)
                }
                StatusCode.CODE_NO_DATA -> {
                    reviewAdapter!!.errorMessage = "Review list not found"
                }
                else -> showMessage(responseBody.message)
            }
        }) { true }
    }

    private fun setUpRecyclerView() {
        recyclerViewReview.layoutManager = LinearLayoutManager(activity)
        reviewAdapter = ReviewAdapter()
        recyclerViewReview.adapter = reviewAdapter

        recyclerViewReview.addOnScrollListener(object: EndlessRecyclerViewScrollListener(recyclerViewReview.layoutManager as LinearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                pages =page
                val parameters = Parameters()
                parameters.vendorId = vendorId
                parameters.vendorType = if (vendorType == ConstantApp.PassValue.ORDER_FOOD) ConstantApp.RESTAUTANT else ConstantApp.BEVERAGE
                parameters.page = pages.toString()
                viewModel.reviews(parameters)
            }
        })
    }
}