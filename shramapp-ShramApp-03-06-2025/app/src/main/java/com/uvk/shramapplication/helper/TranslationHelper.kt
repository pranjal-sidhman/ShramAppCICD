package com.uvk.shramapplication.helper

import android.content.Context
import android.util.Log
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateException
import com.google.cloud.translate.TranslateOptions
import com.uvk.shramapplication.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object TranslationHelper {

    private lateinit var translate: Translate

    fun initialize(context: Context) {
        translate = TranslateOptions.newBuilder()
            .setApiKey(context.getString(R.string.google_api_keys))
            .build()
            .service
    }

    suspend fun translateText(text: String, targetLanguage: String): String {
        return withContext(Dispatchers.IO) {
            try {
                if (!::translate.isInitialized) {
                    Log.e("TranslationHelper", "Translate not initialized!")
                    return@withContext text
                }

                if (targetLanguage == "en") {
                    return@withContext text
                }

                val translation = translate.translate(
                    text,
                    Translate.TranslateOption.targetLanguage(targetLanguage),
                    Translate.TranslateOption.sourceLanguage("en")
                )
                translation.translatedText
            } catch (e: Exception) {
                Log.e("TranslationHelper", "Translation Error", e)
                text
            }
        }
    }


    /*suspend fun translateText(text: String, targetLanguage: String): String {
        return withContext(Dispatchers.IO) {
            try {
                // Avoid translating if target language is same as input
                if (targetLanguage == "en") return@withContext text

                val translation = translate.translate(
                    text,
                    Translate.TranslateOption.targetLanguage(targetLanguage)
                    // DO NOT include sourceLanguage -> let it auto-detect
                )
                translation.translatedText
            } catch (e: TranslateException) {
                Log.e("TranslationHelper", "Translation Error: ${e.message}")
                text // Return original if translation fails
            }
        }
    }*/



    /*suspend fun translateText(text: String, targetLanguage: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val translation = translate.translate(
                    text,
                    Translate.TranslateOption.targetLanguage(targetLanguage),
                    Translate.TranslateOption.sourceLanguage("en")
                )
                translation.translatedText
            } catch (e: TranslateException) {
                Log.e("TranslationHelper", "Translation Error: ${e.message}")
                text // Return original text if translation fails
            }
        }
    }*/
}



