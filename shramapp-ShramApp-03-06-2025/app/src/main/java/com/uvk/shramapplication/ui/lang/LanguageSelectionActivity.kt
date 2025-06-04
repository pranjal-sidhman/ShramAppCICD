package com.uvk.shramapplication.ui.lang

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.mahindra.serviceengineer.savedata.AppRole
import com.mahindra.serviceengineer.savedata.FCM_TOKEN
import com.mahindra.serviceengineer.savedata.FCM_TOKENa
import com.mahindra.serviceengineer.savedata.companyName
import com.mahindra.serviceengineer.savedata.designationName
import com.uvk.shramapplication.MainActivity
import com.uvk.shramapplication.R
import com.uvk.shramapplication.databinding.ActivityLanguageSelectionBinding
import com.uvk.shramapplication.ui.role.RoleSelectedActivity
import com.mahindra.serviceengineer.savedata.isuserlgin
import com.mahindra.serviceengineer.savedata.languageName
import com.mahindra.serviceengineer.savedata.mobile_no
import com.mahindra.serviceengineer.savedata.roleId
import com.mahindra.serviceengineer.savedata.token
import com.mahindra.serviceengineer.savedata.user_profile
import com.mahindra.serviceengineer.savedata.userid
import com.mahindra.serviceengineer.savedata.usermobilenumber
import com.mahindra.serviceengineer.savedata.username
import com.uvk.shramapplication.savedata.language.LanguagePref
import java.util.Locale


class LanguageSelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLanguageSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()


        if(isuserlgin) {
            if (!languageName.isNullOrEmpty()) {
                applyLanguage(languageName)
                navigateToLogin()
                return
            }
        }


        Log.e("LanguageSelectionActivity", "language sele activity: $languageName")

        binding.cbEnglish.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {selectLanguage("en")
            setLanguage("en")}
        }

        binding.cbHindi.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectLanguage("hi")
                setLanguage("hi")
            }
        }

        binding.cbMarathi.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectLanguage("mr")
                setLanguage("mr")
            }
        }

        binding.llMarathi.setOnClickListener {
            Log.e("tag", "Marathi clicked")
            selectLanguage("mr")
        }

        binding.llHindi.setOnClickListener {
            Log.e("tag", "Hindi clicked")
            selectLanguage("hi")
        }

        binding.llEnglish.setOnClickListener {
            Log.e("tag", "Eng clicked")
            selectLanguage("en")
        }

        Log.e("tag", "Language : $languageName")
        Log.e("tag", "isuserlgin : $isuserlgin")

        binding.btnSubmit.setOnClickListener {
            Log.e("tag", "Language btn click : $languageName")

            if (!binding.cbEnglish.isChecked && !binding.cbHindi.isChecked && !binding.cbMarathi.isChecked) {
                showToast(getString(R.string.txxtselect_app_language))
            } else {
                navigateToLogin()
            }
        }
    }

    private fun selectLanguage(languageCode: String) {

        // Update checkbox states based on the selected language
        binding.cbEnglish.isChecked = languageCode == "en"
        binding.cbHindi.isChecked = languageCode == "hi"
        binding.cbMarathi.isChecked = languageCode == "mr"



        // Update background to highlight the selected language
        setSelectedLanguage(
            when (languageCode) {
                "en" -> binding.llEnglish
                "hi" -> binding.llHindi
                "mr" -> binding.llMarathi
                else -> null
            }
        )


        // Reset other languages
        resetOtherLanguages(binding.llEnglish, languageCode == "en")
        resetOtherLanguages(binding.llHindi, languageCode == "hi")
        resetOtherLanguages(binding.llMarathi, languageCode == "mr")

        Log.e("tag", "Language before setLanguage(): $languageCode")
        setLanguage(languageCode)
        Log.e("tag", "Language after setLanguage(): $languageCode")

    }

    private fun setSelectedLanguage(ll: View?) {
        ll?.background = ContextCompat.getDrawable(this, R.drawable.ic_green_rectangle)
    }

    private fun resetOtherLanguages(ll: View, isSelected: Boolean) {
        if (!isSelected) {
            ll.background = ContextCompat.getDrawable(this, R.drawable.border_box)
        }
    }

    private fun setLanguage(languageCode: String) {
        languageName = languageCode
        LanguagePref.setLanguage(this, languageName)
        LanguagePref.setRoleId(this, roleId)
        LanguagePref.setUserLogin(this, isuserlgin)
        LanguagePref.setUserId(this, userid)
        LanguagePref.setAppRole(this, AppRole)
        LanguagePref.setUserName(this, username)
        LanguagePref.setUserProfile(this, user_profile)
        LanguagePref.setMobileNo(this, mobile_no)
        LanguagePref.setMobileNo(this, usermobilenumber)
        LanguagePref.setDesignationName(this, designationName)
        LanguagePref.setCompanyName(this, companyName)
        LanguagePref.setUserToken(this, token)
        LanguagePref.setFCMToken(this, FCM_TOKEN)
        LanguagePref.setFCMToken(this, FCM_TOKENa)
        Log.e("selection", "Language set inside setLanguage(): $languageName")
        applyLanguage(languageName)
    }


    private fun applyLanguage(languageCode: String) {
        if(!languageCode.isNullOrEmpty()) {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val config = Configuration()
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.displayMetrics)
        }else{
            val locale = Locale(languageName)
            Locale.setDefault(locale)
            val config = Configuration()
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.displayMetrics)
        }
    }



    private fun navigateToLogin() {
        if (isuserlgin) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, RoleSelectedActivity::class.java))
        }
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

   /* private fun setSelectedLanguage(ll: View) {
        ll.background = ContextCompat.getDrawable(this, R.drawable.ic_green_rectangle)
    }

    private fun resetOtherLanguages(ll: View) {
        ll.background = ContextCompat.getDrawable(this, R.drawable.border_box)
        //   ll.background = ContextCompat.getDrawable(this, R.drawable.icgrayrectangle)
    }*/

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
        onBackPressedDispatcher.onBackPressed()
    }


}

/*
class LanguageSelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLanguageSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        if (language!!.isNotEmpty()) {
            applyLanguage(language)
            navigateToLogin()
            return
        }

        binding.cbEnglish.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.cbMarathi.isChecked = false
                binding.cbHindi.isChecked = false
                setLanguage("en")
            }
        }

        binding.cbHindi.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.cbMarathi.isChecked = false
                binding.cbEnglish.isChecked = false
                setLanguage("hi")
            }
        }

        binding.cbMarathi.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.cbHindi.isChecked = false
                binding.cbEnglish.isChecked = false
                setLanguage("mr")
            }
        }


        Log.e("tag","Language : $language")

        binding.btnSubmit.setOnClickListener {
            Log.e("tag","Language btn click : $language")

            if (language.isNullOrEmpty()) {
                    showToast(getString(R.string.txxtselect_app_language))
            } else {
                navigateToLogin()
            }
        }
        
    }

    private fun navigateToLogin() {
        if(isuserlgin){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }else{
            // showBottomSheetDialog()
            startActivity(Intent(this, RoleSelectedActivity::class.java))
            finish()

        }
    }



    private fun setLanguage(languageCode: String) {
        language = languageCode
        Log.e("tag", "Language set : $language")
        Log.e("tag", "languageCode set : $languageCode")
        applyLanguage(languageCode)
        Log.e("tag", "Language set 2 : $language")
        Log.e("tag", "languageCode set 2 : $languageCode")
    }


    private fun applyLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    override fun onBackPressed() {
        super.onBackPressed()

        finishAffinity()
        onBackPressedDispatcher.onBackPressed()

    }

}*/
