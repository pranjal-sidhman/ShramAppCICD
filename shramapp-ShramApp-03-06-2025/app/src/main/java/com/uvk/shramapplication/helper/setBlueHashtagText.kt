package com.uvk.shramapplication.helper

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.uvk.shramapplication.R
import android.content.Context

fun setBlueHashtagText(context: Context, textView: TextView, text: String) {
    val regex = "#\\w+".toRegex()
    val colorPrimary = ContextCompat.getColor(context, R.color.colorPrimary)
    val spannableString = SpannableString(text)

    regex.findAll(text).forEach { match ->
        spannableString.setSpan(
            ForegroundColorSpan(colorPrimary),
            match.range.first,
            match.range.last + 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    textView.text = spannableString
}
