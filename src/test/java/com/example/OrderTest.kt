package com.example

import org.junit.Assert
import org.junit.Test

class OrderTest {
    @Test
    fun serialize() {
        val order = Order(mutableListOf(getPizzaMenu().pizzaList.first()), mutableListOf(), "Someone", "00112233")
        val json = order.toJson()
        val parsedOrder = Order.fromJson(json)
        Assert.assertEquals(order, parsedOrder)
    }
}