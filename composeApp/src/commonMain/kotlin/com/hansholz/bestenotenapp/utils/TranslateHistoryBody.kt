package com.hansholz.bestenotenapp.utils

import com.hansholz.bestenotenapp.api.models.History

fun translateHistoryBody(body: String): String =
    body
        .let {
            if (it.startsWith("Updated Read_at")) {
                try {
                    "Am ${formateDate(it.takeLast(21).dropLast(11))} um ${it.takeLast(10).dropLast(5)} als gelesen markiert"
                } catch (e: Exception) {
                    e.printStackTrace()
                    it
                }
            } else {
                it
            }
        }.replace("Created", "Erstellt")
        .replace("Updated Value", "Wert geändert")
        .replace("Updated Tendency", "Tendenz geändert")
        .replace("Updated Given_at", "Wann-Gegeben geändert")
        .replace("->", "zu")
        .replace("''", "keine")
        .replace("'", "")

fun List<History>.filterHistory(): List<History> =
    this
        .filter { !it.body.contains("Read_by_type") }
        .filter { !it.body.contains("Read_by_id") }
