package com.hansholz.bestenotenapp.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random

fun getGreeting(name: String): String {
    val now = Clock.System.now()
    val localTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
    val hour = localTime.hour


    val motivationssprueche = listOf(
        "Wissen ist der Schlüssel zum Erfolg 🔑",
        "Jeder Fehler ist eine Chance zu lernen.",
        "Bleib neugierig!",
        "Du schaffst das! 💪",
        "Auch die längste Reise beginnt mit einem ersten Schritt. 🌱",
        "Glaube an dich, dann ist alles möglich. 🌟"
    )

    val morgenGruesse = listOf(
        "Guten Morgen, $name ☀️ Auf in einen neuen Lerntag!",
        "Moin $name ☕ Zeit, dein Gehirn zu starten.",
        "Der frühe Vogel fängt das Wissen, $name 🦉",
        "Bereit für die Schule, $name? Zeig, was in dir steckt 🚀"
    )

    val vormittagsGruesse = listOf(
        "Hi $name, wie läuft die Schule? 📚",
        "Viel Erfolg im Unterricht heute, $name 👍",
        "Hallo $name. Konzentration und dann ab in die Pause.",
        "Na $name, schon was Neues gelernt heute? 💡"
    )

    val nachmittagsGruesse = listOf(
        "Willkommen zurück, $name 🤓 Zeit, die Hausaufgaben zu rocken.",
        "Hallo $name. Was steht heute auf deinem Lernplan? ✍️",
        "Pause vorbei, $name? Lass uns die nächste Aufgabe meistern.",
        "Nachmittag, $name. Ein guter Zeitpunkt, um Wissen zu festigen 🧠"
    )

    val abendGruesse = listOf(
        "Guten Abend, $name. Alles für heute geschafft? ✅",
        "Schönen Feierabend, $name. Du hast es dir verdient 😌",
        "Na $name, noch eine kleine Runde Vokabeln vor dem Schlafen? ✨",
        "Gut gemacht heute, $name. Morgen ist ein neuer Tag 🌙"
    )

    val allgemeineGruesse = listOf(
        "Hallo $name.",
        "Willkommen zurück in deiner Lern-Zentrale.",
        "Na, $name, bereit für eine Dosis Wissen?"
    )


    val passendeListe = when (hour) {
        in 5..8   -> morgenGruesse
        in 9..13  -> vormittagsGruesse
        in 14..17 -> nachmittagsGruesse
        in 18..22 -> abendGruesse
        else      -> allgemeineGruesse
    }

    var begruessung = passendeListe.random()

    if (Random.nextInt(0, 3) == 0 && hour in 9..22) {
        begruessung += "\n${motivationssprueche.random()}"
    }

    return begruessung
}