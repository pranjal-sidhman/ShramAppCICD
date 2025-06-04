package com.uvk.shramapplication.repository


import com.uvk.shramapplication.api.ApiInterface
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
import retrofit2.Response


class UserAuthRepository() {


    suspend fun mobileOTP(mobile_no: String): retrofit2.Response<OtpResponse>? {
        return ApiInterface.getApi()?.mobileOTP(mobile_no)
    }

    suspend fun logOut(token: String, user_id: String,device_token: String): Response<OtpResponse>? {
        return ApiInterface.getApi()?.logOut(token, user_id,device_token)
    }

    //----------Wallet-----------------

    suspend fun createOrder(user_id: String,amount: String): retrofit2.Response<WalletResponse>? {
        return ApiInterface.getApi()?.createOrder(user_id,amount)
    }

    suspend fun paymentSuccess(user_id: String,razorpay_payment_id: String,razorpay_order_id : String): retrofit2.Response<WalletResponse>? {
        return ApiInterface.getApi()?.paymentSuccess(user_id,razorpay_payment_id,razorpay_order_id)
    }

    suspend fun callEmployee(employer_id: String,employee_id: String): retrofit2.Response<WalletResponse>? {
        return ApiInterface.getApi()?.callEmployee(employer_id,employee_id)
    }

    suspend fun getViewBalance(user_id: String): Response<WalletResponse>? {
        return ApiInterface.getApi()?.getViewBalance(user_id)
    }

     suspend fun getWalletHistory(user_id: String): Response<WalletHistoryResponse>? {
        return ApiInterface.getApi()?.getWalletHistory(user_id)
    }

    suspend fun getCallDeductionStatus(
        employer_id: String,
        employee_id: String,
    ): Response<WalletResponse>? {
        return ApiInterface.getApi()?.getCallDeductionStatus(employer_id, employee_id)
    }



    //----------Wallet-----------------

    suspend fun sendLocation(
        token: String,
        user_id: String,
        longitude: Double,
        latitude: Double
    ): Response<OtpResponse>? {
        return ApiInterface.getApi()?.sendLocation(token, user_id, longitude, latitude)
    }

    suspend fun getLocaton(user_id: String): Response<LocationResponse>? {
        return ApiInterface.getApi()?.getLocation(user_id)
    }

    suspend fun getNotificationCount(user_id: String): Response<NotificationResponse>? {
        return ApiInterface.getApi()?.getNotificationCount(user_id)
    }

    suspend fun getNotificationList(user_id: String): Response<NotificationResponse>? {
        return ApiInterface.getApi()?.getNotificationList(user_id)
    }
    suspend fun getNotificationRead(user_id: String,notification_id: String,sender_id : String): Response<NotificationResponse>? {
        return ApiInterface.getApi()?.getNotificationRead(user_id,notification_id,sender_id)
    }


    suspend fun verifyOTP(
        token: String,
        mobile_no: String,
        otp: String
    ): Response<VerifyOtpResponse>? {
        return ApiInterface.getApi()?.verify_otp(token, mobile_no, otp)
    }

    suspend fun resendOTP(
        token: String,
        mobile_no: String
    ): Response<VerifyOtpResponse>? {
        return ApiInterface.getApi()?.resendOTP(token, mobile_no)
    }


    suspend fun getMainCategories(): Response<MainCategoryResponse> {
        return ApiInterface.getApi()?.fetchMainCategories()!!
    }

    suspend fun getSuggestionList(keyword: String): Response<SuggestionResponse> {
        return ApiInterface.getApi()?.getSuggestionList(keyword = keyword)!!
    }


    suspend fun getCategories(main_category_id: String): CategoryResponse {
        return ApiInterface.getApi()?.getCategories(main_category_id = main_category_id)!!
    }


    suspend fun getSubCategories(request: SubCategoryRequest): SubCategoryResponse {
        return ApiInterface.getApi()?.getSubCategories(request)!!
    }

