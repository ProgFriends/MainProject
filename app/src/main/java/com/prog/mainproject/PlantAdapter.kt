package com.prog.mainproject
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlantAdapter : RecyclerView.Adapter<PlantAdapter.PlantViewHolder>() {

    private val plantList = listOf(
        Plant(R.drawable.geumjonsu, "금전수"),
        Plant(R.drawable.ttokkkal, "떡갈고무나무"),
        Plant(R.drawable.bakjjwiran, "박쥐란"),
        Plant(R.drawable.seukip, "스킨답서스"),
        Plant(R.drawable.aibi, "아이비"),
        Plant(R.drawable.choljjuk, "철쭉"),
        Plant(R.drawable.kupea, "쿠페아"),
        Plant(R.drawable.yajasu, "테이블야자수"),
        Plant(R.drawable.haengunmok, "행운목"),
        Plant(R.drawable.hoya, "호야"),

    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_plant, parent, false)
        return PlantViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val currentPlant = plantList[position]
        holder.plantImage.setImageResource(currentPlant.imageResId)
        holder.plantName.text = currentPlant.name
    }

    override fun getItemCount(): Int {
        return plantList.size
    }

    inner class PlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val plantImage: ImageView = itemView.findViewById(R.id.plantImage)
        val plantName: TextView = itemView.findViewById(R.id.plantName)
    }

    data class Plant(val imageResId: Int, val name: String)
}
