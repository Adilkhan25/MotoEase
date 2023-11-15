package com.adilkhan.motoease.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.adilkhan.motoease.R
import com.adilkhan.motoease.databinding.ActivityIntroAcitvityBinding

class IntroActivity : BaseActivity() {
   private var bindingIntro : ActivityIntroAcitvityBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingIntro = ActivityIntroAcitvityBinding.inflate(layoutInflater)
        setContentView(bindingIntro?.root)
        window.setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        bindingIntro?.btnSignInIntro?.setOnClickListener {
            startActivity(Intent(this@IntroActivity, SignIn::class.java))
        }
        bindingIntro?.btnSignUpIntro?.setOnClickListener {
            startActivity(Intent(this@IntroActivity, SignUp::class.java))

        }
    }
}