    /* suspend fun getJobType():retrofit2.Response<JobResponse>? {
         return  ApiInterface.getApi()?.getJobTypes()
     }

     suspend fun getExperience():retrofit2.Response<ExperienceResponse>? {
         return  ApiInterface.getApi()?.getExperience()
     }*/
    suspend fun getJobType(): Response<JobResponse>? {
        return ApiInterface.getApi()?.getJobTypes()
    }

    suspend fun getExperience(): Response<ExperienceResponse>? {
        return ApiInterface.getApi()?.getExperience()
    }

    suspend fun getGender(): Response<GenderResponse>? {
        return ApiInterface.getApi()?.getGender()
    }

    suspend fun getEducation(): Response<EducationResponse>? {
        return ApiInterface.getApi()?.getEducation()
    }

    suspend fun getSalaryRange(salary_type_id: Int): Response<SalaryResponse>? {
        return ApiInterface.getApi()?.getSalaryRange(salary_type_id = salary_type_id)
    }

    suspend fun getSalaryType(): Response<SalaryResponse>? {
        return ApiInterface.getApi()?.getSalaryType()
    }

    suspend fun addJobPost(token: String, body: AddJobPostRequest): Response<AddJobPostResponse>? {
        return ApiInterface.getApi()?.addJobPost(token = token, body = body)
    }


    suspend fun getJobList(
        userId: String,
        main_category_id: String,
        keyword: String,
        state_id: String,
        district_id: String
    ): Response<JobListResponse>? {
        return ApiInterface.getApi()
            ?.getJobList(
                user_id = userId, main_category_id = main_category_id, keyword = keyword,
                state_id = state_id, district_id = district_id
            )
    }

    suspend fun getJobDetails(job_id: String, user_id: String): Response<JobListResponse>? {
        return ApiInterface.getApi()?.getJobDetails(job_id = job_id, user_id = user_id)
    }

    suspend fun getApplyJobList(user_id: String): Response<JobListResponse>? {
        return ApiInterface.getApi()?.getApplyJobList(user_id = user_id)
    }

    suspend fun getApplyJobDetails(applied_job_id: String): Response<JobListResponse>? {
        return ApiInterface.getApi()?.getApplyJobDetails(applied_job_id = applied_job_id)
    }


    suspend fun getRequestJobList(user_id: String): Response<JobListResponse>? {
        return ApiInterface.getApi()?.getRequestJobList(user_id = user_id)
    }

    suspend fun getSaveJobList(user_id: String): Response<JobListResponse>? {
        return ApiInterface.getApi()?.getSaveJobList(user_id = user_id)
    }

    suspend fun getSaveJobDetails(save_job_id: String): Response<JobListResponse>? {
        return ApiInterface.getApi()?.getSaveJobDetails(save_job_id = save_job_id)
    }


    suspend fun saveJob(
        token: String,
        user_id: String,
        job_id: String
    ): Response<JobListResponse>? {
        return ApiInterface.getApi()?.saveJob(token = token, user_id = user_id, job_id = job_id)
    }

    suspend fun saveToken(
        user_id: String,
        token: String
    ): Response<FcmResponse>? {
        return ApiInterface.getApi()?.saveToken(user_id = user_id, token = token)
    }


    suspend fun applyJob(
        token: String,
        user_id: String,
        job_id: String
    ): Response<JobListResponse>? {
        return ApiInterface.getApi()?.applyJob(token = token, user_id = user_id, job_id = job_id)
    }

    suspend fun requestAccept(
        token: String,
        receiver_id: String,
        job_id: String
    ): Response<JobListResponse>? {
        return ApiInterface.getApi()
            ?.requestAccept(token = token, receiver_id = receiver_id, job_id = job_id)
    }

    suspend fun requestReject(
        token: String,
        receiver_id: String,
        job_id: String
    ): Response<JobListResponse>? {
        return ApiInterface.getApi()
            ?.requestReject(token = token, receiver_id = receiver_id, job_id = job_id)
    }


