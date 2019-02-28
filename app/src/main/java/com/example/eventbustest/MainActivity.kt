package com.example.eventbustest

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        EventBus.register(this)
        val intent = Intent()
        intent.setClass(this,Main2Activity::class.java)
        startActivity(intent)

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun enent(person: Persons) {
        Log.i("swl", person.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.unRegister(this)
    }
}
