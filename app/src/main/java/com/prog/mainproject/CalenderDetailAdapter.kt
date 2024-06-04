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
import org.json.JSONException
import org.json.JSONObject
import java.util.*

data class CalendarListClass(
    var plantSpecies: String,
    var plantName: String,
    var PlantImage: ByteArray,
    var recordDate: String,
    var pestInfo: String,
    var memo: String
)

class CalenderDetailAdapter: RecyclerView.Adapter<CalenderDetailAdapter.ViewHolder>() {

    var calendarDayList = ArrayList<CalendarListClass>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalenderDetailAdapter.ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.row_calendar_day_list, parent, false)
        return ViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: CalenderDetailAdapter.ViewHolder, position: Int) {
        val item = calendarDayList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {          // 생성자로 부터 받은 데이터의 개수를 측정
        return calendarDayList.size
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tv_plantname: TextView = v.findViewById(R.id.TV_PlantName)
        val tv_pestinfo: TextView = v.findViewById(R.id.TV_PestInfo)
        val tv_memo: TextView = v.findViewById(R.id.TV_Memo)
        val ImgV_plant: ImageView = v.findViewById(R.id.ImgV_Plant)
        val btn_delete: ImageButton = v.findViewById(R.id.Btn_Delete)

        init {
            btn_delete.setOnClickListener {
                /*
                val position = adapterPosition

                if (position != RecyclerView.NO_POSITION) {
                    val item = calendarDayList[position]

                    val responseListener = Response.Listener<String> { response ->
                        try {
                            val jsonObject = JSONObject(response)
                            Log.d("식물 삭제 중: Json객체", jsonObject.toString())

                            val success = jsonObject.getBoolean("success")
                            val message = jsonObject.getString("message")

                            if (success) {
                                if (position != RecyclerView.NO_POSITION) {
                                    calendarDayList.removeAt(position)        // plantList에서 position 위치의 값을 지움
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
                */
            }
        }

        fun bind(item: CalendarListClass) {
            tv_plantname.text = item.plantName
            tv_pestinfo.text = item.pestInfo
            tv_memo.text = item.memo

            val bitmap = BitmapFactory.decodeByteArray(item.PlantImage, 0, item.PlantImage.size)
            ImgV_plant.setImageBitmap(bitmap)
        }
    }


}