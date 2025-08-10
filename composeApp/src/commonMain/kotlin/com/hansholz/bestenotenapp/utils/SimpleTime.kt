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

    private fun toTotalMinutes(): Long = hour.toLong() * 60L + minute.toLong()

    private fun fromTotalMinutes(total: Long): SimpleTime {
        val minutesInDay = 24L * 60L
        val m = ((total % minutesInDay) + minutesInDay) % minutesInDay
        val h = (m / 60L).toInt()
        val min = (m % 60L).toInt()
        return SimpleTime(h, min)
    }

    fun plusMinutes(minutes: Long): SimpleTime =
        fromTotalMinutes(toTotalMinutes() + minutes)

    fun minusMinutes(minutes: Long): SimpleTime =
        plusMinutes(-minutes)

    operator fun plus(minutes: Int): SimpleTime =
        plusMinutes(minutes.toLong())

    operator fun minus(minutes: Int): SimpleTime =
        minusMinutes(minutes.toLong())
}