package com.uvk.shramapplication.ui.login

import com.google.gson.annotations.SerializedName

data class MobileOtpRequest(
    @SerializedName("mobile_no")
    var mobile_no: String,

)
