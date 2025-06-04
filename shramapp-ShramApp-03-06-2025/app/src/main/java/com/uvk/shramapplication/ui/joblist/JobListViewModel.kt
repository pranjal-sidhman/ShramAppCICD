package com.uvk.shramapplication.ui.joblist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.repository.UserAuthRepository
import kotlinx.coroutines.launch

class JobListViewModel (application: Application) : AndroidViewModel(application) {

    private val userRepo = UserAuthRepository()
    val jobListResult: MutableLiveData<BaseResponse<JobListResponse>> = MutableLiveData()
    val jobDetailsResult: MutableLiveData<BaseResponse<JobListResponse>> = MutableLiveData()
    val applyJobResult: MutableLiveData<BaseResponse<JobListResponse>> = MutableLiveData()
    val applyJobListResult: MutableLiveData<BaseResponse<JobListResponse>> = MutableLiveData()
    val applyJobDetailsResult: MutableLiveData<BaseResponse<JobListResponse>> = MutableLiveData()
    val requestedJobListResult: MutableLiveData<BaseResponse<JobListResponse>> = MutableLiveData()
    val savedJobListResult: MutableLiveData<BaseResponse<JobListResponse>> = MutableLiveData()
    val savedJobDetailsResult: MutableLiveData<BaseResponse<JobListResponse>> = MutableLiveData()
    val savedJobResult: MutableLiveData<BaseResponse<JobListResponse>> = MutableLiveData()
    val requestAcceptResult: MutableLiveData<BaseResponse<JobListResponse>> = MutableLiveData()
    val requestRejectResult: MutableLiveData<BaseResponse<JobListResponse>> = MutableLiveData()



    fun getJobList(userId:String,
                   main_category_id:String,
                   keyword: String,
                   state_id: String,
                   district_id: String) {

        jobListResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getJobList(
                    userId = userId,
                    main_category_id = main_category_id,
                    keyword = keyword,
                    state_id = state_id,
                    district_id = district_id)

                if (response?.code() == 200) {
                    jobListResult.value = BaseResponse.Success(response.body())
                } else {
                    jobListResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                jobListResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

   fun jobDetails(jobId: String,userId : String) {

        jobDetailsResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getJobDetails(
                    job_id = jobId,
                    user_id = userId,
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
    }

    fun getApplyJobList(userId: String) {

        applyJobListResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getApplyJobList(
                    user_id = userId
                )

                if (response?.code() == 200) {
                    applyJobListResult.value = BaseResponse.Success(response.body())
                } else {
                    applyJobListResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                applyJobListResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun getApplyJobDetails(applied_job_id: String) {

        applyJobDetailsResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getApplyJobDetails(
                    applied_job_id = applied_job_id
                )

                if (response?.code() == 200) {
                    applyJobDetailsResult.value = BaseResponse.Success(response.body())
                } else {
                    applyJobDetailsResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                applyJobDetailsResult.value = BaseResponse.Error(ex.message)
            }
        }
    }



    fun getRquestedJobList(userId: String) {

        requestedJobListResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getRequestJobList(
                    user_id = userId
                )

                if (response?.code() == 200) {
                    requestedJobListResult.value = BaseResponse.Success(response.body())
                } else {
                    requestedJobListResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                requestedJobListResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun getSavedJobList(userId: String) {

        savedJobListResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getSaveJobList(
                    user_id = userId
                )

                if (response?.code() == 200) {
                    savedJobListResult.value = BaseResponse.Success(response.body())
                } else {
                    savedJobListResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                savedJobListResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun getSavedJobDetails(save_job_id: String) {

        savedJobDetailsResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getSaveJobDetails(
                    save_job_id = save_job_id
                )

                if (response?.code() == 200) {
                    savedJobDetailsResult.value = BaseResponse.Success(response.body())
                } else {
                    savedJobDetailsResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                savedJobDetailsResult.value = BaseResponse.Error(ex.message)
            }
        }
    }



    fun applyJob(token:String,userId : String, jobId: String) {

        applyJobResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.applyJob(
                    token = token,
                    user_id = userId,
                    job_id = jobId
                )

                if (response?.code() == 200) {
                    applyJobResult.value = BaseResponse.Success(response.body())
                } else {
                    applyJobResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                applyJobResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun requestAccept(token:String,receiverId : String, jobId: String) {

        requestAcceptResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.requestAccept(
                    token = token,
                    receiver_id = receiverId,
                    job_id = jobId
                )

                if (response?.code() == 200) {
                    requestAcceptResult.value = BaseResponse.Success(response.body())
                } else {
                    requestAcceptResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                requestAcceptResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun requestReject(token:String,receiverId : String, jobId: String) {

        requestRejectResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.requestReject(
                    token = token,
                    receiver_id = receiverId,
                    job_id = jobId
                )

                if (response?.code() == 200) {
                    requestRejectResult.value = BaseResponse.Success(response.body())
                } else {
                    requestRejectResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                requestRejectResult.value = BaseResponse.Error(ex.message)
            }
        }
    }



    fun saveJob(token:String,userId : String, jobId: String) {

        savedJobResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.saveJob(
                    token = token,
                    user_id = userId,
                    job_id = jobId
                )

                if (response?.code() == 200) {
                    savedJobResult.value = BaseResponse.Success(response.body())
                } else {
                    savedJobResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                savedJobResult.value = BaseResponse.Error(ex.message)
            }
        }
    }


}