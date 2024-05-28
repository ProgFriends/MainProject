package com.prog.mainproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class CalenderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calender_home)

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
                    startActivity(Intent(this@CalenderActivity, HomeFragment::class.java))
                    true
                }
                R.id.page_fv -> {
                    // 질병진단 아이템 클릭 시 질병진단 화면으로 이동
                    finish()
                    startActivity(Intent(this@CalenderActivity, PestActivity::class.java))
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
                    startActivity(Intent(this@CalenderActivity, WebCamActivity::class.java))
                    true
                }
                else -> false
            }
        }


        val calendarView = findViewById<CalendarView>(R.id.calendarView)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->

            val selectedDate = "$year-${month + 1}-$dayOfMonth"
            Toast.makeText(this, "Selected Date: $selectedDate", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, CalenderDetailActivity::class.java)
            intent.putExtra("selectedDate", selectedDate)
            startActivity(intent)
        }
    }
}
