package com.example

import com.beust.klaxon.Klaxon
import java.io.File


data class Pizza(val nr: Int, val name: String, var ingredients: Set<String>, val price: List<Int>) {
    fun change(remove: List<String>, add: List<String>) {
        ingredients = ingredients - remove.toSet()
        ingredients = ingredients + add.toSet()
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
