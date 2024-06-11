package com.prog.mainproject

import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

open class CalendarViewHolderHelper(val containerView: View, val baseCalendar: BaseCalendar) : RecyclerView.ViewHolder(containerView) {
    val tv_date: TextView = containerView.findViewById(R.id.tv_date)
    val tv_JustRecord: TextView = containerView.findViewById(R.id.TV_JustRecord)
    val tv_IsPest: TextView = containerView.findViewById(R.id.TV_IsPest)

    init {
        // 각 날짜를 표시하는 TextView에 클릭 리스너를 설정합니다.
        tv_date.setOnClickListener {
            val clickedYear = baseCalendar.CurrentYear
            val clickedMonth = baseCalendar.CurrentMonth
            val clickedDate = baseCalendar.data[adapterPosition] // 클릭된 아이템의 위치에 해당하는 날짜를 가져옵니다.

            // 클릭된 날짜 정보를 사용하여 Calendar 객체 생성
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, clickedYear)
            calendar.set(Calendar.MONTH, clickedMonth - 1) // Calendar.MONTH는 0부터 시작하므로 실제 월보다 1 작게 설정합니다.
            calendar.set(Calendar.DAY_OF_MONTH, clickedDate)

            // Calendar 객체를 Date 객체로 변환
            val date = calendar.time

            // 날짜를 원하는 형식으로 포맷팅
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = dateFormat.format(date)

            //Toast.makeText(itemView.context, "Clicked date: $formattedDate", Toast.LENGTH_SHORT).show()

            // 클릭된 날짜를 CalenderDetailActivity로 전달하는 Intent 생성
            val intent = Intent(containerView.context, CalenderDetailActivity::class.java)
            intent.putExtra("selectedDate", formattedDate)
            containerView.context.startActivity(intent)
        }
    }
}