package com.sgh21.finalproject

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.sgh21.finalproject.databinding.ActivityRegisterBinding
import androidx.core.widget.doAfterTextChanged
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.sgh21.finalproject.VerificationUtils.EMPTY
import com.sgh21.finalproject.VerificationUtils.minimumLength
import com.sgh21.finalproject.VerificationUtils.validateEmail
import com.sgh21.finalproject.data.User
import java.io.ByteArrayOutputStream

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerBinding: ActivityRegisterBinding
    private var password: String = EMPTY
    private var repPassword: String = EMPTY
    private val idsFavorites: MutableList<String> = mutableListOf()
    private lateinit var auth: FirebaseAuth
    var data: Intent? = null
    var resultLauncher =
        registerForActivityResult (ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                data = result.data
                val imageBitmap = data?.extras?.get("data") as Bitmap
                registerBinding.takePictureImageView.setImageBitmap(imageBitmap)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(registerBinding.root)

        auth = Firebase.auth

        registerBinding.takePictureImageView.setOnClickListener {
            data = null
            dispatchTakePictureIntent()
        }

        registerBinding.saveButton.setOnClickListener {
            val email = registerBinding.emailEditText.text.toString()
            val name = registerBinding.nameEditText.text.toString()
            val lastName = registerBinding.lastNameEditText.text.toString()
            val phone = registerBinding.phoneEditText.text.toString()
            if (name.isEmpty()) {
                Toast.makeText(this, getString(R.string.name_error), Toast.LENGTH_SHORT).show()
            } else if (lastName.isEmpty()) {
                Toast.makeText(this, getString(R.string.last_name_error), Toast.LENGTH_SHORT).show()
            }else if (phone.isEmpty()) {
                Toast.makeText(this, getString(R.string.phone_error), Toast.LENGTH_SHORT).show()
            } else if (!validateEmail(email)) {
                Toast.makeText(this, getString(R.string.email_error), Toast.LENGTH_SHORT).show()
            } else if (data == null) {
                Toast.makeText(this, getString(R.string.empty_image), Toast.LENGTH_SHORT).show()
            }else {
                if (password == repPassword && password != EMPTY) {
                    //user = User(name, lastName, email, password)
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener() { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Register", "createUserWithEmail:success")
                                Toast.makeText(this, "Registro exitoso",
                                    Toast.LENGTH_SHORT).show()
                                createUser(name, lastName, phone, email)
                                goToLoginActivity()
                                //val user = auth.currentUser
                                //updateUI(user)
                            } else {
                                var msg = ""
                                if(task.exception?.localizedMessage == "The email address is badly formatted.")
                                    msg = "El correo esta mal escrito"
                                else if(task.exception?.localizedMessage == "The given password is invalid. [ Password should be at least 6 characters ]")
                                    msg = "La contraseña debe tener minimo 6 caracteres"
                                else if(task.exception?.localizedMessage == "The email address is already in use by another account.")
                                    msg = "Ya existe una cuenta con ese correo"
                                Log.w("register", "createUserWithEmail:failure", task.exception)
                                Toast.makeText(this, msg,
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, getString(R.string.password_errors), Toast.LENGTH_SHORT).show()
                }
            }
        }

        registerBinding.emailEditText.doAfterTextChanged {
            registerBinding.emailTextInputLayout.error = if (!validateEmail(registerBinding.emailEditText.text.toString())) getString(R.string.email_error)  else null
        }

        registerBinding.passwordEditText.doAfterTextChanged {
            password = registerBinding.passwordEditText.text.toString()
            registerBinding.passwordTextInputLayout.error = if (!minimumLength(password)) getString(R.string.password_error) else null
        }

        registerBinding.repPasswordEditText.doAfterTextChanged {
            repPassword = registerBinding.repPasswordEditText.text.toString()
            registerBinding.repPasswordTextInputLayout.error = if (password != repPassword) getString(R.string.coincidence_error) else null
        }
    }

    private fun createUser(name: String, lastName: String, phone: String, email: String) {
        val id = auth.currentUser?.uid

        val storage = FirebaseStorage.getInstance()
        val pictureRef = storage.reference.child("Users").child(id.toString())

        registerBinding.takePictureImageView.isDrawingCacheEnabled = true
        registerBinding.takePictureImageView.buildDrawingCache()
        val bitmap = (registerBinding.takePictureImageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = pictureRef.putBytes(data)
        val urlTask =
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                pictureRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val urlPicture = task.result.toString()
                    id?.let { id ->
                        val user = User(id, urlPicture, name, lastName, phone, email, idsFavorites)
                        val db = Firebase.firestore
                        db.collection("users").document(id)
                            .set(user)
                            .addOnSuccessListener { documentReference ->
                                Log.d("createInDB", "DocumentSnapshot added with ID: ${id}")
                            }
                            .addOnFailureListener { e ->
                                Log.w("CreateInDB", "Error adding document", e)
                            }
                    }
                } else {
                    // Handle failures
                    // ...
                }
            }

    }

    private fun dispatchTakePictureIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        resultLauncher.launch(intent)
    }

    private fun goToLoginActivity() {
        val  intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
}