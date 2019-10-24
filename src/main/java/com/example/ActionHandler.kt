package com.example

import com.google.actions.api.ActionRequest
import java.util.*

class ActionHandler {

    val orderManager = OrderManager()

    fun delivery(request: ActionRequest){

        val rb = ResourceBundle.getBundle("resources")
        val user = request.user
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
}