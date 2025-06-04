package com.uvk.shramapplication.ui.registration

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.repository.UserAuthRepository
import com.uvk.shramapplication.ui.employeer.worklist.list.job_request_list.JobResponse
import com.uvk.shramapplication.ui.employeer.worklist.list.job_request_list.UpdateStatusRequest
import com.uvk.shramapplication.ui.employeer.worklist.list.job_request_list.UpdateStatusResponse
import kotlinx.coroutines.launch

class SignupViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepo = UserAuthRepository()
    val signUpResult: MutableLiveData<BaseResponse<SignUpResponse>> = MutableLiveData()
    val addAvailabilityResult: MutableLiveData<BaseResponse<SignUpResponse>> = MutableLiveData()
    val availableStatusResult: MutableLiveData<BaseResponse<SignUpResponse>> = MutableLiveData()
    val dialogJobListResult: MutableLiveData<BaseResponse<JobResponse>> = MutableLiveData()
    val updateFinalEmployeeStatusResult: MutableLiveData<BaseResponse<UpdateStatusResponse>> =
        MutableLiveData()


    fun uploadSignupData(
        name: String,
        mobile_no: String,
        email: String,
        address: String,
        state: Int,
        district: Int,
        company_name: String,
        pincode: String,
        gender_id: Int,
        education_id: Int,
        experience: String,
        main_category_id: String,
        category_ids: List<Int>,
        sub_category_ids: List<Int>,
        designation: String,
        skill: String,
        role: String,
        profile_image: String,
        aadhar_image: String,
        other_category_name: String
       // other_category_name: List<String>
    ) {

        signUpResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val signUpRequest = SignUpRequest(
                    profile_image = profile_image,
                    name = name,
                    mobile_no = mobile_no,
                    email = email,
                    address = address,
                    state = state,
                    district = district,
                    pincode = pincode,
                    gender_id = gender_id,
                    education_id = education_id,
                    experience = experience,
                    main_category_id = main_category_id,
                    category_id = category_ids,
                    sub_category_id = sub_category_ids,
                    skill = skill,
                    role = role,
                    company_name = company_name,
                    designation = designation,
                    aadhar_image = aadhar_image,
                    other_category_name = other_category_name
                )


                val response = userRepo.signUpData(
                    body = signUpRequest
                )


                if (response?.code() == 200) {
                    signUpResult.value = BaseResponse.Success(response.body())
                    //  Log.e("tag","Sign Up request : $signUpRequest")
                } else {
                    signUpResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                signUpResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun addAvailability(token: String, user_id: String, available_status: String) {

        addAvailabilityResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.addAvailability(
                    token = token,
                    user_id = user_id,
                    available_status = available_status
                )

                if (response?.code() == 200) {
                    addAvailabilityResult.value = BaseResponse.Success(response.body())
                } else {
                    addAvailabilityResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                addAvailabilityResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun getAvailableStatus(user_id: String) {

        availableStatusResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getAvailabilityStatus(userId = user_id)

                if (response?.code() == 200) {
                    availableStatusResult.value = BaseResponse.Success(response.body())
                } else {
                    availableStatusResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                availableStatusResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun getJobRequestDialog(user_id: String) {

        dialogJobListResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getJobRequestDialog(userId = user_id)

                if (response?.code() == 200) {
                    dialogJobListResult.value = BaseResponse.Success(response.body())
                } else {
                    dialogJobListResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                dialogJobListResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun updateFinalEmployeeStatus(
        token: String,
        employer_ids: List<Int>,
        employee_id: String,
        job_ids: List<Int>
    ) {

        updateFinalEmployeeStatusResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val updateStatusRequest = UpdateStatusRequest(
                    token = token,
                    employer_ids = employer_ids,
                    employee_id = employee_id,
                    job_ids = job_ids
                )


                val response = userRepo.updateFinalEmployeeStatus(
                    token = token,
                    body = updateStatusRequest
                )



                if (response?.code() == 200) {
                    updateFinalEmployeeStatusResult.value = BaseResponse.Success(response.body())
                } else {
                    updateFinalEmployeeStatusResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                updateFinalEmployeeStatusResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

}