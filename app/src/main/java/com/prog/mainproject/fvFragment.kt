package com.prog.mainproject

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ThumbnailUtils
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class fvFragment : Fragment() {
    private lateinit var btnCapture: Button
    private lateinit var btnUpload: Button
    private val CAMERA_PERMISSION_REQUEST = 101
    private val READ_EXTERNAL_STORAGE_PERMISSION_REQUEST = 102

    private val originalModelPath = "all.tflite"
    private lateinit var originalTfLite: Interpreter
    private val originalClassLabels = arrayOf("Earlyblight", "LeafSpot", "Mite", "SootyMold", "aphids", "healthy", "powdery")

    private val imageCaptureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        handleImageCaptureResult(result.resultCode, result.data)
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        handleGalleryResult(result.resultCode, result.data)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_fv, container, false)

        originalTfLite = getTfliteInterpreter(originalModelPath)
        Log.d("FvFragment", "New Model Interpreter: $originalTfLite")

        btnCapture = view.findViewById(R.id.btnTakePicture)
        btnUpload = view.findViewById(R.id.buttonGallery)

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

        val backIcon = view.findViewById<ImageView>(R.id.back_icon)
        backIcon.setOnClickListener {
            activity?.onBackPressed() // 현재 프래그먼트 종료
        }

        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        // 바텀 네비게이션 아이템 클릭 리스너 설정
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.page_home -> {
                    // 홈 아이템 클릭 시 홈 화면으로 이동
                    activity?.finish()
                    startActivity(Intent(activity, HomeFragment::class.java))
                    true
                }
                R.id.page_fv -> {
                    // 질병진단 아이템 클릭 시 질병진단 화면으로 이동
                    // 현재 프래그먼트이므로 아무 작업도 수행하지 않음
                    true
                }
                R.id.page_ps -> {
                    // 식물 기록 아이템 클릭 시 캘린더 화면으로 이동
                    activity?.
                    finish()
                    //startActivity(Intent(activity, CalenderActivity::class.java))
                    true
                }
                R.id.page_show -> {
                    // 식물 보기 아이템 클릭 시 캘린더 화면으로 이동
                    activity?.finish()
                    startActivity(Intent(activity, WebCamActivity::class.java))
                    true
                }
                else -> false
            }
        }

        return view
    }

    private fun handleImageCaptureResult(resultCode: Int, data: Intent?) {
        Log.d("FvFragment", "enter handler")
        if (resultCode == Activity.RESULT_OK) {
            // 이미지 캡처 성공 처리
            val bp = data?.extras?.get("data") as Bitmap
            val rotatedBitmap = rotateBitmap(bp, 90f)
            val cx = 200
            val cy = 200
            val resizedBitmap = Bitmap.createScaledBitmap(rotatedBitmap, cx, cy, false)
            val pixels = IntArray(cx * cy)
            resizedBitmap.getPixels(pixels, 0, cx, 0, 0, cx, cy)
            val inputImg = getInputImage(pixels, cx, cy)

            val originalPred = Array(1) { FloatArray(originalClassLabels.size) }
            originalTfLite.run(inputImg, originalPred)

            val originalPredictedLabel = getPredictedClassLabel(originalPred[0])
            Log.d("FvFragment", "Predicted Label: $originalPredictedLabel")

            startDiagnosisActivity(originalPredictedLabel)
        }
    }

    // 갤러리에서 이미지를 선택한 결과를 처리하는 메서드
    private fun handleGalleryResult(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            // 선택한 이미지 URI를 이용하여 해당 이미지를 비트맵으로 가져온다.
            val selectedBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, selectedImageUri)

            // 선택한 이미지에 대해 진단 수행
            performDiagnosis(selectedBitmap)
        } else if (resultCode == Activity.RESULT_CANCELED) {
            //Toast.makeText(requireContext(), "Gallery selection cancelled", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkReadExternalStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    // 카메라 권한 허용
    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST
        )
    }

    // 갤러리 권한 허용
    private fun requestReadExternalStoragePermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            READ_EXTERNAL_STORAGE_PERMISSION_REQUEST
        )
    }

    // 선택한 이미지에 대해 진단을 수행하는 메서드
    private fun performDiagnosis(selectedBitmap: Bitmap) {
        val rotatedBitmap = rotateBitmap(selectedBitmap, 90f)
        val cx = 200
        val cy = 200
        val resizedBitmap = Bitmap.createScaledBitmap(rotatedBitmap, cx, cy, false)
        val pixels = IntArray(cx * cy)
        resizedBitmap.getPixels(pixels, 0, cx, 0, 0, cx, cy)
        val inputImg = getInputImage(pixels, cx, cy)

        val originalPred = Array(1) { FloatArray(originalClassLabels.size) }
        originalTfLite.run(inputImg, originalPred)

        val originalPredictedLabel = getPredictedClassLabel(originalPred[0])
        Log.d("FvFragment", "Predicted Label: $originalPredictedLabel")

        startDiagnosisActivity(originalPredictedLabel)
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
            inputImg.putFloat(((pixel shr 16) and 0xff) / 255.0f)
            inputImg.putFloat(((pixel shr 8) and 0xff) / 255.0f)
            inputImg.putFloat((pixel and 0xff) / 255.0f)
        }

        return inputImg
    }

    private fun getTfliteInterpreter(modelPath: String): Interpreter {
        val fileDescriptor = requireContext().assets.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        val buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
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
        when (predictedLabel.toLowerCase()) {
            "leafspot" -> {
                val intent = Intent(activity, PestLeafSpotActivity::class.java)
                startActivity(intent)
            }
            "sootymold" -> {
                val intent = Intent(activity, PestSootyMold::class.java)
                startActivity(intent)
            }
            "mite" -> {
                val intent = Intent(activity, PestMite::class.java)
                startActivity(intent)
            }
            "aphids" -> {
                val intent = Intent(activity, PestAphids::class.java)
                startActivity(intent)
            }
            "healthy" -> {
                val intent = Intent(activity, PestHealthy::class.java)
                startActivity(intent)
            }
            "powdery" -> {
                val intent = Intent(activity, PestConfusePowderyMealy::class.java)
                startActivity(intent)
            }
            "earlyblight" -> {
                val intent = Intent(activity, PestEarlyblight::class.java)
                startActivity(intent)
            }
            else -> {
                // 예측된 클래스 레이블에 대한 특별한 처리가 없을 경우에 대한 로직 추가
            }
        }
    }
}
