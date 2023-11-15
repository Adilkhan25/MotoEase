package com.adilkhan.motoease.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.adilkhan.motoease.R
import com.adilkhan.motoease.firebase.FireStoreClass

class SplashScreenIntro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen_intro)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        Handler().postDelayed({
            val currentUserId = FireStoreClass().getCurrentUserID()
            if(currentUserId.isNotEmpty())
            {
                startActivity(Intent(this@SplashScreenIntro, MainActivity::class.java))
            }
            else
            startActivity(Intent(this@SplashScreenIntro, IntroActivity::class.java))
            finish()
        },2500)
    }
}