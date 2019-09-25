package com.example

import com.beust.klaxon.Klaxon
import getPizzaMenu
import org.junit.Test

data class DialogflowEntity(val value: String, val synonyms: List<String>)

/**
 * Creates the JSON file used by DialogFlow to identify pizza types and ingredients
 */
class DialogflowEntityGenerator {
    val pizzaMenu = getPizzaMenu()
    val klaxon = Klaxon()

    @Test
    fun generatePizzaEntityJson() {
        val entities = pizzaMenu.pizzaList.map { pizza ->
            DialogflowEntity(pizza.nr.toString(), listOf(
                    pizza.nr.toString(),
                    pizza.name,
                    "${pizza.nr} ${pizza.name}",
                    "${pizza.name} ${pizza.nr}"
            ))
        }
        val json = klaxon.toJsonString(entities)
        println(json)
    }

    @Test
    fun generateIngredientEntityJson() {
        val entities = pizzaMenu.pizzaList
                .map { it.ingredients }
                .reduce { resultat, ingredientSet -> resultat + ingredientSet }
                .map { ingredient -> DialogflowEntity(ingredient, makeIngredientSynonyms(ingredient)) }

        val json = klaxon.toJsonString(entities)
        println(json)
    }

    private val ingredientNameBlacklist = listOf("og", "sterk", "sterk", "marinert", "saus")

    private fun makeIngredientSynonyms(ingredient: String): List<String> {
        val spaced = ingredient.replace('-', ' ')
        val splits = spaced.split(' ') - ingredientNameBlacklist
        return (splits + ingredient + spaced).distinct()
    }
}
