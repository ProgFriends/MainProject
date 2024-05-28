package com.prog.mainproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CalenderDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calender_detail)

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
                    startActivity(Intent(this@CalenderDetailActivity, CalenderActivity::class.java))
                    true
                }

                else -> false
            }
        }

        val selectedDate = intent.getStringExtra("selectedDate")

        val textView5 = findViewById<TextView>(R.id.textView5)
        textView5.text = selectedDate

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@CalenderDetailActivity, CalenderAddActivity::class.java)
            intent.putExtra("selectedDate", selectedDate)
            startActivity(intent)
        }


    }
}
