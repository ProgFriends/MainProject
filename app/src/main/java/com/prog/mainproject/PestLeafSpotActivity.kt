package com.prog.mainproject

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import java.text.SimpleDateFormat
import java.util.*

class PestLeafSpotActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pestdiagnosis_leafspot)

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
            val intent = Intent(this@PestLeafSpotActivity, CalenderPestAddActivity::class.java)
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            intent.putExtra("byteArrayExtra", receivedByteArray)
            intent.putExtra("currentDate", currentDate)
            intent.putExtra("pestInfo", "점무늬병")
            //Log.d("보낼날짜: ", currentDate)
            startActivity(intent)
        }

        // Add onClickListeners for recommend1 and recommend2
        val recommend1 = findViewById<ImageView>(R.id.recommend1)
        val recommend2 = findViewById<ImageView>(R.id.recommend2)

        recommend1.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coupang.com/vp/products/5421663779?itemId=8190313211&vendorItemId=75478441990&q=%ED%81%B4%EB%A6%B0%EC%8B%B96&itemsCount=36&searchId=ce6d92adc7444a458c0b4992e0ba1047&rank=1&isAddedCart="))
            startActivity(intent)
        }

        recommend2.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coupang.com/vp/products/1503826448?itemId=2581945436&vendorItemId=70574022544&q=%EA%B0%88%EB%B0%98%EB%82%99%EC%95%A0&itemsCount=36&searchId=8e1a3da6941c48a1b540b9ceaa42c5d7&rank=1&isAddedCart="))
            startActivity(intent)
        }

    }
}