package com.prog.mainproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.PrintWriter
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*


class CalenderAddActivity : AppCompatActivity() {

    private lateinit var ImgV_calendarImage: ImageView
    private lateinit var spinner_nicknameSpecies : Spinner
    private var imageUri: Uri? = null
    private var imageBitmap: Bitmap? = null

    private var plantSpecies: String? = ""
    private var plantName: String = ""
    private var memo: String = ""
    private var recordDate: String = ""
    private var plantImageBytes: ByteArray = byteArrayOf()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calender_add)

        // 날짜 세팅
        recordDate = intent.getStringExtra("selectedDate").toString()
        val tv_selectedDate = findViewById<TextView>(R.id.TV_selectedDate)
        tv_selectedDate.text = recordDate

        ImgV_calendarImage = findViewById(R.id.ImgV_calendarImage)
        spinner_nicknameSpecies = findViewById(R.id.nicknameSpinner)
        var tv_memo = findViewById<TextView>(R.id.TV_CalendarMemo)
        var btn_regicalendar = findViewById<Button>(R.id.Btn_RegiCalendar)


        // 이미지 뷰 클릭 시, 갤러리에서 사진을 선택하고 이미지 뷰를 해당 사진으로 대체
        ImgV_calendarImage.setOnClickListener{
            openGallery()
        }

        // 식물 종 입력 (spinner 어댑터)
        var plantNameList = HomeActivity.adapter.plantList.map { it.PlantName }
        var adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, plantNameList)
        spinner_nicknameSpecies.adapter = adapter
        spinner_nicknameSpecies.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var item = parent?.getItemAtPosition(position)
                plantName = item.toString().substringBefore("(").trim()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }

        btn_regicalendar.setOnClickListener{
            if (imageUri == null) {
                Toast.makeText(applicationContext, "식물 사진을 선택해주세요", Toast.LENGTH_SHORT).show()
            }
            else {
                plantImageBytes = imageUri?.let { getByteArrayFromUri(this, it) }
                    ?: byteArrayOf()       // 이미지를 byteArray로 읽어오기

                memo = tv_memo.text.toString()

                Log.d("plantimage", imageUri.toString())

                val CalendarRegiRequest = MultipartRequest(
                    url = "http://15.165.56.246/android_calendarInput_mysql.php",
                    byteArray = plantImageBytes,
                    params = mapOf(
                        "UID" to LoginActivity.UID,
                        "plantName" to plantName,
                        "plantSpecies" to plantSpecies!!,
                        "recordDate" to recordDate,
                        "pestInfo" to "",
                        "memo" to memo
                    ),
                    listener = { response ->
                        try {
                            val jsonString =
                                String(response.data, Charset.defaultCharset()) // 바이트 배열을 문자열로 변환
                            val jsonObject = JSONObject(jsonString) // 문자열을 JSONObject로 파싱
                            Log.d("식물 등록: Json객체", jsonObject.toString())

                            val success = jsonObject.getBoolean("success")
                            val message = jsonObject.getString("message")
                            Log.d("response message", message)

                            if (success) {
                                Toast.makeText(applicationContext, "기록을 완료했습니다.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    },
                    errorListener = { error ->
                        Log.e("식물 등록", "$error")
                    }
                )
                LoginActivity.queue.add(CalendarRegiRequest)

                plantSpecies = HomeActivity.getPlantSpeciesByPlantName(plantName)      // 식물 이름에 해당하는 식물 종을 가져옴
                Log.d("등록하려는 식물 종:", plantSpecies!!)
                CalenderDetailActivity.adapter.calendarDayList.add(CalendarListClass(plantSpecies!!, plantName, plantImageBytes, recordDate, "", memo))
                CalenderDetailActivity.adapter.notifyDataSetChanged()
                finish()
            }
        }



        val backIcon = findViewById<ImageView>(R.id.back_icon)
        backIcon.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                finish() // 현재 액티비티 종료
            }
        })
    }

    inner class MultipartRequest(
        url: String,
        private val byteArray: ByteArray,
        private val params: Map<String, String>,
        private val listener: Response.Listener<NetworkResponse>, // listener를 클래스의 프로퍼티로 만듭니다.
        errorListener: Response.ErrorListener
    ) : Request<NetworkResponse>(Method.POST, url, errorListener) {

        private val mimeType = "multipart/form-data"
        private val boundary = "apiclient-" + System.currentTimeMillis()
        private val header = HashMap<String, String>()

        init {
            header["Content-Type"] = "$mimeType;boundary=$boundary"
        }

        override fun getParams(): Map<String, String> {
            return params
        }

        override fun getHeaders(): Map<String, String> {
            return header
        }

        override fun getBodyContentType(): String {
            return "$mimeType;boundary=$boundary"
        }

        override fun getBody(): ByteArray {
            val outputStream = ByteArrayOutputStream()
            val writer = PrintWriter(outputStream)

            // Add parameters
            for ((key, value) in params) {
                writer.append("--$boundary\r\n")
                writer.append("Content-Disposition: form-data; name=\"$key\"\r\n")
                writer.append("\r\n$value\r\n")
            }

            // Add image
            writer.append("--$boundary\r\n")
            writer.append("Content-Disposition: form-data; name=\"plantImage\"; filename=\"${plantName}.jpg\"\r\n")
            writer.append("Content-Type: image/jpeg\r\n")
            writer.append("Content-Transfer-Encoding: binary\r\n")
            writer.append("\r\n")
            writer.flush()

            outputStream.write(byteArray)
            outputStream.flush()

            writer.append("\r\n")
            writer.append("--$boundary--\r\n")
            writer.close()

            return outputStream.toByteArray()
        }

        override fun parseNetworkResponse(response: NetworkResponse): Response<NetworkResponse> {
            return Response.success(response, HttpHeaderParser.parseCacheHeaders(response))
        }

        override fun deliverResponse(response: NetworkResponse) {
            listener.onResponse(response) // 이제 listener를 참조할 수 있습니다.
        }
    }

    // 가져온 사진 보여주기
    private val pickImageLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let {
                    imageUri = it
                    imageBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(it))   // 이미지를 비트맵으로 변환하여 변수에 저장
                    ImgV_calendarImage.setImageURI(imageUri)
                }
            }
        }

    // 갤러리 열기
    private fun openGallery() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        pickImageLauncher.launch(gallery)
    }

    // Uri 객체를 바이트어레이로 변환
    fun getByteArrayFromUri(context: Context, uri: Uri): ByteArray? {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        return if (inputStream != null) {
            val buffer = ByteArrayOutputStream()
            val bufferSize = 1024
            val bufferArray = ByteArray(bufferSize)
            var len: Int
            while (inputStream.read(bufferArray).also { len = it } != -1) {
                buffer.write(bufferArray, 0, len)
            }
            buffer.toByteArray()
        } else {
            null
        }
    }
}