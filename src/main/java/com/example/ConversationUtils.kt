package com.example

fun spokenList(items: Set<String>, connective: String ="og") = spokenList(items.toList(), connective)

fun spokenList(items: List<String>, connective: String = "og"): String {
    if (items.size < 2) return items.firstOrNull() ?: ""
    return "${items.dropLast(1).joinToString(", ")} $connective ${items.last()}"
}

fun getFallbackResponse() = listOf(
        "Det fikk jeg ikke med meg. Kan du si det på nytt?",
        "Jeg fikk ikke med meg det du sa. Kan du si det på nytt?",
        "Unnskyld, kan du si det på nytt?",
        "Hæ?",
        "Kan du si det på nytt?",
        "Unnskyld, det fikk jeg ikke med meg.",
        "Unnskyld, hva sa du?",
        "En gang til?",
        "Hva sa du?",
        "Kan du gjenta det?",
        "Det fikk jeg ikke med meg.",
        "Det skjønte jeg ikke.",
        "Det hørte jeg ikke."
).random()