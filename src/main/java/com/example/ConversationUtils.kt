package com.example

fun spokenList(items: Set<String>) = spokenList(items.toList())
fun spokenList(items: List<String>): String {
    if (items.size < 2) return items.firstOrNull() ?: ""
    return items.dropLast(1).joinToString(", ") + " og " + items.last()
}