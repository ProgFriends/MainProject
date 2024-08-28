package com.prog.mainproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class RecommendActivity_2nd : AppCompatActivity() {

    companion object {
        var Q_2nd_Planterior : Boolean = false
        var Q_2nd_AirPurify: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommed_2nd)

        val btn_Recommen_P_2nd = findViewById<Button>(R.id.Btn_Planterior_2nd)
        val btn_Recommen_A_2nd = findViewById<Button>(R.id.Btn_AirPurify_2nd)

        btn_Recommen_P_2nd.setOnClickListener{
            Q_2nd_Planterior = true
            val intent = Intent(applicationContext, RecommendActivity_3rd::class.java)
            startActivity(intent)
        }

        btn_Recommen_A_2nd.setOnClickListener{
            Q_2nd_AirPurify = true
            val intent = Intent(applicationContext, RecommendActivity_3rd::class.java)
            startActivity(intent)
        }


    }
}