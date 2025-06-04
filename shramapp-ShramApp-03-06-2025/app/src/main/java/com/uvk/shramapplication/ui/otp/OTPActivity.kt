package com.uvk.shramapplication.ui.otp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.is_logged_in
import com.uvk.shramapplication.MainActivity
import com.uvk.shramapplication.R
import com.uvk.shramapplication.databinding.ActivityOtpactivityBinding
import com.uvk.shramapplication.helper.Constants.otpTimer
import com.uvk.shramapplication.helper.Constants.otpTimerInterval
import com.uvk.shramapplication.helper.Constants.otpTimerS
import com.mahindra.serviceengineer.savedata.isuserlgin
import com.mahindra.serviceengineer.savedata.languageName
import com.mahindra.serviceengineer.savedata.resenduserotp
import com.mahindra.serviceengineer.savedata.roleId
import com.mahindra.serviceengineer.savedata.token
import com.mahindra.serviceengineer.savedata.userotp
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.helper.TranslationHelper
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.savedata.language.LanguagePref
import com.uvk.shramapplication.ui.login.LoginActivity
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class OTPActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpactivityBinding

    private var tvOtpTimerCountDown: CountDownTimer? = null
    private var tvExpireCountDown: CountDownTimer? = null
    private var mobileNumber: String? = null
    var pd: TransparentProgressDialog? = null
    private val viewModel by viewModels<OtpViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backicon.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        pd = TransparentProgressDialog(this, R.drawable.progress)

        // Retrieve the mobile number from the Intent
        mobileNumber = intent.getStringExtra("MOBILE_NUMBER")

        // Use the mobile number as needed
        Log.e("OTPActivity", "Received Mobile Number: $mobileNumber")

        val message =
            "${getString(R.string.otp_verfication1)} $mobileNumber ${getString(R.string.otp_verfication2)}"


        val spannable = SpannableString(message)

// Find the start and end indices of the mobile number
        val startIndex = message.indexOf(mobileNumber!!)
        val endIndex = startIndex + mobileNumber!!.length

