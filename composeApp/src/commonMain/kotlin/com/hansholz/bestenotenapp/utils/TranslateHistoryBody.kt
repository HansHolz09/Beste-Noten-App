package com.hansholz.bestenotenapp.utils

fun translateHistoryBody(body: String): String = body
    .replace("Created", "Erstellt")
    .replace("Updated Value", "Wert geändert")
    .replace("Updated Tendency", "Tendenz geändert")
    .replace("Updated Given_at", "Wann-Gegeben geändert")
    .replace("->", "zu")
    .replace("''", "keine")
    .replace("'", "")