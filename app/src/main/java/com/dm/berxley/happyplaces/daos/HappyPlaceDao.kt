package com.dm.berxley.happyplaces.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.dm.berxley.happyplaces.entities.HappyPlaceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HappyPlaceDao {
    @Insert
    suspend fun insert(happyPlaceEntity: HappyPlaceEntity)

    @Update
    suspend fun update(happyPlaceEntity: HappyPlaceEntity)

    @Delete
    suspend fun delete(happyPlaceEntity: HappyPlaceEntity)

    @Query("SELECT * FROM `happy_places`")
    fun getAllHappyPlaces():Flow<List<HappyPlaceEntity>>

    @Query("SELECT * FROM `happy_places` where id=:id")
    fun getHappyPlaceById(id: Int):Flow<HappyPlaceEntity>
}