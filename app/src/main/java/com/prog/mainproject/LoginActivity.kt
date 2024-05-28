package com.prog.mainproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class LoginActivity : AppCompatActivity() {
    private lateinit var edit_EMAIL: EditText
    private lateinit var edit_PW: EditText
    private lateinit var btn_Login: Button
    private lateinit var btn_Goregister: Button

    private lateinit var auth: FirebaseAuth

    // static 변수로 설정
    companion object {
        var userEMAIL: String = ""
        var userID: String = ""
        var UID: String = ""
        var userToken: String = ""

        lateinit var queue: RequestQueue
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setTitle("로그인")

        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()       // 파이어베이스

        edit_EMAIL = findViewById(R.id.editText_Login_EMAIL)
        edit_PW = findViewById(R.id.editText_Login_PW)
        btn_Login = findViewById(R.id.Btn_Login)
        btn_Goregister = findViewById(R.id.Btn_GoRegister)

        queue = Volley.newRequestQueue(this)

        // 로그인 버튼을 눌렀을 때
        btn_Login.setOnClickListener {
            val EMAIL = edit_EMAIL.text.toString().trim()
            val PW = edit_PW.text.toString().trim()
            Log.d("로그인 중: 버튼 클릭", "ID: $EMAIL")

            val responseListener = Response.Listener<String> { response ->
                Log.d("로그인 중: response 객체", response)
                try {
                    val jsonObject = JSONObject(response)
                    Log.d("로그인 중: Json객체", jsonObject.toString())
                    val success = jsonObject.getBoolean("success") // mysql 로그인 성공 여부 저장
                    val message = jsonObject.getString("message")   // 성공/실패 메시지 항목 가져오기

                    if (success) {                                      // mysql 로그인에 성공한 경우
                        auth.signInWithEmailAndPassword(EMAIL, PW).addOnCompleteListener { result ->
                            if(result.isSuccessful) {
                                userEMAIL = EMAIL                                 // 로그인한 email
                                userID = jsonObject.getString("userID")     // mysql userID (@앞 부분 따옴)
                                UID = jsonObject.getString("UID")           // mysql 고유 UID (번호)

                                Toast.makeText(applicationContext, "$userID 님 로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                                Log.d("로그인 완료: ", "UID: $UID")

                                // FCM 토큰 가져오기
                                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val token = task.result
                                        userToken = token
                                        Log.d("FCM Token", token)
                                    } else {
                                        Log.e("FCM Token", "Failed to get token")
                                    }
                                }
                                val intent = Intent(applicationContext, NaviActivity::class.java)
                                startActivity(intent)
                            }
                            else {
                                Toast.makeText(applicationContext, "파이어베이스 연결에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    else { // mysql 로그인에 실패한 경우
                        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                        return@Listener
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            Log.d("LoginRequest: ---", responseListener.toString())

            val loginRequest = LoginRequest(EMAIL, PW, responseListener)
            queue.add(loginRequest)
        }

        // 회원가입 버튼을 눌렀을때
        btn_Goregister.setOnClickListener {
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    inner class LoginRequest(userEMAIL: String, userPassword: String, listener: Response.Listener<String>) :
        StringRequest(Method.POST, "http://15.165.56.246/android_login_mysql.php", listener, null) {

        private val map: MutableMap<String, String> = HashMap()

        init {
            // MySQL이 아니라 PHP 파일이 원하는 정보를 줘야함
            map["useremail"] = userEMAIL
            map["userpw"] = userPassword
            Log.d("로그인 중: LoginRequest", "EMAIL: $userEMAIL PW: $userPassword")
        }

        override fun getParams(): Map<String, String> {
            return map
        }
    }
}