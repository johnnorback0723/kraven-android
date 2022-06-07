package com.kraven.data.datasource


import com.kraven.data.pojo.DataWrapper
import com.kraven.data.pojo.Parameters
import com.kraven.data.repository.CommonRepository
import com.kraven.data.service.CommonService
import io.reactivex.Single
import javax.inject.Inject


/**
 * Created by Android Developer on 3/5/19.
 */
class CommonDataSource @Inject constructor(private val commonService: CommonService) : BaseDataSource(), CommonRepository {
    override fun updateLocation(parameters: Parameters): Single<DataWrapper<Any>> {
        return execute(commonService.updateDriverLatLng(parameters))
    }
}