package com.adlab.balda.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.adlab.balda.database.dao.WordsDao
import com.adlab.balda.database.entity.WordInfo

@Database(entities = [WordInfo::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun wordsDao(): WordsDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        @JvmStatic
        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = buildRoomDB(context)
                }
            }
            return INSTANCE!!
        }

        private fun buildRoomDB(context: Context) =
                Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "balda-database"
                ).build()
    }
}