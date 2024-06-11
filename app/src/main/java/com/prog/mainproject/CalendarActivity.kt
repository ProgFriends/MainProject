package com.prog.mainproject

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class CalendarActivity : AppCompatActivity() {

    lateinit var scheduleRecyclerViewAdapter: CalenderRecyAdapter

    private lateinit var rv_schedule: RecyclerView
    private lateinit var tv_prev_month: TextView
    private lateinit var tv_next_month: TextView
    private lateinit var tv_current_month: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calender_home)

        rv_schedule = findViewById(R.id.rv_schedule)
        tv_prev_month = findViewById(R.id.tv_prev_month)
        tv_next_month = findViewById(R.id.tv_next_month)
        tv_current_month = findViewById(R.id.tv_current_month)

        initView()
        var date = tv_current_month.text.toString()
        getData(tv_current_month.text.toString())

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
                    // 홈 아이템 클릭 시 홈 화면으로 이동
                    finish()
                    startActivity(Intent(this@CalendarActivity, HomeActivity::class.java))
                    true
                }
                R.id.page_fv -> {
                    // 질병진단 아이템 클릭 시 질병진단 화면으로 이동
                    finish()
                    startActivity(Intent(this@CalendarActivity, PestActivity::class.java))
                    true
                }
                R.id.page_ps -> {
                    // 식물 기록 아이템 클릭 시 캘린더 화면으로 이동
                    //startActivity(Intent(this@CalenderActivity, CalenderActivity::class.java))
                    true
                }
                R.id.page_show -> {
                    // 식물 기록 아이템 클릭 시 캘린더 화면으로 이동
                    finish()
                    startActivity(Intent(this@CalendarActivity, WebCamActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    fun initView() {

        scheduleRecyclerViewAdapter = CalenderRecyAdapter(this)

        rv_schedule.layoutManager = GridLayoutManager(this, BaseCalendar.DAYS_OF_WEEK)
        rv_schedule.adapter = scheduleRecyclerViewAdapter
        rv_schedule.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL))
        rv_schedule.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        tv_prev_month.setOnClickListener {
            scheduleRecyclerViewAdapter.changeToPrevMonth()
            getData(tv_current_month.text.toString())
        }

        tv_next_month.setOnClickListener {
            scheduleRecyclerViewAdapter.changeToNextMonth()
            getData(tv_current_month.text.toString())
        }
    }

    fun refreshCurrentMonth(calendar: Calendar) {
        val sdf = SimpleDateFormat("yyyy MM", Locale.KOREAN)
        tv_current_month.text = sdf.format(calendar.time)
    }

    fun getData(date: String){
        val parts = date.split(" ")
        val year = parts[0]
        val month = parts[1]
        val formattedDate = "${year}-${month}"
        scheduleRecyclerViewAdapter.CalendarMonthList.clear()

        val responseListener = Response.Listener<String> { response ->
            try {
                val jsonObject = JSONObject(response)
                Log.d("월별 달력 리스트 로딩: Json객체", jsonObject.toString())
                val plantsArray = jsonObject.getJSONArray("calendar")


                val success = jsonObject.getBoolean("success")
                val message = jsonObject.getString("message")

                if (success) { // mysql 데이터 로딩에 성공한 경우
                    for (i in 0 until plantsArray.length()) {
                        val plantObject = plantsArray.getJSONObject(i)
                        val plantSpecies = plantObject.getString("plantSpecies")
                        val plantName = plantObject.getString("plantName")
                        val recordDate = plantObject.getString("recordDate")
                        val pestInfo = plantObject.getString("pestInfo")

                        // 문자열을 Date 객체로 변환
                        val recordDateObj = stringToDate(recordDate, "yyyy-MM-dd")

                        // 식물 객체 생성 후 리스트에 추가
                        scheduleRecyclerViewAdapter.CalendarMonthList.add(CalendarMonthClass(plantSpecies, plantName, recordDate, pestInfo))
                    }
                    scheduleRecyclerViewAdapter.notifyDataSetChanged()
                    Log.d("가져온 월별 달력 리스트: ", scheduleRecyclerViewAdapter.CalendarMonthList.toString())
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

        val calendarmonthlistRequest = CalendarMonthListRequest(LoginActivity.UID, formattedDate, responseListener)
        val queue: RequestQueue = LoginActivity.queue
        queue.add(calendarmonthlistRequest)

    }

    // 문자열을 Date 객체로 변환하는 함수
    private fun stringToDate(dateString: String, format: String): Date {
        val formatter = SimpleDateFormat(format)
        return formatter.parse(dateString) ?: Date()
    }

    inner class CalendarMonthListRequest(UID: String, selectedMonth: String, listener: Response.Listener<String>) :
        StringRequest(Method.POST, "http://15.165.56.246/android_calendarShowbyMonth_mysql.php", listener, null) {

        private val map: MutableMap<String, String> = HashMap()

        init {
            map["UID"] = UID
            map["selectedMonth"] = selectedMonth
        }

        override fun getParams(): Map<String, String> {
            return map
        }
    }
}
