package com.example

fun spokenList(items: Set<String>, connective: String ="og") = spokenList(items.toList(), connective)

fun spokenList(items: List<String>, connective: String = "og"): String {
    if (items.size < 2) return items.firstOrNull() ?: ""
    return "${items.dropLast(1).joinToString(", ")} $connective ${items.last()}"
}