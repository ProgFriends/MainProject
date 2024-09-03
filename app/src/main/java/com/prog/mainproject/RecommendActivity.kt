package com.prog.mainproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView

class RecommendActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        when{
            // scindapsus
            RecommendActivity_2nd.Q_2nd_Planterior && RecommendActivity_5th.Q_5th -> {
                setContentView(R.layout.recommend_scindapsus)
            }

            // hoya
            RecommendActivity_2nd.Q_2nd_AirPurify && RecommendActivity_4th.Q_4th && RecommendActivity_5th.Q_5th -> {
                setContentView(R.layout.recommend_hoya)
            }

            // alocasia
            RecommendActivity_2nd.Q_2nd_AirPurify && RecommendActivity_3rd.Q_3rd && RecommendActivity_4th.Q_4th -> {
                setContentView(R.layout.recommend_alocasia)
            }

            // dracaena
            RecommendActivity_2nd.Q_2nd_AirPurify && RecommendActivity_3rd.Q_3rd && RecommendActivity_5th.Q_5th -> {
                setContentView(R.layout.recommend_dracaena)
            }

            // monstera
            RecommendActivity_1st.Q_1st && RecommendActivity_2nd.Q_2nd_Planterior &&
                    RecommendActivity_3rd.Q_3rd && RecommendActivity_4th.Q_4th && RecommendActivity_5th.Q_5th -> {
                        setContentView(R.layout.recommend_monstera)
            }

            // oak
            RecommendActivity_1st.Q_1st && RecommendActivity_2nd.Q_2nd_AirPurify &&
                    RecommendActivity_3rd.Q_3rd && RecommendActivity_4th.Q_4th -> {
                        setContentView(R.layout.recommend_oak)
                    }

            // staghorn
            RecommendActivity_2nd.Q_2nd_Planterior && RecommendActivity_4th.Q_4th && RecommendActivity_5th.Q_5th -> {
                setContentView(R.layout.recommend_staghorn)
            }

            // ivy
            RecommendActivity_2nd.Q_2nd_AirPurify && RecommendActivity_5th.Q_5th -> {
                setContentView(R.layout.recommend_ivy)
            }
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