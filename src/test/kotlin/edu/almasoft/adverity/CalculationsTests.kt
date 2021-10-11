package edu.almasoft.adverity

import edu.almasoft.adverity.model.ClickRow
import edu.almasoft.adverity.model.Request
import edu.almasoft.adverity.service.EvaluationProcessor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CalculationsTests {
	private fun date(d: String): LocalDate = LocalDate.parse(d, DateTimeFormatter.ofPattern("MM/dd/yy"))

	fun fixture() = listOf(
		ClickRow(1, "ds1", "c1",date("12/01/01"), 10, 20),
		ClickRow(2, "ds1", "c3",date("12/02/01"), 10, 20),
		ClickRow(3, "ds2", "c3",date("12/02/01"), 20, 40),
		ClickRow(4, "ds2", "c3",date("12/04/01"), 23, 230)
	)
	@Test
	fun filtersTest() {

		val ep = EvaluationProcessor()
		val slice = fixture()

		val r = Request(
			groups = listOf("id"),
			fields = listOf("f('clicks')", "f('impressions')"),
			filters = listOf("clicks < 23", "impressions > 20")
		)
		val g = ep.evaluate(slice, r)
		assertEquals(1, g.size)
	}

	@Test
	fun groupsSynteticTest() {
		val ep = EvaluationProcessor()
		val slice = fixture()

		val r = Request(
			groups = listOf("datasource"),
			fields = listOf("1.0*sum('clicks')/sum('impressions')"),
		)
		val g = ep.evaluate(slice, r)
		assertEquals(2, g.size)
		assertEquals(listOf(0.5), g.first { it.key == listOf("ds1") }.fields)
		assertEquals(listOf(43.0/270), g.first { it.key == listOf("ds2") }.fields)
	}

	@Test
	fun groupsSyntetic2Test() {
		val ep = EvaluationProcessor()
		val slice = fixture()

		val r = Request(
			groups = listOf("datasource"),
//			fields = listOf("div(sum('clicks'), sum('impressions'))"),
			fields = listOf("sum('clicks'):sum('impressions')"),
		)
		val g = ep.evaluate(slice, r)
		assertEquals(2, g.size)
		assertEquals(listOf(0.5), g.first { it.key == listOf("ds1") }.fields)
		assertEquals(listOf(43.0/270), g.first { it.key == listOf("ds2") }.fields)
	}

	@Test
	fun groupsCtrTest() {
		val ep = EvaluationProcessor()
		val slice = fixture()

		val r = Request(
			groups = listOf("datasource", "campaign"),
			fields = listOf("1.0*sum('clicks')/sum('impressions')"),
		)
		val g = ep.evaluate(slice, r)
		assertEquals(3, g.size)
		assertEquals(listOf(0.5), g.first { it.key == listOf("ds1", "c1") }.fields)
		assertEquals(listOf(0.5), g.first { it.key == listOf("ds1", "c3") }.fields)
		assertEquals(listOf(43.0/270), g.first { it.key == listOf("ds2", "c3") }.fields)
	}

	@Test
	fun groupsImpressionsTest() {
		val ep = EvaluationProcessor()
		val slice = fixture()

		val r = Request(
			groups = listOf("daily"),
			fields = listOf("sum('impressions')"),
		)
		val g = ep.evaluate(slice, r)
		assertEquals(3, g.size)
		assertEquals(listOf(60), g.first { it.key == listOf(date("12/02/01")) }.fields)
		assertEquals(listOf(20), g.first { it.key == listOf(date("12/01/01")) }.fields)
		assertEquals(listOf(230), g.first { it.key == listOf(date("12/04/01")) }.fields)
	}

	@Test
	fun groupsTest() {
		val ep = EvaluationProcessor()
		val slice = fixture()

		val r = Request(
			groups = listOf("datasource"),
			fields = listOf("sum('clicks')"),
		)
		val g = ep.evaluate(slice, r)
		assertEquals(2, g.size)
		assertEquals(listOf(20), g.first { it.key == listOf("ds1") }.fields)
		assertEquals(listOf(43), g.first { it.key == listOf("ds2") }.fields)
	}

}
