package com.prog.mainproject

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import java.io.ByteArrayOutputStream

class PestConfusePowderyMealy : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pestdiagnosis_confuse_powdery_mealy)

        val receivedByteArray = intent.getByteArrayExtra("byteArrayExtra")
        var PowderyByteArray : ByteArray = byteArrayOf()

        val backIcon = findViewById<ImageView>(R.id.back_icon)
        backIcon.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                finish() // 현재 액티비티 종료
            }
        })

        val goMealy = findViewById<Button>(R.id.goMealybug)
        goMealy.setOnClickListener{
            finish()
            val intent = Intent(this@PestConfusePowderyMealy, PestMealybug::class.java)
            intent.putExtra("byteArrayExtra", receivedByteArray)
            startActivity(intent)
        }

        val goPowder = findViewById<Button>(R.id.goPowdery)
        goPowder.setOnClickListener{
            finish()
            val intent = Intent(this@PestConfusePowderyMealy, PestPowdery::class.java)

            receivedByteArray?.let {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)

                val matrix = Matrix()
                matrix.postRotate(-90f)  // 왼쪽으로 90도 회전하려면 - 붙이면 됨
                val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

                PowderyByteArray = bitmapToByteArray(rotatedBitmap)
            }

            intent.putExtra("byteArrayExtra", PowderyByteArray)
            startActivity(intent)
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        var imageBitmap = resizeBitmap(bitmap, 400) // 이미지 리사이즈
        val stream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    private fun resizeBitmap(source: Bitmap, maxLength: Int): Bitmap {
        val aspectRatio = source.width.toFloat() / source.height.toFloat()
        val width: Int
        val height: Int

        if (aspectRatio > 1) {
            width = maxLength
            height = (maxLength / aspectRatio).toInt()
        } else {
            height = maxLength
            width = (maxLength * aspectRatio).toInt()
        }

        return Bitmap.createScaledBitmap(source, width, height, true)
    }
}