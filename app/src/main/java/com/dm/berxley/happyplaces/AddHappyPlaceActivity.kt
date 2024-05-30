package com.dm.berxley.happyplaces

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.app.Activity
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.text.SimpleDateFormat
import java.util.Locale


class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {
    private var toolbar: Toolbar? = null
    private var etDate: EditText? = null
    private var tvAddImage: TextView? = null
    private var ivPlaceImage: ImageView? = null
    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_happy_place)

        toolbar = findViewById(R.id.tool_bar_add_place)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar?.setNavigationOnClickListener {
            onBackPressed()
        }

        ivPlaceImage = findViewById(R.id.iv_place_image)

        etDate = findViewById(R.id.et_date)
        etDate?.setOnClickListener(this)

        tvAddImage = findViewById(R.id.tv_add_image)
        tvAddImage?.setOnClickListener(this)

        dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val format = "dd/MM/yyyy"
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            etDate?.setText(sdf.format(cal.time).toString())
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
        }
    }

    private fun choosePhotoFromCamera() {
        Dexter.withContext(this)
            .withPermission(
                Manifest.permission.CAMERA
            ).withListener(object : PermissionListener {

                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    //TODO open camera
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent,100)
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
            if (requestCode == 100){
                val thumbNail : Bitmap = data!!.extras!!.get("data") as Bitmap
                ivPlaceImage?.setImageBitmap(thumbNail)
            }
        }
    }
}