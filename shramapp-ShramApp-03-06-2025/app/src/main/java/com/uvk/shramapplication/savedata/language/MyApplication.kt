package com.uvk.shramapplication.savedata.language

import android.app.Application
import android.content.Context
import com.uvk.shramapplication.savedata.language.LanguageHelper

class MyApplication : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { LanguageHelper.setLocale(it, LanguagePref.getLanguage(it)) })
    }
}

