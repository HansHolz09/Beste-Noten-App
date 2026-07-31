package com.hansholz.bestenotenapp.utils

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun getGreeting(name: String): String {
    val now = Clock.System.now()
    val localTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
    val hour = localTime.hour
    val dayOfWeek = localTime.dayOfWeek

    val motivationssprueche =
        listOf(
            "Wissen ist der Schlüssel zum Erfolg. 🔑",
            "Jeder Fehler ist eine Chance zu lernen. ✨",
            "Bleib neugierig und hinterfrage alles! 🤔",
            "Du schaffst das! 💪",
            "Auch die längste Reise beginnt mit einem ersten Schritt. 🌱",
            "Glaube an dich, dann ist alles möglich. 🌟",
            "Der einzige Weg, großartige Arbeit zu leisten, ist, zu lieben, was man tut. ❤️",
            "Erfolg ist die Summe kleiner Anstrengungen, die Tag für Tag wiederholt werden. 🎯",
            "Sei stärker als deine stärkste Ausrede.",
            "Die Zukunft gehört denen, die an die Schönheit ihrer Träume glauben. 🌈",
            "Steter Tropfen höhlt den Stein. Bleib dran!",
            "Fordere dich selbst heraus, du weißt nie, wozu du fähig bist.",
            "Ein Ziel ohne Plan ist nur ein Wunsch. Schreib's auf! ✏️",
            "Lernen ist wie Rudern gegen den Strom. Hört man damit auf, treibt man zurück. 🛶",
            "Der Schmerz, den du heute fühlst, ist die Stärke, die du morgen spürst. 🏋️",
            "Es ist nicht wichtig, wie langsam du gehst, solange du nicht stehen bleibst. 🐢",
            "Bildung ist die mächtigste Waffe, die du verwenden kannst, um die Welt zu verändern. 🌍",
            "Sei heute besser als du gestern warst. 📈",
        )

    val themenAnstupser =
        listOf(
            "Wie wär's heute mit einer Runde Mathe-Übungen? 🧮",
            "Zeit, die Geschichtsbücher zu wälzen und in die Vergangenheit zu reisen. 📜",
            "Lust, heute ein paar neue Vokabeln in einer Fremdsprache zu lernen? 🗣️",
            "Die Welt der Naturwissenschaften wartet darauf, von dir entdeckt zu werden. 🔬",
            "Lass deiner Kreativität im Kunst- oder Musikunterricht freien Lauf. 🎨🎶",
            "Ein Gedicht oder eine Kurzgeschichte lesen? Das erweitert den Horizont. 📚",
            "Sport ist ein super Ausgleich zum Lernen. Schon bewegt heute? 🏃‍♀️",
            "Vergiss nicht, auch die Formeln in Physik und Chemie zu wiederholen. 🧪",
            "Geografie-Skills verbessern? Entdecke die Welt auf der Karte! 🗺️",
        )

    val notenAufmunterung =
        listOf(
            "Jede Note ist nur eine Momentaufnahme. Wichtig ist, was du daraus machst. 📸",
            "Denk daran: Der Weg ist das Ziel, nicht nur das Ergebnis. 🚶‍♂️",
            "Jede gelöste Aufgabe bringt dich deinem Ziel einen Schritt näher. 🪜",
            "Sei stolz auf deine Fortschritte, egal wie klein sie scheinen. 💖",
            "Organisation ist die halbe Miete. Plane deine Aufgaben also sorgfältig! 🗓️",
            "Eine gute Note ist eine Belohnung für deine Mühe. Feiere deine Erfolge! 🎉",
            "Auch aus einer schlechten Note kann man viel lernen. Kopf hoch! 👍",
            "Dein Einsatz wird sich auszahlen, versprochen! 🤞",
        )

    val wochenendeTipps =
        listOf(
            "Nutze das Wochenende, um die Akkus wieder aufzuladen. 🔋",
            "Ein gutes Buch und eine Tasse Tee wirken Wunder. 📖☕",
            "Triff dich mit Freunden, das gibt neue Energie! 😊",
            "Wie wäre es mit einem Spaziergang an der frischen Luft? 🌳☀️",
            "Zeit für dein Hobby! Was wolltest du schon immer mal wieder machen? 🎮",
            "Manchmal ist Nichtstun das Produktivste, was man tun kann. 🧘",
        )

    val wochentagMorgen =
        listOf(
            "Guten Morgen, $name! ☀️ Auf in einen neuen Lerntag!",
            "Moin $name! ☕ Zeit, dein Gehirn zu starten.",
            "Der frühe Vogel fängt das Wissen, $name. 🦉",
            "Bereit für die Schule, $name? Zeig, was in dir steckt! 🚀",
            "Ein neuer Tag, eine neue Chance, zu glänzen, $name!",
            "Guten Morgen, $name. Ein gutes Frühstück und los geht's!",
            "Steh auf, $name, die Welt des Wissens wartet auf dich.",
        )

    val wochentagVormittag =
        listOf(
            "Hi $name, wie läuft die Schule? 📚",
            "Viel Erfolg im Unterricht heute, $name! 👍",
            "Hallo $name. Konzentration und dann ab in die wohlverdiente Pause.",
            "Na $name, schon was Neues gelernt heute? 💡",
            "Halte durch, $name! Das Mittagessen ist nicht mehr weit. 🥪",
            "Hallo $name. Bleib fokussiert, du machst das super!",
            "Ein produktiver Vormittag, $name?",
        )

    val wochentagNachmittag =
        listOf(
            "Willkommen zurück, $name! 🤓 Zeit, die Hausaufgaben zu rocken.",
            "Hallo $name. Was steht heute auf deinem Lernplan? ✍️",
            "Pause vorbei, $name? Meistere die nächste Aufgabe!",
            "Nachmittag, $name. Ein guter Zeitpunkt, um Wissen zu festigen. 🧠",
            "Schule geschafft für heute, $name? Jetzt geht's ans Wiederholen.",
            "Hi $name, wie war dein Schultag? Check deine Noten!",
            "Der Nachmittag ist perfekt, um Projekte voranzutreiben, $name.",
        )

    val wochentagAbend =
        listOf(
            "Guten Abend, $name. Alles für heute geschafft? ✅",
            "Schönen Feierabend, $name. Du hast es dir verdient. 😌",
            "Na $name, noch eine kleine Runde Vokabeln vor dem Schlafen? ✨",
            "Gut gemacht heute, $name. Morgen ist ein neuer Tag. 🌙",
            "Zeit zum Entspannen, $name. Dein Gehirn braucht auch mal eine Pause.",
            "Lass den Tag Revue passieren, $name. Was hast du heute gelernt?",
            "Guten Abend, $name. Vergiss nicht, stolz auf dich zu sein.",
        )

    val wochenendeMorgen =
        listOf(
            "Schönes Wochenende, $name! ☀️ Gut geschlafen?",
            "Guten Morgen, $name! Zeit für ein entspanntes Frühstück. 🥞",
            "Wochenende! 🎉 Was steht Schönes auf dem Plan, $name?",
            "Moin $name! Genieße den freien Tag. 😎",
        )

    val wochenendeTagsueber =
        listOf(
            "Hallo $name, ich hoffe, du genießt dein Wochenende! 😊",
            "Hi $name! Eine kleine Lernpause am Wochenende? Oder lieber komplett frei? 😉",
            "Na $name, entspannst du schön? Das hast du dir verdient!",
            "Willkommen, $name. Auch am Wochenende für dich da, falls du deine Noten sehen willst.",
        )

    val nachtUndFallbackGruesse =
        listOf(
            "Hallo $name! Schön, dich zu sehen.",
            "Willkommen zurück in deiner Noten-Zentrale, $name.",
            "Na, $name, bereit für eine Dosis Wissen?",
            "Spät unterwegs, $name? Eine kleine Lerneinheit geht immer. 😉",
            "Hallo $name. Jede Zeit ist eine gute Zeit zum Lernen.",
            "Willkommen, $name. Lass uns deine Ziele verfolgen.",
        )

    val istWochenende = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY

    val passendeBegruessungsListe =
        if (istWochenende) {
            when (hour) {
                in 8..11 -> wochenendeMorgen
                in 12..22 -> wochenendeTagsueber
                else -> nachtUndFallbackGruesse
            }
        } else {
            when (hour) {
                in 5..9 -> wochentagMorgen
                in 10..13 -> wochentagVormittag
                in 14..17 -> wochentagNachmittag
                in 18..22 -> wochentagAbend
                else -> nachtUndFallbackGruesse
            }
        }

    val begruessung = StringBuilder(passendeBegruessungsListe.random())

    if (Random.nextInt(0, 4) == 0) {
        if (istWochenende && hour > 11) {
            begruessung.append("\n\n")
            when (Random.nextInt(0, 2)) {
                0 -> begruessung.append(wochenendeTipps.random())
                1 -> begruessung.append(motivationssprueche.random())
            }
        } else if (!istWochenende && hour in 9..22) {
            begruessung.append("\n\n")
            when (Random.nextInt(0, 3)) {
                0 -> begruessung.append(motivationssprueche.random())
                1 -> begruessung.append(themenAnstupser.random())
                2 -> begruessung.append(notenAufmunterung.random())
            }
        }
    }

    return begruessung.toString()
}
