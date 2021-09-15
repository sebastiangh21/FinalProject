package com.sgh21.finalproject.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.navArgs
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.sgh21.finalproject.R
import com.sgh21.finalproject.data.Products
import com.sgh21.finalproject.databinding.FragmentEditProductBinding
import com.sgh21.finalproject.databinding.FragmentProductBinding
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class EditProductFragment : Fragment() {

    private lateinit var editBinding: FragmentEditProductBinding
    private val args: EditProductFragmentArgs by navArgs()
    private lateinit var product: Products
    private var isEditing = false
    var resultLauncher =
        registerForActivityResult (ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val data: Intent? = result.data
                val imageBitmap = data?.extras?.get("data") as Bitmap
                editBinding.pictureEditImageView.setImageBitmap(imageBitmap)
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        editBinding = FragmentEditProductBinding.inflate(inflater, container, false)
        editBinding.editButton.setOnClickListener {
            updateProduct()
        }
        editBinding.pictureEditImageView.setOnClickListener {
            if(isEditing) dispatchTakePictureIntent()
        }
        return editBinding.root
    }

    private fun dispatchTakePictureIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        resultLauncher.launch(intent)
    }

    private fun updateProduct() {
        isEditing = !isEditing
        if(isEditing){
            with(editBinding){
                editButton.text = getString(R.string.save)
                nameEditEditText.isEnabled = true
                priceEditEditText.isEnabled = true
                technicalSpecificationsEditEditTextMultiLine.isEnabled = true
                descriptionEditEditTextMultiLine.isEnabled = true
            }
        }else {
            with(editBinding) {
                editButton.text = getString(R.string.edit)

                val idProduct = product.id



                val storage = FirebaseStorage.getInstance()
                val pictureRef = storage.reference.child("Products").child(idProduct.toString())

                pictureEditImageView.isDrawingCacheEnabled = true
                pictureEditImageView.buildDrawingCache()
                val bitmap = (editBinding.pictureEditImageView.drawable as BitmapDrawable).bitmap
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
                            val documentUpdate = HashMap<String, Any>()
                            documentUpdate["name"] = nameEditEditText.text.toString()
                            documentUpdate["price"] = priceEditEditText.text.toString()
                            documentUpdate["features"] = technicalSpecificationsEditEditTextMultiLine.text.toString()
                            documentUpdate["description"] = descriptionEditEditTextMultiLine.text.toString()
                            val db = Firebase.firestore
                            idProduct?.let { it1 -> db.collection("products").document(it1).update(documentUpdate)
                                .addOnSuccessListener {
                                    Toast.makeText(requireContext(),
                                        "Producto actualizado con exito",
                                        Toast.LENGTH_SHORT).show()
                                }}
                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                nameEditEditText.isEnabled = false
                priceEditEditText.isEnabled = false
                technicalSpecificationsEditEditTextMultiLine.isEnabled = false
                descriptionEditEditTextMultiLine.isEnabled = false
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        product = args.product
        updateInformation()
    }

    private fun updateInformation() {
        with(editBinding){
            if(product.urlPictures != null){
                Picasso.get().load(product.urlPictures!![0]).into(pictureEditImageView);
            }
            nameEditEditText.setText(product.name)
            nameEditEditText.isEnabled = false
            priceEditEditText.setText(product.price)
            priceEditEditText.isEnabled = false
            technicalSpecificationsEditEditTextMultiLine.setText(product.features)
            technicalSpecificationsEditEditTextMultiLine.isEnabled = false
            descriptionEditEditTextMultiLine.setText(product.description)
            descriptionEditEditTextMultiLine.isEnabled = false
        }
    }
}