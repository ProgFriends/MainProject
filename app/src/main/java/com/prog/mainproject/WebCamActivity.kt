package com.prog.mainproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import com.google.android.material.bottomnavigation.BottomNavigationView

class WebCamActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_cam)

        /*
        val webview = findViewById<WebView>(R.id.WebView)
        webview.webViewClient = WebViewClient()
        webview.loadUrl("http://172.20.3.46:5000")
*/
        val myWebView: WebView = findViewById(R.id.WebView)
        val webSettings: WebSettings = myWebView.settings
        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        myWebView.webViewClient = WebViewClient()
        myWebView.loadUrl("http://172.20.3.46:5000")

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
                    startActivity(Intent(this@WebCamActivity, HomeActivity::class.java))
                    true
                }
                R.id.page_fv -> {
                    // 질병진단 아이템 클릭 시 질병진단 화면으로 이동
                    finish()
                    startActivity(Intent(this@WebCamActivity, PestActivity::class.java))
                    true
                }
                R.id.page_ps -> {
                    // 식물 기록 아이템 클릭 시 캘린더 화면으로 이동
                    finish()
                    startActivity(Intent(this@WebCamActivity, CalendarActivity::class.java))
                    true
                }
                R.id.page_show -> {
                    // 식물 보기 아이템 클릭 시 캘린더 화면으로 이동
                    //finish()
                    //startActivity(Intent(this@WebCamActivity, WebCamActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}