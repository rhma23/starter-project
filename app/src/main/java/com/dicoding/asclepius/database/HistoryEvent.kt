package com.dicoding.asclepius.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDate

@Entity(tableName = "history")
data class HistoryEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val image: String,
    val category: String,
    val confidentScore: Int,
    val timeAdd: LocalDate
)
