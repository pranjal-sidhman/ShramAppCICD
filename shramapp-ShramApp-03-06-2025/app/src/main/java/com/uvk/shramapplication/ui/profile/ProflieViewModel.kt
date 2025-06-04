package com.uvk.shramapplication.ui.profile

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.repository.UserAuthRepository
import kotlinx.coroutines.launch
import java.io.Serializable

class ProflieViewModel (application: Application, ) : AndroidViewModel(application), Serializable {
    val profileName = MutableLiveData<String>()
    val profileImage = MutableLiveData<String>() // Store Base64 or Image URL

    private val userRepo = UserAuthRepository()
    val getProfileResult: MutableLiveData<BaseResponse<ProfileResponse>> = MutableLiveData()
    val editProfileResult: MutableLiveData<BaseResponse<ProfileResponse>> = MutableLiveData()
    val editPersonalDetailsResult: MutableLiveData<BaseResponse<ProfileResponse>> = MutableLiveData()
    val getProfileCategoryResult: MutableLiveData<BaseResponse<CategoryResponse>> = MutableLiveData()


    fun getProfile(user_id: String) {

        getProfileResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getProfile( userId = user_id)

                if (response?.code() == 200) {
                    getProfileResult.value = BaseResponse.Success(response.body())
                } else {
                    getProfileResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                getProfileResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun editPersonalDetails(  token: String,
                              name: String,
                              profile_image: String,
                              role_id: String,
                              designation: String,
                              company_name: String,
                              userId: String) {

        editPersonalDetailsResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.editPersonalDetails(
                    token = token,
                    name = name,
                    profile_image = profile_image,
                    role_id = role_id,
                    designation = designation,
                    company_name = company_name,
                    userId = userId
                )

                if (response?.code() == 200) {
                    editPersonalDetailsResult.value = BaseResponse.Success(response.body())
                } else {
                    editPersonalDetailsResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                editPersonalDetailsResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun  editProfile(
        user_id: String,
        mobile_no: String,
        email: String,
        address: String,
        state: Int,
        district: Int,
        gender_id: Int,
        education_id: Int,
        pincode: String,
        main_category_id: String,
        category_ids: List<Int>,
        sub_category_ids: List<Int>,
        skill: String,
        experience: String,
        role: String,
        aadhar_image: String,
        aadhar_name: String,
        aadhar_no: String
    ) {

        editProfileResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val prfileRequest = PrfileRequest(
                    user_id = user_id,
                    mobile_no = mobile_no,
                    email = email,
                    address = address,
                    state = state,
                    district = district,
                    gender_id = gender_id,
                    education_id = education_id,
                    pincode = pincode,
                    main_category_id = main_category_id,
                    category_id = category_ids,
                    sub_category_id = sub_category_ids,
                    skill = skill,
                    experience = experience,
                    role = role,
                    aadhar_image = aadhar_image,
                    aadhar_name = aadhar_name,
                    aadhar_no = aadhar_no
                )

        Log.e("tag","Edit profile : $prfileRequest")

                val response = userRepo.EditProfile(
                    body = prfileRequest
                )


                if (response?.code() == 200) {
                    editProfileResult.value = BaseResponse.Success(response.body())
                    //  Log.e("tag","Sign Up request : $signUpRequest")
                } else {
                    editProfileResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                editProfileResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun getProfileCategory(mainCategoryId: String) {

        getProfileCategoryResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getProfileCategories(
                    mainCategoryId = mainCategoryId
                )

                if (response?.code() == 200) {
                    getProfileCategoryResult.value = BaseResponse.Success(response.body())
                } else {
                    getProfileCategoryResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                getProfileCategoryResult.value = BaseResponse.Error(ex.message)
            }
        }
    }
}