package projections
import java.time.LocalDateTime
case class Projection (ProjectionID :Int, StartDate: LocalDateTime,Description:String, Projected :Float, Deposit:Float, MonthYear: LocalDateTime, Payment:Double )
