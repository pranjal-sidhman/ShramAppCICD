package com.uvk.shramapplication.ui.registration

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.repository.UserAuthRepository
import com.uvk.shramapplication.response.DistrictApiResponse
import com.uvk.shramapplication.response.StateApiResponse
import kotlinx.coroutines.launch

class StateDTViewModel (application: Application, ) : AndroidViewModel(application) {

    private val userRepo = UserAuthRepository()

    val stateresult: MutableLiveData<BaseResponse<StateApiResponse>> = MutableLiveData()
    val districtresult: MutableLiveData<BaseResponse<DistrictApiResponse>> = MutableLiveData()


    fun State() {
        stateresult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getState(

                )
                if (response?.code() == 200) {
                    stateresult.value = BaseResponse.Success(response.body())
                } else {
                    stateresult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                stateresult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun district(STATE_ID: Int) {

        districtresult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getDistrict(
                    STATE_ID = STATE_ID
                )

                if (response?.code() == 200) {

                    districtresult.value = BaseResponse.Success(response.body())
                } else {
                    districtresult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                districtresult.value = BaseResponse.Error(ex.message)
            }
        }
    }
}