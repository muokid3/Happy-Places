package com.dm.berxley.happyplaces.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dm.berxley.happyplaces.R
import com.dm.berxley.happyplaces.adapters.HappyPlacesAdapter
import com.dm.berxley.happyplaces.entities.HappyPlaceEntity
import com.dm.berxley.happyplaces.utils.HappyPlaceApp
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var fabAddHappyPlace: FloatingActionButton? = null
    private var recyclerView: RecyclerView? = null
    private var tvNotFound: TextView? = null
    private var adapter: HappyPlacesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvNotFound = findViewById(R.id.tvNotFound)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        val happyPlaceDao = (application as HappyPlaceApp).db.happyPlaceDao()
        lifecycleScope.launch {
            happyPlaceDao.getAllHappyPlaces().collect {
                val list = ArrayList(it)

                if (list.isNotEmpty()){
                    adapter = HappyPlacesAdapter(this@MainActivity,list)
                    recyclerView?.adapter = adapter


                    recyclerView?.visibility = View.VISIBLE
                    tvNotFound?.visibility = View.GONE


                }else{
                    recyclerView?.visibility = View.GONE
                    tvNotFound?.visibility = View.VISIBLE
                }
            }
        }



        fabAddHappyPlace = findViewById(R.id.fabAddHappyPlace)
        fabAddHappyPlace?.setOnClickListener {
            val intent = Intent(this, AddHappyPlaceActivity::class.java)
            startActivity(intent)
        }
    }
}