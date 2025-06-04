package com.uvk.shramapplication.ui.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.uvk.shramapplication.MainActivity
import com.uvk.shramapplication.R
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.databinding.ActivityLoginBinding
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.ui.otp.OTPActivity
import com.uvk.shramapplication.ui.registration.SignUpActivity
import com.mahindra.serviceengineer.savedata.companyName
import com.mahindra.serviceengineer.savedata.designationName
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.isuserlgin
import com.mahindra.serviceengineer.savedata.languageName
import com.mahindra.serviceengineer.savedata.roleId
import com.mahindra.serviceengineer.savedata.userid
import com.mahindra.serviceengineer.savedata.token
import com.mahindra.serviceengineer.savedata.user_profile
import com.mahindra.serviceengineer.savedata.usermobilenumber
import com.mahindra.serviceengineer.savedata.username
import com.mahindra.serviceengineer.savedata.userotp
import com.uvk.shramapplication.helper.TranslationHelper
import com.uvk.shramapplication.helper.savePdfToStorage
import com.uvk.shramapplication.savedata.language.LanguagePref
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val viewModel by viewModels<LoginViewModel>()
    var pd: TransparentProgressDialog? = null
    private lateinit var loginList: List<LoginData>
    val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        pd = TransparentProgressDialog(this, R.drawable.progress)

        if (isuserlgin) {

            startActivity(Intent(this, MainActivity::class.java))
            finish()

        }

        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        binding.tvTermsCondition.setOnClickListener {
            val url = "https://callisol.com/Shram/termsandcondition.html"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)

        }

        binding.btGetOtp.setOnClickListener {

            if (isOnline) {
                lifecycleScope.launch {
                    try {
                        if (binding.cbTerms.isChecked) {
                            when {

                                binding.etMobile.text.isNullOrEmpty() -> {
                                    val translatedName = TranslationHelper.translateText(
                                        "Please Enter Your Mobile Number", languageName
                                    )
                                    binding.etMobile.error = translatedName
                                    binding.etMobile.requestFocus()
                                }

                                binding.etMobile.text!!.length < 10 -> {
                                    val translatedName = TranslationHelper.translateText(
                                        "Mobile Number should contain at least 10 digits.", languageName
                                    )
                                    binding.etMobile.error = translatedName

                                    binding.etMobile.requestFocus()
                                }

                                !binding.etMobile.text!!.matches(Regex("^[6789][0-9]{9}\$")) -> {
                                    val translatedName = TranslationHelper.translateText(
                                        "Please Enter a valid Indian Mobile Number.", languageName
                                    )
                                    binding.etMobile.error = translatedName
                                    binding.etMobile.requestFocus()
                                }

                                else -> {
                                    getOTP()
                                }
                            }



                            if (binding.etMobile.text!!.isNotEmpty()) {
                                saveMobile(binding.etMobile.text.toString())
                            }
                        } else {
                            val translatedName = TranslationHelper.translateText(
                               "Please accept all Terms and Conditions.", languageName
                            )
                            Toast.makeText(this@LoginActivity, translatedName, Toast.LENGTH_SHORT).show()
                        }

                    } catch (e: Exception) {
                        Log.e("Translation Error", "Error translating states: ${e.message}")
                    }
                }


            } else {
                Toast.makeText(this, "Internet not connected", Toast.LENGTH_SHORT).show()
            }


        }
    }

    private fun saveMobile(mobile: String) {
        usermobilenumber = mobile
    }

    private fun getOTP() {
        try {
            if (isOnline) {
                viewModel.OtpResult.removeObservers(this)
                viewModel.OtpResult.observe(this) { response ->
                    when (response) {
                        is BaseResponse.Loading -> {
                            pd?.show()
                            showLoading()
                        }

                        is BaseResponse.Success -> {
                            pd?.dismiss()
                            stopLoading()

                            response.data?.let { data ->
                                // Logging
                                Log.e("TAG", "Response Code: ${data.code}")
                                Log.e("TAG", "Message: ${data.message}")

                                if(response.data.code == "201"){
                                    val intent = Intent(this, SignUpActivity::class.java)
                                    startActivity(intent)
                                } else {

                                }
                                Toast.makeText(this@LoginActivity, data.message, Toast.LENGTH_SHORT)
                                    .show()
                                // Extracting the first user data safely
                                val userData = data.data.firstOrNull()
                                if (userData != null) {
                                    saveName(userData.name)
                                    saveMobile(userData.mobile_no)
                                  //  saveRoleId(userData.role_id)
                                    saveDesignation(userData.designation)
                                    saveCompanyName(userData.company_name)
                                    saveUserid(userData.id)
                                    saveTOKEN(userData.token ?: "")
                                    saveOTP(userData.otp)
                                    saveProfile(userData.profile_image)


                                    LanguagePref.setUserName(this, userData.name)
                                    LanguagePref.setMobileNo(this, userData.mobile_no)
                                    LanguagePref.setCompanyName(this, userData.company_name)
                                    LanguagePref.setDesignationName(this, userData.designation)
                                    LanguagePref.setUserId(this, userData.id)
                                    LanguagePref.setUserToken(this, userData.token ?: "")
                                    LanguagePref.setUserProfile(this, userData.profile_image)
                                    LanguagePref.setUserOTP(this, userData.otp)
                                    LanguagePref.setRoleId(this, userData.role_id)

                                    Log.e("LoginActivity", "OTP: ${userData.otp}")

                                    if (!userData.otp.isNullOrEmpty()) {
                                        val mobileNo = binding.etMobile.text?.trim().toString()
                                        Log.e("LoginActivity", "Mobile No: $mobileNo")

                                        // Navigate to OTPActivity
                                        val intent = Intent(this, OTPActivity::class.java).apply {
                                            putExtra("MOBILE_NUMBER", mobileNo)
                                        }
                                        startActivity(intent)
                                        finish()
                                    } else {

                                    }


                                } else {
                                    Log.e("LoginActivity", "User data is null or empty")
                                }
                            }
                        }

                        is BaseResponse.Error -> {
                            processError(response.msg)
                            Log.e("TAG", "Error: ${response.msg}")
                            Toast.makeText(this@LoginActivity, response.msg, Toast.LENGTH_SHORT)
                                .show()
                            stopLoading()
                        }

                        else -> {
                            stopLoading()
                        }
                    }
                }

                val mobileNo = binding.etMobile.text?.trim().toString()
                val englishNumber = convertToEnglishDigits(mobileNo)


                if (mobileNo.isNotBlank()) {
                    println(englishNumber) // Output: 9876543210
                    viewModel.userMobileOTP(mobileNumber = englishNumber)
                } else {
                    Toast.makeText(this, "Please enter a valid mobile number", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(this, "Internet not connected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("LoginError", "Error occurred: ${e.localizedMessage}")
            Toast.makeText(this, "An error occurred: ${e.localizedMessage ?: "Unknown error"}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun convertToEnglishDigits(input: String): String {
        val sb = StringBuilder()
        for (char in input) {
            when (char) {
                in '०'..'९' -> sb.append(char - '०') // Devanagari digits to English
                in '0'..'9' -> sb.append(char)       // Already English digits
                // optionally ignore or keep other characters like '+' or '-'
                else -> sb.append(char)
            }
        }
        return sb.toString()
       /* val sb = StringBuilder()
        for (char in input) {
            when (char) {
                in '०'..'९' -> sb.append(char - '०') // Devanagari (Hindi/Marathi) to English
                else -> sb.append(char)
            }
        }
        return sb.toString()*/
    }


    private fun saveCompanyName(companyname: String) {
        companyName = companyname
    }

    private fun saveProfile(profileImage: String) {
        user_profile = profileImage
    }


    private fun showLoading() {
        pd!!.show()
    }

    private fun stopLoading() {
        pd!!.dismiss()
    }

    private fun processError(msg: String?) {
        showToast("Error:$msg")
        Log.e(TAG, "processError : $msg")
    }

    private fun processLogin(data: OtpResponse?) {
        showToast("" + data?.message)
        Log.e(TAG, "processLogin msg:$data")
//        if (!data?.data?.token.isNullOrEmpty()) {
//            data?.data?.token?.let { SessionManager.saveAuthToken(this, it) }
//            navigateToHome()
//        }
    }

    private fun showToast(msg: String) {
        Log.e(TAG, "showToast msg:$msg")
        Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_SHORT).show()
    }

    private fun saveName(name: String) {
        username = name
        Log.e("LoginActivity", "$username")
    }

    private fun saveDesignation(designation: String) {
        designationName = designation
        Log.e("LoginActivity", "$username")
    }

    private fun saveRoleId(roleIds: String) {
        roleId = roleIds
    }

    private fun saveOTP(otp: String) {
        userotp = otp
    }

    private fun saveTOKEN(tokens: String) {
        token = tokens
        Log.e(TAG, "token :$token")
    }

    private fun saveUserid(userId: String) {
        userid = userId
        Log.e(TAG, "userid :$userid")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // optional, to close SplashActivity
    }
}