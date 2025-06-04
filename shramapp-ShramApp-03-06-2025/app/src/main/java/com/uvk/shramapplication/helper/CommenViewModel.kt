package com.uvk.shramapplication.helper

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.repository.UserAuthRepository
import com.uvk.shramapplication.response.ChatDeleteRequest
import com.uvk.shramapplication.response.ChatRequest
import com.uvk.shramapplication.response.ChatResponse
import com.uvk.shramapplication.response.FcmResponse
import com.uvk.shramapplication.response.MessageResponse
import com.uvk.shramapplication.response.NotificationResponse
import com.uvk.shramapplication.response.SkillResponse
import com.uvk.shramapplication.response.SuggestionResponse
import com.uvk.shramapplication.response.WalletHistoryResponse
import com.uvk.shramapplication.response.WalletResponse
import com.uvk.shramapplication.ui.category.Category
import com.uvk.shramapplication.ui.employeer.job_post.AddJobPostRequest
import com.uvk.shramapplication.ui.employeer.job_post.AddJobPostResponse
import com.uvk.shramapplication.ui.employeer.job_post.CategoryVacancy
import com.uvk.shramapplication.ui.employeer.job_post.SubCategoryVacancy
import com.uvk.shramapplication.ui.login.OtpResponse
import com.uvk.shramapplication.ui.main_category.MainCategory
import com.uvk.shramapplication.ui.subcategory.SubCategoryRequest
import com.uvk.shramapplication.ui.subcategory.SubcategoryModel
import kotlinx.coroutines.launch
import org.json.JSONObject

class CommenViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepo = UserAuthRepository()

    val jobTyperesult: MutableLiveData<BaseResponse<JobResponse>> = MutableLiveData()
    val experienceresult: MutableLiveData<BaseResponse<ExperienceResponse>> = MutableLiveData()
    val genderResult:  MutableLiveData<BaseResponse<GenderResponse>> = MutableLiveData()
    val educationResult: MutableLiveData<BaseResponse<EducationResponse>> = MutableLiveData()
    val salaryTypeResult: MutableLiveData<BaseResponse<SalaryResponse>> = MutableLiveData()
    val salaryResult: MutableLiveData<BaseResponse<SalaryResponse>> = MutableLiveData()

    val mainCategoryResult: MutableLiveData<BaseResponse<List<MainCategory>>> = MutableLiveData()
    val categoryResult: MutableLiveData<BaseResponse<List<Category>>> = MutableLiveData()
    val subcategoryResult: MutableLiveData<BaseResponse<List<SubcategoryModel>>> = MutableLiveData()
    val addJobPostResult: MutableLiveData<BaseResponse<AddJobPostResponse>> = MutableLiveData()
    val suggestionResult: MutableLiveData<BaseResponse<SuggestionResponse>> = MutableLiveData()
    val sendMessageResult: MutableLiveData<BaseResponse<ChatResponse>> = MutableLiveData()
    val chatListResult: MutableLiveData<BaseResponse<ChatResponse>> = MutableLiveData()
    val messageListResult: MutableLiveData<BaseResponse<MessageResponse>> = MutableLiveData()
    val chatReadResult: MutableLiveData<BaseResponse<ChatResponse>> = MutableLiveData()
    val chatDeleteResult: MutableLiveData<BaseResponse<ChatResponse>> = MutableLiveData()
    val randomSkillListResult: MutableLiveData<BaseResponse<SkillResponse>> = MutableLiveData()
    val skillListResult: MutableLiveData<BaseResponse<SkillResponse>> = MutableLiveData()
    val sectorNameListResult: MutableLiveData<BaseResponse<SkillResponse>> = MutableLiveData()
    val saveTokenResult: MutableLiveData<BaseResponse<FcmResponse>> = MutableLiveData()
    val notificationCountResult: MutableLiveData<BaseResponse<NotificationResponse>> = MutableLiveData()
    val notificationListResult: MutableLiveData<BaseResponse<NotificationResponse>> = MutableLiveData()
    val notificationReadResult: MutableLiveData<BaseResponse<NotificationResponse>> = MutableLiveData()

    val walletcreateOrderResult: MutableLiveData<BaseResponse<WalletResponse>> = MutableLiveData()
    val callDeductStatusResult: MutableLiveData<BaseResponse<WalletResponse>> = MutableLiveData()
    val paymentSucResult: MutableLiveData<BaseResponse<WalletResponse>> = MutableLiveData()
    val walletCallResult: MutableLiveData<BaseResponse<WalletResponse>> = MutableLiveData()
    val walletBalanceResult: MutableLiveData<BaseResponse<WalletResponse>> = MutableLiveData()
    val walletHistoryResult: MutableLiveData<BaseResponse<WalletHistoryResponse>> = MutableLiveData()


    fun createOrder(user_id: String,amount: String) {

        walletcreateOrderResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.createOrder(
                    user_id = user_id,
                    amount = amount
                )

                if (response?.code() == 200) {
                    walletcreateOrderResult.value = BaseResponse.Success(response.body())
                } else {
                    walletcreateOrderResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                walletcreateOrderResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun callDeductStatus( employer_id: String,
                          employee_id: String) {

        callDeductStatusResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getCallDeductionStatus(
                    employer_id = employer_id,
                    employee_id = employee_id
                )

                if (response?.code() == 200) {
                    callDeductStatusResult.value = BaseResponse.Success(response.body())
                } else {
                    callDeductStatusResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                callDeductStatusResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun getViewBalance(user_id: String) {

        walletBalanceResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.getViewBalance(
                    user_id = user_id
                )

                if (response?.code() == 200) {
                    walletBalanceResult.value = BaseResponse.Success(response.body())
                } else {
                    walletBalanceResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                walletBalanceResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun getWalletHistory(user_id: String) {

        walletHistoryResult.value = BaseResponse.Loading()
            viewModelScope.launch {
                try {

                    val response = userRepo.getWalletHistory(
                        user_id = user_id
                    )

                    if (response?.code() == 200) {
                        walletHistoryResult.value = BaseResponse.Success(response.body())
                    } else {
                        walletHistoryResult.value = BaseResponse.Error(response?.message())
                    }

                } catch (ex: Exception) {
                    walletHistoryResult.value = BaseResponse.Error(ex.message)
                }
            }
        }



    fun paymentSuccess(user_id: String,razorpay_payment_id: String,razorpay_order_id : String) {

        paymentSucResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.paymentSuccess(
                    user_id = user_id,
                    razorpay_payment_id = razorpay_payment_id,
                    razorpay_order_id = razorpay_order_id
                )

                if (response?.code() == 200) {
                    paymentSucResult.value = BaseResponse.Success(response.body())
                } else {
                    paymentSucResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                paymentSucResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

   /* fun callEmployee(employer_id: String,employee_id: String) {

        walletCallResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val response = userRepo.callEmployee(
                    employer_id = employer_id,
                    employee_id = employee_id
                )

                if (response?.code() == 200) {
                    walletCallResult.value = BaseResponse.Success(response.body())
                } else {
                    walletCallResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                walletCallResult.value = BaseResponse.Error(ex.message)
            }
        }
    }*/

    fun callEmployee(employer_id: String, employee_id: String) {
        walletCallResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {
                val response = userRepo.callEmployee(
                    employer_id = employer_id,
                    employee_id = employee_id
                )

                if (response?.isSuccessful == true) {
                    walletCallResult.value = BaseResponse.Success(response.body())
                } else {
                    // Parse error body
                    val errorBody = response?.errorBody()?.string()
                    val jsonObject = JSONObject(errorBody ?: "")
                    val message = jsonObject.optString("message", response?.message() ?:"Something went wrong")
                    val statusCode = jsonObject.optInt("status_code", response?.code() ?: 0)

                    val errorResponse = WalletResponse(
                        status = false,
                        message = message,
                        status_code = statusCode,
                        data = emptyList()
                    )

                    walletCallResult.value = BaseResponse.Success(errorResponse) // Not Error: handle in UI
                }
            } catch (ex: Exception) {
                walletCallResult.value = BaseResponse.Error(ex.message ?: "Unknown error")
            }
        }
    }


    fun fetchJobType() {
        jobTyperesult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {
                val response = userRepo.getJobType()
                if (response?.code() == 200) {
                    jobTyperesult.value = BaseResponse.Success(response.body())
                } else {
                    jobTyperesult.value = BaseResponse.Error(response?.message())
                }
            } catch (ex: Exception) {
                jobTyperesult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun fetchExperience() {
        experienceresult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {
                val response = userRepo.getExperience()
                if (response?.code() == 200) {
                    experienceresult.value = BaseResponse.Success(response.body())
                } else {
                    experienceresult.value = BaseResponse.Error(response?.message())
                }
            } catch (ex: Exception) {
                experienceresult.value = BaseResponse.Error(ex.message)
            }
        }
    }


    fun fetchGender() {
        genderResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {
                val response = userRepo.getGender()
                if (response?.code() == 200) {
                    genderResult.value = BaseResponse.Success(response.body())
                } else {
                    genderResult.value = BaseResponse.Error(response?.message())
                }
            } catch (ex: Exception) {
                genderResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun fetchEducation() {
        educationResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {
                val response = userRepo.getEducation()
                if (response?.code() == 200) {
                    educationResult.value = BaseResponse.Success(response.body())
                } else {
                    educationResult.value = BaseResponse.Error(response?.message())
                }
            } catch (ex: Exception) {
                educationResult.value = BaseResponse.Error(ex.message)
            }
        }
    }


    fun fetchSalaryType() {
        salaryTypeResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {
                val response = userRepo.getSalaryType()
                if (response?.code() == 200) {
                    salaryTypeResult.value = BaseResponse.Success(response.body())
                } else {
                    salaryTypeResult.value = BaseResponse.Error(response?.message())
                }
            } catch (ex: Exception) {
                salaryTypeResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun fetchSalaryRange(salary_type_id: Int) {
        salaryResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {
                val response = userRepo.getSalaryRange(
                    salary_type_id = salary_type_id
                )
                if (response?.code() == 200) {
                    salaryResult.value = BaseResponse.Success(response.body())
                } else {
                    salaryResult.value = BaseResponse.Error(response?.message())
                }
            } catch (ex: Exception) {
                salaryResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun notificationCount(user_id: String) {
        notificationCountResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {
                val response = userRepo.getNotificationCount(
                    user_id = user_id
                )
                if (response?.code() == 200) {
                    notificationCountResult.value = BaseResponse.Success(response.body())
                } else {
                    notificationCountResult.value = BaseResponse.Error(response?.message())
                }
            } catch (ex: Exception) {
                notificationCountResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun notificationList(user_id: String) {
        notificationListResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {
                val response = userRepo.getNotificationList(
                    user_id = user_id
                )
                if (response?.code() == 200) {
                    notificationListResult.value = BaseResponse.Success(response.body())
                } else {
                    notificationListResult.value = BaseResponse.Error(response?.message())
                }
            } catch (ex: Exception) {
                notificationListResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun notificationRead(user_id: String,
                         notification_id: String,
                         sender_id : String) {
        notificationReadResult.value = BaseResponse.Loading()
            viewModelScope.launch {
                try {
                    val response = userRepo.getNotificationRead(
                        user_id = user_id,
                        notification_id = notification_id,
                        sender_id = sender_id,
                    )
                    if (response?.code() == 200) {
                        notificationReadResult.value = BaseResponse.Success(response.body())
                    } else {
                        notificationReadResult.value = BaseResponse.Error(response?.message())
                    }
                } catch (ex: Exception) {
                    notificationReadResult.value = BaseResponse.Error(ex.message)
                }
            }
        }



    fun getSuggestionList(keyword: String) {
        suggestionResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {
                val response = userRepo.getSuggestionList(
                    keyword = keyword
                )
                if (response?.code() == 200) {
                    suggestionResult.value = BaseResponse.Success(response.body())
                } else {
                    suggestionResult.value = BaseResponse.Error(response?.message())
                }
            } catch (ex: Exception) {
                suggestionResult.value = BaseResponse.Error(ex.message)
            }
        }
    }


    fun fetchMainCategories() {
        mainCategoryResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {
                val response = userRepo.getMainCategories()
                if (response.isSuccessful && response.body()?.code == 200) {
                    mainCategoryResult.value =
                        BaseResponse.Success(response.body()?.data ?: emptyList())
                } else {
                    mainCategoryResult.value = BaseResponse.Error(response.message())
                }
            } catch (e: Exception) {
                mainCategoryResult.value = BaseResponse.Error(e.message ?: "Unknown error")
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun fetchcategories(categoryIds: String) {
        categoryResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {
                val response = userRepo.getCategories(
                    main_category_id = categoryIds
                )
                Log.e("API_CALL", "categoryResult updated: ${response}")

                if (response.code == 200 && response.status == "success") {
                    categoryResult.value = BaseResponse.Success(response.data)
                } else {
                    categoryResult.value = BaseResponse.Error(response.message)
                }
            } catch (e: Exception) {
                categoryResult.value = BaseResponse.Error(e.message ?: "Unknown error")
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun fetchsubcategories(categoryIds: List<Int>) {
        subcategoryResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {
                val response = userRepo.getSubCategories(SubCategoryRequest(categoryIds))
                // if (response.isSuccessful && response.body()?.code == 200) {
                if (response.code == 200 && response.status == "success") {
                    subcategoryResult.value = BaseResponse.Success(response.data)
                } else {
                    subcategoryResult.value = BaseResponse.Error(response.message)
                }
            } catch (e: Exception) {
                subcategoryResult.value = BaseResponse.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun getRandomSkillList(user_id: String) {
        randomSkillListResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {
                val response = userRepo.getRandomSkillList(
                    user_id = user_id
                )
                if (response?.code() == 200) {
                    randomSkillListResult.value = BaseResponse.Success(response.body())
                } else {
                    randomSkillListResult.value = BaseResponse.Error(response?.message())
                }
            } catch (ex: Exception) {
                randomSkillListResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun getSkillList(  user_id: String,
                       district: String ,
                       state: String ,
                       sector_name: String) {
        skillListResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {
                val response = userRepo.getSkillList(
                    user_id = user_id,
                    district = district,
                    state = state,
                    sector_name = sector_name
                )
                if (response.code() == 200) {
                    skillListResult.value = BaseResponse.Success(response.body())
                } else {
                    skillListResult.value = BaseResponse.Error(response?.message())
                }
            } catch (ex: Exception) {
                skillListResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun getSectorNameList() {
        sectorNameListResult.value = BaseResponse.Loading()
            viewModelScope.launch {
                try {
                    val response = userRepo.getSectorNameList()
                    if (response.code() == 200) {
                        sectorNameListResult.value = BaseResponse.Success(response.body())
                    } else {
                        sectorNameListResult.value = BaseResponse.Error(response?.message())
                    }
                } catch (ex: Exception) {
                    sectorNameListResult.value = BaseResponse.Error(ex.message)
                }
            }
        }




    fun saveToken(
        userId: String,
        token: String
    ) {
        saveTokenResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {
                val response = userRepo.saveToken(
                    user_id = userId,
                    token = token,
                )
                if (response?.code() == 200) {
                    saveTokenResult.value = BaseResponse.Success(response.body())
                } else {
                    saveTokenResult.value = BaseResponse.Error(response?.message())
                }
            } catch (ex: Exception) {
                saveTokenResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun sendMessage(
        token: String,
        senderId: String,
        receiverId: String,
        message: String,
        image: String
    ) {
        sendMessageResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {
                val response = userRepo.sendMessage(
                    token = token,
                    senderId = senderId,
                    receiverId = receiverId,
                    message = message,
                    image = image
                )
                if (response?.code() == 200) {
                    sendMessageResult.value = BaseResponse.Success(response.body())
                } else {
                    sendMessageResult.value = BaseResponse.Error(response?.message())
                }
            } catch (ex: Exception) {
                sendMessageResult.value = BaseResponse.Error(ex.message)
            }
        }
    }



    fun getChatList(
        sender_id: String,
        receiver_id: String
    ) {
        chatListResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {
                val response = userRepo.getChatList(
                    sender_id = sender_id,
                    receiver_id = receiver_id
                )
                if (response?.code() == 200) {
                    chatListResult.value = BaseResponse.Success(response.body())
                } else {
                    chatListResult.value = BaseResponse.Error(response?.message())
                }
            } catch (ex: Exception) {
                chatListResult.value = BaseResponse.Error(ex.message)
            }
        }
    }

    fun getMessageList(
        user_id: String
    ) {
        messageListResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {
                val response = userRepo.getMessageList(
                    user_id = user_id
                )
                if (response?.code() == 200) {
                    messageListResult.value = BaseResponse.Success(response.body())
                } else {
                    messageListResult.value = BaseResponse.Error(response?.message())
                }
            } catch (ex: Exception) {
                messageListResult.value = BaseResponse.Error(ex.message)
            }
        }
    }




    fun chatRead(token: String, sender_id: String, receiver_id: String, chat_ids: List<String>) {
        chatReadResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {
                val chatRequest = ChatRequest(sender_id, receiver_id, chat_ids)
                val response = userRepo.chatRead(token, chatRequest)

                if (response.code == 200) {
                    chatReadResult.value = BaseResponse.Success(response)
                } else {
                    chatReadResult.value = BaseResponse.Error(response.message ?: "Failed to mark messages as read")
                }
            } catch (ex: Exception) {
                chatReadResult.value = BaseResponse.Error(ex.message ?: "Unexpected error occurred")
            }
        }
    }


    fun chatDelete(
        sender_id: String,
        receiver_id: String,
        delete_type: String,
        chat_ids: List<String>

    ) {

        chatDeleteResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {
                val chatRequest = ChatDeleteRequest(
                    sender_id = sender_id,
                    receiver_id = receiver_id,
                    delete_type = delete_type,
                    chat_ids = chat_ids
                )


                val response = userRepo.chatDelete(
                    body = chatRequest
                )

                if (response.code == 200) {
                    chatDeleteResult.value = BaseResponse.Success(response)
                } else {
                    chatDeleteResult.value = BaseResponse.Error(response?.message)
                }

            } catch (ex: Exception) {
                chatDeleteResult.value = BaseResponse.Error(ex.message)
            }
        }
    }


    fun addJobPost(
        token: String,
        user_id: String,
        title: String,
        description: String,
        state_id: Int,
        district_id: Int,
        gender_id: Int,
        location: String,
        job_type_ids: List<Int>,
        company_name: String,
        salary_type_id: Int,
        salary_range_id: Int,
        categories: List<CategoryVacancy>,  // List with vacancies
        main_category_id: String,
        sub_categories: List<SubCategoryVacancy>,  // List with vacancies
        key_responsibilities: String,
        education_id: Int,
        experience: String,
        qualification: String,
        company_description: String,
        job_expiry_date: String,
        job_post_image: String,
        salary_amount: String
    ) {
        addJobPostResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {
                val addJobPostRequest = AddJobPostRequest(
                    user_id = user_id,
                    title = title,
                    description = description,
                    state_id = state_id,
                    district_id = district_id,
                    gender_id = gender_id,
                    location = location,
                    job_type_ids = job_type_ids,
                    company_name = company_name,
                    salary_type_id = salary_type_id,
                    salary_range_id = salary_range_id,
                    categories = categories,
                    main_category_id = main_category_id,
                    sub_categories = sub_categories,
                    key_responsibilities = key_responsibilities,
                    education_id = education_id,
                    experience = experience,
                    qualification = qualification,
                    company_description = company_description,
                    job_expiry_date = job_expiry_date,
                    job_post_image = job_post_image,
                    salary_amount = salary_amount
                )

                val response = userRepo.addJobPost(
                    token = token,
                    body = addJobPostRequest
                )

                if (response?.code() == 200) {
                    addJobPostResult.value = BaseResponse.Success(response.body())
                } else {
                    addJobPostResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                addJobPostResult.value = BaseResponse.Error(ex.message)
            }
        }
    }



}

