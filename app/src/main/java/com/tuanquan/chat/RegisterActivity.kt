package com.tuanquan.chat

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.tuanquan.chat.databinding.ActivityRegisterBinding
import com.tuanquan.chat.model.User

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()

        binding.apply {

            btnSignUp.setOnClickListener {
                val username = usernamePlaceholder.editText?.text.toString()
                val email = emailPlaceholder.editText?.text.toString()
                val password = passwordPlaceholder.editText?.text.toString()

                if(TextUtils.isEmpty(username) || TextUtils.isEmpty(email)
                    || TextUtils.isEmpty(password)){
                    Toast.makeText(this@RegisterActivity,
                        "All fields are required",
                        Toast.LENGTH_SHORT).show()
                }
                else if(password.length < 8){
                    Toast.makeText(this@RegisterActivity,
                        "Password must have at least 8 digits",
                        Toast.LENGTH_SHORT).show()
                }
                else if(TextUtils.isDigitsOnly(password)){
                    Toast.makeText(this@RegisterActivity,
                        "Password must contain number and digit",
                        Toast.LENGTH_SHORT).show()
                }else{
                    addNewUser(password, username, email, "default")
                }
            }
        }
    }

    private fun addNewUser(password: String, username: String, email: String, imageUri: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){
            if(it.isSuccessful){
                addUserToDatabase(username, mAuth.currentUser!!.uid, imageUri)
            }
        }
    }

    private fun addUserToDatabase(username: String, uid: String, imageUri: String) {
        val database = Firebase.database

        mDbRef = database.getReference("users")

        val user = User(username=username, uid=uid, imageUri = "default")

        mDbRef.child(uid).setValue(user).
                addOnSuccessListener {
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                }
            .addOnFailureListener {
                Toast.makeText(this@RegisterActivity,
                    "You can't register with this email",
                    Toast.LENGTH_SHORT).show()
            }
    }
}