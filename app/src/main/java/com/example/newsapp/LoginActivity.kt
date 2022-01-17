package com.example.newsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.newsapp.databinding.ActivityLoginBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {
    private lateinit var etLoginPassword: EditText
    private lateinit var binding:ActivityLoginBinding

    private lateinit var tvRegister: TextView
    private lateinit var btnLogin: Button
    private lateinit var etLoginEmail:EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tvRegister = binding.tvRegister


        tvRegister.setOnClickListener(){
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnLogin = binding.btnLogIn
        etLoginEmail = binding.etLogInEmail
        etLoginPassword = binding.etLogInPassword
        btnLogin.setOnClickListener() {
            if (etLoginEmail.text.toString().trim().isNullOrEmpty()) {
                Toast.makeText(this@LoginActivity, "Please enter email", Toast.LENGTH_SHORT)
                    .show()
            } else if (etLoginPassword.text.toString().trim().isNullOrEmpty()) {

                Toast.makeText(this@LoginActivity, "Please enter password", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val email = etLoginEmail.text.toString().trim()
                val password = etLoginPassword.text.toString().trim()

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(
                        OnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
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
                                    "Logging in was not successful",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
            }
        }



    }
}