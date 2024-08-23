package com.prog.mainproject

import android.content.ContentValues
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
    private var byteArray: ByteArray? = null

    private var plantSpecies: String? = ""
    private var plantName: String = ""
    private var pestInfo: String = ""
    private var memo: String = ""
    private var recordDate: String = ""
    private var plantImageBytes: ByteArray = byteArrayOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calender_add)

        // 날짜 세팅
        if(intent.getStringExtra("currentDate") != null) {
            recordDate = intent.getStringExtra("currentDate").toString()
        }
        else {
            recordDate = intent.getStringExtra("selectedDate").toString()
        }

        val tv_selectedDate = findViewById<TextView>(R.id.TV_selectedDate)
        tv_selectedDate.text = recordDate


        ImgV_calendarImage = findViewById(R.id.ImgV_calendarImage)
        spinner_nicknameSpecies = findViewById(R.id.nicknameSpinner)
        var tv_memo = findViewById<TextView>(R.id.TV_CalendarMemo)
        var btn_regicalendar = findViewById<Button>(R.id.Btn_RegiCalendar)
        val registerCamera = findViewById<TextView>(R.id.registerCamera)

        // 넘어온 병충해 정보가 있다면 받기
        if(intent.getStringExtra("pestInfo") != null) {
            pestInfo = intent.getStringExtra("pestInfo").toString()
        }


        // 인텐트로 받아온 이미지 있으면 세팅
        byteArray = intent.getByteArrayExtra("byteArrayExtra")
        if (byteArray != null){
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
            ImgV_calendarImage.setImageBitmap(bitmap)
        }

        // 이미지 뷰 클릭 시, 갤러리에서 사진을 선택하고 이미지 뷰를 해당 사진으로 대체
        ImgV_calendarImage.setOnClickListener{
            openGallery()
        }

        // 카메라 등록 클릭 시, 카메라 열기
        registerCamera.setOnClickListener {
            openCamera()
        }


        // 식물 종 입력 (spinner 어댑터)
        var plantNameList = HomeActivity.adapter.plantList.map { it.PlantName }
        var adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, plantNameList)
        spinner_nicknameSpecies.adapter = adapter
        spinner_nicknameSpecies.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var item = parent?.getItemAtPosition(position)
                plantName = item.toString().substringBefore("(").trim()
                plantSpecies = HomeActivity.getPlantSpeciesByPlantName(plantName)      // 식물 이름에 해당하는 식물 종을 가져옴
                Log.d("등록하려는 식물 종:", plantSpecies!!)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }

        btn_regicalendar.setOnClickListener{
            if (imageUri == null && byteArray == null) {
                Toast.makeText(applicationContext, "식물 사진을 선택해주세요", Toast.LENGTH_SHORT).show()
            }
            else {
                if(imageUri != null) {      // 이미지를 byteArray로 읽어오기
                    plantImageBytes = imageUri?.let { getByteArrayFromUri(this, it) } ?: byteArrayOf()
                }
                else {
                    plantImageBytes = byteArray!!
                }

                memo = tv_memo.text.toString()

                // Log.d("plantimage", imageUri.toString())

                val CalendarRegiRequest = MultipartRequest(
                    url = "http://15.165.56.246/android_calendarInput_mysql.php",
                    byteArray = plantImageBytes,
                    params = mapOf(
                        "UID" to LoginActivity.UID,
                        "plantName" to plantName,
                        "plantSpecies" to plantSpecies!!,
                        "recordDate" to recordDate,
                        "pestInfo" to pestInfo!!,
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
                                if (CalenderDetailActivity.adapter != null) {
                                    CalenderDetailActivity.adapter.calendarDayList.add(CalendarListClass(plantSpecies!!, plantName, plantImageBytes, recordDate, pestInfo, memo))
                                    CalenderDetailActivity.adapter.notifyDataSetChanged()
                                }
                                if (CalendarActivity.scheduleRecyclerViewAdapter != null) {
                                    CalendarActivity.scheduleRecyclerViewAdapter.CalendarMonthList.add(CalendarMonthClass(plantSpecies!!, plantName, recordDate, pestInfo))
                                    CalendarActivity.scheduleRecyclerViewAdapter.notifyDataSetChanged()
                                }
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
                finish()
            }
        }


        val backIcon = findViewById<ImageView>(R.id.back_icon)
        backIcon.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                finish() // 현재 액티비티 종료
            }
        })

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        // 바텀 네비게이션 아이템 클릭 리스너 설정
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.page_home -> {
                    // 홈 아이템 클릭 시 홈 화면으로 이동
                    finish()
                    startActivity(Intent(this@CalenderAddActivity, HomeActivity::class.java))
                    true
                }
                R.id.page_fv -> {
                    // 질병진단 아이템 클릭 시 질병진단 화면으로 이동
                    finish()
                    startActivity(Intent(this@CalenderAddActivity, PestActivity::class.java))
                    true
                }
                R.id.page_ps -> {
                    // 식물 기록 아이템 클릭 시 캘린더 화면으로 이동
                    finish()
                    startActivity(Intent(this@CalenderAddActivity, CalendarActivity::class.java))
                    true
                }
                R.id.page_show -> {
                    // 식물 보기 아이템 클릭 시 캘린더 화면으로 이동
                    finish()
                    startActivity(Intent(this@CalenderAddActivity, WebCamActivity::class.java))
                    true
                }
                else -> false
            }
        }
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
                    ImgV_calendarImage.setImageURI(imageUri)
                }
            }
        }

    // 갤러리 열기
    private fun openGallery() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        pickImageLauncher.launch(gallery)
    }


    private val takePictureLauncher: ActivityResultLauncher<Uri> = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            imageUri?.let {
                ImgV_calendarImage.setImageURI(it)
                // 나중에 사용할 이미지 byteArray 저장
                plantImageBytes = getByteArrayFromUri(this, it) ?: byteArrayOf()
            }
        }
    }

    private fun openCamera() {
        imageUri = createImageUri() // 이미지 URI 생성
        imageUri?.let {
            takePictureLauncher.launch(it)
        }
    }

    private fun createImageUri(): Uri? {
        val contentResolver = contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "new_image.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }


    // Uri 객체를 바이트어레이로 변환
    fun getByteArrayFromUri(context: Context, uri: Uri): ByteArray? {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        return if (inputStream != null) {
            // Decode the input stream into a bitmap
            val originalBitmap = BitmapFactory.decodeStream(inputStream)

            // Resize the bitmap to 400x400
            val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 400, 400, true)

            // Convert the resized bitmap to a byte array
            val buffer = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, buffer)
            buffer.toByteArray()
        } else {
            null
        }
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