package com.sgh21.finalproject.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.sgh21.finalproject.R
import com.sgh21.finalproject.data.Products
import com.sgh21.finalproject.data.User
import com.sgh21.finalproject.databinding.FragmentPublishBinding
import java.io.ByteArrayOutputStream

class PublishFragment : Fragment() {

    private lateinit var publishBinding: FragmentPublishBinding
    private lateinit var auth: FirebaseAuth
    private val urlPictures: MutableList<String> = mutableListOf()
    var questions: MutableList<String> = mutableListOf()
    var answer: MutableList<String> = mutableListOf()
    var data: Intent? = null
    private var resultLauncher =
        registerForActivityResult (ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                data = result.data
                val imageBitmap = data?.extras?.get("data") as Bitmap
                publishBinding.takePictureImageView.setImageBitmap(imageBitmap)
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        publishBinding = FragmentPublishBinding.inflate(inflater, container, false)

        auth = Firebase.auth

        publishBinding.takePictureImageView.setOnClickListener {
            data = null
            dispatchTakePictureIntent()
        }

        publishBinding.publishButton.setOnClickListener {
            val name = publishBinding.namePublishEditText.text.toString()
            val price = publishBinding.pricePublishEditText.text.toString()
            val technicalSpecifications = publishBinding.technicalSpecificationsPublishEditTextMultiLine.text.toString()
            val description = publishBinding.descriptionPublishEditTextMultiLine.text.toString()
            if (name.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.empty_name), Toast.LENGTH_SHORT).show()
            } else if (price.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.empty_price), Toast.LENGTH_SHORT).show()
            }else if (technicalSpecifications.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.empty_technical_specifications), Toast.LENGTH_SHORT).show()
            }else if (description.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.empty_description), Toast.LENGTH_SHORT).show()
            }else if (data == null) {
                Toast.makeText(requireContext(), getString(R.string.empty_image), Toast.LENGTH_SHORT).show()
            }else{
                createProduct(name, price, technicalSpecifications, description)
            }
        }

        return publishBinding.root
    }

    private fun dispatchTakePictureIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        resultLauncher.launch(intent)

    }

    private fun createProduct(name: String, price: String, technicalSpecifications: String, description: String) {
        val db = Firebase.firestore
        val sellerId = auth.currentUser?.uid.toString()
        var sellerName = ""
        var sellerLastName = ""
        var sellerPhone = ""
        var sellerEmail = ""

        db.collection("users").get().addOnSuccessListener { result ->
            var userExist = false
            for(document in result){
                val user: User = document.toObject<User>()
                if(user.id == sellerId){
                    userExist = true
                    sellerName = user.name.toString()
                    sellerLastName = user.lastName.toString()
                    sellerPhone = user.phone.toString()
                    sellerEmail = user.email.toString()
                }
            }
            if(!userExist){
                Toast.makeText(requireContext(),"Usuario no existe", Toast.LENGTH_SHORT).show()
            }
        }

        val document = db.collection("products").document()
        val id = document.id

        val storage = FirebaseStorage.getInstance()
        val pictureRef = storage.reference.child("Products").child(id)

        publishBinding.takePictureImageView.isDrawingCacheEnabled = true
        publishBinding.takePictureImageView.buildDrawingCache()
        val bitmap = (publishBinding.takePictureImageView.drawable as BitmapDrawable).bitmap
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
                    urlPictures.add(task.result.toString())
                    id?.let { id ->
                        val product = Products(
                            id = id,
                            sellerId = sellerId,
                            sellerName = sellerName,
                            sellerLastName = sellerLastName,
                            sellerPhone = sellerPhone,
                            sellerEmail = sellerEmail,
                            urlPictures = urlPictures,
                            name = name,
                            price = price,
                            features = technicalSpecifications,
                            description = description,
                            questions = questions,
                            answer = answer
                        )
                        db.collection("products").document(id)
                            .set(product)
                            .addOnSuccessListener { documentReference ->
                                Log.d("createInDB", "DocumentSnapshot added with ID: ${id}")
                            }
                            .addOnFailureListener { e ->
                                Log.w("CreateInDB", "Error adding document", e)
                            }
                        cleanViews()
                    }
                } else {
                    // Handle failures
                    // ...
                }
            }
    }

    private fun cleanViews() {
        with(publishBinding){
            namePublishEditText.setText("")
            pricePublishEditText.setText("")
            technicalSpecificationsPublishEditTextMultiLine.setText("")
            descriptionPublishEditTextMultiLine.setText("")
            takePictureImageView.setImageResource(R.drawable.ic_camera)
        }
    }
}