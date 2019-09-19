package com.example

class Bestilling
{
    private var pizzas = mutableListOf<Pizza>()
    private var sides = mutableListOf<Pizza>()
    private var drinks = mutableListOf<Pizza>()
    private var name = ""
    private var phone = ""

    fun addPizza(pizza: Pizza)
    {
        this.pizzas.add(pizza)
    }

    fun addSide(side: Side)
    {
        this.sides.add(side)
    }

    fun addDrink(drink: Drink)
    {
        this.drinks.add(drink)
    }

    fun setName(name_: String)
    {
        this.name = name_
    }

    fun setPhone(phone_: String)
    {
        this.phone = phone_
    }

    fun changePizza(pizza: Pizza, remove: List<Ingredient>, add: List<Ingredient>)
    {
        for(p in pizzas)
        {
            if (p  == pizza)
            {
                // TODO: add / remove
                // wait for Pizza class implementation
            }
        }
    }

}