package edu.almasoft.adverity.controller

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

data class Row(val a: String, val b: String)

@RestController
@RequestMapping("/locations")
class WarehouseController {

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getPrices(): Flux<Row> {
        return Flux.fromIterable(listOf(Row("a", "n")))
    }
}