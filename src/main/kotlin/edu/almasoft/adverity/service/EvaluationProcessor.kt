package edu.almasoft.adverity.service

import edu.almasoft.adverity.model.ClickRow
import edu.almasoft.adverity.model.Request
import edu.almasoft.adverity.model.Response
import org.springframework.beans.BeanWrapperImpl
import org.springframework.expression.spel.standard.SpelExpressionParser

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

    /**
     * field accessor. Access field from EL. Takes first element from collection, which is for test exercise is ok,
     * but for production code, with need to distinguish between any() and f().
     */
    fun f(field: String): Any? {
        return vector[0]?.let { BeanWrapperImpl(it).getPropertyValue(field) }
    }
    /**
     * division operator
     */
    fun div(num1: Number?, num2: Number?): Double? {
        return if (num1 == null || num2 == null) {
             null
        } else {
            num1.toDouble().div(num2.toDouble())
        }
    }
}

/**
 * Processor that takes data (List of rows) and apply date-range filter, filter, groups to it.
 *
 * In future ClickRow can be used as Timeseries interface only
 */
class EvaluationProcessor {
    fun evaluate(slice: List<ClickRow>, r: Request): List<Response> {

        val parser = SpelExpressionParser()
        var collection = slice

        if (r.filters != null && r.filters.isNotEmpty()) {
            collection = collection.filter { row ->
                r.filters.all { filter ->
                    (parser.parseExpression(filter).getValue(row) as Boolean)
                }
            }
        }

        val group : Map<List<Any?>, List<Any?>> = collection
            .groupBy { o -> r.groups.map { parser.parseExpression(it).getValue(o) } }

        return group.map { (groupKey, groupValue) ->
            Response(groupKey,
                r.fields.map { field ->
                    parser.parseExpression(field).getValue(EvaluationContext(vector=groupValue))
                }
            )
        }

    }
}