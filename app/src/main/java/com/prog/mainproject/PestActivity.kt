package com.prog.mainproject

import android.app.Activity
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ThumbnailUtils
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class PestActivity : AppCompatActivity() {
    private lateinit var btnCapture: Button
    private lateinit var imgCapture: ImageView
    private lateinit var btnUpload: Button

    private val imageCaptureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        handleImageCaptureResult(result.resultCode, result.data)
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        handleGalleryResult(result.resultCode, result.data)
    }

    private val ImageCaptureCode = 1

    // 클래스 레이블 정의
    private val classLabels = arrayOf("LeafSpot", "Mite", "SootyMold", "aphids", "edit_powdery", "healthy", "new_earlyblight", "new_lateblight")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pest_home)

        btnCapture = findViewById(R.id.btnTakePicture)
        imgCapture = findViewById(R.id.capturedImage)
        btnUpload = findViewById(R.id.buttonGallery)

        btnCapture.setOnClickListener {
            val cInt = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            imageCaptureLauncher.launch(cInt)
        }

        btnUpload.setOnClickListener {
            // 갤러리에서 이미지 선택을 위한 Intent 생성
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(galleryIntent)
        }

    }

    private fun handleImageCaptureResult(resultCode: Int, data: Intent?) {
        Log.d("PestActivity", "enter handler")
        if (resultCode == Activity.RESULT_OK) {
            // 이미지 캡처 성공 처리
            val bp = data?.extras?.get("data") as Bitmap
            val rotatedBitmap = rotateBitmap(bp, 90f)
            val cx = 150
            val cy = 150
            val resizedBitmap = Bitmap.createScaledBitmap(rotatedBitmap, cx, cy, false)
            val pixels = IntArray(cx * cy)
            resizedBitmap.getPixels(pixels, 0, cx, 0, 0, cx, cy)
            val inputImg = getInputImage(pixels, cx, cy)

            val tfLite = getTfliteInterpreter("main.tflite")
            Log.d("PestActivity", "load model")

            val pred =  Array(1) { FloatArray(classLabels.size) }
            tfLite?.run(inputImg, pred)

            // 예측된 클래스 레이블 가져오기a
            val predictedLabel = getPredictedClassLabel(pred[0])
            Log.d("PestActivity", predictedLabel)

            // 토스트 메시지로 예측된 클래스 레이블 출력
            Toast.makeText(applicationContext, "Predicted Label: $predictedLabel", Toast.LENGTH_LONG).show()
            Log.d("PestActivity1", "Prediction Array: ${pred.contentDeepToString()}")

            imgCapture.setImageBitmap(resizedBitmap)
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // 사용자가 이미지 캡처를 취소한 경우 처리
            Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
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
            Toast.makeText(this, "Gallery selection cancelled", Toast.LENGTH_LONG).show()
        }
    }

    // 선택한 이미지에 대해 진단을 수행하는 메서드
    private fun performDiagnosis(selectedBitmap: Bitmap) {
        // 이 부분에서 선택한 이미지에 대한 진단을 수행
        // 예를 들어, 위의 handleImageCaptureResult 메서드와 유사한 코드를 사용할 수 있음
        // 이 코드는 선택한 이미지에 대해 진단을 수행하고 결과를 출력하는 부분을 나타냅니다.
        val rotatedBitmap = rotateBitmap(selectedBitmap, 90f)
        val cx = 150
        val cy = 150
        val resizedBitmap = Bitmap.createScaledBitmap(rotatedBitmap, cx, cy, false)
        val pixels = IntArray(cx * cy)
        resizedBitmap.getPixels(pixels, 0, cx, 0, 0, cx, cy)
        val inputImg = getInputImage(pixels, cx, cy)

        val tfLite = getTfliteInterpreter("main.tflite")
        Log.d("PestActivity", "load model")

        val pred = Array(1) { FloatArray(classLabels.size) }
        tfLite?.run(inputImg, pred)

        // 예측된 클래스 레이블 가져오기
        val predictedLabel = getPredictedClassLabel(pred[0])
        Log.d("PestActivity", predictedLabel)

        // 토스트 메시지로 예측된 클래스 레이블 출력
        Toast.makeText(applicationContext, "Predicted Label: $predictedLabel", Toast.LENGTH_LONG).show()
        Log.d("PestActivity1", "Prediction Array: ${pred.contentDeepToString()}")

        imgCapture.setImageBitmap(resizedBitmap)
    }

    private fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return ThumbnailUtils.extractThumbnail(Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true), 1080, 1080)
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


    private fun getTfliteInterpreter(modelPath: String): Interpreter? {
        try {
            return Interpreter(loadModelFile(modelPath))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun loadModelFile(modelPath: String): MappedByteBuffer {
        val fileDescriptor: AssetFileDescriptor = assets.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel: FileChannel = inputStream.channel
        val startOffset: Long = fileDescriptor.startOffset
        val declaredLength: Long = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun getPredictedClassLabel(predictions: FloatArray): String {
        val maxIndex = predictions.indices.maxBy { predictions[it] } ?: -1
        Log.d("PestActivity", maxIndex.toString())
        return if (maxIndex != -1 && maxIndex < classLabels.size) {
            // 수정: 대소문자 구분하지 않도록 변경
            classLabels[maxIndex].toLowerCase()
        } else {
            "Unknown"
        }
    }

}