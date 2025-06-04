package com.uvk.shramapplication.ui.employeer.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.repository.UserAuthRepository
import com.uvk.shramapplication.ui.employeer.response.EmployeerRequest
import com.uvk.shramapplication.ui.employeer.response.EmployeerResponse
import com.uvk.shramapplication.ui.employeer.worklist.list.job_request_list.JobResponse
import kotlinx.coroutines.launch

class EmployeerViewModel (application: Application) : AndroidViewModel(application) {

    private val userRepo = UserAuthRepository()
    val availableEmpListResult: MutableLiveData<BaseResponse<EmployeerResponse>> = MutableLiveData()
    val availEmpListResult: MutableLiveData<BaseResponse<EmployeerResponse>> = MutableLiveData()
    val postedJobListResult: MutableLiveData<BaseResponse<EmployeerResponse>> = MutableLiveData()
    val postJobListResult: MutableLiveData<BaseResponse<EmployeerResponse>> = MutableLiveData()
    val deleteJobListResult: MutableLiveData<BaseResponse<EmployeerResponse>> = MutableLiveData()
    val getJobRequestListResult: MutableLiveData<BaseResponse<JobResponse>> = MutableLiveData()
    val getJobRequestDetailsResult: MutableLiveData<BaseResponse<JobResponse>> = MutableLiveData()
    val acceptSelectEmpResult: MutableLiveData<BaseResponse<JobResponse>> = MutableLiveData()
    val rejectSelectEmpResult: MutableLiveData<BaseResponse<JobResponse>> = MutableLiveData()
    val employerOfferCallResult: MutableLiveData<BaseResponse<JobResponse>> = MutableLiveData()
    val getJobAcceptListResult: MutableLiveData<BaseResponse<EmployeerResponse>> = MutableLiveData()
    val jobDataSpinnerResult: MutableLiveData<BaseResponse<EmployeerResponse>> = MutableLiveData()
    val getCategoryWiseEmpResult: MutableLiveData<BaseResponse<EmployeerResponse>> = MutableLiveData()
    val addSelectedEmpResult: MutableLiveData<BaseResponse<EmployeerResponse>> = MutableLiveData()
    val empDetailsResult: MutableLiveData<BaseResponse<EmployeerResponse>> = MutableLiveData()



