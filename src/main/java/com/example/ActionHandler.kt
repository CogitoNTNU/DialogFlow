package com.example

import java.util.Collections.singletonList

class ActionHandler {
    val order = Order()
    val history = ConversationHistory<Any>()

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

    fun removePizza(pizzas: List<Pizza>): RemovePizza {
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