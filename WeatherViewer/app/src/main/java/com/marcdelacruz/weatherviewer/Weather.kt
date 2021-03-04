package com.marcdelacruz.weatherviewer
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone


class Weather (timeStamp: Long, minTemp: Double, maxTemp: Double,
               humidity: Double, description: String, iconName: String){

    val dayOfWeek = convertTImeStampToDay(timeStamp)
    val minTemp: String
    val maxTemp: String
    val humidity: String
    val description: String
    val iconURL: String

    init{
        val numberFormat = NumberFormat.getInstance()
        this.minTemp = numberFormat.format(minTemp) + "\u00B0F"
        this.maxTemp = numberFormat.format(minTemp) + "\u00B0F"
        this.humidity = NumberFormat.getPercentInstance().format(humidity/ 100.0)
        this.description = description
        this.iconURL = "http://openweathermap.org/img/w$iconName.png"
    }
    private fun convertTImeStampToDay(timeStamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeStamp * 1000
        calendar.add(Calendar.MILLISECOND, TimeZone.getDefault().getOffset(calendar.timeInMillis))
        return SimpleDateFormat("EEEE").format(calendar.time);

    }
}