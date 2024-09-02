package com.prog.mainproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class RecommendActivity_1st : AppCompatActivity() {

    companion object {
        var Q_1st : Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommend_1st)

        val btn_Recommen_Y_1st = findViewById<Button>(R.id.Btn_Yes_1st)
        val btn_Recommen_N_1st = findViewById<Button>(R.id.Btn_No_1st)

        btn_Recommen_Y_1st.setOnClickListener{
            Q_1st = true
            val intent = Intent(applicationContext, RecommendActivity_2nd::class.java)
            startActivity(intent)
        }

        btn_Recommen_N_1st.setOnClickListener{
            Q_1st = false
            val intent = Intent(applicationContext, RecommendActivity_2nd::class.java)
            startActivity(intent)
        }
    }
}