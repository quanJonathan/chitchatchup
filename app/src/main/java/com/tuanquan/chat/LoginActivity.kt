package com.tuanquan.chat

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.tuanquan.chat.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        binding.apply {

            txtSignUp.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                finish()
            }

            btnLogin.setOnClickListener {
                val email = emailPlaceholder.editText?.text.toString()
                val password = passwordPlaceholder.editText?.text.toString()
                if (TextUtils.isEmpty(email) or TextUtils.isEmpty(password)) {
                    Toast.makeText(this@LoginActivity,
                        "All fields are required",
                        Toast.LENGTH_SHORT).show()
                } else {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }else{
                            Toast.makeText(this@LoginActivity,
                                "Something went wrong",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        val currentUser = mAuth.currentUser

        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }
}