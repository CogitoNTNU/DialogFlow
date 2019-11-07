package com.example

object ResponseGenerator {

    fun addPizza(result: AddPizza): String {
        val speakList = result.pizzas
                .groupBy { it }
                .map { (pizza, amount) ->
                    "${amount.size} ${pizza.describeChangesToUser()}"
                }
        return "Klart det! Jeg har lagt til ${spokenList(speakList)}. Skal det være noe mer?"
    }

    fun removePizza(removal: RemovePizza): String {
        val speakList = removal.pizzas
                .groupBy { it }
                .map { (pizza, amount) ->
                    "${amount.size} ${pizza.describeChangesToUser()}"
                }
        return "Jeg har tatt vekk ${spokenList(speakList)} fra bestillingen."
    }

    fun pizzaChange(change: ChangePizza): String {
        var outString = "Greit, "
        if (change.additions.isNotEmpty()) {
            outString += "legger til ${spokenList(change.additions)} "
            outString += if (change.removals.isNotEmpty()) "og " else "på "
        }
        if (change.removals.isNotEmpty()) {
            outString += "fjerner ${spokenList(change.additions)} fra "
        }
        outString += "${spokenList(change.pizzas.map { it.name })}."
        return outString
    }

    fun anythingMore() = "Vil du ha noen flere pizzaer?"
}