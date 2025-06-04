package com.uvk.shramapplication.ui.home.job

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.repository.UserAuthRepository
import com.uvk.shramapplication.ui.home.HomeJobResponse
import com.uvk.shramapplication.ui.home.newhome.GotJobResponse
import kotlinx.coroutines.launch

class JobListViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepo = UserAuthRepository()
    val jobListResult: MutableLiveData<BaseResponse<HomeJobResponse>> = MutableLiveData()
    val gotJobListResult: MutableLiveData<BaseResponse<GotJobResponse>> = MutableLiveData()
    val homeGotJobListResult: MutableLiveData<BaseResponse<GotJobResponse>> = MutableLiveData()
    val getAvailableJobListResult: MutableLiveData<BaseResponse<GotJobResponse>> = MutableLiveData()
    // val jobDetailsResult: MutableLiveData<BaseResponse<HomeJobResponse>> = MutableLiveData()


    fun getAvailableJobList(
        user_id: String,
        main_category_id: String,
        keyword: String,
        state_id: String,
        district_id: String
    ) {

        getAvailableJobListResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getAvailableJobList(
                    userId = user_id,
                    main_category_id = main_category_id,
                    keyword = keyword,
                    state_id = state_id,
                    district_id = district_id
                )

                if (response?.code() == 200) {
                    getAvailableJobListResult.value = BaseResponse.Success(response.body())
                } else {
                    getAvailableJobListResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                getAvailableJobListResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun getGotJobList() {

        gotJobListResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getGotJobList()

                if (response?.code() == 200) {
                    gotJobListResult.value = BaseResponse.Success(response.body())
                } else {
                    gotJobListResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                gotJobListResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun getHomeGotJobList() {

        homeGotJobListResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getGotJobList()

                if (response?.code() == 200) {
                    homeGotJobListResult.value = BaseResponse.Success(response.body())
                } else {
                    homeGotJobListResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                homeGotJobListResult.value = BaseResponse.Error(ex.message)
            }
        }
    }


    /*fun jobDetails(jobId: String) {

         jobDetailsResult.value = BaseResponse.Loading()
         viewModelScope.launch {
             try {

                 val response = userRepo.getJobDetails(
                     job_id = jobId
                 )

                 if (response?.code() == 200) {
                     jobDetailsResult.value = BaseResponse.Success(response.body())
                 } else {
                     jobDetailsResult.value = BaseResponse.Error(response?.message())
                 }

             } catch (ex: Exception) {
                 jobDetailsResult.value = BaseResponse.Error(ex.message)
             }
         }
     }*/


}