    /* suspend fun userComplaint(SERVICE_ENGINEER_ID: String, TALUKA_ID: String):retrofit2.Response<ComplaintResponse>? {
         return  ApiInterface.getApi()?.userComplaintList(SERVICE_ENGINEER_ID = SERVICE_ENGINEER_ID, TALUKA_ID = TALUKA_ID)
     }*/

    /*suspend fun resendMobileOTP(resendMobileOtpRequest: ResendMobileOtpRequest): retrofit2.Response<ResendOtpResponse>? {
         return  ApiInterface.getApi()?.resendOTP(resendMobileOtpRequest = resendMobileOtpRequest)
     }*/

    suspend fun getState(): retrofit2.Response<StateApiResponse>? {
        return ApiInterface.getApi()?.getState()
    }


    suspend fun getDistrict(STATE_ID: Int): retrofit2.Response<DistrictApiResponse>? {
        return ApiInterface.getApi()?.getDistricts(state_id = STATE_ID)
    }

    suspend fun getStoryPostList(
        userId: String,
        keyword: String
    ): retrofit2.Response<StoryPostResponse>? {
        return ApiInterface.getApi()?.getStoryPostList(user_id = userId, keyword = keyword)
    }

    suspend fun getMyStoryPostList(userId: String): retrofit2.Response<StoryPostResponse>? {
        return ApiInterface.getApi()?.getMyStoryPostList(user_id = userId)
    }


    suspend fun deleteStoryPost(
        story_post_id: String,
        userId: String
    ): retrofit2.Response<StoryPostResponse>? {
        return ApiInterface.getApi()
            ?.deleteStoryPost(story_post_id = story_post_id, user_id = userId)
    }

    suspend fun sharePost(
        token: String,
        userId: String,
        postId: String
    ): retrofit2.Response<StoryPostResponse>? {
        return ApiInterface.getApi()?.sharePost(token = token, userId = userId, postId = postId)
    }

 suspend fun getStoryPostDetails(
     story_post_id: String
    ): retrofit2.Response<StoryPostResponse>? {
        return ApiInterface.getApi()?.getStoryPostDetails(
            story_post_id = story_post_id)
    }


    suspend fun like(
        token: String,
        userId: String,
        postId: String
    ): retrofit2.Response<StoryPostResponse>? {
        return ApiInterface.getApi()?.likePost(token = token, userId = userId, postId = postId)
    }





    suspend fun commentList(
        storyPostId: String,
        userId: String
    ): retrofit2.Response<CommentResponse>? {
        return ApiInterface.getApi()?.getComments(storyPostId = storyPostId, userId = userId)
    }

    suspend fun commentDelete(
        commentId: String,
        userId: String
    ): retrofit2.Response<CommentResponse>? {
        return ApiInterface.getApi()?.commentDelete(commentId = commentId, userId = userId)
    }


    suspend fun storyCommentsend(
        token: String,
        userId: String,
        postId: String,
        comment: String
    ): retrofit2.Response<CommentResponse>? {
        return ApiInterface.getApi()
            ?.storyCommentsend(token = token, userId = userId, postId = postId, comment = comment)
    }

    suspend fun storyCommentReply(
        token: String,
        userId: String,
        storypostId: String,
        commentId: String,
        comment: String
    ): retrofit2.Response<StoryPostResponse>? {
        return ApiInterface.getApi()?.storyCommentReply(
            token = token,
            userId = userId,
            storypostId = storypostId,
            commentId = commentId,
            comment = comment
        )
    }


    suspend fun storyCommentLike(
        token: String,
        userId: String,
        commentId: String
    ): retrofit2.Response<StoryPostResponse>? {
        return ApiInterface.getApi()
            ?.storyCommentLike(token = token, userId = userId, commentId = commentId)
    }


    suspend fun getRandomSkillList(
        user_id: String
    ): retrofit2.Response<SkillResponse>? {
        return ApiInterface.getApi()
            ?.getRandomSkillList(user_id = user_id)
    }

