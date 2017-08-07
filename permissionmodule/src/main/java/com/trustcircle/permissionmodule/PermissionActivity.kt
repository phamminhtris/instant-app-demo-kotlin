package com.trustcircle.permissionmodule

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Button
import android.widget.Toast

class PermissionActivity : AppCompatActivity() {

    private var mRequestCameraButton: Button? = null
    private var mRequestLocationButtion: Button? = null
    private var mRequestNumberButton: Button? = null

    private val MY_PERMISSIONS_REQUEST_USE_CAMERA = 0
    private val MY_PERMISSIONS_REQUEST_USE_LOCATION = 1
    private val MY_PERMISSIONS_REQUEST_READ_PHONE_NUMBER = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)

        mRequestCameraButton = findViewById<Button>(R.id.request_camera_button)
        mRequestLocationButtion = findViewById<Button>(R.id.request_location_button)
        mRequestNumberButton = findViewById<Button>(R.id.request_number_button)

        mRequestCameraButton!!.setOnClickListener(View.OnClickListener { checkAndRequestPermission(Manifest.permission.CAMERA, MY_PERMISSIONS_REQUEST_USE_CAMERA) })

        mRequestLocationButtion!!.setOnClickListener(View.OnClickListener { checkAndRequestPermission(Manifest.permission.ACCESS_FINE_LOCATION, MY_PERMISSIONS_REQUEST_USE_LOCATION) })

        mRequestNumberButton!!.setOnClickListener(View.OnClickListener { checkAndRequestPermission(Manifest.permission.READ_PHONE_NUMBERS, MY_PERMISSIONS_REQUEST_READ_PHONE_NUMBER) })

    }

    private fun checkAndRequestPermission(permissionType: String, requestCode: Int) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                permissionType) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permissionType)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this,
                        arrayOf(permissionType),
                        requestCode)

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        arrayOf(permissionType),
                        requestCode)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_USE_CAMERA -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(this, "Camera permission granted.", Toast.LENGTH_SHORT).show()

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Oops! Camera permission denied.", Toast.LENGTH_SHORT).show()
                }
                return
            }
            MY_PERMISSIONS_REQUEST_USE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(this, "location permission granted.", Toast.LENGTH_SHORT).show()

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Oops! Location permission denied.", Toast.LENGTH_SHORT).show()
                }
                return
            }
            MY_PERMISSIONS_REQUEST_READ_PHONE_NUMBER -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(this, "Read phone number permission granted.", Toast.LENGTH_SHORT).show()

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Oops! Read phone number permission denied.", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }
}
