package com.prog.mainproject

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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

class HomeActivity : AppCompatActivity(), PlantListAdapter.OnPlantListChanged {

    companion object {
        lateinit var adapter: PlantListAdapter

        fun getPlantSpeciesByPlantName(plantName: String): String? {
            // plantName과 일치하는 PlantListClass 객체를 찾음
            val plantInfo = adapter.plantList.find { it.PlantName == plantName }
            // 찾은 객체가 있으면 해당 객체의 PlantSpecies를 반환, 없으면 null 반환
            return plantInfo?.PlantSpecies
        }
    }

    lateinit var img_YourPlant: ImageView
    lateinit var btn_Recommend: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        adapter = PlantListAdapter(this)

        // 리사이클러뷰에 레이아웃 매니저 설정
        val layoutManager = LinearLayoutManager(this)
        val recyclerView = findViewById<RecyclerView>(R.id.RCV_PlantList)
        val btn_AddPlant = findViewById<ImageButton>(R.id.Btn_AddPlant)
        img_YourPlant = findViewById<ImageView>(R.id.image_YourpPant)
        btn_Recommend = findViewById<Button>(R.id.Btn_Recommend)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter



        val responseListener = Response.Listener<String> { response ->
            try {

                adapter.plantList.clear()

                val jsonObject = JSONObject(response)
                Log.d("식물 리스트 로딩: Json객체", jsonObject.toString())
                val plantsArray = jsonObject.getJSONArray("plants")

                val success = jsonObject.getBoolean("success")
                val message = jsonObject.getString("message")

                if (success) { // mysql 데이터 로딩에 성공한 경우

                    img_YourPlant.visibility = View.INVISIBLE
                    btn_Recommend.visibility = View.INVISIBLE

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

                    }
                    adapter.notifyDataSetChanged()

                }
                else { // mysql 데이터 로딩에 실패한 경우
                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                    return@Listener
                }
            }
            catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        val plnatlistRequest = PlnatListRequest(LoginActivity.UID, responseListener)
        val queue: RequestQueue = LoginActivity.queue
        queue.add(plnatlistRequest)

        // 등록 버튼 클릭시 RegisterInformationActivityActivity로 이동
        btn_AddPlant.setOnClickListener {
            val intent = Intent(this, RegisterInformationActivityActivity::class.java)
            registerActivityLauncher.launch(intent)
        }

        btn_Recommend.setOnClickListener{
            val intent = Intent(applicationContext, RecommendActivity_1st::class.java)
            startActivity(intent)
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
                    startActivity(Intent(this@HomeActivity, CalendarActivity::class.java))
                    true
                }
                R.id.page_show -> {
                    // 식물 보기 아이템 클릭 시 캘린더 화면으로 이동
                    startActivity(Intent(this@HomeActivity, WebCamActivity::class.java))
                    true
                }
                else -> false
            }
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

    // ActivityResultLauncher를 정의하여 결과를 받음
    private val registerActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // 식물 등록 성공시
            img_YourPlant.visibility = View.INVISIBLE
            btn_Recommend.visibility = View.INVISIBLE
        }
    }

    // 인터페이스 메서드 구현
    override fun onPlantListEmpty() {
        img_YourPlant.visibility = View.VISIBLE  // 리스트가 비었을 때 이미지 표시
        btn_Recommend.visibility = View.VISIBLE
    }
}