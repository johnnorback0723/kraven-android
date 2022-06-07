package com.kraven.ui.setting

import com.kraven.data.repository.UserRepository
import com.kraven.ui.base.APILiveData
import com.kraven.ui.base.BaseViewModel
import com.kraven.ui.setting.model.Title
import javax.inject.Inject

class SettingViewModel @Inject constructor(private val userRepository: UserRepository) : BaseViewModel() {

    val titleList = APILiveData<Title>()

    fun getTitleList() {
        userRepository.getTitleList()
                .subscribe(withLiveData(titleList))

    }
}