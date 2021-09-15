package com.sgh21.finalproject

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.sgh21.finalproject.data.User
import com.sgh21.finalproject.databinding.ActivityMainBinding
import com.sgh21.finalproject.databinding.NavHeaderMainBinding
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var  mainBinding: ActivityMainBinding
    private lateinit var navHeaderMainBinding: NavHeaderMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        //user = intent.extras?.getSerializable("user") as User
        //mainBinding.emailTextView.text = user.email
        navHeaderMainBinding = NavHeaderMainBinding.bind(mainBinding.navView.getHeaderView(0))
        auth = Firebase.auth
        updateUser()
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_start, R.id.nav_search, R.id.nav_myPurchases,
                R.id.nav_favorite, R.id.nav_history, R.id.nav_resume,
                R.id.nav_publish, R.id.nav_account
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun updateUser() {
        val db = Firebase.firestore
        val userId = auth.currentUser?.uid.toString()
        db.collection("users").get().addOnSuccessListener { result ->
            var userExist = false
            for(document in result){
                val user: User = document.toObject<User>()
                if(user.id == userId){
                    userExist = true
                    if(user.urlPicture != null){
                        Picasso.get().load(user.urlPicture).into(navHeaderMainBinding.imageView);
                    }
                    navHeaderMainBinding.drawerNameTextView.text = user.name
                    navHeaderMainBinding.drawerEmailTextView.text = user.email
                    break
                }
            }
            if(!userExist){
                Toast.makeText(this,"Usuario no existe", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout_menu -> {
                val  intent = Intent(this, LoginActivity::class.java)
                FirebaseAuth.getInstance().signOut()
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}