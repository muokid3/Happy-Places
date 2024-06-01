package com.dm.berxley.happyplaces.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "happy_places")
data class HappyPlaceEntity (
    @PrimaryKey(autoGenerate = true)
    val  id: Int = 0,
    val title: String?,
    val image: String?,
    val description: String?,
    val date: String?,
    val location: String?,
    val lat: Double?,
    val lng: Double?,

): Serializable