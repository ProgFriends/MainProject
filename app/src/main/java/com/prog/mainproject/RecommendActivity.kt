package com.prog.mainproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView

class RecommendActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if(RecommendActivity_1st.Q_1st == true) {
            setContentView(R.layout.recommend_scindapsus)
        }
        else{
            setContentView(R.layout.activity_recommend)
        }














        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        // 바텀 네비게이션 아이템 클릭 리스너 설정
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.page_home -> {
                    // 홈 아이템 클릭 시 홈 화면으로 이동
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.page_fv -> {
                    // 질병진단 아이템 클릭 시 질병진단 화면으로 이동
                    startActivity(Intent(this, PestActivity::class.java))
                    true
                }
                R.id.page_ps -> {
                    // 식물 기록 아이템 클릭 시 캘린더 화면으로 이동
                    startActivity(Intent(this, CalendarActivity::class.java))
                    true
                }
                R.id.page_show -> {
                    // 식물 보기 아이템 클릭 시 캘린더 화면으로 이동
                    startActivity(Intent(this, WebCamActivity::class.java))
                    true
                }
                else -> false
            }
        }

    }
}