package com.uvk.shramapplication.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.repository.UserAuthRepository
import com.uvk.shramapplication.ui.map.LocationResponse
import kotlinx.coroutines.launch



class LoginViewModel(application: Application, ) : AndroidViewModel(application) {

    private val userRepo = UserAuthRepository()
    val OtpResult: MutableLiveData<BaseResponse<OtpResponse>> = MutableLiveData()
    val logOutResult: MutableLiveData<BaseResponse<OtpResponse>> = MutableLiveData()
    val sendLocationResult: MutableLiveData<BaseResponse<OtpResponse>> = MutableLiveData()
    val getLocationResult: MutableLiveData<BaseResponse<LocationResponse>> = MutableLiveData()


    fun userMobileOTP(mobileNumber : String) {

        OtpResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {
                val mobileOtpRequest = MobileOtpRequest(
                    mobile_no = mobileNumber
                )

                val response = userRepo.mobileOTP(
                    mobile_no = mobileNumber
                )

                if (response?.code() == 200) {
                    OtpResult.value = BaseResponse.Success(response.body())
                } else {
                    OtpResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                OtpResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun logOut(token : String,userId : String,device_token : String) {

        logOutResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {


                val response = userRepo.logOut(
                    token = token,
                    user_id = userId,
                    device_token = device_token
                )

                if (response?.code() == 200) {
                    logOutResult.value = BaseResponse.Success(response.body())
                } else {
                    logOutResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                logOutResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun sendLocation(token: String,
                     userId: String,
                     longitude : Double,
                     latitude : Double) {

        sendLocationResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {
                val response = userRepo.sendLocation(
                    token = token,
                    user_id = userId,
                    longitude = longitude,
                    latitude = latitude
                )

                if (response?.code() == 200) {
                    sendLocationResult.value = BaseResponse.Success(response.body())
                } else {
                    sendLocationResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                sendLocationResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun getLocation(userId: String) {

        getLocationResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {
                val response = userRepo.getLocaton(
                    user_id = userId
                )

                if (response?.code() == 200) {
                    getLocationResult.value = BaseResponse.Success(response.body())
                } else {
                    getLocationResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                getLocationResult.value = BaseResponse.Error(ex.message)
            }
        }
    }


}