    fun getAvailableEmpList(userId: String,
                            main_category_id :String) {

        availableEmpListResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getAvailableEmp(userId = userId,
                    main_category_id = main_category_id)

                if (response?.code() == 200) {
                    availableEmpListResult.value = BaseResponse.Success(response.body())
                } else {
                    availableEmpListResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                availableEmpListResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun getAvailEmp(userId: String,
                            main_category_id :String) {

        availEmpListResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getAvailEmp(userId = userId,
                    main_category_id = main_category_id)

                if (response?.code() == 200) {
                    availEmpListResult.value = BaseResponse.Success(response.body())
                } else {
                    availEmpListResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                availEmpListResult.value = BaseResponse.Error(ex.message)
            }
        }
    }



    fun getpostedJobList(userId:String) {

        postedJobListResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getPostedJobList(
                    userId = userId
                )

                if (response?.code() == 200) {
                    postedJobListResult.value = BaseResponse.Success(response.body())
                } else {
                    postedJobListResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                postedJobListResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun getPostJobList(userId:String) {

        postJobListResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getPostJobList(
                    userId = userId
                )

                if (response?.code() == 200) {
                    postJobListResult.value = BaseResponse.Success(response.body())
                } else {
                    postJobListResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                postJobListResult.value = BaseResponse.Error(ex.message)
            }
        }
    }



    fun getDeleteJobList(token : String,userId:String,jobId : String) {

        deleteJobListResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getDeleteJobList(
                    token = token,
                    userId = userId,
                    job_id = jobId
                )

                if (response?.code() == 200) {
                    deleteJobListResult.value = BaseResponse.Success(response.body())
                } else {
                    deleteJobListResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                deleteJobListResult.value = BaseResponse.Error(ex.message)
            }
        }
    }



    fun employerOfferCall(
        token:String,
        employer_id:String,
        employee_id:String,
        job_id:String
    ) {

        employerOfferCallResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.employerOfferCall(
                    token = token,
                    employerId = employer_id,
                    employeeId = employee_id,
                    jobId = job_id
                )

                if (response?.code() == 200) {
                    employerOfferCallResult.value = BaseResponse.Success(response.body())
                } else {
                    employerOfferCallResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                employerOfferCallResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun getJobRequestList(userId:String) {

        getJobRequestListResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getJobRequestList(
                    userId = userId
                )

                if (response?.code() == 200) {
                    getJobRequestListResult.value = BaseResponse.Success(response.body())
                } else {
                    getJobRequestListResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                getJobRequestListResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun getJobRequestDetails(user_id:String,
                             apply_job_id :String,
                             apply_status : String,
                             job_request_id: String) {

        getJobRequestDetailsResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getJobRequestDetails(
                    user_id = user_id,
                    apply_job_id = apply_job_id,
                    apply_status = apply_status,
                    job_request_id = job_request_id
                )

                if (response?.code() == 200) {
                    getJobRequestDetailsResult.value = BaseResponse.Success(response.body())
                } else {
                    getJobRequestDetailsResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                getJobRequestDetailsResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun acceptSelectEmp( token: String,
                            user_id: String,
                            employee_id: Int,
                            job_id: String) {

        acceptSelectEmpResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.acceptSelectEmp(
                    token = token,
                    user_id = user_id,
                    employee_id = employee_id,
                    job_id = job_id
                )

                if (response?.code() == 200) {
                    acceptSelectEmpResult.value = BaseResponse.Success(response.body())
                } else {
                    acceptSelectEmpResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                acceptSelectEmpResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

     fun rejectEmp( token: String,
                            user_id: String,
                            employee_id: Int,
                            job_id: String) {

        rejectSelectEmpResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.rejectEmp(
                    token = token,
                    user_id = user_id,
                    employee_id = employee_id,
                    job_id = job_id
                )

                if (response?.code() == 200) {
                    rejectSelectEmpResult.value = BaseResponse.Success(response.body())
                } else {
                    rejectSelectEmpResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                rejectSelectEmpResult.value = BaseResponse.Error(ex.message)
            }
        }
    }



    fun getJobAcceptList(userId:String) {

        getJobAcceptListResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getJobAcceptList(
                    userId = userId
                )

                if (response?.code() == 200) {
                    getJobAcceptListResult.value = BaseResponse.Success(response.body())
                } else {
                    getJobAcceptListResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                getJobAcceptListResult.value = BaseResponse.Error(ex.message)
            }
        }
    }



    fun getJobDataSpinner(userId:String) {

        jobDataSpinnerResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getJobDataSpinner(
                    userId = userId
                )

                if (response?.code() == 200) {
                    jobDataSpinnerResult.value = BaseResponse.Success(response.body())
                } else {
                    jobDataSpinnerResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                jobDataSpinnerResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

fun getCategoryWiseEmp(userId:String,
                       main_category_id : String,
                       state : String,
                       district : String,
                       keyword : String) {

        getCategoryWiseEmpResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getCategoryWiseEmp(
                    userId = userId,
                    main_category_id = main_category_id,
                    state = state,
                    district = district,
                    keyword = keyword
                )

                if (response?.code() == 200) {
                    getCategoryWiseEmpResult.value = BaseResponse.Success(response.body())
                } else {
                    getCategoryWiseEmpResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                getCategoryWiseEmpResult.value = BaseResponse.Error(ex.message)
            }
        }
    }



    fun addSelectedEmployees(
        token: String,
        user_id: String,
        job_id: Int,
        employee_ids: List<Int>
    ) {

        addSelectedEmpResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val employeerRequest = EmployeerRequest(
                    token = token,
                    user_id = user_id,
                    job_id = job_id,
                    employee_ids = employee_ids
                )

                Log.e("tag","employeerRequest : $employeerRequest")

                val response = userRepo.addSelectedEmployees(
                    token = token,
                    body = employeerRequest
                )

                if (response?.code() == 200) {
                    addSelectedEmpResult.value = BaseResponse.Success(response.body())
                } else {
                    addSelectedEmpResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                addSelectedEmpResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun getEmpDetails(userId:String) {

        empDetailsResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getEmpDetails(
                    userId = userId
                )

                if (response?.code() == 200) {
                    empDetailsResult.value = BaseResponse.Success(response.body())
                } else {
                    empDetailsResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                empDetailsResult.value = BaseResponse.Error(ex.message)
            }
        }
    }


}