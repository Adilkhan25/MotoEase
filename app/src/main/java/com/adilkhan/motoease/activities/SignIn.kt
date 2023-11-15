package com.adilkhan.motoease.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.adilkhan.motoease.R
import com.adilkhan.motoease.databinding.ActivitySignInBinding
import com.adilkhan.motoease.firebase.FireStoreClass
import com.adilkhan.motoease.models.User
import com.google.firebase.auth.FirebaseAuth

class SignIn : BaseActivity() {
    private var bindingSignIn: ActivitySignInBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingSignIn = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(bindingSignIn?.root)
        setupActionBar()
        bindingSignIn?.btnSignIn?.setOnClickListener {
            signInRegisteredUser()
        }
        bindingSignIn?.tvForgetPassword?.setOnClickListener {
            bindingSignIn?.signInUi?.visibility = View.GONE
            bindingSignIn?.forgetPasswordUi?.visibility = View.VISIBLE
            bindingSignIn?.btnResetPassword?.setOnClickListener {
                val enterEmail = bindingSignIn?.etResetPassword?.text.toString()
                if(TextUtils.isEmpty(enterEmail))
                {
                    showErrorSnackBar("Please Enter your email")
                }
                else{
                    FirebaseAuth.getInstance().sendPasswordResetEmail(enterEmail).addOnSuccessListener {
                        showSuccessSnackBar("check your email to reset the password")
                        bindingSignIn?.signInUi?.visibility = View.VISIBLE
                        bindingSignIn?.forgetPasswordUi?.visibility = View.GONE
                    }.addOnFailureListener{
                        showErrorSnackBar(it.toString())
                        bindingSignIn?.signInUi?.visibility = View.VISIBLE
                        bindingSignIn?.forgetPasswordUi?.visibility = View.GONE
                    }

                }
            }

        }
    }
    // TODO (Step 7: A function for setting up the actionBar.)
    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        setSupportActionBar(bindingSignIn?.toolbarSignInActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        bindingSignIn?.toolbarSignInActivity?.setNavigationOnClickListener { onBackPressed() }
    }
    // TODO (Step 2: A function for Sign-In using the registered user using the email and password.)
    // START
    /**
     * A function for Sign-In using the registered user using the email and password.
     */
    private fun signInRegisteredUser() {
        // Here we get the text from editText and trim the space
        val email: String = bindingSignIn?.etEmail?.text.toString().trim { it <= ' ' }
        val password: String = bindingSignIn?.etPassword?.text.toString().trim { it <= ' ' }

        if (validateForm(email, password)) {
            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            // Sign-In using FirebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {

                        // Calling the FirestoreClass signInUser function to get the data of user from database.
                        val isVerify = FirebaseAuth.getInstance().currentUser?.isEmailVerified
                        if(isVerify == true)
                        FireStoreClass().loadUserData(this@SignIn)
                        else showErrorSnackBar("Please verify your email")
                    } else {
                        Toast.makeText(
                            this@SignIn,
                            task.exception!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }
    // END

    // TODO (Step 3: A function to validate the entries of a user.)
    // START
    /**
     * A function to validate the entries of a user.
     */
    private fun validateForm(email: String, password: String): Boolean {
        return if (TextUtils.isEmpty(email)) {
            showErrorSnackBar("Please enter email.")
            false
        } else if (TextUtils.isEmpty(password)) {
            showErrorSnackBar("Please enter password.")
            false
        } else {
            true
        }
    }
    // END
    /**
     * A function to get the user details from the fireStore database after authentication.
     */
    fun signInSuccess(user: User) {

        hideProgressDialog()

        startActivity(Intent(this@SignIn, MainActivity::class.java))
        this.finish()
    }//END
}