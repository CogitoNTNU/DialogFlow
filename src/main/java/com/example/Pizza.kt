package com.example

import com.beust.klaxon.Klaxon
import java.io.File


data class Pizza(
        val nr: Int,
        val name: String,
        val originalIngredients: Set<String>,
        val price: List<Int>,
        private var additions: Set<String> = emptySet(),
        private var removals: Set<String> = emptySet()
) {
    val ingredients: Set<String> = originalIngredients - removals + additions

    fun change(add: Set<String>, remove: Set<String>) {
        additions += add
        removals += remove
        additions -= remove
        removals -= add
    }

    fun describeChangesToUser(): String {
        val output = StringBuilder(name)
        if (additions.isNotEmpty()) output.append(" med ekstra ").append(spokenList(additions))
        if (additions.isNotEmpty() && removals.isNotEmpty()) output.append(", og ")
        if (removals.isNotEmpty()) output.append(" uten ").append(spokenList(removals))
        return output.toString()
    }
}


data class PizzaMenu(val pizzaList: List<Pizza>) {
    fun getPizza(nr: Int) = pizzaList.find { it.nr == nr }

    fun getPizza(name: String) = pizzaList.find { it.name == name }
}

val json = File(Pizza::javaClass.javaClass.getResource("../../pizza_overview.json").file)


fun getPizzaMenu(): PizzaMenu {
    return Klaxon().parse<PizzaMenu>(json)!!
}

fun main(args: Array<String>) {

    print(getPizzaMenu().pizzaList)

}
