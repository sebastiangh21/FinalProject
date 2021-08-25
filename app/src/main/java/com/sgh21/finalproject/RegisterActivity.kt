package com.sgh21.finalproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sgh21.finalproject.databinding.ActivityRegisterBinding
import androidx.core.widget.doAfterTextChanged
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sgh21.finalproject.VerificationUtils.EMPTY
import com.sgh21.finalproject.VerificationUtils.minimumLength
import com.sgh21.finalproject.VerificationUtils.validateEmail

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerBinding: ActivityRegisterBinding
    private lateinit var user: User
    private var password: String = EMPTY
    private var repPassword: String = EMPTY
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(registerBinding.root)

        auth = Firebase.auth

        registerBinding.saveButton.setOnClickListener {
            val email = registerBinding.emailEditText.text.toString()
            val name = registerBinding.nameEditText.text.toString()
            val lastName = registerBinding.lastNameEditText.text.toString()
            if (name.isEmpty()) {
                Toast.makeText(this, getString(R.string.name_error), Toast.LENGTH_SHORT).show()
            } else if (lastName.isEmpty()) {
                Toast.makeText(this, getString(R.string.last_name_error), Toast.LENGTH_SHORT).show()
            } else if (!validateEmail(email)) {
                Toast.makeText(this, getString(R.string.email_error), Toast.LENGTH_SHORT).show()
            } else {
                if (password == repPassword && password != EMPTY) {
                    user = User(name, lastName, email, password)

                    //
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener() { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Register", "createUserWithEmail:success")
                                Toast.makeText(this, "Registro exitoso",
                                    Toast.LENGTH_SHORT).show()
                                //createUser(name: String, lastName: String, email: String, password: String)
                                val user = auth.currentUser
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
                    //
                    goToLoginActivity()
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

    private fun createUser(name: String, lastName: String, email: String, password: String) {
        val id = auth.currentUser?.uid
        id?.let { id ->
            val user = User(id, name, lastName, email, password)
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

    }

    private fun goToLoginActivity() {
        val  intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("user",user)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
}