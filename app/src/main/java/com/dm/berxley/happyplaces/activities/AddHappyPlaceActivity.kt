package com.dm.berxley.happyplaces.activities

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.dm.berxley.happyplaces.R
import com.dm.berxley.happyplaces.daos.HappyPlaceDao
import com.dm.berxley.happyplaces.entities.HappyPlaceEntity
import com.dm.berxley.happyplaces.utils.HappyPlaceApp
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID


class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {
    private var toolbar: Toolbar? = null
    private var etTitle:  EditText? = null
    private var etDescription: EditText? = null
    private var etDate: EditText? = null
    private var etLocation: EditText? = null
    private var imagePath: String? = null
    private var tvAddImage: TextView? = null
    private var ivPlaceImage: ImageView? = null
    private var btnSave: Button? = null
    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private var mHappyPlace: HappyPlaceEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_happy_place)

        toolbar = findViewById(R.id.tool_bar_add_place)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar?.setNavigationOnClickListener {
            onBackPressed()
        }

        etTitle = findViewById(R.id.et_title)
        etDescription = findViewById(R.id.et_description)
        etLocation = findViewById(R.id.et_location)
        ivPlaceImage = findViewById(R.id.iv_place_image)

        etDate = findViewById(R.id.et_date)
        etDate?.setOnClickListener(this)

        tvAddImage = findViewById(R.id.tv_add_image)
        tvAddImage?.setOnClickListener(this)

        btnSave = findViewById(R.id.btn_save)
        btnSave?.setOnClickListener(this)

        dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val format = "dd/MM/yyyy"
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            etDate?.setText(sdf.format(cal.time).toString())
        }

        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)){
            mHappyPlace = intent.getSerializableExtra(MainActivity.EXTRA_PLACE_DETAILS) as HappyPlaceEntity
        }

        if (mHappyPlace != null){
            supportActionBar?.title = "Edit Happy Place"

            etTitle?.setText(mHappyPlace!!.title)
            etLocation?.setText(mHappyPlace!!.location)
            etDescription?.setText(mHappyPlace!!.description)
            etDate?.setText(mHappyPlace!!.date)

            imagePath = mHappyPlace!!.image
            ivPlaceImage?.setImageURI(Uri.parse(imagePath))

            btnSave?.text = "Update"
        }




    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.et_date -> {
                DatePickerDialog(
                    this,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            R.id.tv_add_image -> {
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogOptions =
                    arrayOf("Select Photo from Gallery", "Capture photo from Camera")
                pictureDialog.setItems(pictureDialogOptions) { dialog, which ->
                    when (which) {
                        0 -> choosePhotoFromGallery()
                        1 -> choosePhotoFromCamera()
                    }
                }
                pictureDialog.show()
            }

            R.id.btn_save -> {
                val happyPlaceDao = (application as HappyPlaceApp).db.happyPlaceDao()
                if (mHappyPlace != null){
                    updateHappyPlace(happyPlaceDao = happyPlaceDao)
                }else{
                    addHappyPlace(happyPlaceDao = happyPlaceDao)
                }
            }
        }
    }

    private fun addHappyPlace(happyPlaceDao: HappyPlaceDao){

        if (checkNulls()){
            lifecycleScope.launch {
                happyPlaceDao.insert(HappyPlaceEntity(
                    title = etTitle?.text.toString(),
                    description = etDescription?.text.toString(),
                    image = imagePath,
                    date = etDate?.text.toString(),
                    location = etLocation?.text.toString(),
                    lat = 0.0,
                    lng = 0.0))

                etTitle?.text?.clear()
                etDescription?.text?.clear()
                imagePath=null
                etDate?.text?.clear()
                etLocation?.text?.clear()
                ivPlaceImage?.setImageDrawable(getDrawable(R.drawable.add_screen_image_placeholder))

                AlertDialog.Builder(this@AddHappyPlaceActivity)
                    .setTitle("Success")
                    .setMessage("Happy Place has been added. You can continue adding more or go back to homepage to see your happy places.")
                    .setPositiveButton("Ok",DialogInterface.OnClickListener { dialog, _ ->
                        dialog.dismiss()
                    }).show()
            }
        }

    }

    private fun updateHappyPlace(happyPlaceDao: HappyPlaceDao){

        if (checkNulls()){
            lifecycleScope.launch {
                mHappyPlace?.date = etDate?.text.toString()
                mHappyPlace?.title = etTitle?.text.toString()
                mHappyPlace?.description = etDescription?.text.toString()
                mHappyPlace?.image = imagePath
                mHappyPlace?.location = etLocation?.text.toString()
                happyPlaceDao.update(mHappyPlace!!)

                AlertDialog.Builder(this@AddHappyPlaceActivity)
                    .setTitle("Success")
                    .setMessage("Happy Place has been updated.")
                    .setPositiveButton("Ok",DialogInterface.OnClickListener { dialog, _ ->
                        dialog.dismiss()
                        finish()
                    }).show()
            }
        }

    }

    private fun checkNulls(): Boolean{
        var success = true

        if (etTitle?.text?.isEmpty() == true){
            etTitle?.error = "Title is required"
            success= false
        }

        if (etDescription?.text?.isEmpty() == true){
            etDescription?.error = "Description is required"
            success= false
        }
        if (etDate?.text?.isEmpty() == true){
            etDate?.error = "Date is required"
            success= false
        }

        if (etLocation?.text?.isEmpty() == true){
            etLocation?.error = "Location is required"
            success= false
        }

        if (imagePath == null){
            Toast.makeText(this, "Please upload an image from the camera", Toast.LENGTH_SHORT)
                .show()
            success= false
        }

        return success
    }

    private fun choosePhotoFromCamera() {
        Dexter.withContext(this)
            .withPermission(
                Manifest.permission.CAMERA
            ).withListener(object : PermissionListener {

                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    //TODO open camera
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, CAMERA)
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    AlertDialog.Builder(this@AddHappyPlaceActivity)
                        .setTitle("Permission denied")
                        .setMessage("You need to grant Camera permissions to use the Camera")
                        .setPositiveButton("Ok",DialogInterface.OnClickListener { dialog, _ ->
                            dialog.dismiss()
                        }).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            }).onSameThread().check()
    }

    private fun choosePhotoFromGallery() {
//        // Register ActivityResult handler
//        val requestPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
//            // Handle permission requests results
//            // See the permission example in the Android platform samples: https://github.com/android/platform-samples
//        }
//
//        // Permission request logic
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//            requestPermissions.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, READ_MEDIA_VISUAL_USER_SELECTED))
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            requestPermissions.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO))
//        } else {
//            requestPermissions.launch(arrayOf(READ_EXTERNAL_STORAGE))
//        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK){
            if (requestCode == CAMERA){
                val thumbNail : Bitmap = data!!.extras!!.get("data") as Bitmap
                val saveImageToInternalStorage = saveImageToInternalStorage(thumbNail)
                imagePath = saveImageToInternalStorage.toString()
                Log.e("saved image: ", "Path: $saveImageToInternalStorage")
                ivPlaceImage?.setImageBitmap(thumbNail)
            }
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap) : Uri{
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        }catch (e: IOException){
            e.printStackTrace()
        }

        return  Uri.parse(file.absolutePath)
    }

    companion object {
        private const val CAMERA = 100
        private const val IMAGE_DIRECTORY = "HappyPlacesImages"
    }
}