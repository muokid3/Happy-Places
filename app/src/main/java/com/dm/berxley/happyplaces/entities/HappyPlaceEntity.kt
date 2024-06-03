package com.dm.berxley.happyplaces.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "happy_places")
data class HappyPlaceEntity (
    @PrimaryKey(autoGenerate = true)
    val  id: Int = 0,
    var title: String?,
    var image: String?,
    var description: String?,
    var date: String?,
    var location: String?,
    var lat: Double?,
    var lng: Double?,

): Serializable