package com.uvk.shramapplication.ui.otp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.repository.UserAuthRepository
import kotlinx.coroutines.launch

class OtpViewModel(application: Application, ) : AndroidViewModel(application) {

    private val userRepo = UserAuthRepository()
    val resendOtpResult: MutableLiveData<BaseResponse<VerifyOtpResponse>> = MutableLiveData()

    val verifyOtpResult: MutableLiveData<BaseResponse<VerifyOtpResponse>> = MutableLiveData()

    fun verifyOtp(token: String, mobileNo: String, otp: String) {
        verifyOtpResult.value = BaseResponse.Loading()

        viewModelScope.launch {
            try {
                val response = userRepo.verifyOTP(token, mobileNo, otp)

                if (response!!.isSuccessful && response.body() != null) {
                    verifyOtpResult.value = BaseResponse.Success(response.body()!!)
                } else {
                    verifyOtpResult.value = BaseResponse.Error(response.message())
                }
            } catch (e: Exception) {
                verifyOtpResult.value = BaseResponse.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun resendOTP(
        token : String,
        mobileNumber : String) {

        resendOtpResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {


                val response = userRepo.resendOTP(
                    token = token,
                    mobile_no = mobileNumber
                )

                if (response?.code() == 200) {
                    resendOtpResult.value = BaseResponse.Success(response.body())
                } else {
                    resendOtpResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                resendOtpResult.value = BaseResponse.Error(ex.message)
            }
        }
    }
}