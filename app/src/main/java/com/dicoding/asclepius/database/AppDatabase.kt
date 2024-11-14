package com.dicoding.asclepius.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.TypeConverters
import com.dicoding.asclepius.Converters

@Database(entities = [HistoryEvent::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class) // Daftarkan konverter di sini
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyEventDao(): HistoryEventDao // Akses ke DAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }

        }
    }
}
