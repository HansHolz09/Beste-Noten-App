package com.hansholz.bestenotenapp.smartspacer.requirement

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object SchooltimeRequirementSettings {
    private fun prefs(ctx: Context): SharedPreferences =
        ctx.getSharedPreferences("school_hours_requirement", Context.MODE_PRIVATE)

    fun getPaddingBefore(ctx: Context, id: String): Int =
        prefs(ctx).getInt("padding_before_$id", 0)

    fun getPaddingAfter(ctx: Context, id: String): Int =
        prefs(ctx).getInt("padding_after_$id", 0)

    fun setPaddingBefore(ctx: Context, id: String, minutes: Int) {
        prefs(ctx).edit { putInt("padding_before_$id", minutes.coerceIn(0, 120)) }
    }

    fun setPaddingAfter(ctx: Context, id: String, minutes: Int) {
        prefs(ctx).edit { putInt("padding_after_$id", minutes.coerceIn(0, 120)) }
    }
}