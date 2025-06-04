package com.mahindra.serviceengineer.savedata

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi


const val SELECTED_TYPE: String = "selected_type"
const val USER_NAME: String = "user_name"
const val USER_PROFILE: String = "profile_image"
const val MOBILE_NO: String = "mobile_no"
const val SERVICE_ENGINEER_ID: String = "service_engineer_id"
const val ROLE_ID: String = "role_id"
const val APP_ROLE: String = "app_role"
const val DESIGNATION: String = "designationName"
const val COMPANY_NAME: String = "companyName"
const val UER_ID: String = "user_id"
const val USER_OTP: String = "user_otp"
const val USER_TOKEN: String = "user_token"
const val CUSTOMER_OTP: String = "customer_otp"
const val Resend_USER_OTP: String = "resend_user_otp"
const val TALUKA_ID: String = "taluka_id"
const val STATE_ID: String = "state_id"
const val DIST_ID: String = "dist_id"
var FCM_TOKEN: String = "FCM_TOKEN"
const val USER_MOBILE_NUMBER: String = "user_mobile_number"
const val COMPLAINTS_ID: String = "COMPLAINTS_ID"
const val TYPE: String = "TYPE"
const val COMP_TYPE: String = "COMP_TYPE"
var JOB_GIVER: String = "job_giver"

const val IS_LOGGED_IN = "is_logged_in"
const val LANGUAGE = "languageName"
const val LANGUAGE_ID = "LANGUAGE_ID"




@Suppress("DEPRECATION")
inline val Context.isOnline: Boolean
    @RequiresApi(Build.VERSION_CODES.M)
    get() {
        (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).let { connectivityManager ->
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.let {
                return it.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            }
        }
        return false

    }

var Context.mobile_no: String
    get() = this.getString(MOBILE_NO, "")
    set(selectedLanguage) = this.save(MOBILE_NO, selectedLanguage)


var Context.usermobilenumber: String
    get() = this.getString(USER_MOBILE_NUMBER ,"")
    set(selectedLanguage) = this.save(USER_MOBILE_NUMBER, selectedLanguage)

var Context.username: String
    get() = this.getString(USER_NAME, "")
    set(selectedLanguage) = this.save(USER_NAME, selectedLanguage)

var Context.user_profile: String
    get() = this.getString(USER_PROFILE, "")
    set(selectedLanguage) = this.save(USER_PROFILE, selectedLanguage)



var Context.selected_type: String
    get() = this.getString(SELECTED_TYPE, "")
    set(selectedLanguage) = this.save(SELECTED_TYPE, selectedLanguage)


var Context.complaintid: String
    get() = this.getString(COMPLAINTS_ID, "")
    set(selectedLanguage) = this.save(COMPLAINTS_ID, selectedLanguage)

var Context.type: String
    get() = this.getString(TYPE, "")
    set(selectedLanguage) = this.save(TYPE, selectedLanguage)

var Context.Comp_type: String
    get() = this.getString(COMP_TYPE, "")
    set(selectedLanguage) = this.save(COMP_TYPE, selectedLanguage)


var Context.FCM_TOKENa: String
    get() = this.getString(FCM_TOKEN, "")
    set(selectedLanguage) = this.save(FCM_TOKEN, selectedLanguage)


var Context.designationName: String
    get() = this.getString(DESIGNATION, "")
    set(selectedLanguage) = this.save(DESIGNATION, selectedLanguage)

var Context.companyName: String
    get() = this.getString(COMPANY_NAME, "")
    set(selectedLanguage) = this.save(COMPANY_NAME, selectedLanguage)



var Context.roleId: String
    get() = this.getString(ROLE_ID, "")
    set(selectedLanguage) = this.save(ROLE_ID, selectedLanguage)

// Property to store and retrieve language
var Context.is_logged_in: String
    get() = this.getString(IS_LOGGED_IN, "")
    set(selectedLanguage) = this.save(IS_LOGGED_IN, selectedLanguage)

var Context.languageName: String
    get() = this.getString(LANGUAGE, "")
    set(selectedLanguage) = this.save(LANGUAGE, selectedLanguage)

var Context.language_id: String
    get() = this.getString(LANGUAGE_ID, "")
    set(selectedLanguage) = this.save(LANGUAGE_ID, selectedLanguage)

var Context.AppRole: String
    get() = this.getString(APP_ROLE, "")
    set(selectedLanguage) = this.save(APP_ROLE, selectedLanguage)




var Context.userid: String
    get() = this.getString(UER_ID, "")
    set(selectedLanguage) = this.save(UER_ID, selectedLanguage)


var Context.userotp: String
    get() = this.getString(USER_OTP,"" )
    set(selectedLanguage) = this.save(USER_OTP, selectedLanguage)

var Context.token: String
    get() = this.getString(USER_TOKEN,"" )
    set(selectedLanguage) = this.save(USER_TOKEN, selectedLanguage)

var Context.customerotp: String
    get() = this.getString(CUSTOMER_OTP,"" )
    set(selectedLanguage) = this.save(CUSTOMER_OTP, selectedLanguage)


var Context.resenduserotp: String
    get() = this.getString(Resend_USER_OTP,"" )
    set(selectedLanguage) = this.save(Resend_USER_OTP, selectedLanguage)


var Context.talukaid: String
    get() = this.getString(TALUKA_ID,"" )
    set(selectedLanguage) = this.save(TALUKA_ID, selectedLanguage)


var Context.stateid: String
    get() = this.getString(STATE_ID,"" )
    set(selectedLanguage) = this.save(STATE_ID, selectedLanguage)


var Context.distId: String
    get() = this.getString(DIST_ID,"" )
    set(selectedLanguage) = this.save(DIST_ID, selectedLanguage)


var Context.job_giver: String
    get() = this.getString(JOB_GIVER,"" )
    set(selectedLanguage) = this.save(JOB_GIVER, selectedLanguage)