    suspend fun getSkillList(
        user_id: String,
        district: String,
        state: String,
        sector_name: String
    ): retrofit2.Response<SkillResponse> {
        return ApiInterface.getApi()!!.getSkillList(
            user_id = user_id,
            district = district,
            state = state,
            sector_name = sector_name
        )
    }

    suspend fun getSectorNameList(): retrofit2.Response<SkillResponse> {
        return ApiInterface.getApi()!!.getSectorNameList()
    }


    suspend fun sendMessage(
        token: String,
        senderId: String,
        receiverId: String,
        message: String,
        image: String
    ): retrofit2.Response<ChatResponse>? {
        return ApiInterface.getApi()
            ?.sendMessage(
                token = token,
                senderId = senderId,
                receiverId = receiverId,
                message = message,
                image = image
            )
    }

    suspend fun getChatList(
        sender_id: String,
        receiver_id: String
    ): retrofit2.Response<ChatResponse>? {
        return ApiInterface.getApi()
            ?.getChatList(sender_id = sender_id, receiver_id = receiver_id)
    }

    suspend fun getMessageList(
        user_id: String
    ): retrofit2.Response<MessageResponse>? {
        return ApiInterface.getApi()?.getMessageList(user_id = user_id)
    }


    suspend fun chatRead(token: String, body: ChatRequest): ChatResponse {
        return ApiInterface.getApi()?.chatRead(token = token, body = body)!!
    }

    suspend fun chatDelete(body: ChatDeleteRequest): ChatResponse {
        return ApiInterface.getApi()?.deleteMessage(body = body)!!
    }


    /*suspend fun signUpData(
        name: RequestBody,
        mobileNo: RequestBody,
        address: RequestBody,
        email: RequestBody,
        state: RequestBody,
        district: RequestBody,
        pincode: RequestBody,
        designation: RequestBody,
        company_name: RequestBody,
        skill: RequestBody,
        role: RequestBody,
        profileImage: MultipartBody.Part,
        aadharImage: MultipartBody.Part
    ): Response<SignUpResponse>? {
        return ApiInterface.getApi()?.SignUp(
            name, mobileNo, address, email, state, district,
            pincode, designation,company_name, skill,role, profileImage, aadharImage
        )
    }*/

    suspend fun signUpData(body: SignUpRequest): Response<SignUpResponse>? {
        return ApiInterface.getApi()?.SignUp(signUpRequest = body)
    }

    suspend fun addAvailability(
        token: String,
        user_id: String,
        available_status: String
    ): retrofit2.Response<SignUpResponse>? {
        return ApiInterface.getApi()
            ?.addAvailability(token = token, userId = user_id, available_status = available_status)
    }

    suspend fun getAvailabilityStatus(userId: String): retrofit2.Response<SignUpResponse>? {
        return ApiInterface.getApi()?.getAvailableStatus(user_id = userId)
    }

    suspend fun getProfile(userId: String): retrofit2.Response<ProfileResponse>? {
        return ApiInterface.getApi()?.getProfile(user_id = userId)
    }


    suspend fun editPersonalDetails(
        token: String,
        name: String,
        profile_image: String,
        role_id: String,
        designation: String,
        company_name: String,
        userId: String
    ): retrofit2.Response<ProfileResponse>? {
        return ApiInterface.getApi()?.editPersonalDetails(
            token = token,
            name = name,
            profile_image = profile_image,
            role_id = role_id,
            designation = designation,
            company_name = company_name,
            user_id = userId
        )
    }


    suspend fun EditProfile(body: PrfileRequest): Response<ProfileResponse>? {
        return ApiInterface.getApi()?.editProfile(profileRequest = body)
    }


    suspend fun getProfileCategories(mainCategoryId: String): retrofit2.Response<com.uvk.shramapplication.ui.profile.CategoryResponse>? {
        return ApiInterface.getApi()?.getProfileCategories(mainCategoryId = mainCategoryId)
    }


    //----------Network Connection
    suspend fun getConnectionList(
        userId: String,
        keyword: String
    ): retrofit2.Response<NetworkResponse>? {
        return ApiInterface.getApi()?.getNetworkConnection(user_id = userId, keyword = keyword)
    }

