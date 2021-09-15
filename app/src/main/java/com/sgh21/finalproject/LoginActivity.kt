package com.sgh21.finalproject


import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sgh21.finalproject.VerificationUtils.EMPTY
import com.sgh21.finalproject.VerificationUtils.minimumLength
import com.sgh21.finalproject.VerificationUtils.validateEmail
import com.sgh21.finalproject.data.User
import com.sgh21.finalproject.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var user: User
    private var password: String = EMPTY
    private var check = arrayOf(false,false)

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        auth = Firebase.auth

        loginBinding.loginButton.setOnClickListener {
            singIn()
            /*val email = loginBinding.emailLoginEditText.text.toString()
            password = loginBinding.passwordEditText.text.toString()
            if(intent.extras != null){
                user = intent.extras?.getSerializable("user") as User
                if (user.email == email && user.password == password) {
                    mainactivity()
                }else{
                    Toast.makeText(this, getString(R.string.login_error), Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, getString(R.string.unregistered_user), Toast.LENGTH_SHORT).show()
            }*/
        }

        loginBinding.emailLoginEditText.doAfterTextChanged {

            if(!validateEmail(loginBinding.emailLoginEditText.text.toString())){
                check[0] = false
                loginBinding.emailLoginTextInputLayout.error = getString(R.string.email_error)
            }else{
                loginBinding.emailLoginTextInputLayout.error = null
                check[0] = true
            }
            enableButton()
        }

        loginBinding.passwordEditText.doAfterTextChanged {
            password = loginBinding.passwordEditText.text.toString()
            if (!minimumLength(password)){
                loginBinding.passwordLoginTextInputLayout.error = getString(R.string.password_error)
                check[1] = false
            }else{
                loginBinding.passwordLoginTextInputLayout.error = null
                check[1] = true
            }
            enableButton()
        }

        loginBinding.signUpTextView.setOnClickListener {
            val  intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun singIn() {
        val email = loginBinding.emailLoginEditText.text.toString()
        password = loginBinding.passwordEditText.text.toString()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("login", "signInWithEmail:success")
                    val user = auth.currentUser
                    goToMainActivity()
                    //updateUI(user)
                } else {
                    var msg = ""
                    if(task.exception?.localizedMessage == "The email address is badly formatted.")
                        msg = "El correo esta mal escrito"
                    else if(task.exception?.localizedMessage == "There is no user record corresponding to this identifier. The user may have been deleted.")
                        msg = "No existe una cuenta con ese correo electronico"
                    else if(task.exception?.localizedMessage == "The password is invalid or the user does not have a password.")
                        msg = "Correo o contraseña invalida"
                    // If sign in fails, display a message to the user.
                    Log.w("login", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        this, msg,
                        Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                }
            }
    }

    private fun enableButton() {
        loginBinding.loginButton.isEnabled = check[0] && check[1]
    }

    private fun goToMainActivity() {
        val  intent = Intent(this, MainActivity::class.java)
        //intent.putExtra("user",user)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
}