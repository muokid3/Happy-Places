package com.dm.berxley.happyplaces.activities

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dm.berxley.happyplaces.R
import com.dm.berxley.happyplaces.adapters.HappyPlacesAdapter
import com.dm.berxley.happyplaces.daos.HappyPlaceDao
import com.dm.berxley.happyplaces.entities.HappyPlaceEntity
import com.dm.berxley.happyplaces.utils.HappyPlaceApp
import com.dm.berxley.happyplaces.utils.SwipeToDeleteCallback
import com.dm.berxley.happyplaces.utils.SwipeToEditCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var fabAddHappyPlace: FloatingActionButton? = null
    private var recyclerView: RecyclerView? = null
    private var tvNotFound: TextView? = null
    private var adapter: HappyPlacesAdapter? = null
    private var list: ArrayList<HappyPlaceEntity>? = null
    private var happyPlaceDao: HappyPlaceDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvNotFound = findViewById(R.id.tvNotFound)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        happyPlaceDao = (application as HappyPlaceApp).db.happyPlaceDao()
        lifecycleScope.launch {
            happyPlaceDao?.getAllHappyPlaces()?.collect {
                list = ArrayList(it)

                if (list!!.isNotEmpty()){
                    adapter = HappyPlacesAdapter(this@MainActivity,list!!)
                    recyclerView?.adapter = adapter

                    adapter?.setOnClickListener(object : HappyPlacesAdapter.OnClickListener{
                        override fun onClick(id: Int, model: HappyPlaceEntity) {
                            val intent = Intent(this@MainActivity, HappyPlaceDetailsActivity::class.java)
                            intent.putExtra(EXTRA_PLACE_DETAILS, model)
                            startActivity(intent)
                        }
                    })


                    recyclerView?.visibility = View.VISIBLE
                    tvNotFound?.visibility = View.GONE


                }else{
                    recyclerView?.visibility = View.GONE
                    tvNotFound?.visibility = View.VISIBLE
                }
            }
        }

        val editCallBack = object : SwipeToEditCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter?.notifyEditItem(this@MainActivity, viewHolder.adapterPosition, ADD_PLACE_ACTIVITY_REQUEST_CODE)
            }
        }
        val editItemHelper = ItemTouchHelper(editCallBack)
        editItemHelper.attachToRecyclerView(recyclerView)


        val deleteCallBack = object : SwipeToDeleteCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val item = list!![viewHolder.adapterPosition]

                lifecycleScope.launch {
                    happyPlaceDao?.delete(item)

                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("Delete Successful")
                        .setMessage("Item has been deleted successfully")
                        .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, which -> {
                            dialog.dismiss()
                        } })
                        .show()
                }
            }
        }
        val deleteItemHelper = ItemTouchHelper(deleteCallBack)
        deleteItemHelper.attachToRecyclerView(recyclerView)



        fabAddHappyPlace = findViewById(R.id.fabAddHappyPlace)
        fabAddHappyPlace?.setOnClickListener {
            val intent = Intent(this, AddHappyPlaceActivity::class.java)
            startActivity(intent)
        }
    }

    companion object {
        const val EXTRA_PLACE_DETAILS = "extra_place_details"
        const val ADD_PLACE_ACTIVITY_REQUEST_CODE = 200
    }
}
