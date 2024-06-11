package com.prog.mainproject

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.*

data class CalendarMonthClass(
    var plantSpecies: String,
    var plantName: String,
    var recordDate: String,
    var pestInfo: String
)

class CalenderRecyAdapter(val Activity: CalendarActivity) : RecyclerView.Adapter<CalendarViewHolderHelper>() {

    val baseCalendar = BaseCalendar()
    var CalendarMonthList = ArrayList<CalendarMonthClass>()

    init {
        baseCalendar.initBaseCalendar {
            refreshView(it)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolderHelper {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule, parent, false)
        return CalendarViewHolderHelper(view, baseCalendar)
    }

    override fun getItemCount(): Int {
        return BaseCalendar.LOW_OF_CALENDAR * BaseCalendar.DAYS_OF_WEEK
    }

    override fun onBindViewHolder(holder: CalendarViewHolderHelper, position: Int) {

        if (position % BaseCalendar.DAYS_OF_WEEK == 0) holder.tv_date.setTextColor(Color.parseColor("#ff1200"))
        else holder.tv_date.setTextColor(Color.parseColor("#676d6e"))

        if (position < baseCalendar.prevMonthTailOffset || position >= baseCalendar.prevMonthTailOffset + baseCalendar.currentMonthMaxDate) {
            holder.tv_date.alpha = 0.3f
        } else {
            holder.tv_date.alpha = 1f
        }
        holder.tv_date.text = baseCalendar.data[position].toString()
    }

    fun changeToPrevMonth() {
        baseCalendar.changeToPrevMonth {
            refreshView(it)
        }
    }

    fun changeToNextMonth() {
        baseCalendar.changeToNextMonth {
            refreshView(it)
        }
    }

    private fun refreshView(calendar: Calendar) {
        notifyDataSetChanged()
        Activity.refreshCurrentMonth(calendar)
    }
}