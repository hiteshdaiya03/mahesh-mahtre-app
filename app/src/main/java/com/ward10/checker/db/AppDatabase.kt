package com.ward10.checker.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WardPerson::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wardDao(): WardDao
    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ward10_db"
                ).build()
                INSTANCE = db
                db
            }
        }
    }
}
