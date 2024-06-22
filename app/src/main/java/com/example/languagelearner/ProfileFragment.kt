package com.example.languagelearner

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.fragment.findNavController
import com.example.languagelearner.activities.EditProfile
import com.example.languagelearner.activities.LessonsPage
import com.example.languagelearner.activities.Login
import com.example.languagelearner.activities.OverviewActivity
import com.example.languagelearner.auth.DefaultResponse
import com.example.languagelearner.auth.ProfileUpdateRequest
import com.example.languagelearner.auth.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var name: TextView
    private lateinit var email: TextView
    private lateinit var phone: TextView
    private lateinit var editButton: Button
    private lateinit var logOutButton: Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var imageView: ImageView
    private lateinit var imageButton: Button
    private lateinit var overviewButton: Button

    private val imageContract = registerForActivityResult(ActivityResultContracts.GetContent()){
        imageView.setImageURI(it)
        if(imageView.drawable != null){
            val bitmapImg = imageView.drawable.toBitmap()
            val encodedImg = encodeImage(bitmapImg)
            val userEmail = sharedPreferences.getString("userEmail", "")
            if (userEmail != null){
                if (encodedImg != null) {
                    updateImage(userEmail, encodedImg)
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        name = view.findViewById(R.id.profile_name)
        email = view.findViewById(R.id.profile_email)
        phone = view.findViewById(R.id.profile_phone)
        editButton = view.findViewById(R.id.button_edit_profile)
        logOutButton = view.findViewById(R.id.button_logout)
        imageView = view.findViewById(R.id.profile_image)
        imageButton = view.findViewById(R.id.button_change_img)
        overviewButton = view.findViewById(R.id.button_overview)
        sharedPreferences = requireContext().getSharedPreferences("nameForUser", Context.MODE_PRIVATE)



        val userName = sharedPreferences.getString("userName", "")
        val userEmail = sharedPreferences.getString("userEmail", "")
        val userPhone = sharedPreferences.getString("userPhone", "")
        val userImage = sharedPreferences.getString("userImage", "")
        val bitmapImage = userImage?.let { stringToBitmap(it) }
        name.text = userName
        email.text = userEmail
        phone.text = userPhone
        if(bitmapImage != null){
            imageView.setImageBitmap(bitmapImage)
        } else {
            imageView.setImageResource(R.drawable.profile)
        }

        editButton.setOnClickListener {
            val intent = Intent(activity, EditProfile::class.java)
            startActivity(intent)
        }
        overviewButton.setOnClickListener {
            val intent = Intent(activity, OverviewActivity::class.java)
            startActivity(intent)
        }

        logOutButton.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.setMessage(" Are you sure you want to log out?")
            alertDialogBuilder.setPositiveButton("Yes"){_,_ ->
                val intent = Intent(activity, Login::class.java)
                startActivity(intent)

            }
            alertDialogBuilder.setNegativeButton("No"){_,_->

            }
            val alertDialogBox = alertDialogBuilder.create()
            alertDialogBox.show()

        }

        imageButton.setOnClickListener {
//            imageContract.launch("image/*")
//            var img = ""
//            if(imageView.drawable != null){
//                img = encodeImage(imageView.drawable.toBitmap()).toString()
//
//            }
//
            var alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.setMessage("You want to choose photo as profile?")
            alertDialogBuilder.setNegativeButton("No") {_,_->}
            alertDialogBuilder.setPositiveButton("Yes") {_,_ ->
                imageContract.launch("image/*")
            }
            val alertBox = alertDialogBuilder.create()
            alertBox.show()

        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    private fun updateImage( email: String, image: String) {
        val imageUpdateRequest = ImageUpdateRequest(email, image)
        RetrofitInstance.service.changeProfileImage(imageUpdateRequest)
            .enqueue(object: Callback<DefaultResponse> {

                override fun onResponse(
                    call: Call<DefaultResponse>,
                    response: Response<DefaultResponse>
                ) {
                    if(response.isSuccessful){
                        val defaultResponse = response.body()
                        if(defaultResponse != null && !defaultResponse.error){
                            Toast.makeText(requireContext(), "Changed successfully", Toast.LENGTH_SHORT).show()
//                            val intent = Intent(requireContext(), LessonsPage::class.java)
//                            startActivity(intent)
                        } else {
                            Toast.makeText(requireContext(), "Change failed: ${defaultResponse?.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val error = response.errorBody()?.string() ?: "Unknown error"
                        Toast.makeText(requireContext(), "Change failed: $error?", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Change failed: ${t.message}", Toast.LENGTH_SHORT).show()

                }

            })
    }

    private fun encodeImage(bm: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    private fun stringToBitmap(encodedString: String): Bitmap? {
        return try {
            val imageBytes = Base64.decode(encodedString, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        } catch (e: IllegalArgumentException) {
            Log.e("decode error", "Invalid base64 str")
            null
        }
    }
}