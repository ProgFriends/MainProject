package com.prog.mainproject

import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.loader.content.CursorLoader
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.URLEncoder
import java.util.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.toolbox.HttpHeaderParser
import java.nio.charset.Charset
import java.text.SimpleDateFormat

class RegisterInformationActivityActivity : AppCompatActivity() {

    private lateinit var ImgV_plantImage: ImageView
    private lateinit var spinner_plantSpecies : Spinner
    private var imageUri: Uri? = null
    private var imageBitmap: Bitmap? = null

    private var plantSpecies: String? = null
    private var plantName: String = ""
    private var bringDate: String = ""
    private var plantImageBytes: ByteArray = byteArrayOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_information)

        val backIcon = findViewById<ImageView>(R.id.back_icon)
        backIcon.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                finish() // 현재 액티비티 종료
            }
        })

        ImgV_plantImage = findViewById(R.id.ImgV_plantImage)
        spinner_plantSpecies = findViewById(R.id.Spinner_plantSpecies)
        var edit_plantname = findViewById<EditText>(R.id.editText_plantName)
        var tv_bringDate = findViewById<TextView>(R.id.TV_bringDate)
        var btn_regiplant = findViewById<Button>(R.id.Btn_RegiPlant)

        // 이미지 뷰 클릭 시, 갤러리에서 사진을 선택하고 이미지 뷰를 해당 사진으로 대체
        ImgV_plantImage.setOnClickListener{
            openGallery()
        }

        // 식물 종 입력 (spinner 어댑터)
        var plantSpeciesList = listOf("scindapsus(스킨답서스)", "hoya(호야)", "frydek(알로카시아)", "corn(옥수수)", "monstera(몬스테라)", "oak(오크)", "staghorn(박쥐난)", "ivy(아이비)")
        var adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, plantSpeciesList)
        spinner_plantSpecies.adapter = adapter
        spinner_plantSpecies.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var item = parent?.getItemAtPosition(position)
                plantSpecies = item.toString().substringBefore("(").trim()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }

        // 식물을 데려온 날짜 입력
        tv_bringDate.setOnClickListener{
            var calendar = Calendar.getInstance()
            var year = calendar.get(Calendar.YEAR)
            var month = calendar.get(Calendar.MONTH)
            var day = calendar.get(Calendar.DAY_OF_MONTH)

            var bringDate_listner = object: DatePickerDialog.OnDateSetListener{
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
                    tv_bringDate.setText("${year}-${month+1}-${day}")
                }
            }
            var builder = DatePickerDialog(this, bringDate_listner, year, month, day)
            builder.show()
        }

        // 등록하기 버튼을 눌렀을 때
        btn_regiplant.setOnClickListener{
            if (imageUri == null) {
                Toast.makeText(applicationContext, "식물 사진을 선택해주세요", Toast.LENGTH_SHORT).show()
            }
            else if (edit_plantname.text.length == 0) {
                Toast.makeText(applicationContext, "식물의 애칭을 입력해주세요", android.widget.Toast.LENGTH_SHORT).show()
            }
            else if (tv_bringDate.text.equals("")) {
                Toast.makeText(applicationContext, "식물을 데려온 날짜를 입력해주세요", android.widget.Toast.LENGTH_SHORT).show()
            }
            else {
                plantName = edit_plantname.text.toString().trim()               // 식물 이름 읽어오기
                plantImageBytes = imageUri?.let { getByteArrayFromUri(this, it) } ?: byteArrayOf()       // 이미지를 byteArray로 읽어오기

                bringDate = tv_bringDate.text.toString().trim()
                val bringDateObj = stringToDate(bringDate, "yyyy-MM-dd")// 식물 데려온 날짜 읽어오기

                // 식물 종의 중복 여부를 확인
                if (isPlantSpeciesDuplicate(plantSpecies!!)) {
                    Toast.makeText(applicationContext, "이미 등록한 식물종입니다. 다른 종을 선택해주세요!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val PlantRegiRequest = MultipartRequest(
                    url = "http://15.165.56.246/android_plantInput_mysql.php",
                    plantName = plantName,
                    byteArray = plantImageBytes,
                    params = mapOf(
                        "UID" to LoginActivity.UID,
                        "plantSpecies" to plantSpecies!!,
                        "plantName" to plantName,
                        "BringDate" to bringDate
                    ),
                    listener = { response ->
                        try {
                            val jsonString = String(response.data, Charset.defaultCharset()) // 바이트 배열을 문자열로 변환
                            val jsonObject = JSONObject(jsonString) // 문자열을 JSONObject로 파싱
                            Log.d("식물 등록: Json객체", jsonObject.toString())

                            val success = jsonObject.getBoolean("success")
                            val message = jsonObject.getString("message")
                            Log.d("response message", message)

                            if (success) {
                                Toast.makeText(applicationContext, "식물을 성공적으로 등록했습니다.", Toast.LENGTH_SHORT).show()
                                HomeFragment.adapter.plantList.add(PlantListClass(plantSpecies!!, plantName, plantImageBytes, bringDateObj))
                                HomeFragment.adapter.notifyDataSetChanged() // 어댑터에게 데이터 변경을 알림
                                finish()
                            }
                            else {
                                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    },
                    errorListener = { error ->
                        Log.e("식물 등록","$error")
                    }
                )
                LoginActivity.queue.add(PlantRegiRequest)
            }
        }
    }

    // 새로운 함수 추가
    private fun isPlantSpeciesDuplicate(plantSpecies: String): Boolean {
        for (plant in HomeFragment.adapter.plantList) {
            if (plant.PlantSpecies == plantSpecies) {
                return true
            }
        }
        return false
    }


    // new code
    inner class MultipartRequest(
        url: String,
        private val plantName: String,
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
                    ImgV_plantImage.setImageURI(imageUri)
                }
            }
        }

    // 갤러리 열기
    private fun openGallery() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        pickImageLauncher.launch(gallery)
    }

    // 문자열을 Date 객체로 변환하는 함수
    private fun stringToDate(dateString: String, format: String): Date {
        val formatter = SimpleDateFormat(format)
        return formatter.parse(dateString) ?: Date()
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