package com.prog.mainproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var adapter : PlantListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        adapter = PlantListAdapter()
        // 리사이클러뷰에 레이아웃 매니저 설정
        val layoutManager = LinearLayoutManager(this)
        val recyclerView = findViewById<RecyclerView>(R.id.RCV_PlantList)
        val btn_AddPlant = findViewById<ImageButton>(R.id.Btn_AddPlant)
        val img_YourPlant = findViewById<ImageView>(R.id.image_YourpPant)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        val responseListener = Response.Listener<String> { response ->
            try {
                val jsonObject = JSONObject(response)
                Log.d("식물 리스트 로딩: Json객체", jsonObject.toString())
                val plantsArray = jsonObject.getJSONArray("plants")


                val success = jsonObject.getBoolean("success")
                val message = jsonObject.getString("message")

                if (success) { // mysql 데이터 로딩에 성공한 경우
                    for (i in 0 until plantsArray.length()) {
                        val plantObject = plantsArray.getJSONObject(i)
                        val plantSpecies = plantObject.getString("plantSpecies")
                        val plantName = plantObject.getString("plantName")
                        val plantImageString = plantObject.getString("plantImage")
                        val bringDate = plantObject.getString("bringDate")

                        val plantImageByteArray = Base64.decode(plantImageString, Base64.DEFAULT)

                        // 문자열을 Date 객체로 변환
                        val bringDateObj = stringToDate(bringDate, "yyyy-MM-dd")

                        // 식물 객체 생성 후 리스트에 추가
                        adapter.plantList.add(PlantListClass(plantSpecies, plantName, plantImageByteArray, bringDateObj))
                        if (adapter.plantList.size != 0) {
                            img_YourPlant.visibility = View.INVISIBLE
                        }
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


            val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            // 바텀 네비게이션 아이템 클릭 리스너 설정
            bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.page_home -> {
                        // 홈 아이템 클릭 시 홈 화면으로 이동
                        true
                    }
                    R.id.page_fv -> {
                        // 질병진단 아이템 클릭 시 질병진단 화면으로 이동
                        startActivity(Intent(this@HomeActivity, PestActivity::class.java))
                        true
                    }
                    R.id.page_ps -> {
                        // 식물 기록 아이템 클릭 시 캘린더 화면으로 이동
                        startActivity(Intent(this@HomeActivity, CalenderActivity::class.java))
                        true
                    }
                    R.id.page_show -> {
                        // 식물 기록 아이템 클릭 시 캘린더 화면으로 이동
                        startActivity(Intent(this@HomeActivity, WebCamActivity::class.java))
                        true
                    }
                    else -> false
                }
            }
        }

        val plnatlistRequest = PlnatListRequest(LoginActivity.UID, responseListener)
        val queue: RequestQueue = LoginActivity.queue
        queue.add(plnatlistRequest)


        btn_AddPlant.setOnClickListener{
            val intent = Intent(applicationContext, RegisterInformationActivityActivity::class.java)
            startActivity(intent)
        }
    }

    // 문자열을 Date 객체로 변환하는 함수
    private fun stringToDate(dateString: String, format: String): Date {
        val formatter = SimpleDateFormat(format)
        return formatter.parse(dateString) ?: Date()
    }

    inner class PlnatListRequest(UID: String, listener: Response.Listener<String>) :
        StringRequest(Method.POST, "http://15.165.56.246/android_plantShow_mysql.php", listener, null) {

        private val map: MutableMap<String, String> = HashMap()

        init {
            map["UID"] = UID
        }

        override fun getParams(): Map<String, String> {
            return map
        }
    }
}