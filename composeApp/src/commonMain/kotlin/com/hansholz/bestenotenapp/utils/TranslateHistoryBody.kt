package com.hansholz.bestenotenapp.utils

import com.hansholz.bestenotenapp.api.models.History

fun translateHistoryBody(
    type: String,
    body: String,
    teachers: List<Pair<Int, String>>,
): String =
    body
        .let {
            if (it.startsWith("Updated Read_at")) {
                try {
                    "$type am ${formateDate(it.takeLast(21).dropLast(11))} um ${it.takeLast(10).dropLast(5)} als gelesen markiert"
                } catch (e: Exception) {
                    e.printStackTrace()
                    it
                }
            } else {
                it
            }
        }.let {
            teachers.fold(it) { str, (id, name) ->
                str.replace(id.toString(), name)
            }
        }.replace(Regex("""(\d{4})-(\d{2})-(\d{2}) 00:00:00"""), "$3.$2.$1")
        .replace(Regex("""(\d{4})-(\d{2})-(\d{2})"""), "$3.$2.$1")
        .replace("Created", "$type erstellt")
        .replace("Deleted", "$type gelöscht")
        .replace("Restored", "$type wiederhergestellt")
        .replace("Updated Value", "Wert der $type geändert")
        .replace("Updated Tendency", "Tendenz der $type geändert")
        .replace("Updated Given_at", "Geändert, wann die $type gegeben wurde")
        .replace("Updated Visible_from", "Geändert, ab wann die $type sichtbar ist")
        .replace("Updated Teacher_id", "Lehrer der $type geändert")
        .replace("Updated Type", "Typ der $type geändert")
        .replace("Updated Name", "Name der $type geändert")
        .replace("->", "zu")
        .replace("''", "nichts")
        .replace("'", "")

fun List<History>.filterHistory(): List<History> =
    this
        .filter { !it.body.contains("Read_by_type") }
        .filter { !it.body.contains("Read_by_id") }
