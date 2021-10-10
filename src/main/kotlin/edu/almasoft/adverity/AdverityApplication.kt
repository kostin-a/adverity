package edu.almasoft.adverity

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.reactive.config.EnableWebFlux

@SpringBootApplication
@EnableWebFlux
class AdverityApplication

fun main(args: Array<String>) {
	runApplication<AdverityApplication>(*args)
}

