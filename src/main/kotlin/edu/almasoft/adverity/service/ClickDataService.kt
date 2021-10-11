package edu.almasoft.adverity.service

import edu.almasoft.adverity.csv.CSVBeanReader
import edu.almasoft.adverity.model.ClickRow
import edu.almasoft.adverity.model.Request
import edu.almasoft.adverity.model.Response
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.core.env.Environment
import org.springframework.core.io.ResourceLoader
import org.springframework.dao.DataAccessException
import org.springframework.data.repository.CrudRepository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import java.time.LocalDate
import javax.transaction.Transactional

@Repository
interface ClickRowRepository : CrudRepository<ClickRow?, Long?> {
    fun findByDailyBetween(from: LocalDate, to: LocalDate): List<ClickRow>
    fun findByDailyGreaterThanEqualAndDailyLessThanEqual(from: LocalDate, to: LocalDate): List<ClickRow>
}

@Service
@Transactional
class ClickDataService (val clickRowRepository: ClickRowRepository,
                        val environment: Environment,
                        val resourceLoader: ResourceLoader,
                        val cSVBeanReader: CSVBeanReader,
                        val jdbcTemplate: JdbcTemplate
) {
    private val log = LoggerFactory.getLogger(javaClass)
    @EventListener(classes=[ApplicationReadyEvent::class])
    fun loadData() {
        try {
            val rows = jdbcTemplate.queryForObject<Long>("select count(*) from click_row")
            if (rows > 0) {
                log.debug("Data already loaded rows: $rows")
                return
            }
        } catch (e: DataAccessException) {
            log.error("Error during fetch rows number", e)
            return
        }

        val csv = resourceLoader.getResource(environment.getProperty("adverity.datainit.path")
            ?: throw IllegalStateException("Please specify path to load CSV data: 'adverity.datainit.path'"))

        val data = cSVBeanReader.readCsv(
            csv.file,
            ClickRow::class,
            mapOf("datasource" to 0, "campaign" to 1, "daily" to 2, "clicks" to 3, "impressions" to 4),
            skipLines = 1)

        clickRowRepository.saveAll(data)
        print("loaded all data: ${data.size}")

    }

    fun readAndBuildGroups(request: Request): List<Response> {
        val rows = clickRowRepository.findByDailyBetween(request.from!!, request.to!!)
        log.info("Fetched ${rows.size} rows from database")

        val response = EvaluationProcessor().evaluate(rows, request)
        log.info("Got ${response.size} rows after grouping")
        return response
    }
}