package com.example.newsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.newsapp.databinding.ActivityRegisterBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var btnRegister: Button
    private lateinit var etRegisterEmail: EditText
    private lateinit var etRegisterPassword: EditText
    private lateinit var tvLogin:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnRegister = binding.btnRegister
        etRegisterEmail = binding.etRegisterEmail
        etRegisterPassword = binding.etRegisterPassword
        tvLogin = binding.tvLogin

        tvLogin.setOnClickListener(){
           onBackPressed()
        }

        btnRegister.setOnClickListener() {
            if (etRegisterEmail.text.toString().trim().isNullOrEmpty()) {
                Toast.makeText(this@RegisterActivity, "Please enter email", Toast.LENGTH_SHORT)
                    .show()
            } else if (etRegisterPassword.text.toString().trim().isNullOrEmpty()) {

                Toast.makeText(this@RegisterActivity, "Please enter password", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val email = etRegisterEmail.text.toString().trim()
                val password = etRegisterPassword.text.toString().trim()

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(
                        OnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val firebaseUser: FirebaseUser = task.result!!.user!!
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "You were registered successfully!", Toast.LENGTH_SHORT
                                ).show()

                                val intent =
                                    Intent(this@RegisterActivity, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra("userId", firebaseUser.uid)
                                intent.putExtra("emailId", email)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Registration is not successful",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
            }
        }
    }
}