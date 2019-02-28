package com.example.eventbustest

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

/**
 * 项目名: haiguicode
 * 包名: com.example.eventbustest
 * 创建者: Lzx
 * 创建时间: 2019/2/28 10:49
 * 描述:
 */
class Main2Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun doClick(view: View) {
        Thread(Runnable {
            EventBus.post(Persons("swl", "123456"))
        }).start()
        finish()
    }
}