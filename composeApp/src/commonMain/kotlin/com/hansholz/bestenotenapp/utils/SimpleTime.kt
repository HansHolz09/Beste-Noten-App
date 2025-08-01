package com.hansholz.bestenotenapp.utils

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class SimpleTime(val hour: Int, val minute: Int) : Comparable<SimpleTime> {
    companion object {
        fun parse(timeString: String): SimpleTime {
            return try {
                val parts = timeString.split(":")
                SimpleTime(parts[0].toInt(), parts[1].toInt())
            } catch (_: Exception) {
                SimpleTime(0, 0)
            }
        }

        @OptIn(ExperimentalTime::class)
        fun now(): SimpleTime {
            return try {
                val time = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time
                SimpleTime(time.hour, time.minute)
            } catch (_: Exception) {
                SimpleTime(0, 0)
            }
        }
    }

    override fun compareTo(other: SimpleTime): Int {
        if (this.hour != other.hour) {
            return this.hour.compareTo(other.hour)
        }
        return this.minute.compareTo(other.minute)
    }

    fun minutesUntil(other: SimpleTime): Long {
        val thisTotalMinutes = this.hour * 60 + this.minute
        val otherTotalMinutes = other.hour * 60 + other.minute
        return (otherTotalMinutes - thisTotalMinutes).toLong()
    }
}