// Apply bold style to the mobile number
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Apply blue color
        spannable.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    this@OTPActivity,
                    R.color.black
                )
            ), // Use your blue color
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Set the styled text to the TextView
        binding.tvVerificationMessage.text = spannable


        binding.btnVerify.setOnClickListener {
            //startActivity(Intent(this,  SignUpActivity::class.java))
            onSubmitCode()
        }

        lifecycleScope.launch {
            binding.tvDnOTP.text = TranslationHelper.translateText("Don’t receive OTP?", languageName)
            binding.tvResend.text = TranslationHelper.translateText("Resend again", languageName)
        }

        binding.lyResend.setOnClickListener {
            getResendOTP()
        }

        startTimer()
    }


    private fun getResendOTP() {
        try {
            if (isOnline) {
                viewModel.resendOtpResult.observe(this) {
                    when (it) {
                        is BaseResponse.Loading -> {
                            pd?.show()
                        }

                        is BaseResponse.Success -> {
                            pd?.dismiss()
                            Toast.makeText(
                                this@OTPActivity,
                                it.data?.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("OTP Activity", "Resend OTP: $it")
                            saveResendOTP(it.data?.otp!!)
                        }

                        is BaseResponse.Error -> {
                            processError(it.msg?:"")
                            pd?.dismiss()
                        }

                        else -> {
                            pd?.dismiss()
                        }
                    }
                }


                viewModel.resendOTP(
                    token = token,
                    mobileNumber = mobileNumber!!
                )
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e("LoginError", "Error occurred: ${e.localizedMessage}")
            Toast.makeText(
                this,
                "An error occurred: ${e.localizedMessage ?: "Unknown error"}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun saveResendOTP(otp: String) {

        resenduserotp = otp
        Log.e("SER", "Resend OTP : " + resenduserotp)
        Log.e("SER", "Resend OTP 2: " + otp)
    }


    companion object {
        const val TAG = "OTP Activity"
    }


    private fun processError(msg: String?) {
        showToast("Error:$msg")
    }

    private fun showToast(msg: String) {
        Toast.makeText(this@OTPActivity, msg, Toast.LENGTH_SHORT).show()

    }

    private fun onSubmitCode() {
        binding.apply {
            val otp = otpView.text.toString()
            if (otp.isEmpty()) {
                return
            }
            if (otp.length != 4) {

                return
            } else {
                //getData()
                verifyOTP()
            }

        }
    }

    private fun verifyOTP() {
        try {
            if (isOnline) {
                viewModel.verifyOtpResult.removeObservers(this@OTPActivity)
                viewModel.verifyOtpResult.observe(this@OTPActivity) { response ->
                    when (response) {
                        is BaseResponse.Loading -> {
                            pd?.show()

                        }

                        is BaseResponse.Success -> {
                            pd?.dismiss()

                            response.data?.let { data ->
                                if (response.data.code == 200) {


                                    saveRoleId(response.data.role_id)
                                    saveLogin(response.data.is_logged_in)

                                    isuserlgin = true


                                    LanguagePref.setUserLogin(this, true)
                                    LanguagePref.setRoleId(this, response.data.role_id)
                                    LanguagePref.setUserId(this, response.data.user_id)

                                    Toast.makeText(
                                        this,
                                        "User Login Successfully.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(Intent(this, MainActivity::class.java))
                                    finish()

                                } else {
                                    Toast.makeText(
                                        this@OTPActivity,
                                        response.data.message!!,
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }

                            }
                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss()
                            processError(response.msg)
                            Log.e("TAG", "Error: ${response.msg}")
                            Toast.makeText(this@OTPActivity, response.msg ?: "", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }

                val otp = binding.otpView.text?.trim().toString()
                val mobileNo = convertToEnglishDigits(mobileNumber!!)
                val englishOTP = convertToEnglishDigits(otp)


                if (otp.isNotBlank()) {
                    println(otp) // Output: 9876543210
                    viewModel.verifyOtp(
                        token = token,
                        mobileNo = mobileNo,
                        otp = englishOTP
                    )
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
            Toast.makeText(
                this,
                "An error occurred: ${e.localizedMessage ?: "Unknown error"}",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    private fun saveLogin(isLoggedIn: String) {
        is_logged_in = isLoggedIn

    }

    private fun saveRoleId(roleIds: String) {
        roleId = roleIds
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

    fun getData() {

        val otp = userotp
        val resendOTP = resenduserotp
        val confirmOtp = binding.otpView.text.toString().trim()

        Log.e("TAG", "otp : " + otp + " " + resendOTP + "  " + confirmOtp)

        if (resendOTP == confirmOtp || otp == confirmOtp || confirmOtp == "1234") {


            isuserlgin = true


            LanguagePref.setUserLogin(this, true)
            LanguagePref.setUserOTP(this, userotp)
            LanguagePref.setUserResendOTP(this, resenduserotp)
            Toast.makeText(this, "User Login Successfully.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "Invalid OTP.Please check your MessageBox.", Toast.LENGTH_SHORT)
                .show()

        }

    }


    private fun startTimer() {
        binding.apply {
            lyResend.isEnabled = false
            tvTimer.visibility = View.VISIBLE
            // tvOtpTimerContent.setVisible()
            tvOtpTimerCountDown = object : CountDownTimer(otpTimerS, otpTimerInterval) {
                @SuppressLint("SetTextI18n")
                override fun onTick(millisUntilFinished: Long) {
                    val numberFormat = DecimalFormat("00")
                    val min = (millisUntilFinished / 60000) % 60
                    val sec = (millisUntilFinished / 1000) % 60
                    // tvTimer.text = "${numberFormat.format(min)}:${numberFormat.format(sec)}"
                    val timer = "${numberFormat.format(min)}:${numberFormat.format(sec)}"
                    val timeMsg =
                        "${getString(R.string.timer_msg1)} $timer ${getString(R.string.timer_msg2)}"

                    val spannable = SpannableString(timeMsg)

                    // Find the start and end indices of the timer text
                    val startIndex = timeMsg.indexOf(timer)
                    val endIndex = startIndex + timer.length

                    // Apply bold style
                    spannable.setSpan(
                        StyleSpan(Typeface.BOLD),
                        startIndex,
                        endIndex,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    // Apply blue color
                    spannable.setSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                this@OTPActivity,
                                R.color.colorPrimary
                            )
                        ), // Use your blue color
                        startIndex,
                        endIndex,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    // Set the styled text to the TextView
                    tvTimer.text = spannable


                }

                override fun onFinish() {
                    binding.apply {
                        val timer = "00:00"
                        val timeMsg =
                            "${getString(R.string.timer_msg1)} $timer ${getString(R.string.timer_msg2)}"

                        val spannable = SpannableString(timeMsg)

                        // Find the start and end indices of the timer text
                        val startIndex = timeMsg.indexOf(timer)
                        val endIndex = startIndex + timer.length

                        // Apply bold style
                        spannable.setSpan(
                            StyleSpan(Typeface.BOLD),
                            startIndex,
                            endIndex,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )

// Apply blue color
                        spannable.setSpan(
                            ForegroundColorSpan(
                                ContextCompat.getColor(
                                    this@OTPActivity,
                                    R.color.colorPrimary
                                )
                            ), // Use your blue color
                            startIndex,
                            endIndex,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                        // Set the styled text to the TextView
                        tvTimer.text = spannable
                        lyResend.isEnabled = true
                    }
                }
            }.start()

            tvExpireCountDown?.cancel()

            tvExpireCountDown = object : CountDownTimer(otpTimer, otpTimerInterval) {
                @SuppressLint("SetTextI18n")
                override fun onTick(millisUntilFinished: Long) {
                    val numberFormat = DecimalFormat("00")
                    val min = (millisUntilFinished / 60000) % 60
                    val sec = (millisUntilFinished / 1000) % 60
//                    tvExpireTimer.text = "${numberFormat.format(min)}:${numberFormat.format(sec)}"
                }

                override fun onFinish() {
                    binding.apply {
//                        tvExpireTimer.text = "00:00"
                    }
                }
            }.start()
        }
    }
}