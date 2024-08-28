package com.prog.mainproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class RecommendActivity_5th : AppCompatActivity() {

    companion object {
        var Q_5th : Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommend_5th)

        val btn_Recommen_Y_5th = findViewById<Button>(R.id.Btn_Yes_5th)
        val btn_Recommen_N_5th = findViewById<Button>(R.id.Btn_No_5th)

        btn_Recommen_Y_5th.setOnClickListener{
            Q_5th = true
            GoRecommend()
        }

        btn_Recommen_N_5th.setOnClickListener{
            GoRecommend()
        }
    }

    private fun GoRecommend(){      // 여기서 각 문항의 true/false에 따라 식물 추천 페이지로 넘어갈거임!

    }
}