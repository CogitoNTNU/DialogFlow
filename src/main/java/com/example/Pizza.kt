import com.beust.klaxon.Klaxon
import java.io.File
import java.io.FileReader
import java.util.*


data class Pizza(val nr:Int, val name: String, val ingredients:Set<String>, val price:List<Int>) {
    fun change(remove: List<String>, add: List<String>) {
        TODO("This isn't supported yet")
    }
}

data class PizzaMenu(val pizzaList:List<Pizza>)
{
    fun getPizza(nr: Int) = pizzaList.find { it.nr == nr }

    fun getPizza(name: String) = pizzaList.find { it.name == name }
}

val json = File(Pizza::javaClass.javaClass.getResource("pizza_overview.json").file)



fun getPizzaMenu():PizzaMenu{
    return Klaxon().parse<PizzaMenu>(json)!!
}

fun main(args: Array<String>) {

    print(getPizzaMenu().pizzaList)

}






