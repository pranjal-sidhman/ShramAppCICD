package com.uvk.shramapplication.helper

object GlobalFunctions {
    fun smartMaskEmail(email: String): String {
        val parts = email.split("@")
        if (parts.size != 2) return email

        val username = parts[0]
        val domain = parts[1]

        return if (username.length <= 2) {
            username.first() + "*" + "@" + domain
        } else {
            username.first() + "*".repeat(username.length - 2) + username.last() + "@" + domain
        }
    }

    fun maskMobileNumber(mobile: String): String {
        if (mobile.length < 4) return mobile // Too short, no masking

        val visibleStart = mobile.substring(0, 2)
        val visibleEnd = mobile.takeLast(2)
        val maskedMiddle = "*".repeat(mobile.length - 4)

        return "$visibleStart$maskedMiddle$visibleEnd"
    }

}
