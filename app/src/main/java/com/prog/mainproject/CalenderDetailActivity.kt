package com.prog.mainproject

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class CalenderDetailActivity : AppCompatActivity() {

    companion object {
        lateinit var adapter: CalenderDetailAdapter
    }

    // Intent에서 전달된 날짜 데이터 가져오기
    var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calender_detail)

        val tv_selectedDate = findViewById<TextView>(R.id.TV_selectDate)
        val btn_AddPlant = findViewById<ImageButton>(R.id.Btn_AddPlant)

        selectedDate = intent.getStringExtra("selectedDate").toString()
        Log.d("getStringExtra", selectedDate)
        tv_selectedDate.text = selectedDate

        // 리사이클러뷰에 레이아웃 매니저 설정
        val layoutManager = LinearLayoutManager(this)
        val recyclerView = findViewById<RecyclerView>(R.id.RCV_CalendarList)
        recyclerView.layoutManager = layoutManager

        adapter = CalenderDetailAdapter()
        recyclerView.adapter = adapter

        val responseListener = Response.Listener<String> { response ->
            try {
                val jsonObject = JSONObject(response)
                Log.d("일별 달력 리스트 로딩: Json객체", jsonObject.toString())
                val plantsArray = jsonObject.getJSONArray("calendar")

                val success = jsonObject.getBoolean("success")
                val message = jsonObject.getString("message")

                if (success) { // mysql 데이터 로딩에 성공한 경우
                    for (i in 0 until plantsArray.length()) {
                        val plantObject = plantsArray.getJSONObject(i)
                        val plantSpecies = plantObject.getString("plantSpecies")
                        val plantName = plantObject.getString("plantName")
                        val plantImageString = plantObject.getString("plantImage")
                        val recordDate = plantObject.getString("recordDate")
                        val pestInfo = plantObject.getString("pestInfo")
                        val memo = plantObject.getString("memo")

                        val plantImageByteArray = Base64.decode(plantImageString, Base64.DEFAULT)


                        // 식물 객체 생성 후 리스트에 추가
                        adapter.calendarDayList.add(CalendarListClass(plantSpecies, plantName, plantImageByteArray, recordDate, pestInfo, memo))
                    }
                    adapter.notifyDataSetChanged()

                }
                else { // mysql 데이터 로딩에 실패한 경우
                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                    return@Listener
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        val CalenderDayListRequest = CalenderDetailRequest(LoginActivity.UID, selectedDate, responseListener)
        val queue: RequestQueue = LoginActivity.queue
        queue.add(CalenderDayListRequest)


        btn_AddPlant.setOnClickListener {
            val intent = Intent(applicationContext, CalenderAddActivity::class.java)
            intent.putExtra("selectedDate", selectedDate)
            startActivity(intent)
        }


        val backIcon = findViewById<ImageView>(R.id.back_icon)
        backIcon.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                finish() // 현재 액티비티 종료
            }
        })

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        // 바텀 네비게이션 아이템 클릭 리스너 설정
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.page_home -> {
                    startActivity(Intent(this@CalenderDetailActivity, HomeFragment::class.java))
                    true
                }

                R.id.page_ps -> {
                    // 식물 기록 아이템 클릭 시 캘린더 화면으로 이동
                    startActivity(Intent(this@CalenderDetailActivity, CalendarActivity::class.java))
                    true
                }

                else -> false
            }
        }

    }

    inner class CalenderDetailRequest(UID: String, selectedDate: String, listener: Response.Listener<String>) :
        StringRequest(Method.POST, "http://15.165.56.246/android_calendarShowbyDay_mysql.php", listener, null) {

        private val map: MutableMap<String, String> = HashMap()

        init {
            map["UID"] = UID
            map["selectedDate"] = selectedDate
        }

        override fun getParams(): Map<String, String> {
            return map
        }
    }
}
