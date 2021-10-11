package edu.almasoft.adverity

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.web.reactive.config.EnableWebFlux
import java.time.format.DateTimeFormatter


@SpringBootApplication
@EnableWebFlux
class AdverityApplication

@Configuration
class Jackson2ObjectMapperConfig: Jackson2ObjectMapperBuilderCustomizer {
	override fun customize(jacksonObjectMapperBuilder: Jackson2ObjectMapperBuilder?) {
		jacksonObjectMapperBuilder?.deserializers( LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
		jacksonObjectMapperBuilder?.serializers( LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
	}
}


fun main(args: Array<String>) {
	runApplication<AdverityApplication>(*args)
}

