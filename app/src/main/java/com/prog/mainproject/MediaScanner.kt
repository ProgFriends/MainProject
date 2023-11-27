package com.prog.mainproject

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.text.TextUtils

/**
 * 이미지 저장 후 미디어 스캐닝을 수행해줄 때 사용하는 유틸 클래스
 */
class MediaScanner private constructor(context: Context) {
    private val mContext: Context = context.applicationContext
    private var mFilePath: String = ""
    private val mMediaScanner: MediaScannerConnection

    init {
        val mediaScanClient = object : MediaScannerConnection.MediaScannerConnectionClient {
            override fun onMediaScannerConnected() {
                mMediaScanner.scanFile(mFilePath, null)
            }

            override fun onScanCompleted(path: String, uri: Uri) {
                println("::::MediaScan Success::::")
                mMediaScanner.disconnect()
            }
        }
        mMediaScanner = MediaScannerConnection(mContext, mediaScanClient)
    }

    companion object {
        private var mMediaInstance: MediaScanner? = null

        fun getInstance(context: Context): MediaScanner? {
            if (mMediaInstance == null) {
                mMediaInstance = MediaScanner(context)
            }
            return mMediaInstance
        }

        fun releaseInstance() {
            mMediaInstance = null
        }
    }

    fun mediaScanning(path: String) {
        if (TextUtils.isEmpty(path)) return
        mFilePath = path
        if (!mMediaScanner.isConnected) {
            mMediaScanner.connect()
        }
    }
}