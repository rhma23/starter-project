
package com.dicoding.asclepius

import androidx.room.TypeConverter
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

object Converters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.format(formatter)
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it, formatter) }
    }
}
