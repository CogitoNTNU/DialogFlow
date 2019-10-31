package com.example

import java.util.Collections.singletonList

class ActionHandler {
    val order = Order()
    val history = ConversationHistory<Any>()

    @Deprecated("This should be in DialogflowCommunicator") // TODO
    fun addPizza(types: List<Int>, amount: List<Int>, pizzaMenu: PizzaMenu): AddPizza {
        val pizzas: MutableList<Pizza> = mutableListOf()
        for (i in types.indices) {
            val type = types[i]
            for (j in 0 until amount[i]) {
                val pizza = pizzaMenu.getPizza(type)
                if (pizza != null) pizzas.add(pizza)
            }
        }
        return addPizza(pizzas)
    }

    fun addPizza(pizzas: List<Pizza>): AddPizza {
        order.addPizza(pizzas)
        return history.add(AddPizza(pizzas))
    }

    fun setDeliveryAddress(deliver: Boolean, address: String): Boolean = if (deliver) {
        order.deliver(true)
        order.addAddress(address)
        true
    } else {
        order.deliver(false)
        false
    }

    fun findPizza(requestedIngredients: List<String>, pizzaMenu: PizzaMenu): List<Pizza> {
        val suggestions = pizzaMenu.pizzaList
                .sortedByDescending { pizza -> pizza.ingredients.count { it in requestedIngredients } }
                .take(3)
        history.add(PizzaSuggestion(suggestions))
        return suggestions
    }

    @Deprecated("This should be in DialogflowCommunicator") // TODO
    fun removePizza(types: List<Int>, amount: List<Int>): Boolean {
        val removedPizzas = mutableListOf<Pizza>()
        return if (order.pizzas.isNotEmpty()) {
            for (pizza: Pizza in order.pizzas) {
                for (i in types.indices) {
                    if (pizza.nr == types[i]) {
                        for (j in 0 until amount[i]) {
                            removedPizzas.add(pizza)
                        }
                    }
                }
            }
            removePizza(removedPizzas)
            true
        } else {
            false
        }
    }

    private fun removePizza(pizzas: List<Pizza>): RemovePizza {
        for (pizza in pizzas) order.removePizza(pizza)
        return history.add(RemovePizza(pizzas))
    }

    @Throws(AmbiguityException::class)
    fun changePizza(explicitPizza: Pizza?, additions: List<String>, removals: List<String>): ChangePizza = try {
        // Either the user said now which pizza they want to change…
        val pizza = explicitPizza
        //         … or they said so previously:
                ?: history.findCurrentEntity<PizzaMention>().pizzas.singleOrNull()
                // … or we should complain that we don't know what they're talking about:
                ?: throw AmbiguityException(Pizza::class)

        if (order.changePizza(pizza, additions, removals)) {
            history.add(ChangePizza(pizza, additions.toSet(), removals.toSet()))
        } else {
            // This pizza is not ordered yet. Add it, and try again
            addPizza(singletonList(pizza))
            changePizza(pizza, additions, removals)
        }
    } catch (e: NoSuchElementException) {
        throw AmbiguityException(Pizza::class)
    }

}