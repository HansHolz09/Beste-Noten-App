package com.hansholz.bestenotenapp.utils

import kotlin.math.pow
import kotlin.math.roundToInt

fun Float.roundToDecimals(decimals: Int): Float {
    val factor = 10.0.pow(decimals).toFloat()
    return (this * factor).roundToInt() / factor
}
