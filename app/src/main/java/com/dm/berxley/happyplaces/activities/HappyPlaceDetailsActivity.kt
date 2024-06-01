package com.dm.berxley.happyplaces.activities

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dm.berxley.happyplaces.R
import com.dm.berxley.happyplaces.entities.HappyPlaceEntity

class HappyPlaceDetailsActivity : AppCompatActivity() {
    private var toolbar: Toolbar? = null
    private var iv_place_image: ImageView? = null
    private var tv_description: TextView? = null
    private var tv_location: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_happy_place_details)

        toolbar = findViewById(R.id.toolbar_happy_place_detail)
        iv_place_image = findViewById(R.id.iv_place_image)
        tv_description = findViewById(R.id.tv_description)
        tv_location = findViewById(R.id.tv_location)

        var placeModel: HappyPlaceEntity? =null

        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)){
            placeModel = intent.getSerializableExtra(MainActivity.EXTRA_PLACE_DETAILS) as HappyPlaceEntity?
        }

        if (placeModel != null){
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toolbar?.setNavigationOnClickListener {
                onBackPressed()
            }
            supportActionBar!!.title = placeModel.title

            iv_place_image?.setImageURI(Uri.parse(placeModel.image))
            tv_description?.text = placeModel.description
            tv_location?.text = placeModel.location

        }


    }
}