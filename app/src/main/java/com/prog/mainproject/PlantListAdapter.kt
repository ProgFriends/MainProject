package com.prog.mainproject


import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject
import java.util.*

data class PlantListClass(
    var PlantSpecies: String,
    var PlantName: String,
    var PlantImage: ByteArray,
    var BringDate: Date,
)

class PlantListAdapter: RecyclerView.Adapter<PlantListAdapter.ViewHolder>() {

    var plantList = ArrayList<PlantListClass>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.item_plant, parent, false)
        return ViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = plantList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {          // 생성자로 부터 받은 데이터의 개수를 측정
        return plantList.size
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v){
        val tv_plantname : TextView = v.findViewById(R.id.TV_PlantName)
        val tv_plantspecies : TextView = v.findViewById(R.id.TV_PlantSpecies)
        val tv_dday : TextView = v.findViewById(R.id.TV_Dday)
        val ImgV_plant : ImageView = v.findViewById(R.id.ImgV_Plant)
        val btn_delete : ImageButton = v.findViewById(R.id.Btn_Delete)

        init {
            btn_delete.setOnClickListener {             // 삭제버튼이 눌렸을 때
                val position = adapterPosition

                if (position != RecyclerView.NO_POSITION) {
                    val item = plantList[position]

                    val responseListener = Response.Listener<String> { response ->
                        try {
                            val jsonObject = JSONObject(response)
                            Log.d("식물 삭제 중: Json객체", jsonObject.toString())

                            val success = jsonObject.getBoolean("success")
                            val message = jsonObject.getString("message")

                            if (success) {
                                if (position != RecyclerView.NO_POSITION) {
                                    plantList.removeAt(position)        // plantList에서 position 위치의 값을 지움
                                    notifyItemRemoved(position)         // RecyclerView에 변경사항을 알림
                                    Toast.makeText(this.itemView.context, message, Toast.LENGTH_SHORT).show()
                                }
                            }
                            else {
                                Toast.makeText(this.itemView.context, message, Toast.LENGTH_SHORT).show()
                                return@Listener
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }

                    val deleteplnatRequest = DeletePlnatRequest(item.PlantSpecies, responseListener)
                    val queue: RequestQueue = LoginActivity.queue
                    queue.add(deleteplnatRequest)
                }
            }
        }

        fun bind(item: PlantListClass){
            tv_plantname.text = item.PlantName
            tv_plantspecies.text = item.PlantSpecies

            val broughtDate = item.BringDate // 데이터 클래스에서 가져온 데려온 날짜를 LocalDate로 변환
            val currentDate = Calendar.getInstance().time  // 현재 날짜를 가져옴
            val periodInMillis = currentDate.time - broughtDate.time
            val periodInDays = periodInMillis / (1000 * 60 * 60 * 24)  // 밀리초를 일 단위로 변환
            tv_dday.text = "식물을 데려온지 ${periodInDays}일째"  // TextView에 설정

            val bitmap = BitmapFactory.decodeByteArray(item.PlantImage, 0, item.PlantImage.size)
            ImgV_plant.setImageBitmap(bitmap)
        }
    }


    inner class DeletePlnatRequest(PlantSpecies: String, listener: Response.Listener<String>) :
        StringRequest(Method.POST, "http://15.165.56.246/android_plantDelete_mysql.php", listener, null) {

        private val map: MutableMap<String, String> = HashMap()

        init {
            map["UID"] = LoginActivity.UID
            map["plantSpecies"] = PlantSpecies
        }

        override fun getParams(): Map<String, String> {
            return map
        }
    }
}
/*
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
*/
