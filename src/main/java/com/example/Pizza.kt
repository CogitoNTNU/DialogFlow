import com.beust.klaxon.Klaxon
import org.apache.commons.lang.ObjectUtils
import java.io.FileReader



data class Pizza(val nr:Int, val name: String, val ingredients:Set<String>, val price:List<Int>) {
    fun change(remove: List<String>, add: List<String>) {
        TODO("This isn't supported yet")
    }
}

data class PizzaMenu(val pizza:List<Pizza>)
{
    fun getPizza(nr: Int) : Pizza
    {
        for(p in getPizzaMenu().pizza)
        {
            if(p.nr == nr)
            {
                return p
            }
        }
        return null!!
    }

    fun getPizza(name: String) : Pizza
    {
        for(p in getPizzaMenu().pizza)
        {
            if(p.name == name)
            {
                return p
            }
        }
        return null!!
    }
}

val json = FileReader("src/main/resources/pizza_overview.json")



fun getPizzaMenu():PizzaMenu{
    return Klaxon().parse<PizzaMenu>(json)!!
}

fun main(args: Array<String>) {

    print(getPizzaMenu().pizza)

}






