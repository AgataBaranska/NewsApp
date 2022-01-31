package com.example.newsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.newsapp.databinding.ActivityRegisterBinding
import com.example.newsapp.models.Item
import com.example.newsapp.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var btnRegister: Button
    private lateinit var etRegisterEmail: EditText
    private lateinit var etRegisterPassword: EditText
    private lateinit var etRegisterName:EditText
    private lateinit var tvLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnRegister = binding.btnRegister
        btnRegister.setOnClickListener(this)
        etRegisterEmail = binding.etRegisterEmail
        etRegisterPassword = binding.etRegisterPassword
        etRegisterName = binding.etRegisterName
        tvLogin = binding.tvLogin
        tvLogin.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                binding.btnRegister.id -> {
                    registerUser()
                }
                binding.tvLogin.id ->{
                    onBackPressed()
                    finish()
                }
            }
        }
    }

    private fun validateRegisterDetails(): Boolean {
        when {
            etRegisterName.text.toString().trim().isEmpty() -> {
                Toast.makeText(this@RegisterActivity, "Please enter name", Toast.LENGTH_SHORT)
                    .show()
                return false
            }
            etRegisterEmail.text.toString().trim().isNullOrEmpty() -> {
                Toast.makeText(this@RegisterActivity, "Please enter email", Toast.LENGTH_SHORT)
                    .show()
                return false

            }
            etRegisterPassword.text.toString().trim().isEmpty() -> {

                Toast.makeText(this@RegisterActivity, "Please enter password", Toast.LENGTH_SHORT)
                    .show()
                return false
            }
            else -> return true
        }
    }

    private fun registerUser() {
        if (validateRegisterDetails()) {
            val email = etRegisterEmail.text.toString().trim()
            val password = etRegisterPassword.text.toString().trim()
            val name = etRegisterName.text.toString().trim()

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    OnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            val user = User(firebaseUser.uid, name)
                            FirestoreClass().registerUser(this,user)

                        } else {
                            Toast.makeText(
                                this@RegisterActivity,
                                task.exception!!.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
        }
    }

    fun userRegisterSuccess(){
        Toast.makeText(
            this@RegisterActivity,
            "You were registered successfully!", Toast.LENGTH_SHORT
        ).show()

        val intent =
            Intent(this@RegisterActivity, MainActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}