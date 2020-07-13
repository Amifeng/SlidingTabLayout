package com.finn.slidingtablayout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        initListener()
    }

    private fun initView() {
        slidingTabLayout.setSelectedTab(2)
    }

    private fun initListener() {
        selectCATE.setOnClickListener {
            slidingTabLayout.updateSelectedTab(0)
        }
    }
}