    suspend fun getMyConnectionList(
        userId: String,
        keyword: String
    ): retrofit2.Response<NetworkResponse>? {
        return ApiInterface.getApi()?.getMyConnection(user_id = userId, keyword = keyword)
    }

    suspend fun getSendRequestList(userId: String): retrofit2.Response<NetworkResponse>? {
        return ApiInterface.getApi()?.getSendRequestList(user_id = userId)
    }


    suspend fun getinvitationList(userId: String): retrofit2.Response<NetworkResponse>? {
        return ApiInterface.getApi()?.invitationList(user_id = userId)
    }

    suspend fun cancelRequest(
        userId: String,
        networkId: String
    ): retrofit2.Response<NetworkResponse>? {
        return ApiInterface.getApi()?.cancelRequest(user_id = userId, network_id = networkId)
    }


    suspend fun requestSend(
        token: String,
        senderId: String,
        receiverId: String
    ): retrofit2.Response<NetworkResponse>? {
        return ApiInterface.getApi()
            ?.sendRequest(token = token, sender_id = senderId, receiver_id = receiverId)
    }

    suspend fun acceptRequest(
        token: String,
        networkId: String,
        receiverId: String
    ): retrofit2.Response<NetworkResponse>? {
        return ApiInterface.getApi()
            ?.acceptRequest(token = token, network_id = networkId, receiver_id = receiverId)
    }

    suspend fun rejectRequest(
        token: String,
        networkId: String,
        receiverId: String
    ): retrofit2.Response<NetworkResponse>? {
        return ApiInterface.getApi()
            ?.rejectRequest(token = token, network_id = networkId, receiver_id = receiverId)
    }

    suspend fun removeConnection(
        networkId: String,
        userId: String
    ): retrofit2.Response<NetworkResponse>? {
        return ApiInterface.getApi()?.removeConnection(network_id = networkId, user_id = userId)
    }

    suspend fun connectionDetails(
        token: String,
        userId: String,
        network_id: String
    ): retrofit2.Response<NetworkResponse>? {
        return ApiInterface.getApi()
            ?.connectionDetails(token = token, user_id = userId, network_id = network_id)
    }


    //---------Home Got job list
    suspend fun getGotJobList(): Response<GotJobResponse>? {
        return ApiInterface.getApi()?.getGotJobList()
    }

    suspend fun getHomeGotJobList(): Response<GotJobResponse>? {
        return ApiInterface.getApi()?.getHomeGotJobList()
    }


    suspend fun getAvailableJobList(
        userId: String, main_category_id: String, keyword: String,
        state_id: String,
        district_id: String
    ): Response<GotJobResponse>? {
        return ApiInterface.getApi()?.getAvailableJobList(
            user_id = userId,
            main_category_id = main_category_id,
            keyword = keyword,
            state_id = state_id,
            district_id = district_id
        )
    }

    suspend fun addStory(token: String, body: StoryPostRequestBody): Response<StoryPostResponse>? {
        return ApiInterface.getApi()?.addStoryPost(token = token, body = body)
    }

    //******************* EMPLOYEER API *********************

    suspend fun getAvailableEmp(
        userId: String,
        main_category_id: String
    ): retrofit2.Response<EmployeerResponse>? {
        return ApiInterface.getApi()
            ?.getAvailableEmp(user_id = userId, main_category_id = main_category_id)
    }

    suspend fun getAvailEmp(
        userId: String,
        main_category_id: String
    ): retrofit2.Response<EmployeerResponse>? {
        return ApiInterface.getApi()
            ?.getAvailEmp(user_id = userId, main_category_id = main_category_id)
    }


    suspend fun getPostedJobList(userId: String): retrofit2.Response<EmployeerResponse>? {
        return ApiInterface.getApi()?.getPostedJobList(user_id = userId)
    }

    suspend fun getPostJobList(userId: String): retrofit2.Response<EmployeerResponse>? {
        return ApiInterface.getApi()?.getPostJobList(user_id = userId)
    }


