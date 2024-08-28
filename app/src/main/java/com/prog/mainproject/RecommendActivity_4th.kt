package com.prog.mainproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class RecommendActivity_4th : AppCompatActivity() {

    companion object {
        var Q_4th : Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommend_4th)

        val btn_Recommen_Y_4th = findViewById<Button>(R.id.Btn_Yes_4th)
        val btn_Recommen_N_4th = findViewById<Button>(R.id.Btn_No_4th)

        btn_Recommen_Y_4th.setOnClickListener{
            Q_4th = true
            val intent = Intent(applicationContext, RecommendActivity_5th::class.java)
            startActivity(intent)
        }

        btn_Recommen_N_4th.setOnClickListener{
            val intent = Intent(applicationContext, RecommendActivity_5th::class.java)
            startActivity(intent)
        }

    }
}