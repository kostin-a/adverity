package edu.almasoft.adverity.csv

import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import org.springframework.beans.BeanWrapper
import org.springframework.beans.BeanWrapperImpl
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import java.beans.PropertyEditorSupport
import java.io.File
import java.nio.charset.Charset
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

@Component
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

    fun <T: Any> readCsv(
        file: Resource,
        target: KClass<T>,
        mapping: Map<String, Int>,
        separator: Char = ',',
        skipLines: Int = 0): List<T> {
        val retVal = mutableListOf<T>()

        val parser = CSVParserBuilder()
            .withSeparator(separator)
            .build()

        file.inputStream.reader(Charset.forName("utf-8")).use { ios ->
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

//fun main() {
//    var slice = CSVBeanReader().readCsv(
//        File("src/main/resources/adverity.csv"),
//        ClickRow::class,
//        mapOf("datasource" to 0, "campaign" to 1, "daily" to 2, "clicks" to 3, "impressions" to 4),
//        skipLines = 1
//    )
//
//
//
//    val r = Request(groups = listOf("datasource", "campaign"), fields = listOf("sum('clicks')", "1.0*sum('clicks') / sum('impressions')"))
//
//
//}