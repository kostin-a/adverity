package edu.almasoft.adverity

import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class ClickRow (
    var datasource: String? = null,
    var campaign: String? = null,
    var daily: LocalDate? = null,
    var clicks: Int? = null,
    var impressions: Int? = null

)

data class Request (
    val fields: List<String> = listOf(),
    val groups: List<String> = listOf(),
    val filters: List<String> = listOf(),
    val from: LocalDate? = null,
    val to: LocalDate? = null,
)

data class Response (
    val key: List<Any?>,
    val value: List<Any?>
)