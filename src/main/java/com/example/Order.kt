package com.example

class Order
{
    private var pizzas = mutableListOf<Pizza>()
    private var sides = mutableListOf<Side>()
    private var drinks = mutableListOf<Drink>()
    private var name = ""
    private var phone = ""

    fun addPizza(pizza: Pizza)
    {
        pizzas.add(pizza)
    }

    fun addPizza(pizza: List<Pizza>)
    {
        for(p in pizza)
        {
            pizzas.add(p)
        }
    }

    fun addSide(side: Side)
    {
        sides.add(side)
    }

    fun addDrink(drink: Drink)
    {
        drinks.add(drink)
    }

    fun setName(name_: String)
    {
        name = name_
    }

    fun setPhone(phone_: String)
    {
        phone = phone_
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

    fun removePizza(pizza: Pizza)
    {
        pizzas.pop(pizza)
    }

    fun removeSide(side: Side)
    {
        sides.pop(side)
    }

    fun removeDrink(drink: Drink)
    {
        drinks.pop(drink)
    }
}