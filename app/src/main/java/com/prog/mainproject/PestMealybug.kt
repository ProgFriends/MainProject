package com.prog.mainproject

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView

class PestMealybug : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pestdiagnosis_mealybug)

        val backIcon = findViewById<ImageView>(R.id.back_icon)
        backIcon.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                finish() // 현재 액티비티 종료
            }
        })

        val registerButton = findViewById<Button>(R.id.goCalender)
        registerButton.setOnClickListener{
            finish()
            val intent = Intent(this@PestMealybug, CalenderPestAddActivity::class.java)
            startActivity(intent)
        }

        // Add onClickListeners for recommend1 and recommend2
        val recommend1 = findViewById<ImageView>(R.id.recommend1)
        val recommend2 = findViewById<ImageView>(R.id.recommend2)

        recommend1.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coupang.com/vp/products/4622703980?itemId=5732166426&vendorItemId=73030887945&pickType=COU_PICK&q=%EA%B9%8D%EC%A7%80%EC%A0%9C%EB%A1%9C&itemsCount=36&searchId=70941092e6e9472d9386d521f4423ffb&rank=1&isAddedCart="))
            startActivity(intent)
        }

        recommend2.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coupang.com/vp/products/7623361945?itemId=20216398918&vendorItemId=76227279386&q=%EB%B0%94%EB%A1%9C%ED%82%AC&itemsCount=36&searchId=5c562b63ea7a4216b4483a6d70d2bd4d&rank=0&isAddedCart="))
            startActivity(intent)
        }

    }
}