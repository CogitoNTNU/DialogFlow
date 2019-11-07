package com.example

import java.util.Collections.singletonList

class ActionHandler {
    val order = Order()
    val history = ConversationHistory<Any>()

    fun addPizza(pizzas: List<Pizza>): AddPizza {
        order.addPizza(pizzas)
        return history.add(AddPizza(pizzas))
    }

    fun setDeliveryAddress(deliver: Boolean): Boolean = if (deliver) {
        order.delivery = true
        true
    } else {
        order.delivery = false
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
        if (pizzas.isEmpty()) {
            val question = history.add(WhichPizzaToRemoveQuestion())
            return removePizza(
                    history.findEntity<PizzaMention> { it !is RemovePizza }?.pizzas
                            ?: throw AmbiguityException(Pizza::class, question)
            )
        }
        for (pizza in pizzas) order.removePizza(pizza)
        return history.add(RemovePizza(pizzas))
    }

    @Throws(AmbiguityException::class)
    fun changePizza(explicitPizza: Pizza?, additions: Set<String>, removals: Set<String>): ChangePizza = try {
        // Either the user said now which pizza they want to change…
        val pizza = explicitPizza
        //         … or they said so previously:
                ?: history.findCurrentEntity<PizzaMention>()?.pizzas?.singleOrNull()
                // … or we should complain that we don't know what they're talking about:
                ?: throw AmbiguityException(
                        Pizza::class,
                        askWhichPizzaToChange(additions, removals)
                )

        if (order.changePizza(pizza, additions, removals)) {
            history.add(ChangePizza(pizza, additions, removals))
        } else {
            // This pizza is not ordered yet. Add it, and try again
            addPizza(singletonList(pizza))
            changePizza(pizza, additions, removals)
        }
    } catch (e: NoSuchElementException) {
        throw AmbiguityException(
                Pizza::class,
                askWhichPizzaToChange(additions, removals)
        )
    }

    private fun askWhichPizzaToChange(additions: Set<String>, removals: Set<String>) =
            history.add(WhichPizzaToChangeQuestion(ChangePizza(emptyList(), additions, removals)))

    fun logPizzaInfo(pizza: Pizza) {
        history.add(UnclassifiedPizzaMention(pizza))
    }

    fun garbageInput(): Question? {
        val latest = history.findCurrentEntity<Any>()
        // If we've already asked a question, don't ask another one
        if (latest is Question) return null
        // If the user has given us delivery info, ask if we should enter the order
        if (order.pizzas.isEmpty()) return FirstPizzaQuestion()
        if (order.address.isBlank()) return DeliverOrPickupQuestion()
        return PlaceOrderQuestion()
    }

    fun pizzaName(pizza: Pizza): PizzaMention? {
        return when (val question = history.findCurrentEntity<Question>()) {
            is WhichPizzaToChangeQuestion -> {
                val change = question.changePizza
                changePizza(pizza, change.additions, change.removals)
            }
            is AnythingMoreQuestion, is FirstPizzaQuestion -> addPizza(singletonList(pizza))
            is WhichPizzaToRemoveQuestion -> removePizza(singletonList(pizza))
            else -> null
        }
    }

}