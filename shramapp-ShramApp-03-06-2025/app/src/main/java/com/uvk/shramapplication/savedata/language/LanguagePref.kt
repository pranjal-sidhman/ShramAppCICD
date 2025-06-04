package com.uvk.shramapplication.savedata.language

import android.content.Context
import androidx.annotation.StringRes
import com.mahindra.serviceengineer.savedata.PrefsFileName
import com.mahindra.serviceengineer.savedata.getStringRes
import com.uvk.shramapplication.R

object LanguagePref {
   // val Context.PREF_NAME get() = "${this.getStringRes(R.string.app_name)}_prefs"
  //  fun Context.getStringRes(@StringRes id: Int) = resources.getString(id)
    private const val PREF_NAME = "app_lang"
    private const val LANGUAGE_KEY = "selected_lang"
    private const val ROLE_ID = "role_id"
    private const val KEY_IS_USER_LOGIN = "is_user_login"
    const val IS_USER_APP_PURCHASED = "is_user_app_purchased"
    const val UER_ID: String = "user_id"
    const val APP_ROLE: String = "app_role"
    const val USER_NAME: String = "user_name"
    const val USER_PROFILE: String = "profile_image"
    const val MOBILE_NO: String = "mobile_no"
    const val DESIGNATION: String = "designationName"
    const val COMPANY_NAME: String = "companyName"
    const val USER_TOKEN: String = "user_token"
    const val FCM_TOKEN: String = "FCM_TOKEN"
    const val USER_OTP: String = "user_otp"
    const val Resend_USER_OTP: String = "resend_user_otp"


    fun setUserToken(context: Context, userToken: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(USER_TOKEN, userToken)
            .apply()
    }

    fun getUserToken(context: Context): String {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(USER_TOKEN, "") ?: ""
    }

    fun setUserResendOTP(context: Context, UserResendOTP: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(Resend_USER_OTP, UserResendOTP)
            .apply()
    }

    fun getUserResendOTP(context: Context): String {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(Resend_USER_OTP, "") ?: ""
    }

    fun setUserOTP(context: Context, userOTP: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(USER_OTP, userOTP)
            .apply()
    }

    fun getUserOTP(context: Context): String {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(USER_OTP, "") ?: ""
    }

    fun setFCMToken(context: Context, fcmToken: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(FCM_TOKEN, fcmToken)
            .apply()
    }

    fun getFCMToken(context: Context): String {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(FCM_TOKEN, "") ?: ""
    }

    fun setUserName(context: Context, userName: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(USER_NAME, userName)
            .apply()
    }

    fun getUserName(context: Context): String {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(USER_NAME, "") ?: ""
    }

    fun setDesignationName(context: Context, designationName: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(DESIGNATION, designationName)
            .apply()
    }

    fun getDesignationName(context: Context): String {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(DESIGNATION, "") ?: ""
    }

    fun setCompanyName(context: Context, companyName: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(COMPANY_NAME, companyName)
            .apply()
    }

    fun getCompanyName(context: Context): String {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(COMPANY_NAME, "") ?: ""
    }

    fun setLanguage(context: Context, langCode: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(LANGUAGE_KEY, langCode)
            .apply()
    }

    fun getLanguage(context: Context): String {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(LANGUAGE_KEY, "en") ?: "en" // default to English
    }

    fun setRoleId(context: Context, roleId: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(ROLE_ID, roleId)
            .apply()
    }

    fun getRoleId(context: Context): String {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(ROLE_ID, "") ?: ""
    }

    fun setMobileNo(context: Context, mobileNo: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(MOBILE_NO, mobileNo)
            .apply()
    }

    fun getMobileNo(context: Context): String {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(MOBILE_NO, "") ?: ""
    }

    fun setUserProfile(context: Context, userProfile: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(USER_PROFILE, userProfile)
            .apply()
    }

    fun getUserProfile(context: Context): String {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(USER_PROFILE, "") ?: ""
    }

    fun setAppRole(context: Context, AppRole: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(APP_ROLE, AppRole)
            .apply()
    }

    fun getAppRole(context: Context): String {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(APP_ROLE, "") ?: ""
    }

    fun setUserId(context: Context, userId: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(UER_ID, userId)
            .apply()
    }

    fun getUserId(context: Context): String {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(UER_ID, "") ?: ""
    }

    fun setUserLogin(context: Context, isLoggedIn: Boolean) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(IS_USER_APP_PURCHASED, isLoggedIn)
            .apply()
    }

    fun isUserLoggedIn(context: Context): Boolean {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getBoolean(IS_USER_APP_PURCHASED, false)
    }
}
