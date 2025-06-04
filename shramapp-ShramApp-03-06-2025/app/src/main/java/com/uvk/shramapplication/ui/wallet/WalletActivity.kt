package com.uvk.shramapplication.ui.wallet

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mahindra.serviceengineer.savedata.isOnline
import com.mahindra.serviceengineer.savedata.languageName
import com.mahindra.serviceengineer.savedata.mobile_no
import com.mahindra.serviceengineer.savedata.userid
import com.mahindra.serviceengineer.savedata.username
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.uvk.shramapplication.R
import com.uvk.shramapplication.base.BaseResponse
import com.uvk.shramapplication.databinding.ActivityWalletBinding
import com.uvk.shramapplication.helper.CommenViewModel
import com.uvk.shramapplication.helper.TranslationHelper
import com.uvk.shramapplication.helper.TransparentProgressDialog
import com.uvk.shramapplication.response.RazorpayOrderData
import com.uvk.shramapplication.response.WalletHistoryData
import com.uvk.shramapplication.ui.employeer.home.jobPostList.PostedJobListAdapter
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import org.json.JSONObject

class WalletActivity : AppCompatActivity(), PaymentResultListener {

    private lateinit var binding: ActivityWalletBinding
    private val viewModel by viewModels<CommenViewModel>()
    private lateinit var razorpayCheckout: Checkout
    private var pd: TransparentProgressDialog? = null
    private var Balance : String? = null
    private var isBalanceVisible = false
    private lateinit var orderList: List<RazorpayOrderData>
    private lateinit var walletHistoryList: List<WalletHistoryData>
    private var orderId: String? = null
    private lateinit var historyWalletAdapter: HistoryWalletAdapter
    private lateinit var allButtons: List<Button>
    val TAG = "WalletActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pd = TransparentProgressDialog(this, R.drawable.progress)
        Checkout.preload(applicationContext)

        lifecycleScope.launch {
            val translatedText = TranslationHelper.translateText("Wallet", languageName)
            binding.tlName.text = translatedText

            val btnPay = TranslationHelper.translateText("Add Balance", languageName)
            binding.btnPay.text = btnPay

            val ViewHis = TranslationHelper.translateText("Transaction History", languageName)
            binding.tvWalletHis.text = ViewHis

            val myBal = TranslationHelper.translateText("My Balance", languageName)
            binding.tvMyBalance.text = myBal

        }

        setupUI()
        getBalance()
        observeWalletResult()
        getHistoryWallet()


        allButtons = listOf(binding.btn50, binding.btn100, binding.btn200, binding.btn500)

        // Set default selection to ₹50
        selectAmount(50, binding.btn50)

        binding.btn50.setOnClickListener { selectAmount(50, binding.btn50) }
        binding.btn100.setOnClickListener { selectAmount(100, binding.btn100) }
        binding.btn200.setOnClickListener { selectAmount(200, binding.btn200) }
        binding.btn500.setOnClickListener { selectAmount(500, binding.btn500) }

