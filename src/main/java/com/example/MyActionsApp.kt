/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example

import com.google.actions.api.ActionRequest
import com.google.actions.api.ActionResponse
import com.google.actions.api.DialogflowApp
import com.google.actions.api.ForIntent
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Implements all intent handlers for this Action. Note that your App must extend from DialogflowApp
 * if using Dialogflow or ActionsSdkApp for ActionsSDK based Actions.
 */
class MyActionsApp : DialogflowApp() {
    val orderManager = OrderManager()

    val pizzaMenu = getPizzaMenu()

    @ForIntent("Legg til pizza intent")
    fun bestill(request: ActionRequest): ActionResponse {
        LOGGER.info("Bestill pizza start")
        val responseBuilder = getResponseBuilder(request)
        val user = request.user
        val order: Order = orderManager[request]

        var types = (request.getParameter("Type") as List<String>).map { it.toFloat().toInt() }
        var amount = (request.getParameter("Amount") as List<Any>?)
                ?.map { if (it is Int) it else it.toString().toFloat().toInt() }
                ?: emptyList()

        if(amount.size != types.size){
            amount = mutableListOf()
            for (i in amount.size until types.size){
                amount.add(1)
            }
        }

        var pizzas: MutableList<Pizza> = mutableListOf()
        var counter = 0
        for (i in 0 until types.size) {
            var type = types[i]
            for (j in 0 until amount[i]) {
                var pizza = pizzaMenu.getPizza(type)
                if(pizza != null){
                    counter++
                    pizzas.add(pizza)
                }
            }
        }

        if(pizzas.size > 0){
            order.addPizza(pizzas)
            var response = "Du har bestillt "
            for (i in 0 until pizzas.distinct().size) {
                var pizza = pizzas.distinct()[i]
                var number = amount[i]
                response += number.toString() +" "+ pizza.name
                if(i != (pizzas.size-1)){
                    response += " og "
                }
            }
            responseBuilder.add("Klart det! " + response)
        }
        else {
            responseBuilder.add("Ukjent pizza, vil du ha noen anbefalinger?")
        }
        LOGGER.info(responseBuilder.toString())

        orderManager[request] = order
        LOGGER.info("Bestill pizza slutt")
        return responseBuilder.build()
    }

    @ForIntent("List bestilling")
    fun listOrder(request: ActionRequest): ActionResponse {
        val responseBuilder = getResponseBuilder(request)
        val order: Order = orderManager[request]

        val outString = StringBuilder("Du har bestilt ${order.pizzas.size} pizza")
        if (order.pizzas.size != 1) outString.append("er")
        outString.append(". ")
        val spokenPizzas = order.pizzas.map { pizza ->
            "En nr. ${pizza.nr} ${pizza.name}, med ${spokenList(pizza.ingredients)}"
        }
        outString.append(spokenList(spokenPizzas))

        responseBuilder.add(outString.toString())

        return responseBuilder.build()
    }

    @ForIntent("Fjern ingredients intent")
    fun removeIngredient(request: ActionRequest): ActionResponse {
        LOGGER.info("Fjern ingredients start")
        val responseBuilder = getResponseBuilder(request)
        val rb = ResourceBundle.getBundle("resources")
        val user = request.user

        val order: Order = orderManager[request]

        if(order.pizzas.size > 0) {

            //val rm_i = request.getParameter("rm_i") as List<String>
            val rm_i = request.getParameter("rm_i") as List<String>

            order.changePizza(order.pizzas.last(), rm_i, emptyList())

            responseBuilder.add("Fjernet ${spokenList(rm_i)}, fra ${order.pizzas.last().name}\n" +
                    "Den siste pizzaen i ordren din inneholder nå:\n" +
                    "${spokenList(order.pizzas.last().ingredients)}")
        }else{
            responseBuilder.add("Du har ikke bestilt denne pizzaen")
        }
        LOGGER.info("Fjern ingredients slutt")

        orderManager[request] = order
        return responseBuilder.build()
    }

