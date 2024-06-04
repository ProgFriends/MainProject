package com.prog.mainproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
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
                    startActivity(Intent(this@CalendarActivity, HomeFragment::class.java))
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
        }

        tv_next_month.setOnClickListener {
            scheduleRecyclerViewAdapter.changeToNextMonth()
        }
    }

    fun refreshCurrentMonth(calendar: Calendar) {
        val sdf = SimpleDateFormat("yyyy MM", Locale.KOREAN)
        tv_current_month.text = sdf.format(calendar.time)
    }
}
