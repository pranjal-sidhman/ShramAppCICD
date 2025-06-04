package com.uvk.shramapplication.api


import androidx.lifecycle.lifecycleScope
import com.uvk.shramapplication.helper.EducationResponse
import com.uvk.shramapplication.helper.ExperienceResponse
import com.uvk.shramapplication.helper.GenderResponse
import com.uvk.shramapplication.helper.JobResponse
import com.uvk.shramapplication.helper.SalaryResponse
import com.uvk.shramapplication.response.ChatDeleteRequest
import com.uvk.shramapplication.response.ChatRequest
import com.uvk.shramapplication.response.ChatResponse
import com.uvk.shramapplication.response.DistrictApiResponse
import com.uvk.shramapplication.response.FcmResponse
import com.uvk.shramapplication.response.MessageResponse
import com.uvk.shramapplication.response.NotificationResponse
import com.uvk.shramapplication.response.SkillResponse
import com.uvk.shramapplication.response.StateApiResponse
import com.uvk.shramapplication.response.SuggestionResponse
import com.uvk.shramapplication.response.WalletHistoryResponse
import com.uvk.shramapplication.response.WalletResponse
import com.uvk.shramapplication.ui.category.CategoryResponse
import com.uvk.shramapplication.ui.employeer.job_post.AddJobPostRequest
import com.uvk.shramapplication.ui.employeer.job_post.AddJobPostResponse
import com.uvk.shramapplication.ui.profile.PrfileRequest
import com.uvk.shramapplication.ui.profile.ProfileResponse
import com.uvk.shramapplication.ui.employeer.response.EmployeerRequest
import com.uvk.shramapplication.ui.employeer.response.EmployeerResponse
import com.uvk.shramapplication.ui.employeer.worklist.list.job_request_list.UpdateStatusRequest
import com.uvk.shramapplication.ui.employeer.worklist.list.job_request_list.UpdateStatusResponse
import com.uvk.shramapplication.ui.login.OtpResponse
import com.uvk.shramapplication.ui.main_category.MainCategoryResponse
import com.uvk.shramapplication.ui.networkconnection.NetworkResponse
import com.uvk.shramapplication.ui.otp.VerifyOtpResponse
import com.uvk.shramapplication.ui.post.StoryPostResponse
import com.uvk.shramapplication.ui.registration.SignUpResponse
import com.uvk.shramapplication.ui.subcategory.SubCategoryRequest
import com.uvk.shramapplication.ui.subcategory.SubCategoryResponse
import com.uvk.shramapplication.ui.home.newhome.GotJobResponse
import com.uvk.shramapplication.ui.joblist.JobListResponse
import com.uvk.shramapplication.ui.map.LocationResponse
import com.uvk.shramapplication.ui.post.CommentResponse
import com.uvk.shramapplication.ui.post.StoryPostRequestBody
import com.uvk.shramapplication.ui.registration.SignUpRequest
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.http.*


interface ApiInterface {


    /*companion object {
        fun getApi(): ApiInterface? {
            return ApiClient.client?.create(ApiInterface::class.java)
        }

    }
    companion object {
        fun getApi(): ApiInterface? {
            return try {
                ApiClient.apiService
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    }*//* companion object {
         fun getApi(): ApiInterface? {
             return ApiClient.apiService
         }
     }*/

