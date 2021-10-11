package edu.almasoft.adverity.model

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

//@Table("click_row")
@Entity
data class ClickRow (
    @Id @GeneratedValue
    var id: Long? = null,
    var datasource: String? = null,
    var campaign: String? = null,
    @JsonFormat(pattern="MM/dd/yy")
    var daily: LocalDate? = null,
    var clicks: Int? = null,
    var impressions: Int? = null

)

data class Request (
    val fields: List<String> = listOf(),
    val groups: List<String> = listOf(),
    val filters: List<String>? = null,
    val from: LocalDate? = null,
    val to: LocalDate? = null,
)

data class Response (
    val key: List<Any?>,
    val fields: List<Any?>
)