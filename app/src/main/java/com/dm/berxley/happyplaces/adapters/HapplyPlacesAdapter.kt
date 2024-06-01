package com.dm.berxley.happyplaces.adapters


import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dm.berxley.happyplaces.R
import com.dm.berxley.happyplaces.entities.HappyPlaceEntity

open class HappyPlacesAdapter(
    private val context: Context,
    private var list: ArrayList<HappyPlaceEntity>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_happy_place,
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            holder.iv_place_image.setImageURI(Uri.parse(model.image))
            holder.tvTitle.text = model.title
            holder.tvDescription.text = model.description
        }
    }



    override fun getItemCount(): Int {
        return list.size
    }


    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val iv_place_image: ImageView = view.findViewById(R.id.iv_place_image)
        val tvTitle: TextView
        val tvDescription: TextView
        init {
            tvTitle = view.findViewById(R.id.tvTitle)
            tvDescription = view.findViewById(R.id.tvDescription)
        }
    }
}