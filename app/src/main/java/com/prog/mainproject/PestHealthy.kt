package com.prog.mainproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import java.text.SimpleDateFormat
import java.util.*

class PestHealthy : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pestdiagnosis_healthy)

        val receivedByteArray = intent.getByteArrayExtra("byteArrayExtra")

        val backIcon = findViewById<ImageView>(R.id.back_icon)
        backIcon.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                finish() // 현재 액티비티 종료
            }
        })

        val registerButton = findViewById<Button>(R.id.goCalender)
        registerButton.setOnClickListener{
            finish()
            val intent = Intent(this@PestHealthy, CalenderPestAddActivity::class.java)
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            intent.putExtra("byteArrayExtra", receivedByteArray)
            intent.putExtra("currentDate", currentDate)
            //Log.d("보낼날짜: ", currentDate)
            startActivity(intent)
        }
    }
}