    companion object {
        fun getApi(): ApiInterface? {
            return try {
                ApiClient.apiService
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    }

    @FormUrlEncoded
    @POST("verify_otp")
    suspend fun verify_otp(
        @Header("Authorization") token: String,
        @Field("mobile_no") mobile_no: String,
        @Field("otp") otp: String
    ): Response<VerifyOtpResponse>

    @FormUrlEncoded
    @POST("resend_otp")
    suspend fun resendOTP(
        @Header("Authorization") token: String,
        @Field("mobile_no") mobile_no: String,
    ): Response<VerifyOtpResponse>


    @FormUrlEncoded
    @POST("login")
    suspend fun mobileOTP(
        @Field("mobile_no") mobile_no: String
    ): Response<OtpResponse>


    @FormUrlEncoded
    @POST("logout")
    suspend fun logOut(
        @Header("Authorization") token: String,
        @Field("user_id") user_id: String,
        @Field("token") device_token: String,
    ): Response<OtpResponse>


    //-----------Wallet---------------------------
    @FormUrlEncoded
    @POST("create_razorpay_order")
    suspend fun createOrder(
        @Field("user_id") user_id: String,
        @Field("amount") amount: String
    ): Response<WalletResponse>

    @FormUrlEncoded
    @POST("razorpay_payment_success")
    suspend fun paymentSuccess(
        @Field("user_id") user_id: String,
        @Field("razorpay_payment_id") razorpay_payment_id: String,
        @Field("razorpay_order_id") razorpay_order_id: String
    ): Response<WalletResponse>

    @FormUrlEncoded
    @POST("call_employee")
    suspend fun callEmployee(
        @Field("employer_id") employer_id: String,
        @Field("employee_id") employee_id: String
    ): Response<WalletResponse>


    @GET("view_wallet_balance")
    suspend fun getViewBalance(@Query("user_id") user_id: String): Response<WalletResponse>

    @GET("view_wallet_history")
    suspend fun getWalletHistory(@Query("user_id") user_id: String): Response<WalletHistoryResponse>

     @GET("check_call_deduction")
     suspend fun getCallDeductionStatus(@Query("employer_id") employer_id: String,@Query("employee_id") employee_id: String): Response<WalletResponse>




    //-----------Wallet---------------------------

    @FormUrlEncoded
    @POST("send_location_data")
    suspend fun sendLocation(
        @Header("Authorization") token: String,
        @Field("user_id") userId: String,
        @Field("longitude") longitude: Double,
        @Field("latitude") latitude: Double
    ): Response<OtpResponse>

    @GET("get_user_location_list")
    suspend fun getLocation(@Query("user_id") user_id: String): Response<LocationResponse>

    @GET("notifications_count")
    suspend fun getNotificationCount(@Query("user_id") user_id: String): Response<NotificationResponse>

    @GET("notification_list")
    suspend fun getNotificationList(@Query("user_id") user_id: String): Response<NotificationResponse>

    @GET("mark_notification_as_read")
    suspend fun getNotificationRead(
        @Query("user_id") user_id: String,
        @Query("notification_id") notification_id: String,
        @Query("sender_id") sender_id: String
    ): Response<NotificationResponse>


    @GET("states")
    suspend fun getState(): Response<StateApiResponse>

    @GET("districts?")
    suspend fun getDistricts(
        @Query("state_id") state_id: Int
    ): Response<DistrictApiResponse>

    @GET("main_category")
    suspend fun fetchMainCategories(): Response<MainCategoryResponse>

    @GET("suggestion_list")
    suspend fun getSuggestionList(@Query("keyword") keyword: String): Response<SuggestionResponse>

    @FormUrlEncoded
    @POST("category")
    suspend fun getCategories(@Field("main_category_id") main_category_id: String): CategoryResponse

    @POST("sub_category")
    suspend fun getSubCategories(@Body request: SubCategoryRequest): SubCategoryResponse

    @GET("job_types")
    suspend fun getJobTypes(): Response<JobResponse>

    @GET("experience")
    suspend fun getExperience(): Response<ExperienceResponse>

    @GET("gender")
    suspend fun getGender(): Response<GenderResponse>

    @GET("education")
    suspend fun getEducation(): Response<EducationResponse>

    @GET("salary_range")
    suspend fun getSalaryRange(@Query("salary_type_id") salary_type_id: Int): Response<SalaryResponse>

    @GET("salary_types")
    suspend fun getSalaryType(): Response<SalaryResponse>


    @GET("got_job_request_data")
    suspend fun getGotJobList(): Response<GotJobResponse>

    @GET("get_got_job_request_data")
    suspend fun getHomeGotJobList(): Response<GotJobResponse>

    @GET("get_job_list")
    suspend fun getAvailableJobList(
        @Query("user_id") user_id: String,
        @Query("main_category_id") main_category_id: String,
        @Query("keyword") keyword: String,
        @Query("state_id") state_id: String,
        @Query("district_id") district_id: String
    ): Response<GotJobResponse>

    @GET("job_list")
    suspend fun getJobList(
        @Query("user_id") user_id: String,
        @Query("main_category_id") main_category_id: String,
        @Query("keyword") keyword: String,
        @Query("state_id") state_id: String,
        @Query("district_id") district_id: String
    ): Response<JobListResponse>

    @GET("job_details")
    suspend fun getJobDetails(
        @Query("job_id") job_id: String, @Query("user_id") user_id: String
    ): Response<JobListResponse>

    @GET("applied_job_list")
    suspend fun getApplyJobList(
        @Query("user_id") user_id: String
    ): Response<JobListResponse>

    @GET("applied_job_details")
    suspend fun getApplyJobDetails(
        @Query("applied_job_id") applied_job_id: String,
    ): Response<JobListResponse>


    @GET("saved_job_list")
    suspend fun getSaveJobList(
        @Query("user_id") user_id: String
    ): Response<JobListResponse>


    @GET("saved_job_details")
    suspend fun getSaveJobDetails(
        @Query("save_job_id") save_job_id: String
    ): Response<JobListResponse>


    @FormUrlEncoded
    @POST("save_job")
    suspend fun saveJob(
        @Header("Authorization") token: String,
        @Field("user_id") user_id: String,
        @Field("job_id") job_id: String,
    ): Response<JobListResponse>

    @FormUrlEncoded
    @POST("update_token")
    suspend fun saveToken(
        @Field("user_id") user_id: String,
        @Field("token") token: String,
    ): Response<FcmResponse>


    @GET("requested_job_list")
    suspend fun getRequestJobList(
        @Query("user_id") user_id: String
    ): Response<JobListResponse>

    @FormUrlEncoded
    @POST("accept_job_request")
    suspend fun requestAccept(
        @Header("Authorization") token: String,
        @Field("receiver_id") receiver_id: String,
        @Field("job_id") job_id: String,
    ): Response<JobListResponse>

    @FormUrlEncoded
    @POST("reject_job_request")
    suspend fun requestReject(
        @Header("Authorization") token: String,
        @Field("receiver_id") receiver_id: String,
        @Field("job_id") job_id: String,
    ): Response<JobListResponse>

    @FormUrlEncoded
    @POST("apply_job")
    suspend fun applyJob(
        @Header("Authorization") token: String,
        @Field("user_id") user_id: String,
        @Field("job_id") job_id: String,
    ): Response<JobListResponse>

    @POST("signup")
    suspend fun SignUp(
        @Body signUpRequest: SignUpRequest
    ): Response<SignUpResponse>

    @GET("get_profile")
    suspend fun getProfile(@Query("user_id") user_id: String): Response<ProfileResponse>


    @POST("edit_profile")
    suspend fun editProfile(
        @Body profileRequest: PrfileRequest
    ): Response<ProfileResponse>

    @FormUrlEncoded
    @POST("edit_user_personal_details")
    suspend fun editPersonalDetails(
        @Header("Authorization") token: String,
        @Field("name") name: String,
        @Field("profile_image") profile_image: String,
        @Field("role_id") role_id: String,
        @Field("designation") designation: String,
        @Field("company_name") company_name: String,
        @Field("user_id") user_id: String
    ): Response<ProfileResponse>


    @FormUrlEncoded
    @POST("category")
    suspend fun getProfileCategories(@Field("main_category_id") mainCategoryId: String): Response<com.uvk.shramapplication.ui.profile.CategoryResponse>

    @FormUrlEncoded
    @POST("add_availability")
    suspend fun addAvailability(
        @Header("Authorization") token: String, // Authorization header
        @Field("user_id") userId: String,       // user_id as form data
        @Field("available_status") available_status: String  // story_post_id as form data
    ): Response<SignUpResponse>

    @GET("available_status")
    suspend fun getAvailableStatus(
        @Query("user_id") user_id: String
    ): Response<SignUpResponse>

    @POST("add_story_post")
    suspend fun addStoryPost(
        @Header("Authorization") token: String, @Body body: StoryPostRequestBody
    ): Response<StoryPostResponse>


    /* ---- Story Post ----- */

    @GET("story_post_list")
    suspend fun getStoryPostList(
        @Query("user_id") user_id: String, @Query("keyword") keyword: String
    ): Response<StoryPostResponse>

    @GET("get_user_story_post_list")
    suspend fun getMyStoryPostList(
        @Query("user_id") user_id: String
    ): Response<StoryPostResponse>


    @GET("story_post_delete")
    suspend fun deleteStoryPost(
        @Query("story_post_id") story_post_id: String, @Query("user_id") user_id: String
    ): Response<StoryPostResponse>

    @FormUrlEncoded
    @POST("share")
    suspend fun sharePost(
        @Header("Authorization") token: String, // Authorization header
        @Field("user_id") userId: String,       // user_id as form data
        @Field("story_post_id") postId: String  // story_post_id as form data
    ): Response<StoryPostResponse>

    @GET("story_post_details")
    suspend fun getStoryPostDetails(
        @Query("story_post_id") story_post_id: String
    ): Response<StoryPostResponse>


    @FormUrlEncoded
    @POST("like")
    suspend fun likePost(
        @Header("Authorization") token: String, // Authorization header
        @Field("user_id") userId: String,       // user_id as form data
        @Field("story_post_id") postId: String  // story_post_id as form data
    ): Response<StoryPostResponse>  //"Post liked successfully!"   "You already like the post!"

    @GET("comment_list")
    suspend fun getComments(
        @Query("story_post_id") storyPostId: String,
        @Query("user_id") userId: String,
    ): Response<CommentResponse>

    @GET("comment_delete")
    suspend fun commentDelete(
        @Query("comment_id") commentId: String,
        @Query("user_id") userId: String,
    ): Response<CommentResponse>

    @FormUrlEncoded
    @POST("comment")
    suspend fun storyCommentsend(
        @Header("Authorization") token: String, // Authorization header
        @Field("user_id") userId: String,       // user_id as form data
        @Field("story_post_id") postId: String, // story_post_id as form data
        @Field("comment") comment: String
    ): Response<CommentResponse>

    @FormUrlEncoded
    @POST("add_comment_reply")
    suspend fun storyCommentReply(
        @Header("Authorization") token: String, // Authorization header
        @Field("user_id") userId: String,       // user_id as form data
        @Field("story_post_id") storypostId: String, // story_post_id as form data
        @Field("parent_comment_id") commentId: String, @Field("comment") comment: String
    ): Response<StoryPostResponse>

    @FormUrlEncoded
    @POST("comment_like")
    suspend fun storyCommentLike(
        @Header("Authorization") token: String, // Authorization header
        @Field("user_id") userId: String,       // user_id as form data
        @Field("comment_id") commentId: String
    ): Response<StoryPostResponse>


    //-------------Skill India -------------------

    @GET("random_skills_list")
    suspend fun getRandomSkillList(@Query("user_id") user_id: String): Response<SkillResponse>

    @GET("skills_list")
    suspend fun getSkillList(
        @Query("user_id") user_id: String,
        @Query("district") district: String,
        @Query("state") state: String,
        @Query("sector_name") sector_name: String
    ): Response<SkillResponse>

    @GET("sector_name_list")
    suspend fun getSectorNameList(): Response<SkillResponse>


    //-----------Chat----------------------
    @FormUrlEncoded
    @POST("send_message")
    suspend fun sendMessage(
        @Header("Authorization") token: String, // Authorization header
        @Field("sender_id") senderId: String,
        @Field("receiver_id") receiverId: String,
        @Field("message") message: String,
        @Field("image") image: String
    ): Response<ChatResponse>

    @GET("chat_list")
    suspend fun getChatList(
        @Query("sender_id") sender_id: String, @Query("receiver_id") receiver_id: String
    ): Response<ChatResponse>

    @GET("chat_list_with_unread_count")
    suspend fun getMessageList(@Query("user_id") user_id: String): Response<MessageResponse>

    @POST("mark_message_read")
    suspend fun chatRead(
        @Header("Authorization") token: String, @Body body: ChatRequest
    ): ChatResponse

    @POST("delete_message")
    suspend fun deleteMessage(@Body body: ChatDeleteRequest): ChatResponse


    //----------Network Connection---------------

    @GET("connection_list")
    suspend fun getNetworkConnection(
        @Query("user_id") user_id: String, @Query("keyword") keyword: String
    ): Response<NetworkResponse>

    @GET("my_connection_list")
    suspend fun getMyConnection(
        @Query("user_id") user_id: String, @Query("keyword") keyword: String
    ): Response<NetworkResponse>

    @GET("get_send_request_list")
    suspend fun getSendRequestList(@Query("sender_id") user_id: String): Response<NetworkResponse>

    @GET("invitation_list")
    suspend fun invitationList(@Query("user_id") user_id: String): Response<NetworkResponse>

    @FormUrlEncoded
    @POST("send_request")
    suspend fun sendRequest(
        @Header("Authorization") token: String, // Authorization header
        @Field("sender_id") sender_id: String,       // user_id as form data
        @Field("receiver_id") receiver_id: String  // story_post_id as form data
    ): Response<NetworkResponse>

    @GET("cancel_connection_request")
    suspend fun cancelRequest(
        @Query("sender_id") user_id: String,
        @Query("network_id") network_id: String,
    ): Response<NetworkResponse>

    @FormUrlEncoded
    @POST("accept_request")
    suspend fun acceptRequest(
        @Header("Authorization") token: String, // Authorization header
        @Field("network_id") network_id: String,
        @Field("receiver_id") receiver_id: String   // user_id as form data
    ): Response<NetworkResponse>

    @FormUrlEncoded
    @POST("reject_request")
    suspend fun rejectRequest(
        @Header("Authorization") token: String, // Authorization header
        @Field("network_id") network_id: String,
        @Field("receiver_id") receiver_id: String   // user_id as form data
    ): Response<NetworkResponse>

    @FormUrlEncoded
    @POST("connection_list_details")
    suspend fun connectionDetails(
        @Header("Authorization") token: String, // Authorization header
        @Field("user_id") user_id: String, @Field("network_id") network_id: String
    ): Response<NetworkResponse>


    @GET("remove_user")
    suspend fun removeConnection( // Authorization header
        @Query("network_id") network_id: String,
        @Query("user_id") user_id: String   // user_id as form data
    ): Response<NetworkResponse>


    //************************ EMPLOYEER API ***************************

    @GET("available_employee_list")
    suspend fun getAvailableEmp(
        @Query("user_id") user_id: String, @Query("main_category_id") main_category_id: String
    ): Response<EmployeerResponse>

    @GET("available_employee_list")
    suspend fun getAvailEmp(
        @Query("user_id") user_id: String, @Query("main_category_id") main_category_id: String
    ): Response<EmployeerResponse>


    @POST("add_job_post")
    suspend fun addJobPost(
        @Header("Authorization") token: String, @Body body: AddJobPostRequest
    ): Response<AddJobPostResponse>

    @GET("employer_job_post_list")
    suspend fun getPostedJobList(
        @Query("user_id") user_id: String   // user_id as form data
    ): Response<EmployeerResponse>

    @GET("get_employer_job_post_list")
    suspend fun getPostJobList(
        @Query("user_id") user_id: String   // user_id as form data
    ): Response<EmployeerResponse>


    @FormUrlEncoded
    @POST("delete_job")
    suspend fun getDeleteJobList(
        @Header("Authorization") token: String,
        @Field("user_id") user_id: String,
        @Field("job_id") job_id: String
    ): Response<EmployeerResponse>


    @GET("employer_job_request_list")
    suspend fun getJobRequestList(
        @Query("user_id") user_id: String   // user_id as form data
    ): Response<com.uvk.shramapplication.ui.employeer.worklist.list.job_request_list.JobResponse>

    @GET("employer_job_request_details")
    suspend fun getJobRequestDetails(
        @Query("user_id") user_id: String,
        @Query("apply_job_id") apply_job_id: String,
        @Query("apply_status") apply_status: String,
        @Query("job_request_id") job_request_id: String
    ): Response<com.uvk.shramapplication.ui.employeer.worklist.list.job_request_list.JobResponse>

    @FormUrlEncoded
    @POST("select_employee_by_employer")
    suspend fun acceptSelectEmp(
        @Header("Authorization") token: String,
        @Field("user_id") user_id: String,
        @Field("employee_id") employee_id: Int,
        @Field("job_id") job_id: String
    ): Response<com.uvk.shramapplication.ui.employeer.worklist.list.job_request_list.JobResponse>

    @FormUrlEncoded
    @POST("reject_employee_by_employer")
    suspend fun rejectEmp(
        @Header("Authorization") token: String,
        @Field("user_id") user_id: String,
        @Field("employee_id") employee_id: Int,
        @Field("job_id") job_id: String
    ): Response<com.uvk.shramapplication.ui.employeer.worklist.list.job_request_list.JobResponse>


    //Emp show dialog
    @GET("get_job_request_data")
    suspend fun getJobRequestDialog(
        @Query("user_id") user_id: String   // user_id as form data
    ): Response<com.uvk.shramapplication.ui.employeer.worklist.list.job_request_list.JobResponse>

    @POST("update_final_employee_status")
    suspend fun updateFinalEmployeeStatus(
        @Header("Authorization") token: String, // Pass token as Bearer token
        @Body body: UpdateStatusRequest
    ): Response<UpdateStatusResponse>

    @FormUrlEncoded
    @POST("add_employer_offer_call")
    suspend fun employerOfferCall(
        @Header("Authorization") token: String,
        @Field("employer_id") employer_id: String,
        @Field("employee_id") employee_id: String,
        @Field("job_id") job_id: String,
    ): Response<com.uvk.shramapplication.ui.employeer.worklist.list.job_request_list.JobResponse>

    @GET("employer_accepted_job_request_list")
    suspend fun getJobAcceptList(
        @Query("user_id") user_id: String   // user_id as form data
    ): Response<EmployeerResponse>


    @POST("add_select_employees_data")
    suspend fun addSelectedEmployees(
        @Header("Authorization") token: String, @Body requestBody: EmployeerRequest
    ): Response<EmployeerResponse>

    @GET("get_job_data")
    suspend fun getJobDataSpinner(
        @Query("user_id") user_id: String   // user_id as form data
    ): Response<EmployeerResponse>

    @GET("available_employee_details")
    suspend fun getEmpDetails(
        @Query("user_id") user_id: String   // user_id as form data
    ): Response<EmployeerResponse>


    @GET("category_wise_employee_list")
    suspend fun getCategoryWiseEmp(
        @Query("user_id") user_id: String,   // user_id as form data
        @Query("main_category_id") main_category_id: String,
        @Query("state") state: String,
        @Query("district") district: String,
        @Query("keyword") keyword: String,
    ): Response<EmployeerResponse>

}

/*
val retrofit = Retrofit.Builder()
    .baseUrl("http://saathimitra.in/mahindraapi/api/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val apiService = retrofit.create(ApiInterface::class.java)*/