    @ForIntent("Legg til ingredients intent")
    fun addIngredient(request: ActionRequest): ActionResponse {
        LOGGER.info("Legg til ingredients start")
        val responseBuilder = getResponseBuilder(request)
        val rb = ResourceBundle.getBundle("resources")
        val user = request.user

        val order: Order = orderManager[request]

        if(order.pizzas.size > 0) {

            //val rm_i = request.getParameter("rm_i") as List<String>
            val add_i = request.getParameter("add_i") as List<String>

            order.changePizza(order.pizzas.last(), emptyList(), add_i)

            responseBuilder.add("La til ${spokenList(add_i)}, til ${order.pizzas.last().name}\n"  +
                    "\nDen siste pizzaen i ordren din inneholder nå:\n" +
                    spokenList(order.pizzas.last().ingredients))
        }else{
            responseBuilder.add("Du har ikke bestilt denne pizzaen")
        }
        LOGGER.info("Legg til ingredients slutt")

        orderManager[request] = order
        return responseBuilder.build()
    }


    @ForIntent("Finn pizza intent")
    fun findPizza(request: ActionRequest): ActionResponse {
        LOGGER.info("Finn pizza start")
        val responseBuilder = getResponseBuilder(request)
        val rb = ResourceBundle.getBundle("resources")
        val user = request.user

        val order: Order = orderManager[request]

        val ingredient = request.getParameter("Ingredient") as List<String>
        val pizzaList = pizzaMenu.pizzaList.filter { pizza -> pizza.ingredients.containsAll(ingredient) }.map{p -> p.name}


        if(pizzaList.isNotEmpty()){
            responseBuilder.add("Her er noen pizzaer du kan like " +
                    spokenList(pizzaList))
        }else{
            responseBuilder.add("Den valgte ingrediensen finnes ikke")
        }

        LOGGER.info("Finn pizza slutt")

        orderManager[request] = order
        return responseBuilder.build()
    }


    @ForIntent("Default Welcome Intent")
    fun welcome(request: ActionRequest): ActionResponse {
        LOGGER.info("Welcome intent start.")
        val responseBuilder = getResponseBuilder(request)
        val rb = ResourceBundle.getBundle("resources")
        val user = request.user

        if (user != null && user.lastSeen != null) {
            responseBuilder.add(rb.getString("welcome_back"))
        } else {
            responseBuilder.add(rb.getString("welcome"))
        }

        LOGGER.info("Welcome intent end.")
        return responseBuilder.build()
    }

    @ForIntent("Fjern pizza")
    fun removePizza(request: ActionRequest): ActionResponse {

        LOGGER.info("Starter fjerning av pizza")

        val responseBuilder = getResponseBuilder(request)

        var types = (request.getParameter("Type") as List<String>).map { it.toFloat().toInt() }
        var amount = (request.getParameter("Amount") as List<Any>?)
                ?.map { if (it is Int) it else it.toString().toFloat().toInt() }
                ?: emptyList()

        val order: Order = orderManager[request]

        for(pizza : Pizza in order.pizzas) {
            for (i in 0 until types.size) {
                if ( pizza.name.equals(types[i]) || pizza.nr.equals(types[i])) {
                    for (j in 0 until amount[i]) {
                        order.removePizza(pizza)
                    }
                    responseBuilder.add("Fjernet" + amount[i] + pizza)
                }
            }
        }
        return responseBuilder.build()
    }

    @ForIntent("bye")
    fun bye(request: ActionRequest): ActionResponse {
        LOGGER.info("Bye intent start.")
        val responseBuilder = getResponseBuilder(request)
        val rb = ResourceBundle.getBundle("resources")

        responseBuilder.add(rb.getString("bye")).endConversation()
        LOGGER.info("Bye intent end.")
        return responseBuilder.build()
    }

    companion object {

        private val LOGGER = LoggerFactory.getLogger(MyActionsApp::class.java)
    }
}
