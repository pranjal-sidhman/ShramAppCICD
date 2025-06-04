package com.uvk.shramapplication.savedata.language

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import java.util.Locale

object LanguageHelper {

    fun setLocale(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context, locale)
        } else {
            updateResourcesLegacy(context, locale)
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context, locale: Locale): Context {
        val config = context.resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return context.createConfigurationContext(config)
    }

    private fun updateResourcesLegacy(context: Context, locale: Locale): Context {
        val resources = context.resources
        val config = resources.configuration
        config.locale = locale
        config.setLayoutDirection(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        return context
    }
}
