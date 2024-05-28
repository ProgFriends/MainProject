package com.prog.mainproject

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    companion object {
        lateinit var adapter: PlantListAdapter
        var isLoginActivityInitialized = false // LoginActivity 초기화 여부

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        adapter = PlantListAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize RecyclerView and other views here
        val recyclerView = view.findViewById<RecyclerView>(R.id.RCV_PlantList)
        val btn_AddPlant = view.findViewById<ImageButton>(R.id.Btn_AddPlant)
        val img_YourPlant = view.findViewById<ImageView>(R.id.image_YourpPant)


        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        // Handle the response listener
        val responseListener = Response.Listener<String> { response ->
            try {
                val jsonObject = JSONObject(response)
                Log.d("식물 리스트 로딩: Json객체", jsonObject.toString())
                val plantsArray = jsonObject.getJSONArray("plants")

                val success = jsonObject.getBoolean("success")
                val message = jsonObject.getString("message")

                if (success) { // mysql 데이터 로딩에 성공한 경우
                    for (i in 0 until plantsArray.length()) {
                        val plantObject = plantsArray.getJSONObject(i)
                        val plantSpecies = plantObject.getString("plantSpecies")
                        val plantName = plantObject.getString("plantName")
                        val plantImageString = plantObject.getString("plantImage")
                        val bringDate = plantObject.getString("bringDate")

                        val plantImageByteArray = Base64.decode(plantImageString, Base64.DEFAULT)

                        // 문자열을 Date 객체로 변환
                        val bringDateObj = stringToDate(bringDate, "yyyy-MM-dd")

                        // 식물 객체 생성 후 리스트에 추가
                        adapter.plantList.add(
                            PlantListClass(
                                plantSpecies,
                                plantName,
                                plantImageByteArray,
                                bringDateObj
                            )
                        )
                        if (adapter.plantList.size != 0) {
                            img_YourPlant.visibility = View.INVISIBLE
                        }
                    }
                    adapter.notifyDataSetChanged()

                }
                else { // mysql 데이터 로딩에 실패한 경우
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    return@Listener
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        // Ensure LoginActivity is initialized

        val plantListRequest = PlantListRequest(LoginActivity.UID, responseListener)
        val queue: RequestQueue = LoginActivity.queue
        queue.add(plantListRequest)


        btn_AddPlant.setOnClickListener {
            val intent = Intent(context, RegisterInformationActivityActivity::class.java)
            startActivity(intent)
        }
        return view
    }

    // 문자열을 Date 객체로 변환하는 함수
    private fun stringToDate(dateString: String, format: String): Date {
        val formatter = SimpleDateFormat(format)
        return formatter.parse(dateString) ?: Date()
    }

    inner class PlantListRequest(UID: String, listener: Response.Listener<String>) :
        StringRequest(Method.POST, "http://15.165.56.246/android_plantShow_mysql.php", listener, null) {

        private val map: MutableMap<String, String> = HashMap()

        init {
            map["UID"] = UID
        }

        override fun getParams(): Map<String, String> {
            return map
        }
    }
}
