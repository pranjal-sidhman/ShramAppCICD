package com.uvk.shramapplication.response


data class WalletResponse(
    val status: Boolean,
    val message: String,
    val status_code: Int,
    val data: List<RazorpayOrderData>
)

data class RazorpayOrderData(
    val balance: String,
    val order_id: String,
    val amount: Int,
    val currency: String
)


data class WalletHistoryResponse(
    val status: Boolean,
    val message: String,
    val data: List<WalletHistoryData>
)

data class WalletHistoryData(
    val id  : String,
    val user_id: String,
    val type: String,
    val name: String,
    val amount: String,
    val payment_id: String,
    val description: String,
    val created_at: String
)