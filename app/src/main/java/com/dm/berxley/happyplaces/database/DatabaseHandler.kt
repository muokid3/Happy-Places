package com.dm.berxley.happyplaces.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dm.berxley.happyplaces.daos.HappyPlaceDao
import com.dm.berxley.happyplaces.entities.HappyPlaceEntity

@Database(entities = [HappyPlaceEntity::class], version = 1)
abstract class DatabaseHandler : RoomDatabase() {
    abstract  fun employeeDao(): HappyPlaceDao

    companion object {
        @Volatile
        private var INSTANCE: DatabaseHandler? = null

        fun getInstance(context: Context): DatabaseHandler{
            synchronized(this){
                var  instance = INSTANCE
                if (instance == null){
                    instance = Room.databaseBuilder(context.applicationContext,
                        DatabaseHandler::class.java,
                        "happy_places_db").fallbackToDestructiveMigration().build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}