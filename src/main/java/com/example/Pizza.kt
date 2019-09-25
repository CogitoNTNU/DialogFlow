import com.beust.klaxon.*
import java.io.FileReader



data class Pizza(val nr:Int, val name: String, val ingredients:List<String>, val price:List<Int>)
data class PizzaMenu(val pizza:List<Pizza>)

val json = FileReader("src/main/resources/pizza_overview.json")


fun getPizzaMenu():PizzaMenu{
    return Klaxon().parse<PizzaMenu>(json)!!
}

fun main(args: Array<String>) {

    print(getPizzaMenu().pizza)

}






