package com.example

data class AddPizza(val pizza: Pizza?, val amount: Int = 1)
data class RemovePizza(val pizza: Set<Pizza>)
data class ChangePizza(
        val pizza: Pizza?,
        val additions: Set<String> = emptySet(),
        val removals: Set<String> = emptySet()
)