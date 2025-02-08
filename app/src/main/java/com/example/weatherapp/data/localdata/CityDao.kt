package com.example.weatherapp.data.localdata

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.domain.models.CitiesItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {
    @Query("SELECT * FROM cities_table")
    suspend fun getAllCities(): List<CitiesItem>
    @Query("SELECT * FROM cities_table WHERE name LIKE :query")
    suspend fun searchCities(query: String): List<CitiesItem>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCity(citiesItem: CitiesItem)
    @Delete
    suspend fun removeCity(citiesItem: CitiesItem)
}