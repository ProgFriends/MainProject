package com.prog.mainproject

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PsFragment : Fragment() {

    lateinit var scheduleRecyclerViewAdapter: CalenderRecyAdapter

    private lateinit var rv_schedule: RecyclerView
    private lateinit var tv_prev_month: TextView
    private lateinit var tv_next_month: TextView
    private lateinit var tv_current_month: TextView

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.calender_home, container, false)

        rv_schedule = view.findViewById(R.id.rv_schedule)
        tv_prev_month = view.findViewById(R.id.tv_prev_month)
        tv_next_month = view.findViewById(R.id.tv_next_month)
        tv_current_month = view.findViewById(R.id.tv_current_month)

        initView(view)

        val backIcon = view.findViewById<ImageView>(R.id.back_icon)
        backIcon.setOnClickListener {
            activity?.finish()
        }

        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.page_home -> {
                    activity?.finish()
                    startActivity(Intent(activity, HomeFragment::class.java))
                    true
                }
                R.id.page_fv -> {
                    activity?.finish()
                    startActivity(Intent(activity, PestActivity::class.java))
                    true
                }
                R.id.page_ps -> {
                    true
                }
                R.id.page_show -> {
                    activity?.finish()
                    startActivity(Intent(activity, WebCamActivity::class.java))
                    true
                }
                else -> false
            }
        }

        return view
    }

    private fun initView(view: View) {
        val calendarActivity = activity as? CalendarActivity ?: return

        scheduleRecyclerViewAdapter = CalenderRecyAdapter(calendarActivity)

        rv_schedule.layoutManager = GridLayoutManager(calendarActivity, BaseCalendar.DAYS_OF_WEEK)
        rv_schedule.adapter = scheduleRecyclerViewAdapter
        rv_schedule.addItemDecoration(DividerItemDecoration(calendarActivity, DividerItemDecoration.HORIZONTAL))
        rv_schedule.addItemDecoration(DividerItemDecoration(calendarActivity, DividerItemDecoration.VERTICAL))

        tv_prev_month.setOnClickListener {
            scheduleRecyclerViewAdapter.changeToPrevMonth()
        }

        tv_next_month.setOnClickListener {
            scheduleRecyclerViewAdapter.changeToNextMonth()
        }
    }

    fun refreshCurrentMonth(calendar: Calendar) {
        val sdf = SimpleDateFormat("yyyy MM", Locale.KOREAN)
        tv_current_month.text = sdf.format(calendar.time)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
