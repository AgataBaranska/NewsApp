package com.example.newsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.newsapp.databinding.ActivityLoginBinding
import com.facebook.login.widget.LoginButton
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.facebook.FacebookException

import com.facebook.login.LoginResult

import com.facebook.FacebookCallback

import android.content.ContentValues.TAG
import android.util.Log
import android.view.View
import com.example.newsapp.models.User
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.google.firebase.auth.FacebookAuthProvider
import java.util.*


class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var etLoginPassword: EditText
    private lateinit var binding: ActivityLoginBinding

    private lateinit var tvRegister: TextView
    private lateinit var btnLogin: Button
    private lateinit var etLoginEmail: EditText
    private lateinit var btnLoginFacebook: LoginButton
    private lateinit var tvForgotPassword:TextView
    private lateinit var callbackManager: CallbackManager
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        tvRegister = binding.tvRegister
        tvRegister.setOnClickListener(this)
        btnLogin = binding.btnLogIn
        btnLogin.setOnClickListener(this)
        etLoginEmail = binding.etLogInEmail
        etLoginPassword = binding.etLogInPassword
        tvForgotPassword = binding.tvForgotPassword
        tvForgotPassword.setOnClickListener(this)

        btnLoginFacebook = binding.btnLoginFacebook
        callbackManager = CallbackManager.Factory.create()
        btnLoginFacebook.setReadPermissions("email", "public_profile")
        btnLoginFacebook.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
            }

            override fun onError(exception: FacebookException) {
                Log.d(TAG, "facebook:onError", exception)
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser!=null){
            updateUI(currentUser)
        }
    }

    private fun updateUI(user: FirebaseUser?) {

        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.putExtra("user", user)
        startActivity(intent)
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {

                binding.btnLogIn.id -> {
                  loginUser()
                }
                binding.tvRegister.id -> {
                    startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                }
                binding.tvForgotPassword.id ->{
                    startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
                }
            }
        }
    }

    private fun validateLoginDetails(): Boolean {
        if (etLoginEmail.text.toString().trim().isEmpty()) {
            Toast.makeText(this@LoginActivity, "Please enter email", Toast.LENGTH_SHORT)
                .show()
            return false
        } else if (etLoginPassword.text.toString().trim().isEmpty()) {
            Toast.makeText(this@LoginActivity, "Please enter password", Toast.LENGTH_SHORT)
                .show()
            return false
        }
        return true
    }

    private fun loginUser(){
        if(validateLoginDetails()){
            val email = etLoginEmail.text.toString().trim()
            val password = etLoginPassword.text.toString().trim()

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    OnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val firebaseUser: FirebaseUser =
                                FirebaseAuth.getInstance().currentUser!!
                            Toast.makeText(
                                this@LoginActivity,
                                "You were logged in  successfully!", Toast.LENGTH_SHORT
                            ).show()

                            val intent =
                                Intent(this@LoginActivity, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            intent.putExtra("userId", firebaseUser.uid)
                            intent.putExtra("emailId", email)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                task.exception!!.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
        }}

    fun userDataSuccess(user: User) {
        Log.i("id", user.id)

        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()

    }

}