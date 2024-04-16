package com.prog.mainproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.app.DatePickerDialog
import android.widget.Button
import java.text.SimpleDateFormat
import java.util.*


class CalenderAddActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calender_add)

        val backIcon = findViewById<ImageView>(R.id.back_icon)
        backIcon.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                finish() // 현재 액티비티 종료
            }
        })

        val selectedDate = intent.getStringExtra("selectedDate")

        val datePickerButton = findViewById<Button>(R.id.datePickerButton)
        datePickerButton.text = selectedDate
        datePickerButton.setOnClickListener {
            showDatePickerDialog()
        }


        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        // 바텀 네비게이션 아이템 클릭 리스너 설정
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.page_home -> {
                    startActivity(Intent(this@CalenderAddActivity, HomeActivity::class.java))
                    true
                }

                R.id.page_ps -> {
                    // 식물 기록 아이템 클릭 시 캘린더 화면으로 이동
                    startActivity(Intent(this@CalenderAddActivity, CalenderActivity::class.java))
                    true                }

                else -> false
            }
        }

    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
                // 날짜를 선택한 경우, 버튼의 텍스트를 업데이트
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                updateButtonText(selectedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun updateButtonText(selectedDate: Calendar) {
        val dateFormat = SimpleDateFormat("MMM dd yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(selectedDate.time)

        val datePickerButton = findViewById<Button>(R.id.datePickerButton)
        datePickerButton.text = formattedDate
    }
}