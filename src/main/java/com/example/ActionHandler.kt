package com.example

import com.google.actions.api.ActionRequest
import sun.misc.Request
import java.util.*

class ActionHandler {

    val orderManager = OrderManager()

    fun addPizza(types : List<Int>,  amount: List<Int>, pizzaMenu : PizzaMenu, order : Order): String {

        var pizzas: MutableList<Pizza> = mutableListOf()
        var counter = 0
        for (i in types.indices) {
            var type = types[i]
            for (j in 0 until amount[i]) {
                var pizza = pizzaMenu.getPizza(type)
                if (pizza != null) {
                    counter++
                    pizzas.add(pizza)
                }
            }
        }

        return if (pizzas.size > 0) {
            order.addPizza(pizzas)
            val speakList = pizzas
                    .groupBy { it }
                    .map { (pizza, amount) ->
                        "${amount.size} ${pizza.describeChangesToUser()}"
                    }
            "Klart det! Du har bestilt ${spokenList(speakList)}"
        } else {
            "Den pizzaen har jeg ikke hørt om. Hvilke ingredienser vil du ha på pizzaen din?"
        }


    }



    fun delivery(request: ActionRequest){
        val order: Order = orderManager[request]
        val delivery = request.getParameter("Deliver") as String
        var address = request.getParameter("Address") as String

        if (delivery == "deliver") {
            order.deliver(true)
            order.addAddress(address)
        } else {
            order.deliver(false)
        }
        return
    }


    fun findPizza(requrestedIngredients : List<String>, pizzaMenu : PizzaMenu): String {
        var response = ""

        val pizzaList = pizzaMenu.pizzaList
                .sortedByDescending { pizza -> pizza.ingredients.count { it in requrestedIngredients } }
                .take(3)
                .map { p -> p.name }

        response += if(pizzaList.isNotEmpty()){
            "Her er noen pizzaer du kanskje liker: " + spokenList(pizzaList)
        }else{
            "Beklager, vi har ingen pizzaer med dette på. Vil du ha noe annet?"
        }

        return response
    }



    fun removePizza(order : Order,types : List<Int>, amount : List<Int>): Boolean {

        if (order.pizzas.size > 0) {
            for (pizza: Pizza in order.pizzas) {
                for (i in types.indices) {
                    if (pizza.nr == types[i]) {
                        for (j in 0 until amount[i]) {
                            order.removePizza(pizza)
                        }
                    }
                }
            }
        }
        else {
            return false
        }
        return true
    }

}