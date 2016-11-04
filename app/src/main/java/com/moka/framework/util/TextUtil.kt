package com.moka.framework.util


import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.RelativeSizeSpan


object TextUtil {

    fun afterSmall(first: String, second: String): CharSequence {
        val spannableStringBuilder = SpannableStringBuilder()

        spannableStringBuilder.append(first)

        val spannableString = SpannableString(second)
        spannableString.setSpan(RelativeSizeSpan(0.8f), 0, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableStringBuilder.append(spannableString)

        return spannableStringBuilder
    }

    fun afterSmall(first: String, second: String, ratio: Float): CharSequence {
        val spannableStringBuilder = SpannableStringBuilder()

        spannableStringBuilder.append(first)

        val spannableString = SpannableString(second)
        spannableString.setSpan(RelativeSizeSpan(ratio), 0, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableStringBuilder.append(spannableString)

        return spannableStringBuilder
    }

    fun sizeof(text: String, size: Float): CharSequence {

        val spannableString = SpannableString(text)
        spannableString.setSpan(RelativeSizeSpan(size), 0, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        return spannableString
    }

    fun getDayString(dayInt: Int): String {
        when (dayInt) {
            1 -> return "일요일"
            2 -> return "월요일"
            3 -> return "화요일"
            4 -> return "수요일"
            5 -> return "목요일"
            6 -> return "금요일"
            7 -> return "토요일"
            else -> return "월요일"
        }
    }

}
