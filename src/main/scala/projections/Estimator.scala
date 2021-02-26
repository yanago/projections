package projections

import java.time.format.DateTimeFormatter
import java.util.Locale
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.Month

object Estimator {
  def main(args: Array[String]) = {
    //Load sample data
    val projectionInventory = scala.io.Source.fromFile("src/main/resources/data.csv")
    val actualsInventory = scala.io.Source.fromFile("src/main/resources/actuals.csv")
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault)
    val formatterActuals = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault)

    projectionInventory.getLines.next()
    val projectionList = projectionInventory.getLines.toList

    actualsInventory.getLines.next()
    val actualsList = actualsInventory.getLines.toList

    var projCounter = 1
    var startDate = LocalDate.of(2019, Month.APRIL, 1)

    val parsedProjections = projectionList.map(line => {
      val cols = line.split(",").map(_.trim)
      val dateTime = LocalDateTime.parse(cols(2), formatter)
      if (dateTime.getYear != startDate.getYear) {
        startDate = LocalDate.of(dateTime.getYear, dateTime.getMonth, dateTime.getDayOfMonth)
        projCounter = projCounter + 1
      }
      Projection(projCounter, startDate.atStartOfDay(), cols(1), cols(4).toFloat, cols(0).toFloat, dateTime, cols(3).toDouble)
    })
    var actualCounter = 1
    val parsedActuals = actualsList.map(line => {
      val cols = line.split(",").map(_.trim)
      val dateTimeActuals = LocalDateTime.parse(cols(2), formatterActuals)
      actualCounter = actualCounter + 1
      Actuals(actualCounter, cols(0).toDouble, cols(1), dateTimeActuals, cols(3).toInt)
    })

    //use scala's functional paradigm to group and display
    val prodActuals = parsedProjections zip parsedActuals
    val estimates2 = prodActuals.groupBy(r => (r._1.ProjectionID, r._1.MonthYear.getMonth + "-" + r._1.MonthYear.getYear)).map(m => (m._1._1, m._2))
    val estimates = prodActuals.groupBy(r => (r._1.ProjectionID, r._1.MonthYear.getMonth + "-" + r._1.MonthYear.getYear)).mapValues(
      _.map(p => {

        val description = p._2.Amount match {
          case c if c < 0 => "Withdawal"
          case _ => "Deposit"
        }
        val deposits = "Deposit:" + p._1.Description + "," + p._1.Deposit
        val withdrawals = "Withdrawal:" + p._1.Payment + "," + p._2.Amount + "," + p._1.Projected
        val estimateRepprtMap = Map(p._1.MonthYear.getMonth + "-" + p._1.MonthYear.getYear -> List(deposits, withdrawals))
        estimateRepprtMap
      }
      )).map(rec => (Map(rec._1._1 -> rec._2.flatten.toMap)))
    println(estimates.mkString(";"))

    //TODO
    // - Persist above result into file
    // - OR
    // - Expose to  Rest API endpoint

  }
}