    suspend fun getDeleteJobList(
        token: String,
        userId: String,
        job_id: String
    ): retrofit2.Response<EmployeerResponse>? {
        return ApiInterface.getApi()?.getDeleteJobList(token = token,user_id = userId, job_id = job_id)
    }


    suspend fun getJobRequestList(userId: String): retrofit2.Response<com.uvk.shramapplication.ui.employeer.worklist.list.job_request_list.JobResponse>? {
        return ApiInterface.getApi()?.getJobRequestList(user_id = userId)
    }

    suspend fun getJobRequestDetails(
        user_id: String,
        apply_job_id: String,
        apply_status: String,
        job_request_id: String

    ): retrofit2.Response<com.uvk.shramapplication.ui.employeer.worklist.list.job_request_list.JobResponse>? {
        return ApiInterface.getApi()?.getJobRequestDetails(
            user_id = user_id,
            apply_job_id = apply_job_id,
            apply_status = apply_status,
            job_request_id = job_request_id
        )
    }

    suspend fun acceptSelectEmp(
        token: String,
        user_id: String,
        employee_id: Int,
        job_id: String

    ): retrofit2.Response<com.uvk.shramapplication.ui.employeer.worklist.list.job_request_list.JobResponse>? {
        return ApiInterface.getApi()?.acceptSelectEmp(
            token = token,
            user_id = user_id,
            employee_id = employee_id,
            job_id = job_id
        )

    }

    suspend fun rejectEmp(
        token: String,
        user_id: String,
        employee_id: Int,
        job_id: String

    ): retrofit2.Response<com.uvk.shramapplication.ui.employeer.worklist.list.job_request_list.JobResponse>? {
        return ApiInterface.getApi()?.rejectEmp(
            token = token,
            user_id = user_id,
            employee_id = employee_id,
            job_id = job_id
        )

    }


    suspend fun getJobRequestDialog(userId: String): retrofit2.Response<com.uvk.shramapplication.ui.employeer.worklist.list.job_request_list.JobResponse>? {
        return ApiInterface.getApi()?.getJobRequestDialog(user_id = userId)
    }

    suspend fun updateFinalEmployeeStatus(
        token: String,
        body: UpdateStatusRequest
    ): Response<UpdateStatusResponse>? {
        return ApiInterface.getApi()?.updateFinalEmployeeStatus(token = token, body = body)
    }

    suspend fun employerOfferCall(
        token: String,
        employerId: String,
        employeeId: String,
        jobId: String
    ): retrofit2.Response<com.uvk.shramapplication.ui.employeer.worklist.list.job_request_list.JobResponse>? {
        return ApiInterface.getApi()?.employerOfferCall(
            token = token,
            employer_id = employerId,
            employee_id = employeeId,
            job_id = jobId
        )
    }

    suspend fun getJobAcceptList(userId: String): retrofit2.Response<EmployeerResponse>? {
        return ApiInterface.getApi()?.getJobAcceptList(user_id = userId)
    }

    suspend fun getJobDataSpinner(userId: String): retrofit2.Response<EmployeerResponse>? {
        return ApiInterface.getApi()?.getJobDataSpinner(user_id = userId)
    }

    suspend fun getEmpDetails(userId: String): retrofit2.Response<EmployeerResponse>? {
        return ApiInterface.getApi()?.getEmpDetails(user_id = userId)
    }


    suspend fun getCategoryWiseEmp(
        userId: String,
        main_category_id: String,
        state: String,
        district: String,
        keyword: String
    ): retrofit2.Response<EmployeerResponse>? {
        return ApiInterface.getApi()?.getCategoryWiseEmp(
            user_id = userId,
            main_category_id = main_category_id,
            state = state,
            district = district,
            keyword = keyword
        )
    }


    suspend fun addSelectedEmployees(
        token: String,
        body: EmployeerRequest
    ): Response<EmployeerResponse>? {
        return ApiInterface.getApi()?.addSelectedEmployees(token = token, requestBody = body)
    }
}