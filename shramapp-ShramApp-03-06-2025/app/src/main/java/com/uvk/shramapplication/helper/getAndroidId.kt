package com.uvk.shramapplication.helper

import android.provider.Settings
import android.content.Context

fun getAndroidId(context: Context): String {
    return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
}
