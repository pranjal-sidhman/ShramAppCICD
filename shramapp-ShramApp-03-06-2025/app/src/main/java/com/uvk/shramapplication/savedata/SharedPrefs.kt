package com.mahindra.serviceengineer.savedata

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.NonNull
import androidx.annotation.StringRes
import com.uvk.shramapplication.R
import org.json.JSONArray


val Context.PrefsFileName get() = "${this.getStringRes(R.string.app_name)}_prefs"
fun Context.getStringRes(@StringRes id: Int) = resources.getString(id)

const val DEVISE_USER_DATA = "devise_user_data"
const val IS_USER_APP_PURCHASED = "is_user_app_purchased"
const val FL_USER_APP_PURCHASED = "fl_user_app_purchased"
const val DL_USER_APP_PURCHASED = "dl_user_app_purchased"
const val EX_DL_USER_APP_PURCHASED = "ex_dl_user_app_purchased"
const val USER_CURRENT_CHAT_COUNT = "user_current_chat_count"
const val USER_CURRENT_RING = "user_current_ring"
const val FL_USER_CURRENT_RING = "fl_current_ring"
const val DL_USER_CURRENT_RING = "dl_current_ring"
const val EX_DL_USER_CURRENT_RING = "ex_dl_current_ring"
var USER_CHET_NUMBER = "user_chet_number"

var useridddd="0"
var Context.isuserlgin: Boolean
    get() = this.getBoolean(IS_USER_APP_PURCHASED, false)
    set(isAppPurchased) = this.save(IS_USER_APP_PURCHASED, isAppPurchased)


/**
 * Extension method for get SharedPreferences
 */
inline val Context.getPrefs: SharedPreferences get() = this.getSharedPreferences(PrefsFileName, Context.MODE_PRIVATE)

fun Context.contain(@NonNull fKey: String): Boolean {
    return this.getPrefs.contains(fKey)
}

/**
 * Extension method for clear SharedPreferences object
 */
fun Context.clearPrefs() {
    this.getPrefs.edit().clear().apply()
}

/**
 * Extension method for remove SharedPreferences Key
 *
 * @param fKey SharedPreferences Key witch you want to remove
 */
fun Context.removeKey(@NonNull fKey: String) {
    this.getPrefs.edit().remove(fKey).apply()
}

/**
 * Extension method for Save Boolean Value
 *
 * @param fKey SharedPreferences Key
 * @param fValue Boolean Value
 */
fun Context.save(@NonNull fKey: String, @NonNull fValue: Boolean) {
    this.getPrefs.edit().putBoolean(fKey, fValue).apply()
}

/**
 * Extension method for get Boolean Value
 * with your pre-defined default value
 *
 * @param fKey SharedPreferences Key
 * @param fDefaultValue your pre-defined default value
 */
fun Context.getBoolean(@NonNull fKey: String, fDefaultValue: Boolean = false): Boolean {
    return this.getPrefs.getBoolean(fKey, fDefaultValue)
}

/**
 * Extension method for Save String Value
 *
 * @param fKey SharedPreferences Key
 * @param fValue String Value
 */
fun Context.save(@NonNull fKey: String, @NonNull fValue: String) {
    this.getPrefs.edit().putString(fKey, fValue).apply()
}

/**
 * Extension method for get String Value
 * with your pre-defined default value
 *
 * @param fKey SharedPreferences Key
 * @param fDefaultValue your pre-defined default value
 */
fun Context.getString(@NonNull fKey: String, fDefaultValue: String = ""): String {
    return this.getPrefs.getString(fKey, fDefaultValue) ?: fDefaultValue
}

/**
 * Extension method for Save Integer Value
 *
 * @param fKey SharedPreferences Key
 * @param fValue Int Value
 */
fun Context.save(@NonNull fKey: String, @NonNull fValue: Int) {
    this.getPrefs.edit().putInt(fKey, fValue).apply()
}

/**
 * Extension method for get Integer Value
 * with your pre-defined default value
 *
 * @param fKey SharedPreferences Key
 * @param fDefaultValue your pre-defined default value
 */
fun Context.getInt(@NonNull fKey: String, fDefaultValue: Int = 0): Int {
    return this.getPrefs.getInt(fKey, fDefaultValue)
}

/**
 * Extension method for Save Float Value
 *
 * @param fKey SharedPreferences Key
 * @param fValue Float Value
 */
fun Context.save(@NonNull fKey: String, @NonNull fValue: Float) {
    this.getPrefs.edit().putFloat(fKey, fValue).apply()
}

/**
 * Extension method for get Float Value
 * with your pre-defined default value
 *
 * @param fKey SharedPreferences Key
 * @param fDefaultValue your pre-defined default value
 */
fun Context.getFloat(@NonNull fKey: String, fDefaultValue: Float = 0f): Float {
    return this.getPrefs.getFloat(fKey, fDefaultValue)
}

/**
 * Extension method for Save Long Value
 *
 * @param fKey SharedPreferences Key
 * @param fValue Long Value
 */
fun Context.save(@NonNull fKey: String, @NonNull fValue: Long) {
    this.getPrefs.edit().putLong(fKey, fValue).apply()
}

/**
 * Extension method for get Long Value
 * with your pre-defined default value
 *
 * @param fKey SharedPreferences Key
 * @param fDefaultValue your pre-defined default value
 */
fun Context.getLong(@NonNull fKey: String, fDefaultValue: Long = 0): Long {
    return this.getPrefs.getLong(fKey, fDefaultValue)
}


fun Context.saveArray(@NonNull key: String, array: List<Int>) {
    val jsonString = JSONArray(array).toString()
    this.save(key, jsonString)
}


/*fun Context.getArray(@NonNull key: String): MutableList<String> {
    val jsonString = this.getString(key, "[]")
    val jsonArray = JSONArray(jsonString)
    val list = mutableListOf<String>()
    for (i in 0 until jsonArray.length()) {
        list.add(jsonArray.getInt(i).toString())
    }
    return list
}*/

fun Context.getArray(key: String): List<Int> {
  //  val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val jsonString = this.getString(key, "[]")
    val jsonArray = JSONArray(jsonString)
    val list = mutableListOf<Int>()
    for (i in 0 until jsonArray.length()) {
        list.add(jsonArray.getInt(i))
    }
    return list
}
