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

import org.json.JSONObject
import org.junit.Test

class AddPizzaTest {

    @Test
    @Throws(Exception::class)
    fun testAddPizza() {
        val app = MyActionsApp()
        val requestBody = fromFile("request_add_pizza.json")

        val future = app.handleRequest(requestBody, null)

        val responseJson = JSONObject(future.get())
        val textToSpeech = extractTextToSpeech(responseJson)

        assert("Kokkens favoritt" in textToSpeech)
    }

    @Test
    @Throws(Exception::class)
    fun testAddTwoPizzas() {
        val app = MyActionsApp()
        val requestBody = fromFile("request_add_pizza.json")

        app.handleRequest(requestBody, null).get()
        app.handleRequest(requestBody, null).get()

        assert(app.orderManager.orders.values.single().pizzas.size == 2)
    }
}
