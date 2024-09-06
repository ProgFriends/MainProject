package com.prog.mainproject

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import java.text.SimpleDateFormat
import java.util.*

class PestMite : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pestdiagnosis_mite)

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
            val intent = Intent(this@PestMite, CalenderPestAddActivity::class.java)
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            intent.putExtra("byteArrayExtra", receivedByteArray)
            intent.putExtra("currentDate", currentDate)
            intent.putExtra("pestInfo", "응애")
            //Log.d("보낼날짜: ", currentDate)
            startActivity(intent)
        }

        // Add onClickListeners for recommend1 and recommend2
        val recommend1 = findViewById<ImageView>(R.id.recommend1)
        val recommend2 = findViewById<ImageView>(R.id.recommend2)

        recommend1.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coupang.com/vp/products/6267451011?itemId=12798170909&vendorItemId=80064209689&pickType=COU_PICK&q=%EA%B7%B8%EB%A6%B0%EC%8D%AC%EA%B7%B8%EB%A6%B0%ED%82%AC&itemsCount=35&searchId=5aa38eb53bdc4109a8fb917610028072&rank=0&isAddedCart="))
            startActivity(intent)
        }

        recommend2.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coupang.com/vp/products/7623361945?itemId=20216398918&vendorItemId=76227279386&q=%EB%B0%94%EB%A1%9C%ED%82%AC&itemsCount=36&searchId=5c562b63ea7a4216b4483a6d70d2bd4d&rank=0&isAddedCart="))
            startActivity(intent)
        }

    }
}