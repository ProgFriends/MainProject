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

class PestAphids : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pestdiagnosis_aphids)

        val receivedByteArray = intent.getByteArrayExtra("byteArrayExtra")

        val backIcon = findViewById<ImageView>(R.id.back_icon)
        backIcon.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                finish() // 현재 액티비티 종료
            }
        })

        val registerButton = findViewById<Button>(R.id.goCalender)
        registerButton.setOnClickListener{
            val intent = Intent(this@PestAphids, CalenderAddActivity::class.java)
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            intent.putExtra("byteArrayExtra", receivedByteArray)
            intent.putExtra("currentDate", currentDate)
            intent.putExtra("pestInfo", "진딧물")
            //Log.d("보낼날짜: ", currentDate)
            startActivity(intent)
        }


        // Add onClickListeners for recommend1 and recommend2
        val recommend1 = findViewById<ImageView>(R.id.recommend1)
        val recommend2 = findViewById<ImageView>(R.id.recommend2)

        recommend1.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coupang.com/vp/products/4622704003?itemId=5732166466&vendorItemId=73030888101&pickType=COU_PICK&q=%EC%A7%84%EB%94%94%EC%A0%9C%EB%A1%9C&itemsCount=36&searchId=fe1b5d7bd77d427a9b7157826977e329&rank=1&isAddedCart="))
            startActivity(intent)
        }

        recommend2.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coupang.com/vp/products/6269289783?itemId=12812174907&vendorItemId=80078092917&q=%EB%AA%A8%EB%91%90%EC%8B%B9&itemsCount=36&searchId=5818b247926145df8b5f57dab3702812&rank=1&isAddedCart="))
            startActivity(intent)
        }

    }
}