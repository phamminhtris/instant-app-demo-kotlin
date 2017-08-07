package com.firebaseapp.instant_app_demo.androidinstantappdemokotlin.mainmodule

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
//import com.trustcircle.internetmodule.InternetActivity
//import com.trustcircle.permissionmodule.PermissionActivity

class MainActivity : AppCompatActivity() {

    private var mPermissionRequestButton: Button? = null
    private var mInternetAccessButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mPermissionRequestButton = findViewById<Button>(R.id.permission_module_button)
        mPermissionRequestButton!!.setOnClickListener {
            //startActivity(Intent(this@MainActivity, PermissionActivity::class.java))
        }

        mInternetAccessButton = findViewById<Button>(R.id.internet_access_module_button)
        mInternetAccessButton!!.setOnClickListener {
            //startActivity(Intent(this@MainActivity, InternetActivity::class.java))
        }


    }

}