        binding.addToWalletBtn.setOnClickListener {
            val amount = binding.amountEditText.text.toString().trim()
            if (amount.isNotEmpty() && amount.toIntOrNull() != null && amount.toInt() >= 50) {
                createOrder(amount)
            } else {
                lifecycleScope.launch {
                    Toast.makeText(
                        this@WalletActivity,
                        TranslationHelper.translateText("Enter at least ₹50", languageName),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }


        binding.btnPay.setOnClickListener {
            showPaymentDialog()
        }
    }

    private fun selectAmount(amount: Int, selectedButton: Button) {
        binding.amountEditText.setText(amount.toString())
        lifecycleScope.launch {
            binding.addToWalletBtn.text = TranslationHelper.translateText("Add ₹$amount to Wallet", languageName)
        }


        for (button in allButtons) {
            if (button == selectedButton) {
                button.setBackgroundColor(ContextCompat.getColor(this, R.color.selected_button_bg))
                button.setTextColor(ContextCompat.getColor(this, R.color.selected_button_text))
            } else {
                button.setBackgroundColor(ContextCompat.getColor(this, R.color.unselected_button_bg))
                button.setTextColor(ContextCompat.getColor(this, R.color.unselected_button_text))
            }
        }
    }

    private fun getHistoryWallet() {
        if (isOnline) {
            pd?.show() // Show loading indicator before making the API call

            viewModel.walletHistoryResult.observe(this) { response ->
                when (response) {
                    is BaseResponse.Success -> {
                        pd?.dismiss() // Dismiss loading indicator

                        Log.e("tag", "Wallet history resp : $response")

                        if (response.data?.data.isNullOrEmpty()) {
                            // No data available, show 'no data' image
                            binding.nodataimg.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE
                        } else {
                            // Data is available, show the RecyclerView
                            walletHistoryList = response.data?.data ?: emptyList()
                            binding.recyclerView.visibility = View.VISIBLE
                            binding.nodataimg.visibility = View.GONE

                            binding.recyclerView.layoutManager = LinearLayoutManager(this)
                            historyWalletAdapter = HistoryWalletAdapter(this, walletHistoryList)
                            binding.recyclerView.adapter = historyWalletAdapter
                            historyWalletAdapter.notifyDataSetChanged()
                        }
                    }

                    is BaseResponse.Error -> {
                        pd?.dismiss() // Dismiss loading indicator
                        Toast.makeText(this@WalletActivity, response.msg, Toast.LENGTH_SHORT)
                            .show() // Show error message
                    }

                    is BaseResponse.Loading -> {
                        // Show loading indicator if needed
                        pd?.show()
                    }
                }
            }

            viewModel.getWalletHistory(userid)

        } else {
            Toast.makeText(this@WalletActivity, "Internet not connected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupUI() {
        binding.backicon.setOnClickListener { finish() }

        lifecycleScope.launch {
            binding.tlName.text = TranslationHelper.translateText("Wallet", languageName)
            binding.btnPay.text = TranslationHelper.translateText("Add Balance", languageName)
            binding.titleTextView.text = TranslationHelper.translateText("Shram Pay Balance", languageName)
            binding.tvNote.text = TranslationHelper.translateText("You can add up to ₹500.00", languageName)
            binding.amountEditText.hint = TranslationHelper.translateText("Enter Amount", languageName)
            binding.tvNote1.text = TranslationHelper.translateText("Add a minimum of ₹50.00 or more", languageName)
            binding.addToWalletBtn.text = TranslationHelper.translateText("Add ₹${binding.amountEditText.text} to Wallet", languageName)

        }


        binding.toggleBalanceButton.setOnClickListener {
            isBalanceVisible = !isBalanceVisible
            //getBalance()
            lifecycleScope.launch {
                try {
                    if (isBalanceVisible) {
                        val balance = TranslationHelper.translateText(
                            "INR " + Balance,
                            languageName
                        )

                        binding.tvbal.text = balance
                        binding.balanceTextView.text = balance
                        binding.toggleBalanceButton.setImageDrawable(
                            ContextCompat.getDrawable(this@WalletActivity, R.drawable.eye_view)
                        )
                    } else {
                        val balanceLength = Balance!!.length
                        val hiddenBalance = "X".repeat(balanceLength)
                        binding.tvbal.text = hiddenBalance
                        binding.balanceTextView.text = hiddenBalance
                        binding.toggleBalanceButton.setImageDrawable(
                            ContextCompat.getDrawable(this@WalletActivity, R.drawable.eye_hide)
                        )
                    }

                } catch (e: Exception) {
                    Log.e(
                        "Translation Error",
                        "Error translating balance: ${e.message}"
                    )
                }
            }
        }
    }

    private fun getBalance() {
        try {
            if (isOnline) {
                viewModel.walletBalanceResult.observe(this) { response ->
                    pd?.show()
                    when (response) {
                        is BaseResponse.Loading -> {
                            pd?.show()
                        }

                        is BaseResponse.Success -> {
                            pd?.dismiss()
                            response.data?.let { apiResponse ->

                                lifecycleScope.launch {
                                    try {

                                        orderList = response.data?.data!!

                                        Balance = orderList[0].balance

                                        val balanceLength = Balance!!.length
                                        val hiddenBalance = "X".repeat(balanceLength)
                                        binding.balanceTextView.text = hiddenBalance
                                        binding.tvbal.text = hiddenBalance
                                        binding.toggleBalanceButton.setImageDrawable(
                                            ContextCompat.getDrawable(this@WalletActivity, R.drawable.eye_hide)
                                        )


                                    } catch (e: Exception) {
                                        Log.e(
                                            "Translation Error",
                                            "Error translating balance: ${e.message}"
                                        )
                                    }
                                }
                            }
                        }

                        is BaseResponse.Error -> {
                            pd?.dismiss()

                        }

                        else -> {
                            Log.e("Wallet balance", "Unhandled case")
                        }
                    }
                }

                viewModel.getViewBalance(userid)

            } else {
                Toast.makeText(this, "Internet not connected", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Exception) {
            // Handle any exception that might occur during the process
            Log.e(TAG, "Error occurred: balance ${e.localizedMessage}")
        }
    }


    private fun observeWalletResult() {
        viewModel.paymentSucResult.observe(this) { result ->
            when (result) {
                is BaseResponse.Loading -> {
                    pd?.show()
                }

                is BaseResponse.Success -> {
                    pd?.dismiss()
                    lifecycleScope.launch {
                        Toast.makeText(this@WalletActivity, TranslationHelper.translateText("${result.data?.message}", languageName), Toast.LENGTH_SHORT).show()
                        getBalance()
                        getHistoryWallet()
                    }
                    Log.e("Succ", "Wallet : ${result.data?.message}")
                    Log.e("Succ", "Wallet res: ${result}")
                }

                is BaseResponse.Error -> {
                    pd?.dismiss()
                    Log.e("Succ", "Server error: ${result.msg}")
                    Toast.makeText(this, "Server error: ${result.msg}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun showPaymentDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_recharge_wallet, null)
        val etAmount = dialogView.findViewById<EditText>(R.id.etAmount)
        val btnPay = dialogView.findViewById<Button>(R.id.btnPay)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        lifecycleScope.launch {
            etAmount.hint = TranslationHelper.translateText("Enter amount (in ₹)", languageName)
            btnPay.text = TranslationHelper.translateText("Pay Now", languageName)
            btnCancel.text = TranslationHelper.translateText("Cancel", languageName)
        }

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        btnPay.setOnClickListener {
            val amountText = etAmount.text.toString().trim()
            if (amountText.isNotEmpty()) {
                createOrder(amountText)
                alertDialog.dismiss()
            } else {
                lifecycleScope.launch {
                    val msg =
                        TranslationHelper.translateText("Please Enter amount (in ₹)", languageName)
                    Toast.makeText(this@WalletActivity, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnCancel.setOnClickListener { alertDialog.dismiss() }

        alertDialog.show()
    }

    private fun createOrder(amount: String) {
        if (!isOnline) {
            Toast.makeText(this, "Internet not connected", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.walletcreateOrderResult.observe(this) { response ->
            when (response) {
                is BaseResponse.Loading -> pd?.show()
                is BaseResponse.Success -> {
                    pd?.dismiss()

                    val orderData = response.data?.data?.firstOrNull()
                    if (orderData != null) {
                        this.orderId = orderData.order_id
                        startRazorpayCheckout(orderData)
                    } else {
                        Log.e("Wallet", "data null: ${response.data?.message}")
                        Toast.makeText(this, "${response.data?.message}", Toast.LENGTH_SHORT).show()
                    }
                }

                is BaseResponse.Error -> {
                    pd?.dismiss()
                    Toast.makeText(this, " ${response.msg}", Toast.LENGTH_SHORT).show()
                }

                else -> Log.e("Wallet", "Unhandled case")
            }
        }

        viewModel.createOrder(userid, amount)
    }


    private fun startRazorpayCheckout(orderData: RazorpayOrderData) {
        razorpayCheckout = Checkout()
         razorpayCheckout.setKeyID("rzp_test_uQE8CY1kvRdgXC")
       // razorpayCheckout.setKeyID("rzp_live_dxMrFl30491dqF")
        // razorpayCheckout.setKeyID("rzp_live_KPfzHyYiX1f4tV")

        try {
            val options = JSONObject()
            options.put("name", "ShramKart")
            options.put("description", "Wallet Recharge")
            options.put("currency", orderData.currency)
            options.put("order_id", orderData.order_id)
            options.put("amount", orderData.amount)

            val prefill = JSONObject()
            prefill.put("name", "test@gmail.com")
            prefill.put("contact", mobile_no)

            options.put("prefill", prefill)

            razorpayCheckout.open(this@WalletActivity, options)
            Log.e("wallet", "Options: $options")
        } catch (e: Exception) {
            Log.e("RazorpayError", "Error in starting Razorpay Checkout", e)
        }
    }

    override fun onPaymentSuccess(razorpayPaymentID: String) {
        Toast.makeText(this, "Payment Success: $razorpayPaymentID", Toast.LENGTH_LONG).show()
        Log.e("tag", "Payment Success: $razorpayPaymentID")

        orderId?.let {
            viewModel.paymentSuccess(userid, razorpayPaymentID, it)
        } ?: run {
            Toast.makeText(this, "Order ID missing for payment success", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPaymentError(code: Int, response: String?) {
        Log.e("tag", "Payment failed: $response")

        val message = try {
            JSONObject(response ?: "{}")
                .optJSONObject("error")
                ?.optString("description") ?: "Payment failed"
        } catch (e: Exception) {
            "Payment failed: Unexpected error"
        }

        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }


}
