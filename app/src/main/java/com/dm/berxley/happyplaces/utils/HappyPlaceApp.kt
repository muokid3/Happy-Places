package com.dm.berxley.happyplaces.utils

import android.app.Application
import com.dm.berxley.happyplaces.database.DatabaseHandler

class HappyPlaceApp: Application() {
    val db by lazy {
        DatabaseHandler.getInstance(this)
    }
}