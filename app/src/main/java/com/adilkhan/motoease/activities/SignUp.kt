package com.adilkhan.motoease.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.adilkhan.motoease.R
import com.adilkhan.motoease.databinding.ActivitySignUpBinding
import com.adilkhan.motoease.firebase.FireStoreClass
import com.adilkhan.motoease.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.w3c.dom.Text

class SignUp : BaseActivity() {
    private var bindingSignUp : ActivitySignUpBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingSignUp = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(bindingSignUp?.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
       setUpActionBar()
        bindingSignUp?.btnSignUp?.setOnClickListener {
            registerTheUser()
        }
    }
    private fun setUpActionBar()
    {
        setSupportActionBar(bindingSignUp?.toolbarSignUpActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        bindingSignUp?.toolbarSignUpActivity?.setNavigationOnClickListener { onBackPressed() }
    }
    private fun registerTheUser()
    {
            val name = bindingSignUp?.etName?.text.toString().trim{it<=' '}
            val email = bindingSignUp?.etEmail?.text.toString().trim{it<=' '}
            val password = bindingSignUp?.etPassword?.text.toString().trim{it <= ' '}
        if(validateForm(name, email,password))
        {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->

                        // Hide the progress dialog
                        hideProgressDialog()

                        // If the registration is successfully done
                        if (task.isSuccessful) {

                            // Firebase registered user
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            // Registered Email
                            val registeredEmail = firebaseUser.email!!

                            val user = User(
                                firebaseUser.uid, name, registeredEmail
                            )
                            //verify email
                            FirebaseAuth.getInstance().currentUser?.sendEmailVerification()?.addOnSuccessListener {
                                showSuccessSnackBar("Please verify your email")
                                // call the registerUser function of FirestoreClass to make an entry in the database.
                                FireStoreClass().registerUser(this@SignUp, user)
                            }?.addOnFailureListener{
                                Toast.makeText(
                                    this@SignUp,
                                    task.exception!!.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }



                        } else {
                            Toast.makeText(
                                this@SignUp,
                                task.exception!!.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
        }
    }
    private fun validateForm(name:String , email:String, password:String):Boolean{
        return when{
            TextUtils.isEmpty(name)->
            {
                showErrorSnackBar("please Enter your name")
                false
            }
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("please Enter your email")
                false
            }
            TextUtils.isEmpty(password)->{
                showErrorSnackBar("please Enter your password")
                false
            }
            else -> true
        }
    }
    /**
     * A function to be called the user is registered successfully and entry is made in the firestore database.
     */
    fun userRegisteredSuccess() {

        Toast.makeText(
            this@SignUp,
            "You have successfully registered.",
            Toast.LENGTH_SHORT
        ).show()

        // Hide the progress dialog
        hideProgressDialog()

        /**
         * Here the new user registered is automatically signed-in so we just sign-out the user from firebase
         * and send him to Intro Screen for Sign-In
         */
        FirebaseAuth.getInstance().signOut()
        // Finish the Sign-Up Screen
        finish()
    }//END
}