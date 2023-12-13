package com.prog.mainproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView

class PestConfusePowderyMealy : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pest_confuse_powdery_mealy)

        val backIcon = findViewById<ImageView>(R.id.back_icon)
        backIcon.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                finish() // 현재 액티비티 종료
            }
        })

        val goMealy = findViewById<Button>(R.id.goMealybug)
        goMealy.setOnClickListener{
            val intent = Intent(this@PestConfusePowderyMealy, PestMealybug::class.java)
            startActivity(intent)
        }

        val goPowder = findViewById<Button>(R.id.goPowdery)
        goPowder.setOnClickListener{
            val intent = Intent(this@PestConfusePowderyMealy, PestPowdery::class.java)
            startActivity(intent)
        }
    }
}