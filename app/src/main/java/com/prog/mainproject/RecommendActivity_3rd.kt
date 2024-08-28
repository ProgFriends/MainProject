package com.prog.mainproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class RecommendActivity_3rd : AppCompatActivity() {

    companion object {
        var Q_3rd : Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommend_3rd)

        val btn_Recommen_Y_3rd = findViewById<Button>(R.id.Btn_Yes_3rd)
        val btn_Recommen_N_3rd = findViewById<Button>(R.id.Btn_No_3rd)

        btn_Recommen_Y_3rd.setOnClickListener{
            Q_3rd = true
            val intent = Intent(applicationContext, RecommendActivity_4th::class.java)
            startActivity(intent)
        }

        btn_Recommen_N_3rd.setOnClickListener{
            val intent = Intent(applicationContext, RecommendActivity_4th::class.java)
            startActivity(intent)
        }

    }
}