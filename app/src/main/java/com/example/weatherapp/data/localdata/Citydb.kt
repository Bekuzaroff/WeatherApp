package com.example.weatherapp.data.localdata

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherapp.domain.models.CitiesItem

@Database(entities = [CitiesItem::class], version = 1)
abstract class Citydb: RoomDatabase(){
    abstract fun getDao(): CityDao

    companion object{
        @Volatile
        private var INSTANCE: Citydb ?= null

        fun getDb(context: Context): Citydb{
            val temp_instance = INSTANCE
            if (temp_instance != null){
                return temp_instance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(context, Citydb::class.java, "city.db").build()
                INSTANCE = instance
                return instance
            }

        }
    }
}