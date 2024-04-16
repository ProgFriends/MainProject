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

class RegisterInformationActivityActivity : AppCompatActivity() {

    private lateinit var ImgV_plantImage: ImageView
    private lateinit var spinner_plantSpecies : Spinner
    private var imageUri: Uri? = null
    private var imageBitmap: Bitmap? = null

    private var plantSpecies: String? = null
    private var plantName: String = ""
    private var bringDate: String = ""
    private var plantImageBytes: ByteArray = byteArrayOf()
    private var plantImageBytesString : String = ""

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
            val drawable = ContextCompat.getDrawable(this, R.drawable.plant)

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
                Log.d("plantImageBytes:  ",  plantImageBytes.contentToString())
                Log.d("plantimage", imageUri.toString())

                //val bitmap: Bitmap = byteArrayToBitmap(plantImageBytes)
                //plantImageBytesString = bitmapToString(bitmap)
                //Log.d("plantImageBytes:  ",  plantImageBytesString)

                /*  // 안드로이드 스튜디오 내의 Drawable 파일은 가져올 수 있나 테스트
                if (drawable != null) {
                    // Drawable 이미지를 byte 배열로 변환
                    val plantImageBytes = getByteArrayFromDrawable(drawable)
                    // 이미지를 Base64 문자열로 변환
                    plantImageBytesString = Base64.encodeToString(plantImageBytes, Base64.DEFAULT)
                    Log.d("plantImageBytes 1:  ",  plantImageBytesString)
                }
                 */

                bringDate = tv_bringDate.toString().trim()                      // 식물 데려온 날짜 읽어오기

                val responseListener = Response.Listener<String> { response ->
                    try {
                        val jsonObject = JSONObject(response)
                        Log.d("식물 등록: Json객체", jsonObject.toString())

                        val success = jsonObject.getBoolean("success")
                        val message = jsonObject.getString("message")

                        if (success) {
                            Toast.makeText(applicationContext, "식물을 성공적으로 등록했습니다.", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                            return@Listener
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                val plnatRegiRequest = PlnatRegiRequest(LoginActivity.UID, plantSpecies!!, plantName, plantImageBytes, bringDate, responseListener)
                val queue: RequestQueue = LoginActivity.queue
                queue.add(plnatRegiRequest)



            }
        }
    }

    inner class PlnatRegiRequest(UID: String, plantSpecies: String, plantName: String, PlantImage: ByteArray, BringDate: String, listener: Response.Listener<String>) :
        StringRequest(Method.POST, "http://15.165.56.246/android_plantInput_mysql.php", listener, null) {

        private val map: MutableMap<String, String> = HashMap()

        init {
            map["UID"] = UID
            map["plantSpecies"] = plantSpecies
            map["plantName"] = plantName
            // map["plantImage"] = Base64.encodeToString(PlantImage, Base64.DEFAULT)
            map["plantImage"] = PlantImage.toString()
            map["BringDate"] = BringDate

            Log.d("plantImageEn:  ",  Base64.encodeToString(PlantImage, Base64.DEFAULT))
        }

        override fun getParams(): Map<String, String> {
            return map
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







    // 여기서부터 이미지를 바이트어레이로 변환할 때 테스트하며 사용한 함수들이에여...

    // 이미지 파일을 바이트 배열로 읽어오는 함수
    private fun getImageBytes(uri: Uri): ByteArray {
        val inputStream = contentResolver.openInputStream(uri)
        return inputStream?.use { it.readBytes() } ?: ByteArray(0)
    }


    // 비트맵 파일을 받아서 바이트 어레이로 변환
    fun bitmapToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, baos)
        val arr = baos.toByteArray()                            // ByteArrayOutputStream 객체인 baos를 생성 -> 비트맵 압축 -> 배열화
        val image = Base64.encodeToString(arr, Base64.DEFAULT)
        var temp = ""
        try {
            temp = "&imagedevice=" + URLEncoder.encode(image, "utf-8")
        } catch (e: Exception) {
            Log.e("exception", e.toString())
        }
        return temp
    }


    // 바이트 어레이를 비트맵으로 변환
    fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }


    // Drawable 파일을 바이트어레이로 변환
    fun getByteArrayFromDrawable(d: Drawable): ByteArray {
        val bitmap = (d as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, stream)
        return stream.toByteArray()
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


    // 이미지 주소를 절대경로로 바꿔주는 메소드
    fun getRealPathFromUri(context: Context, uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(context, uri, projection, null, null, null)
        val cursor: Cursor? = loader.loadInBackground()
        cursor?.use {
            val column_index = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            return it.getString(column_index)
        }
        return null
    }


    fun createCopyAndReturnRealPath(context: Context, uri: Uri): String? {
        val contentResolver: ContentResolver = context.getContentResolver() ?: return null

        // 파일 경로를 만듬
        val filePath: String = (context.getApplicationInfo().dataDir + File.separator
                + System.currentTimeMillis())
        val file = File(filePath)
        try {
            // 매개변수로 받은 uri 를 통해  이미지에 필요한 데이터를 불러 들인다.
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            // 이미지 데이터를 다시 내보내면서 file 객체에  만들었던 경로를 이용한다.
            val outputStream: OutputStream = FileOutputStream(file)
            val buf = ByteArray(1024)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
            outputStream.close()
            inputStream.close()
        } catch (ignore: IOException) {
            return null
        }
        return file.getAbsolutePath()
    }
}