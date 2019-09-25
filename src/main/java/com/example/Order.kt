package com.example

// Data classes for storing drinks and side dishes
data class Extra (val name: String, val price: Double);

class Order
{
    private var pizzas = mutableListOf<Pizza>()
    private var extras = mutableListOf<Extra>()
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

    fun addExtra(side: Extra)
    {
        extras.add(side)
    }

    fun addExtra(itemName: String, price: Double)
    {
        extras.add(Extra(itemName, price))
    }

    fun setName(name_: String)
    {
        name = name_
    }

    fun setPhone(phone_: String)
    {
        phone = phone_
    }

    fun changePizza(pizza: Pizza, remove: List<String>, add: List<String>)
    {
        for(p in pizzas)
        {
            if (p  == pizza)
            {
                p.change(remove, add);
            }
        }
    }

    fun removePizza(pizza: Pizza)
    {
        pizzas.pop(pizza)
    }

    fun removeExtras(item: Extra)
    {
        extras.remove(item)
    }

    fun removeExtras(itemName: String)
    {
        for(e in extras)
        {
            if(e.name == itemName)
            {
                extras.remove(e)
            }
        }
    }
}

