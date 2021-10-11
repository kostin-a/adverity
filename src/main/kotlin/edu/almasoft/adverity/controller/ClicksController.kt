package edu.almasoft.adverity.controller

import edu.almasoft.adverity.model.Request
import edu.almasoft.adverity.model.Response
import edu.almasoft.adverity.service.ClickDataService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class Row(val a: String, val b: String)

@RestController
@RequestMapping("/clicks")
class ClicksController(val clickDataService: ClickDataService) {
    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("groups/{groups}/fields/{fields}", "groups/{groups}/fields/{fields}/filter/{filter}")
    fun getClicks(
        @PathVariable("groups") groups: String,
        @PathVariable("fields") fields: String,
        @RequestParam("from") from: LocalDate?,
        @RequestParam("to") to: LocalDate?,
        @PathVariable("filter", required = false) filter: String?
    ): Flux<Response> {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yy")
        val request = Request(
            groups = groups.split(","),
            fields = fields.split(","),
            from = from ?: LocalDate.parse("01/01/10", formatter ),
            to = to ?: LocalDate.parse("01/01/99", formatter),
            filters = filter?.split(",")
        )

        log.info("Fetching data from $from to $to by the request: $request")

        val response = clickDataService.readAndBuildGroups(request)
        return Flux.fromIterable(response)
    }
}