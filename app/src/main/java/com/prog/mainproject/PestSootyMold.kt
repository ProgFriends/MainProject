package com.prog.mainproject

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import java.text.SimpleDateFormat
import java.util.*

class PestSootyMold : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pestdiagnosis_sootymold)

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
            val intent = Intent(this@PestSootyMold, CalenderPestAddActivity::class.java)
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            intent.putExtra("byteArrayExtra", receivedByteArray)
            intent.putExtra("currentDate", currentDate)
            intent.putExtra("pestInfo", "그을음병")
            //Log.d("보낼날짜: ", currentDate)
            startActivity(intent)
        }

        // Add onClickListeners for recommend1 and recommend2
        val recommend1 = findViewById<ImageView>(R.id.recommend1)
        val recommend2 = findViewById<ImageView>(R.id.recommend2)

        recommend1.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coupang.com/vp/products/1336006148?itemId=2361835508&vendorItemId=87627560007&q=%ED%81%B4%EB%A6%B0%ED%8C%A1&itemsCount=36&searchId=e54d953baf0b486094eebb71b0135263&rank=0&isAddedCart="))
            startActivity(intent)
        }

        recommend2.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coupang.com/vp/products/4564972763?itemId=5567390895&vendorItemId=72866743699&sourceType=SDW_TOP_SELLING_WIDGET_V2&searchId=39e5d2e7b18c4d3492bf06e517c2ba68&q=%EB%8B%A4%EC%9E%A1%EC%95%84&isAddedCart="))
            startActivity(intent)
        }

    }
}