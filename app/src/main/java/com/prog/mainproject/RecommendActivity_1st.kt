package com.prog.mainproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class RecommendActivity_1st : AppCompatActivity() {

    companion object {
        var Q_1st : Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommend_1st)
    }
}