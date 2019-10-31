package com.example

import java.util.Collections.singletonList

interface PizzaMention {
    val pizzas: List<Pizza>
}

data class PizzaSuggestion(override val pizzas: List<Pizza>) : PizzaMention
data class AddPizza(override val pizzas: List<Pizza>) : PizzaMention
data class RemovePizza(override val pizzas: List<Pizza>) : PizzaMention
data class UnclassifiedPizzaMention(override val pizzas: List<Pizza>) : PizzaMention {
    constructor(pizza: Pizza) : this(singletonList(pizza))
}

data class ChangePizza(
        override val pizzas: List<Pizza>,
        val additions: Set<String> = emptySet(),
        val removals: Set<String> = emptySet()
) : PizzaMention {
    constructor(pizza: Pizza, additions: Set<String> = emptySet(), removals: Set<String> = emptySet()) :
            this(singletonList(pizza), additions, removals)
}

interface Question
class AnythingMoreQuestion : Question
data class WhichPizzaToChangeQuestion(val changePizza: ChangePizza) : Question
class DeliverOrPickupQuestion : Question
class PlaceOrderQuestion : Question
class FirstPizzaQuestion : Question