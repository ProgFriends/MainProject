package com.prog.mainproject

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONException
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    private lateinit var edit_Regi_EMAIL: EditText
    private lateinit var edit_Regi_PW: EditText
    private lateinit var edit_Regi_PW2: EditText
    private lateinit var btn_Register: Button

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setTitle("회원가입")

        auth = FirebaseAuth.getInstance()       // 파이어베이스

        edit_Regi_EMAIL = findViewById(R.id.editText_Regi_EMAIL)
        edit_Regi_PW = findViewById(R.id.editText_Regi_PW)
        edit_Regi_PW2 = findViewById(R.id.editText_Regi_PW2)
        btn_Register = findViewById(R.id.Btn_Register)

        // 회원가입 버튼 클릭시
        btn_Register.setOnClickListener {
            val EMAIL = edit_Regi_EMAIL.text.toString().trim()
            val PW = edit_Regi_PW.text.toString().trim()
            val PW2 = edit_Regi_PW2.text.toString().trim()

            
            if(PW.equals(PW2)) {
                auth.createUserWithEmailAndPassword(EMAIL,PW).addOnCompleteListener{ result ->      // 파이어베이스에 등록하기
                    if(result.isSuccessful) {
                        Log.d("Firebase-----", "currentUser" + auth.currentUser)
    
                        val responseListener = Response.Listener<String> { response ->
                            Log.d("회원가입 중-----", "response:" + response.toString())
                            try {
                                val jsonObject = JSONObject(response)
                                val success = jsonObject.getBoolean("success") // mysql 회원가입 성공 여부 저장
                                val message = jsonObject.getString("message") // 성공/실패 메시지 항목 가져오기
                                Log.d("회원가입 중-----", "Success: $success")
    
                                if (success) {
                                    Toast.makeText(applicationContext, "회원 등록에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                                    finish()
                                } else { // mysql 회원등록에 실패한 경우
                                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                                }
    
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                        // 서버로 Volley를 이용해서 요청을 함.
                        val registerRequest = RegisterRequest(EMAIL, PW, PW2, responseListener)
                        val queue: RequestQueue = Volley.newRequestQueue(this@RegisterActivity)
                        queue.add(registerRequest)
                    }
                    else {
                        Toast.makeText(applicationContext, "파이어베이스 회원가입 오류: 올바른 형식의 이메일이 아닙니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else {
                Toast.makeText(applicationContext, "비밀번호를 확인하세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    inner class RegisterRequest(EMAIL: String, PW: String, PW2: String, listener: Response.Listener<String>)
        : StringRequest(Method.POST, "http://15.165.56.246/android_register_mysql.php", listener, null) {

        private val params: MutableMap<String, String> = HashMap()

        init {
            // MySQL이 아니라 PHP 파일이 원하는 정보를 줘야함
            params["useremail"] = EMAIL
            params["userpw"] = PW
            params["userpw_ch"] = PW2
        }

        @Throws(AuthFailureError::class)
        override fun getParams(): Map<String, String> {
            return params
        }
    }
}
