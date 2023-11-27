package com.prog.mainproject

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.prog.tedpermission.PermissionListener
import com.prog.tedpermission.TedPermission
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PestActivity : AppCompatActivity() {

    private val REQUEST_IMAGE_CAPTURE = 672
    private var imageFilePath: String? = null
    private var photoUri: Uri? = null
    private lateinit var mMediaScanner: MediaScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pest_home)

        mMediaScanner = MediaScanner.getInstance(applicationContext)

        TedPermission.with(applicationContext)
            .setPermissionListener(permissionListener)
            .setRationaleMessage("카메라 권한이 필요합니다.")
            .setDeniedMessage("거부하셨습니다.")
            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
            .check()

        findViewById<View>(R.id.pestbtn).setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(packageManager) != null) {
                var photoFile: File? = null
                try {
                    photoFile = createImageFile()
                } catch (e: IOException) {
                }
                if (photoFile != null) {
                    photoUri = FileProvider.getUriForFile(applicationContext, packageName, photoFile)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "TEST_$timeStamp" + "_"
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
        imageFilePath = image.absolutePath
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val bitmap = BitmapFactory.decodeFile(imageFilePath)
            var exif: ExifInterface? = null
            try {
                exif = ExifInterface(imageFilePath!!)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val exifOrientation: Int
            val exifDegree: Int

            exifOrientation = exif?.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                ?: ExifInterface.ORIENTATION_NORMAL
            exifDegree = exifOrientationToDegress(exifOrientation)

            var result = ""
            val formatter = SimpleDateFormat("yyyy-MM-dd HHmmss", Locale.getDefault())
            val curDate = Date(System.currentTimeMillis())
            val filename = formatter.format(curDate)

            val strFolderName =
                Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES).toString() + File.separator + "HONGDROID" + File.separator
            val file = File(strFolderName)
            if (!file.exists()) file.mkdirs()

            val f = File("$strFolderName/$filename.png")
            result = f.path

            var fOut: FileOutputStream? = null
            try {
                fOut = FileOutputStream(f)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                result = "Save Error fOut"
            }

            rotate(bitmap, exifDegree).compress(Bitmap.CompressFormat.PNG, 70, fOut)

            try {
                fOut?.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            try {
                fOut?.close()
                mMediaScanner.mediaScanning("$strFolderName/$filename.png")
            } catch (e: IOException) {
                e.printStackTrace()
                result = "File close Error"
            }

            (findViewById<View>(R.id.pestImage) as ImageView).setImageBitmap(rotate(bitmap, exifDegree))
        }
    }

    private fun exifOrientationToDegress(exifOrientation: Int): Int {
        return when (exifOrientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    }

    private fun rotate(bitmap: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private val permissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            Toast.makeText(applicationContext, "권한이 허용됨", Toast.LENGTH_SHORT).show()
        }

        override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
            Toast.makeText(applicationContext, "권한이 거부됨", Toast.LENGTH_SHORT).show()
        }
    }
}