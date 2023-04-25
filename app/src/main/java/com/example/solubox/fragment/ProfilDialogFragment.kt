@file:Suppress("DEPRECATION")

package com.example.solubox.fragment


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import com.example.solubox.R.drawable
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.lifecycle.Observer
import com.example.solubox.MainActivity
import com.example.solubox.R
import com.example.solubox.network.NetworkConnectivityChecker
import com.example.solubox.databinding.FragmentProfilDialogBinding


class ProfilDialogFragment: BottomSheetDialogFragment() {


    private lateinit var binding: FragmentProfilDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentProfilDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.fullnameBtn.setOnClickListener {
            editProfilfields(binding.fullnameBtn, binding.fullname, "name", "Bitte Vorname Feld ausfüllen")
        }

        binding.lastnameBtn.setOnClickListener {
            editProfilfields(binding.lastnameBtn, binding.lastnameEdt, "lastname", "Bitte Nachname Feld ausfüllen")
        }

        binding.emailBtn.setOnClickListener {
            editProfilfields(binding.emailBtn, binding.emailEdt, "email", "Bitte E-mail Feld ausfüllen")
        }

        binding.phoneBtn.setOnClickListener {
            editProfilfields(binding.phoneBtn, binding.phoneEdt, "phonenumber", "Bitte Telefonnummer Feld ausfüllen")
        }

        binding.addressBtn.setOnClickListener {
            editProfilfields(binding.addressBtn, binding.addressEdt, "address", "Bitte Adresse Feld ausfüllen")
        }

        binding.postcodeBtn.setOnClickListener {
            editProfilfields(binding.postcodeBtn, binding.postcodeEdt, "post_code", "Bitte Postleitzahl Feld ausfüllen")
        }

        binding.cityBtn.setOnClickListener {
            editProfilfields(binding.cityBtn, binding.cityEdt, "city", "Bitte Stadt Feld ausfüllen")
        }

        binding.updateBtn.setOnClickListener {
            //update(requireActivity())
            (activity as MainActivity).update()

            NetworkConnectivityChecker.checkForConnection()
            setInternetConnectivityObserver()
        }

        binding.addPhoto.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED){
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    //show popup to request runtime permission
                    requestPermissions(permissions, PERMISSION_CODE)
                }
                else{
                    //permission already granted
                    pickImageFromGallery()
                }
            }
            else{
                //system OS is < Marshmallow
                pickImageFromGallery()
            }
        }

        displayFieldContent(binding.fullname, "name")
        displayFieldContent(binding.emailEdt, "email")
        displayFieldContent(binding.lastnameEdt, "lastname")
        displayFieldContent(binding.phoneEdt, "phonenumber")
        displayFieldContent(binding.addressEdt, "address")
        displayFieldContent(binding.postcodeEdt, "post_code")
        displayFieldContent(binding.cityEdt, "city")
    }

    private fun displayFieldContent(editText: EditText, tag: String){
        val sh = activity?.getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE)
        val s = sh?.getString(tag, "")
        editText.editableText!!.append(s)
    }

    private fun editProfilfields(button: ImageButton, textField: EditText, tag: String, msg: String){
        val sh = activity?.getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE)
        val editor = sh?.edit()

        if (!textField.isEnabled) {
            textField.isEnabled = true
            button.setImageResource(drawable.ic_check)
        } else
            if (textField.text.isEmpty())
            {
                emptyFieldCheck(textField, msg)
            } else {
                val str = textField.text.toString()
                editor?.putString(tag, str)
                editor?.apply()
                textField.isEnabled = false
                button.setImageResource(drawable.ic_edit)
            }
        }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    companion object {
        //image pick code
        private const val IMAGE_PICK_CODE = 1000
        //Permission code
        private const val PERMISSION_CODE = 1001
    }

    //handle requested permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup granted
                    pickImageFromGallery()
                }
                else{
                    //permission from popup denied
                    Toast.makeText(activity, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            binding.profilPhoto.setImageURI(data?.data)
        }
    }

    private fun setInternetConnectivityObserver() { NetworkConnectivityChecker.observe(viewLifecycleOwner, liveDataObserver) }

    private val liveDataObserver:Observer<Boolean?> = Observer { isConnected ->
        if (!isConnected!!) {
            //Can use your own logic later -- Using snackbar as default. Build your own listener to create custom view
            R.layout.fragment_profil_dialog.let {
                Toast.makeText(activity, "No internet connection", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun emptyFieldCheck(textField: EditText, msg: String){
        if (TextUtils.isEmpty(textField.text.toString())) {
           textField.error = msg

        }
    }









    override fun onDestroy() {
        super.onDestroy()
    }


}