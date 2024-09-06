package com.prog.mainproject

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.tensorflow.lite.Interpreter
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class PestActivity : AppCompatActivity() {
    private lateinit var btnCapture: Button
    private lateinit var btnUpload: Button
    private val CAMERA_PERMISSION_REQUEST = 101
    private val READ_EXTERNAL_STORAGE_PERMISSION_REQUEST = 102
    private var byteArray: ByteArray? = null

    private val originalModelPath = "all.tflite"
    private lateinit var originalTfLite: Interpreter
    private val originalClassLabels = arrayOf("Earlyblight", "LeafSpot", "Mite", "SootyMold", "aphids", "healthy", "powdery")

    private val imageCaptureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        handleImageCaptureResult(result.resultCode, result.data)
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        handleGalleryResult(result.resultCode, result.data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pest_home)

        originalTfLite = getTfliteInterpreter(originalModelPath)
        Log.d("PestActivity", "New Model Interpreter: $originalTfLite")

        btnCapture = findViewById(R.id.btnTakePicture)
        btnUpload = findViewById(R.id.buttonGallery)

        btnCapture.setOnClickListener {
            // 카메라 권한 확인 및 요청
            if (checkCameraPermission()) {
                val cInt = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                imageCaptureLauncher.launch(cInt)
            } else {
                requestCameraPermission()
            }
        }

        btnUpload.setOnClickListener {
            // 갤러리 권한 확인 및 요청
            if (checkReadExternalStoragePermission()) {
                // 갤러리에서 이미지 선택을 위한 Intent 생성
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                galleryLauncher.launch(galleryIntent)
            } else {
                requestReadExternalStoragePermission()
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
                    startActivity(Intent(this@PestActivity, HomeActivity::class.java))
                    true
                }
                R.id.page_fv -> {
                    // 질병진단 아이템 클릭 시 질병진단 화면으로 이동
                    //startActivity(Intent(this@PestActivity, PestActivity::class.java))
                    true
                }
                R.id.page_ps -> {
                    // 식물 기록 아이템 클릭 시 캘린더 화면으로 이동
                    finish()
                    startActivity(Intent(this@PestActivity, CalendarActivity::class.java))
                    true
                }
                R.id.page_show -> {
                    // 식물 보기 아이템 클릭 시 캘린더 화면으로 이동
                    finish()
                    startActivity(Intent(this@PestActivity, WebCamActivity::class.java))
                    true
                }
                else -> false
            }
        }

    }

    private fun handleImageCaptureResult(resultCode: Int, data: Intent?) {
        Log.d("PestActivity", "enter handler")
        if (resultCode == Activity.RESULT_OK) {
            // 이미지 캡처 성공 처리
            val bp = data?.extras?.get("data") as Bitmap
            // val rotatedBitmap = rotateBitmap(bp, 90f)
            val cx = 200
            val cy = 200
            val resizedBitmap = Bitmap.createScaledBitmap(bp, cx, cy, false)
            val pixels = IntArray(cx * cy)
            resizedBitmap.getPixels(pixels, 0, cx, 0, 0, cx, cy)
            val inputImg = getInputImage(pixels, cx, cy)

            val originalPred = Array(1) { FloatArray(originalClassLabels.size) }
            originalTfLite.run(inputImg, originalPred)

            val originalPredictedLabel = getPredictedClassLabel(originalPred[0])
            Log.d("PestActivity", "Predicted Label: $originalPredictedLabel")

            byteArray = bitmapToByteArray(bp)

            startDiagnosisActivity(originalPredictedLabel)
        }
    }


    // 갤러리에서 이미지를 선택한 결과를 처리하는 메서드
    private fun handleGalleryResult(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            // 선택한 이미지 URI를 이용하여 해당 이미지를 비트맵으로 가져온다.
            val selectedBitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)

            // 선택한 이미지에 대해 진단 수행
            performDiagnosis(selectedBitmap)
        } else if (resultCode == Activity.RESULT_CANCELED) {
            //Toast.makeText(this, "Gallery selection cancelled", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkCameraPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun checkReadExternalStoragePermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    // 카메라 권한 허용
    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST
        )
    }

    // 갤러리 권한 허용
    private fun requestReadExternalStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            READ_EXTERNAL_STORAGE_PERMISSION_REQUEST
        )
    }

    // 선택한 이미지에 대해 진단을 수행하는 메서드
    private fun performDiagnosis(selectedBitmap: Bitmap) {
        // 이 부분에서 선택한 이미지에 대한 진단을 수행
        // 예를 들어, 위의 handleImageCaptureResult 메서드와 유사한 코드를 사용할 수 있음
        // 이 코드는 선택한 이미지에 대해 진단을 수행하고 결과를 출력하는 부분을 나타냅니다.
        // val rotatedBitmap = rotateBitmap(selectedBitmap, 90f)
        val cx = 200
        val cy = 200
        val resizedBitmap = Bitmap.createScaledBitmap(selectedBitmap, cx, cy, false)
        val pixels = IntArray(cx * cy)
        resizedBitmap.getPixels(pixels, 0, cx, 0, 0, cx, cy)
        val inputImg = getInputImage(pixels, cx, cy)

        val originalPred = Array(1) { FloatArray(originalClassLabels.size) }
        originalTfLite.run(inputImg, originalPred)

        val originalPredictedLabel = getPredictedClassLabel(originalPred[0])
        Log.d("PestActivity", "Predicted Label: $originalPredictedLabel")

        // var imageBitmap = resizeBitmap(selectedBitmap, 400) // 이미지 리사이즈
        byteArray = bitmapToByteArray(selectedBitmap)

        startDiagnosisActivity(originalPredictedLabel)
    }

    private fun getInputImage(pixels: IntArray, cx: Int, cy: Int): ByteBuffer {
        val inputImg = ByteBuffer.allocateDirect(cx * cy * 3 * 4)
        inputImg.order(ByteOrder.nativeOrder())

        for (pixel in pixels) {
            // 수정: putInt 대신 putFloat를 사용해야 합니다.
            inputImg.putFloat(((pixel shr 16) and 0xff) / 255.0f)
            inputImg.putFloat(((pixel shr 8) and 0xff) / 255.0f)
            inputImg.putFloat((pixel and 0xff) / 255.0f)
        }

        return inputImg
    }

    private fun getTfliteInterpreter(modelPath: String): Interpreter {
        val fileDescriptor: AssetFileDescriptor = assets.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel: FileChannel = inputStream.channel
        val startOffset: Long = fileDescriptor.startOffset
        val declaredLength: Long = fileDescriptor.declaredLength
        val buffer: ByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        return Interpreter(buffer)
    }

    private fun getPredictedClassLabel(predictions: FloatArray): String {
        val maxIndex = predictions.indices.maxByOrNull { predictions[it] } ?: -1
        return if (maxIndex != -1) {
            originalClassLabels[maxIndex]
        } else {
            "Unknown"
        }
    }


    // 진단서 페이지로 연결
    private fun startDiagnosisActivity(predictedLabel: String) {
        // 클래스 레이블에 따른 액티비티 호출
        when (predictedLabel.toLowerCase()) {
            "leafspot" -> {
                val intent = Intent(this, PestLeafSpotActivity::class.java)
                intent.putExtra("byteArrayExtra", byteArray)
                startActivity(intent)
            }
            "sootymold" -> {
                // val intent = Intent(this, PestSootyMold::class.java)
                val intent = Intent(this, PestEarlyblight::class.java)
                intent.putExtra("byteArrayExtra", byteArray)
                startActivity(intent)
            }
            "mite" -> {
                // val intent = Intent(this, PestMite::class.java)
                val intent = Intent(this, PestConfusePowderyMealy::class.java)
                intent.putExtra("byteArrayExtra", byteArray)
                startActivity(intent)
            }
            "aphids" -> {
                // val intent = Intent(this, PestAphids::class.java)
                val intent = Intent(this, PestConfusePowderyMealy::class.java)
                intent.putExtra("byteArrayExtra", byteArray)
                startActivity(intent)
            }
            "healthy" -> {
                val intent = Intent(this, PestHealthy::class.java)
                intent.putExtra("byteArrayExtra", byteArray)
                startActivity(intent)
            }
            "powdery" -> {
                val intent = Intent(this, PestConfusePowderyMealy::class.java)
                intent.putExtra("byteArrayExtra", byteArray)
                startActivity(intent)
            }
            "earlyblight" -> {
                val intent = Intent(this, PestEarlyblight::class.java)
                intent.putExtra("byteArrayExtra", byteArray)
                startActivity(intent)
            }
            // 다른 클래스 레이블에 대한 처리 추가
            // 예: "Mite" -> startActivity(Intent(this, PestMiteActivity::class.java))
            // ...
            else -> {
                // 예측된 클래스 레이블에 대한 특별한 처리가 없을 경우에 대한 로직 추가
            }
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