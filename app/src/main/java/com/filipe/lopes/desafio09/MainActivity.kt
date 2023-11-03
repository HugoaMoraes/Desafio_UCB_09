package com.filipe.lopes.desafio09

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.MapView

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationPC: FusedLocationProviderClient
    var longitude = "-48.028871041204184"
    var latitude = "-15.86395916559091"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationPC = LocationServices.getFusedLocationProviderClient(this)

        val locationNameEditText: EditText = findViewById(R.id.editTextText)

        val button: Button = findViewById(R.id.button)
        val buttonFav1: Button = findViewById(R.id.button2)
        val buttonFav2: Button = findViewById(R.id.button3)
        val buttonFav3: Button = findViewById(R.id.button4)

        getCurrentLocation()

        button.setOnClickListener {
            var url = Uri.parse("geo:?q=")

            if(locationNameEditText.text.isNotEmpty()) {
                url = Uri.parse("geo:${locationNameEditText.text.toString()}?q=${locationNameEditText.text.toString()}")
            } else {
                url = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")
            }

            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(intent)
        }

        buttonFav1.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:33°48'26.12,-117°55'4.96?q=Disneyland"))
            startActivity(intent)
        }
        buttonFav2.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=Jeriquaquara"))
            startActivity(intent)
        }
        buttonFav3.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=Alemanha"))
            startActivity(intent)
        }
    }

    private fun getCurrentLocation(){
        if(checkPermission()){
            if(isLocationEnabled()){
                fusedLocationPC.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if(location == null) {
                        Toast.makeText(this,"Localização Vazia", Toast.LENGTH_SHORT).show()
                    }else {
                        Toast.makeText(this,"Localização adquirida com sucesso", Toast.LENGTH_SHORT).show()
                        latitude = location.latitude.toString()
                        longitude = location.longitude.toString()
                    }
                }
            } else {
                Toast.makeText(this,"Dispositivo sem GPS ou Internet", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this,"Ative a sua localização", Toast.LENGTH_SHORT).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    private fun checkPermission() : Boolean {
        val coarseLocation = ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val fineLocation = ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )

        val permissionGranted = PackageManager.PERMISSION_GRANTED

        if(coarseLocation == permissionGranted && fineLocation == permissionGranted){
            return true
        }
        return false
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            100
        )
    }

    private fun isLocationEnabled() : Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsProvider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val netProvider = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        return gpsProvider && netProvider
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext,"Sucesso",Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            } else {
                Toast.makeText(applicationContext,"Negado",Toast.LENGTH_SHORT).show()
            }
        }
    }
}