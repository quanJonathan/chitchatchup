package com.tuanquan.chat

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.tuanquan.chat.databinding.ActivityUserSettingBinding
import com.tuanquan.chat.model.User


class UserSettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserSettingBinding
    private lateinit var storageRef: StorageReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private val IMAGE_REQUEST = 1
    private lateinit var imageUri: Uri
    private lateinit var uploadTask: UploadTask
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_setting)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = Firebase.database.getReference("users").child(mAuth.currentUser!!.uid)

        mDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue<User>()
                binding.username.text = user?.username?:"User name"

                if (user?.imageUri.equals("default")){
                    binding.avatar.setImageResource(R.mipmap.ic_launcher)
                } else {
                    Glide.with(this@UserSettingActivity)
                        .load(user?.imageUri).into(binding.avatar)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        storageRef = Firebase.storage
            .getReference("uploads").child(mAuth.currentUser!!.uid)

        binding.avatar.setOnClickListener {
            openImage()
        }
    }

    private fun openImage() {
        Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
            startActivityForResult(this, IMAGE_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK &&
            data != null && data.data != null) {
                imageUri = data.data!!
            }
        uploadImage(getFileExtensions(imageUri, data))
    }

    private fun getFileExtensions(uri: Uri, returnIntent: Intent?): String? {

        val mimeType: String? = returnIntent!!.data?.let {
            contentResolver.getType(uri)
        }
        return mimeType
    }

    private fun uploadImage(extension: String?) {
        val builder = AlertDialog.Builder(this)
        builder.setView(R.layout.progress)

        dialog = builder.create()
        dialog.show()

        val fileReference: StorageReference = storageRef
            .child(System.currentTimeMillis().toString()
                + "." + extension)
        uploadTask = fileReference.putFile(imageUri)


        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            fileReference.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result

                mDbRef = Firebase.database.getReference("users").child(mAuth.currentUser!!.uid)

                val map = hashMapOf<String, Any>()
                map["imageUri"] = downloadUri.toString()

                mDbRef.updateChildren(map)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }.addOnFailureListener{
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
        }

        if(imageUri == null){
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }
}