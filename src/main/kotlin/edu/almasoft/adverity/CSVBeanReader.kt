package edu.almasoft.adverity

import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import org.springframework.beans.BeanWrapper
import org.springframework.beans.BeanWrapperImpl
import org.springframework.expression.spel.standard.SpelExpressionParser
import java.beans.PropertyEditorSupport
import java.io.File
import java.nio.charset.Charset
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance


class CSVBeanReader {

    private class LocalDateFormatter: PropertyEditorSupport() {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yy")
        override fun getAsText(): String {
            return (value as LocalDate).format(formatter)
        }

        override fun setAsText(text: String?) {
            value = LocalDate.parse(text, formatter)
        }
    }

    fun <T: Any> readCsv(file: File,
                         target: KClass<T>,
                         mapping: Map<String, Int>,
                         separator: Char = ',',
                         skipLines: Int = 0): List<T> {
        val retVal = mutableListOf<T>()

        val parser = CSVParserBuilder()
            .withSeparator(separator)
            .build()

        file.reader(Charset.forName("utf-8")).use { ios ->
            val reader = CSVReaderBuilder(ios)
                .withSkipLines(skipLines)
                .withCSVParser(parser)
                .build();

            reader.use { r ->
                var line = r.readNext()
                while (line != null) {

                    val properties = mapping.map { (field, index) ->
                        field to line[index]
                    }.toMap()
                    try {
                        val o = createWrapper(target, properties)
                        retVal.add(o)
                    } catch (e: Exception) {
                        e.printStackTrace() //todo
                    }
                    line = r.readNext()
                }
            }
        }
        return retVal
    }

    private fun <T: Any> createWrapper(k: KClass<T>, properties: Map<String, Any>): T {
        val wrapper: BeanWrapper = BeanWrapperImpl(k.createInstance())
        wrapper.registerCustomEditor(LocalDate::class.java, LocalDateFormatter())
        for ((key, value) in properties) {
            wrapper.setPropertyValue(key, value)
        }
        return wrapper.wrappedInstance as T
    }
}

/**
 * This is a root object for Spring EL parser. This class can be extended to add custom functions
 * for example: avg(), substring() etc. Currently only bare minimum is implemented
 * @param vector is the collection for which custom functions are calculated
 */
class EvaluationContext (val vector: List<Any?>) {
    /**
     * calculates sum of all not-null objects for collection @param vector by field @param filed.
     * Function expects field to be resolved as Int (in future can be extended for Number)
     */
    fun sum(field: String): Int {
        return vector
            .filterNotNull()
            .mapNotNull { BeanWrapperImpl(it).getPropertyValue(field) as Int? }
            .sum()
    }
}
fun main() {
    var slice = CSVBeanReader().readCsv(
        File("src/main/resources/adverity.csv"),
        ClickRow::class,
        mapOf("datasource" to 0, "daily" to 2, "campaign" to 1, "clicks" to 3, "impressions" to 4),
        skipLines = 1
    )



    val r = Request(groups = listOf("datasource", "campaign"), fields = listOf("sum('clicks')", "1.0*sum('clicks') / sum('impressions')"))

    // we consider list of object as timeseries so first
    // cut not necessary parts of data by date
    if (r.from != null || r.to != null) {
        slice = slice.filter {
            r.from?.isBefore(it.daily) ?: true &&
            r.to?.isAfter(it.daily) ?: true
        }
    }
    val parser = SpelExpressionParser()

    if (r.filters.isNotEmpty()) {
        slice.filter { row ->
            r.filters.all { filter ->
               !(parser.parseExpression(filter).getValue(row) as Boolean)
            }
        }
    }

    val group : Map<List<Any?>, List<Any?>> = slice.groupBy { o -> r.groups.map { parser.parseExpression(it).getValue(o) } }
    val retVal = group.map { (groupKey, groupValue) ->
        Response(groupKey,
            r.fields.map { field ->
                parser.parseExpression(field).getValue(EvaluationContext(vector=groupValue))
            }
        )
    }

    println(retVal)
//    println(parser.parseExpression("count('clicks')").getValue(context))

//    print(f.size)
}