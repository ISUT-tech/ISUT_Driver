package com.isut.cabdriver

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.navigation.NavigationView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.database.FirebaseDatabase
import com.isut.cabdriver.user.UserModel
import com.google.firebase.database.DatabaseReference
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.CameraUpdate
import android.location.Geocoder
import android.os.Bundle
import com.google.android.gms.location.places.Places
import android.content.Intent
import android.os.Build
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.content.IntentSender.SendIntentException
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.maps.CameraUpdateFactory
import android.util.TypedValue
import android.location.LocationManager
import android.app.LoaderManager
import android.app.ProgressDialog
import android.content.Loader
import android.graphics.Color
import android.location.Address
import android.location.Location
import com.isut.cabdriver.apiclient.APIClient
import com.isut.cabdriver.apiclient.APIInterface
import com.isut.cabdriver.model.booking.BookingModel
import android.os.AsyncTask
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.*
import org.json.JSONObject
import com.isut.cabdriver.adapter.DirectionsJSONParser
import com.isut.cabdriver.model.ValidResponse
import com.isut.cabdriver.model.addcar.AddCarModel
import com.isut.cabdriver.model.cabdetails.CabDetailsModel
import com.isut.cabdriver.model.notibooking.Booking
import com.isut.cabdriver.model.notibooking.NotiBookingResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import java.util.*

  class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    OnMapReadyCallback, LoaderManager.LoaderCallbacks<Any>, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener {
    var bottonln: LinearLayout? = null
    var mainln: LinearLayout? = null
    var firebaseDatabase: FirebaseDatabase? = null
    var droverModelList: MutableList<UserModel?> = ArrayList()
    var databaseReference: DatabaseReference? = null
    var name: String? = null
    var mobile: String? = null
    var cabno: String? = null
    var cabmodel: String? = null
    var img_profile: ImageView? = null
    var img_his: ImageView? = null
    var imglog: ImageView? = null
    var Header = "Bearer"
    var progress: ProgressDialog? = null
    var book_button: Button? = null
    var cancle_button: Button? = null
    private var mPolyline: Polyline? = null
    var booking = 0
    var tv_fair: TextView? = null
    var tv_distqnce: TextView? = null
    var complete_button: TextView? = null
    var usertname: String? = null
    var driverId: String? = null
    var fair: String? = null
    var userid: String? = null
    var sourceLocation: String? = null
    var destination: String? = null
    var myCLocation: Button? = null
    var myCLocation2: Button? = null
    var lng = 0.0
    var lat = 0.0
    private var mapFragment: MapFragment? = null
    private var mMap: GoogleMap? = null
    private var googleApiClient: GoogleApiClient? = null
    private var mLastLocation: Location? = null
    private var request: LocationRequest? = null
    var mapView: View? = null
    private var mRequestingLocationUpdates = false
    var cLocation: CameraUpdate? = null
    var latitude = 0.0
    var longitude = 0.0
    var now: Marker? = null
    var srchMarker: Marker? = null
    var switchs: SwitchCompat? = null
    var destin: Marker? = null
    var tvstatuss: TextView? = null
    private var mOrigin: LatLng? = null
    private var mDestination: LatLng? = null
    var geocoder: Geocoder? = null
    var addresses: List<Address>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_bar_main)
        mapFragment = fragmentManager
            .findFragmentById(R.id.map) as MapFragment
        mapView = mapFragment!!.view
        CheckMapPermission()
        img_profile = findViewById(R.id.img_profile)
        switchs = findViewById(R.id.switchs)
        tvstatuss = findViewById(R.id.tvstatuss)
        tv_fair = findViewById(R.id.tv_fair)
        complete_button = findViewById(R.id.complete_button)
        bottonln = findViewById(R.id.bottonln)
        mainln = findViewById(R.id.mainln)
        img_his = findViewById(R.id.img_his)
        tv_distqnce = findViewById(R.id.tv_distqnce)
        imglog = findViewById(R.id.imglog)
        book_button = findViewById(R.id.book_button)
        cancle_button = findViewById(R.id.cancle_button)
        myCLocation = findViewById(R.id.myCLocation)
        myCLocation2 = findViewById(R.id.myCLocation2)
        cancle_button?.setOnClickListener(View.OnClickListener { v: View -> onClick(v) })
        book_button?.setOnClickListener(View.OnClickListener { v: View -> onClick(v) })
        complete_button?.setOnClickListener(View.OnClickListener { v: View -> onClick(v) })
        mapFragment!!.getMapAsync(this)
        googleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .addApi(Places.GEO_DATA_API)
            .addApi(Places.PLACE_DETECTION_API)
            .build()
        val sharedpreferences = getSharedPreferences(FixValue.MyPREFERENCES, MODE_PRIVATE)
        mobile = sharedpreferences.getString(FixValue.MOBILE, "")
        if (sharedpreferences.getString("status", "").equals("0"))
        {
            switchs?.isChecked = false
        }
        else{
            switchs?.isChecked = true
        }
     //   getdata()
        img_profile?.setOnClickListener(View.OnClickListener { v: View -> onClick(v) })
        img_his?.setOnClickListener(View.OnClickListener { v: View -> onClick(v) })
        imglog?.setOnClickListener(View.OnClickListener { v: View -> onClick(v) })
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        //toolbar.setTitle("Cab Driver");
        setSupportActionBar(toolbar)
        val intent = intent
        if (intent != null) {
            if (intent.hasExtra("key")) {
                if (intent.getStringExtra("key").equals("fromonti", ignoreCase = true)) {

                     val isInvoice = intent.getBooleanExtra("isInvoice",false)
                     val discount = intent.getStringExtra("discount")
                    if (isInvoice && discount != null)
                    {
                        val tot = intent.getStringExtra("fair")
                        invoiceDialog(tot!!,discount)

                    }
                    else {
                        bottonln?.setVisibility(View.VISIBLE)
                        mainln?.setVisibility(View.VISIBLE)
                        tv_distqnce!!.visibility = View.VISIBLE
                        tv_fair!!.visibility = View.VISIBLE
                        fair = intent.getStringExtra("fair")

                        tv_fair!!.text = "$ $fair"
                        book_button!!.text = "Accept  $ $fair"
                    }
                }
            }
        }
        switchs?.setOnCheckedChangeListener { buttonView, isChecked ->
          if (isChecked)
          {
              activeStatus(isChecked)
          }
            else{
              activeStatus(isChecked)

          }
        }    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.img_profile -> {
                val i = Intent(this@MainActivity, ProfileActivity::class.java)
                startActivity(i)
            }
            R.id.img_his -> {
                val i2 = Intent(this@MainActivity, HistroyActivity::class.java)
                startActivity(i2)
            }
            R.id.imglog -> logdialog()
            R.id.book_button -> {
                progress = ProgressDialog(this@MainActivity, R.style.AppCompatAlertDialogStyle)
                progress!!.setTitle("Loading")
                progress!!.setMessage("Wait while loading...")
                progress!!.show()
                submit(1)
            }
            R.id.complete_button -> {
                progress = ProgressDialog(this@MainActivity, R.style.AppCompatAlertDialogStyle)
                progress!!.setTitle("Loading")
                progress!!.setMessage("Wait while loading...")
                progress!!.show()
                submit(3)
            }
            R.id.cancle_button -> {
                progress = ProgressDialog(this@MainActivity, R.style.AppCompatAlertDialogStyle)
                progress!!.setTitle("Loading")
                progress!!.setMessage("Wait while loading...")
                progress!!.show()
                submit(2)
            }
        }
    }

    private fun CheckMapPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    1002
                )
            } else {
                setupLocationManager()
            }
        } else {
            setupLocationManager()
        }
    }

    private fun setupLocationManager() {
        //buildGoogleApiClient();
        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build()
            //mGoogleApiClient = new GoogleApiClient.Builder(this);
        }
        googleApiClient!!.connect()
        createLocationRequest()
    }

    protected fun createLocationRequest() {
        request = LocationRequest()
        request!!.smallestDisplacement = 10f
        request!!.fastestInterval = 50000
        request!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        request!!.numUpdates = 3
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(request)
        builder.setAlwaysShow(true)
        val result = LocationServices.SettingsApi.checkLocationSettings(
            googleApiClient,
            builder.build()
        )
        result.setResultCallback { result ->
            val status = result.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> setInitialLocation()
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->                         // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(
                            this@MainActivity,
                            REQUEST_CHECK_SETTINGS
                        )
                    } catch (e: SendIntentException) {
                        // Ignore the error.
                    }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {}
            }
        }
    }



    override fun onLoadFinished(loader: Loader<Any>, data: Any) {}
    override fun onLoaderReset(loader: Loader<Any>) {}
    override fun onConnected(bundle: Bundle?) {}
    override fun onConnectionSuspended(i: Int) {}
    override fun onConnectionFailed(connectionResult: ConnectionResult) {}
    override fun onLocationChanged(location: Location) {
        mLastLocation = location
        lat = location.latitude
        lng = location.longitude
        //   mOrigin = new LatLng(lat, lng);
        latitude = lat
        longitude = lng
        try {
            if (now != null) {
                now!!.remove()
            }
            val positionUpdate = LatLng(latitude, longitude)
            val update = CameraUpdateFactory.newLatLngZoom(positionUpdate, 15f)
            now = mMap!!.addMarker(
                MarkerOptions().position(positionUpdate)
                    .title("Your Location").flat(true)
                    .icon(
                        BitmapDescriptorFactory
                            .fromResource(R.drawable.cansing)
                    )
                    .anchor(0.5f, 0.5f)
                    .position(
                        LatLng(
                            location.latitude, location
                                .longitude
                        )
                    )
            )
            mMap!!.animateCamera(update)
            /*   if(mOrigin != null && mDestination != null)
            {
                drawRoute();
            }*/
            //myCurrentloc.setText( ""+latitude );
        } catch (ex: Exception) {
            ex.printStackTrace()
            Log.e("MapException", ex.message!!)
        }

        //Geocode current location details
        try {
            geocoder = Geocoder(this@MainActivity, Locale.ENGLISH)
            addresses = geocoder!!.getFromLocation(latitude, longitude, 1)
            val str = StringBuilder()
            if (Geocoder.isPresent()) {
                /*Toast.makeText(getApplicationContext(),
                                "geocoder present", Toast.LENGTH_SHORT).show();*/
                val returnAddress = addresses?.get(0)
                val localityString = returnAddress?.getAddressLine(0)
                //String city = returnAddress.getAddressLine(1);
                //String region_code = returnAddress.getAddressLine(2);
                //String zipcode = returnAddress.getAddressLine(3);
                str.append(localityString).append("")
                // str.append( city ).append( "" ).append( region_code ).append( "" );
                // str.append( zipcode ).append( "" );

                //   myCurrentloc.setText(str);
               // Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
            } else {
                /*    Toast.makeText(getApplicationContext(),
                                "geocoder not present", Toast.LENGTH_SHORT).show();*/
            }

// } else {
// Toast.makeText(getApplicationContext(),
// "address not available", Toast.LENGTH_SHORT).show();
// }
        } catch (e: IOException) {
// TODO Auto-generated catch block
            Log.e("tag", e.message!!)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        /* this.mMap.getUiSettings().setMyLocationButtonEnabled(true);
        this.mMap.setMyLocationEnabled(true);
*/

        /*    try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this ) );

            if (!success) {
                Log.e( TAG, "Style parsing failed." );
            }
        } catch (Resources.NotFoundException e) {
            Log.e( TAG, "Can't find style. Error: ", e );
        }*/if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then over   riding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        //This line will show your current location on Map with GPS dot
        // mMap.setMyLocationEnabled( true );
        //  setInitialLocation();
        mMap!!.isMyLocationEnabled = true
        locationButton()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return false
    }

    private fun setInitialLocation() {
        if (ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
            googleApiClient,
            request
        ) { location ->
            mLastLocation = location
            lat = location.latitude
            lng = location.longitude
            //  mOrigin = new LatLng(lat, lng);
            latitude = lat
            longitude = lng
            try {
                if (now != null) {
                    now!!.remove()
                }
                val positionUpdate = LatLng(latitude, longitude)
                val update = CameraUpdateFactory.newLatLngZoom(positionUpdate, 15f)
                now = mMap!!.addMarker(
                    MarkerOptions().position(positionUpdate)
                        .title("Your Location").flat(true)
                        .icon(
                            BitmapDescriptorFactory
                                .fromResource(R.drawable.cansing)
                        )
                        .anchor(0.5f, 0.5f)
                        .position(
                            LatLng(
                                location.latitude, location
                                    .longitude
                            )
                        )
                )
                mMap!!.animateCamera(update)
                val intent = intent
                if (intent != null) {
                    if (intent.hasExtra("key")) {
                        if (intent.getStringExtra("key").equals("fromonti", ignoreCase = true)) {
                            //  CheckMapPermission();
                            /*  googleApiClient = new GoogleApiClient.Builder( this )
                                .addConnectionCallbacks( this )
                                .addOnConnectionFailedListener( this )
                                .addApi( LocationServices.API )
                                .addApi( Places.GEO_DATA_API )
                                .addApi( Places.PLACE_DETECTION_API )
                                .build();*/
                            //   onResume();
                            usertname = intent.getStringExtra("name")
                            userid = intent.getStringExtra("userid")
                            driverId = intent.getStringExtra("driverid")
                            sourceLocation = intent.getStringExtra("source")
                            destination = intent.getStringExtra("destination")
                            fair = intent.getStringExtra("fair")
                            booking = intent.getIntExtra("bookingoid", 0)
                            getLocationFromAddress(sourceLocation, destination, 0)
                            myCLocation!!.text = sourceLocation
                            myCLocation2!!.text = destination
                        }
                    }
                }
                /* if(mOrigin != null && mDestination != null)
                        {
                            drawRoute();
                        }*/
                //myCurrentloc.setText( ""+latitude );
            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.e("MapException", ex.message!!)
            }

            //Geocode current location details
            try {
                geocoder = Geocoder(this@MainActivity, Locale.ENGLISH)
                addresses = geocoder!!.getFromLocation(latitude, longitude, 1)
                val str = StringBuilder()
                if (Geocoder.isPresent()) {
                    /*Toast.makeText(getApplicationContext(),
                                    "geocoder present", Toast.LENGTH_SHORT).show();*/
                    val returnAddress = addresses?.get(0)
                    val localityString = returnAddress?.getAddressLine(0)
                    //String city = returnAddress.getAddressLine(1);
                    //String region_code = returnAddress.getAddressLine(2);
                    //String zipcode = returnAddress.getAddressLine(3);
                    str.append(localityString).append("")
                    // str.append( city ).append( "" ).append( region_code ).append( "" );
                    // str.append( zipcode ).append( "" );

                    //myCurrentloc.setText(str);
                  //  Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
                } else {
                    /*    Toast.makeText(getApplicationContext(),
                                    "geocoder not present", Toast.LENGTH_SHORT).show();*/
                }

// } else {
// Toast.makeText(getApplicationContext(),
// "address not available", Toast.LENGTH_SHORT).show();
// }
            } catch (e: IOException) {
// TODO Auto-generated catch block
                Log.e("tag", e.message!!)
            }
        }
    }

    private fun locationButton() {
        val mapFragment = fragmentManager
            .findFragmentById(R.id.map) as MapFragment
        val locationButton =
            (mapFragment.view!!.findViewById<View>("1".toInt()).parent as View).findViewById<View>("2".toInt())
        if (locationButton != null && locationButton.layoutParams is RelativeLayout.LayoutParams) {
            // location button is inside of RelativeLayout
            val params = locationButton.layoutParams as RelativeLayout.LayoutParams

            // Align it to - parent BOTTOM|LEFT
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)

            // Update margins, set to 10dp
            val margin = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 150f,
                resources.displayMetrics
            ).toInt()
            params.setMargins(margin, margin, margin, margin)
            locationButton.layoutParams = params
        }
    }


    fun logdialog() {
        val alert = AlertDialog.Builder(this@MainActivity)
        alert.setMessage("Are you sure you want to LogOut?")
        alert.setPositiveButton("YES") { dialog, which ->
            val preferences = getSharedPreferences(FixValue.MyPREFERENCES, MODE_PRIVATE)
            val editor = preferences.edit()
            editor.clear()
            editor.apply()
            againSetData()
            val intent = Intent(this@MainActivity, LogInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
        alert.setNegativeButton("NO") { dialog, which -> // close dialog
            dialog.cancel()
        }
        alert.show()
    }

    private fun againSetData() {
        val sharedpreferences = getSharedPreferences(FixValue.MyPREFERENCES, MODE_PRIVATE)
        val edit = sharedpreferences.edit()
        edit.putString(FixValue.IN_lOGIN, "")
        //edit.putString(IsAppOpenFirst,OUT_LOGIN);
        edit.commit()
    }

    override fun onResume() {
        super.onResume()
        checkvlaid()
        //        getdata();
        // CheckMapPermission();
        if (googleApiClient!!.isConnected) {
            setInitialLocation()
        }
        val service = getSystemService(LOCATION_SERVICE) as LocationManager
        val enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER)

        // Check if enabled and if not send user to the GPS settings
        if (!enabled) {
            buildAlertMessageNoGps()
        }
        if (enabled) {
            //mMap.animateCamera( update );

        }
    }

    protected fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Please Turn ON your GPS Connection")
            .setTitle("GPS Not Enabled")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            .setNegativeButton("No") { dialog, id -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }

    override fun onStart() {
        super.onStart()
        googleApiClient!!.connect()
        //mGoogleApiClient.connect();
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (googleApiClient!!.isConnected) {
            googleApiClient!!.disconnect()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("onActivityResult()", Integer.toString(resultCode))
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> when (resultCode) {
                RESULT_OK -> {
                    setInitialLocation()
                   // Toast.makeText(this@MainActivity, "Location enabled", Toast.LENGTH_LONG).show()
                }
                RESULT_CANCELED -> {
                    // The user was asked to change settings, but chose not to
                    //Toast.makeText(this@MainActivity, "Location not enabled", Toast.LENGTH_LONG)
                      //  .show()
                    mRequestingLocationUpdates = false
                }
                else -> {}
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1002 -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        setupLocationManager()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Permission Denied", Toast.LENGTH_SHORT)
                        .show()
                    //finish();
                }
            }
        }
    }

    fun submit(status: Int) {
        val sharedpreferences = getSharedPreferences(FixValue.MyPREFERENCES, MODE_PRIVATE)
        val edit = sharedpreferences.edit()
        Header = "Bearer " + sharedpreferences.getString(FixValue.TOKEN, "")
        val ids = java.lang.Long.valueOf(booking.toLong())
        // SharedPreferences sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        //  final SharedPreferences.Editor editor = sharedPreferences.edit();
        val url = FixValue.baseurl + "booking/" + ids + "/updateStatus/" + status
        val apiService = APIClient.client?.create(APIInterface::class.java)
        val call = apiService?.fixbooing(Header, url)
        call?.enqueue(object : Callback<NotiBookingResponse> {
            override fun onResponse(call: Call<NotiBookingResponse>, response: Response<NotiBookingResponse>) {
                if (response.body()!!.status.equals("ok", ignoreCase = true)) {
                  //  bottonln!!.visibility = View.GONE
                    cancle_button!!.visibility = View.GONE
                    book_button!!.visibility = View.GONE
                    complete_button!!.visibility = View.VISIBLE
                    mainln!!.visibility = View.VISIBLE
                    Toast.makeText(
                        this@MainActivity,
                        "" + response.body()!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    if(response.body()?.data?.booking?.scheduleStatus.equals("0"))
                    {
                        complete_button?.visibility  = View.VISIBLE

                    }
                    else{
                        complete_button?.visibility  = View.GONE

                    }
                    if (status == 3 || status == 2  )
                    {
                        bottonln!!.visibility = View.GONE
                    }
              if (status == 3   )
                                {
                  invoiceDialog(response.body()!!.data.booking.fair.toString(),response.body()!!.data.discount.toString())
                                }

                    progress!!.dismiss()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "" + response.body()!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    progress!!.dismiss()
                }
            }

            override fun onFailure(call: Call<NotiBookingResponse>, t: Throwable) {}
        })
    }

    fun getLocationFromAddress(strAddress: String?, destination: String?, type: Int) {
        //Create coder with Activity context - this
        val coder = Geocoder(this)
        val address: List<Address>?
        val destaddress: List<Address>?
        try {
            //Get latLng from String
            address = coder.getFromLocationName(strAddress, 5)
            destaddress = coder.getFromLocationName(destination, 5)

            //check for null
            if (address == null) {
                return
            }
            if (destaddress == null) {
                return
            }
            if (type == 0) {
                //Lets take first possibility from the all possibilities.
                val location2 = destaddress[0]
                val DlatLng = LatLng(location2.latitude, location2.longitude)

                //Put marker on map on that LatLng
                // Log.d("sdfg","sdfg"+latLng.toString());
                destin = mMap!!.addMarker(
                    MarkerOptions().position(DlatLng).title("Your Destination")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.placeholder))
                )
                mDestination = LatLng(DlatLng.latitude, DlatLng.longitude)

                //Animate and Zoon on that map location
                //  mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                /*    Polyline line = mMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(lat, lng), latLng)
                        .width(5)
                        .color(Color.RED));*/
            }
            if (type == 0) {
                val location = address[0]
                val latLng = LatLng(location.latitude, location.longitude)

                //Put marker on map on that LatLng
                // Log.d("sdfg","sdfg"+latLng.toString());
                srchMarker = mMap!!.addMarker(
                    MarkerOptions().position(latLng).title("Your Location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.cansing))
                )
                mOrigin = LatLng(latLng.latitude, latLng.longitude)

                //Animate and Zoon on that map location
                mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                mMap!!.animateCamera(CameraUpdateFactory.zoomTo(13f))

                /* Polyline line = mMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(lat, lng), latLng)
                        .width(5)
                        .color(Color.RED));*/
            }
            if (mOrigin != null && mDestination != null) {
                drawRoute()
                CalculationByDistance(mOrigin!!, mDestination!!)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun drawRoute() {

        // Getting URL to the Google Directions API
        val url = getDirectionsUrl(mOrigin, mDestination)
        val downloadTask: DownloadTask = DownloadTask()

        // Start downloading json data from Google Directions API
        downloadTask.execute(url)
    }

    private fun getDirectionsUrl(origin: LatLng?, dest: LatLng?): String {
        // Origin of route
        val str_origin = "origin=" + origin!!.latitude + "," + origin.longitude

        // Destination of route
        val str_dest = "destination=" + dest!!.latitude + "," + dest.longitude


        // Sensor enabled

        // Building the parameters to the web service
        val sensor = "sensor=true"
        val mode = "mode=walking"
        val key = "key=" + resources.getString(R.string.google_maps_key)
        // Building the parameters to the web service
        val parameters = "$str_origin&$str_dest&$sensor&$mode&$key"

        // Output format
        val output = "json"

        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters"
    }

    @Throws(IOException::class)
    private fun downloadUrl(strUrl: String): String {
        var data = ""
        var iStream: InputStream? = null
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(strUrl)

            // Creating an http connection to communicate with url
            urlConnection = url.openConnection() as HttpURLConnection

            // Connecting to url
            urlConnection!!.connect()

            // Reading data from url
            iStream = urlConnection.inputStream
            val br = BufferedReader(InputStreamReader(iStream))
            val sb = StringBuffer()
            var line: String? = ""
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }
            data = sb.toString()
            br.close()
        } catch (e: Exception) {
            Log.d("Exception on download", e.toString())
        } finally {
            iStream!!.close()
            urlConnection!!.disconnect()
        }
        return data
    }

    private inner class DownloadTask : AsyncTask<String?, Void?, String>() {
        // Downloading data in non-ui thread

        // Executes in UI thread, after the execution of
        // doInBackground()
        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            val parserTask = ParserTask()

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result)
        }

        override fun doInBackground(vararg params: String?): String {
            var data = ""
            try {
                // Fetching the data from web service
                data = downloadUrl(params[0].toString())
                Log.d("DownloadTask", "DownloadTask : $data")
            } catch (e: Exception) {
                Log.d("Background Task", e.toString())
            }
            return data
        }
    }

    private inner class ParserTask :
        AsyncTask<String?, Int?, List<List<HashMap<String, String>>>?>() {
        // Parsing the data in non-ui thread

        // Executes in UI thread, after the parsing process
        override fun onPostExecute(result: List<List<HashMap<String, String>>>?) {
            var points: ArrayList<LatLng?>? = null
            var lineOptions: PolylineOptions? = null

            // Traversing through all the routes
            for (i in result!!.indices) {
                points = ArrayList()
                lineOptions = PolylineOptions()

                // Fetching i-th route
                val path = result[i]

                // Fetching all the points in i-th route
                for (j in path.indices) {
                    val point = path[j]
                    val lat = point["lat"]!!.toDouble()
                    val lng = point["lng"]!!.toDouble()
                    val position = LatLng(lat, lng)
                    points.add(position)
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points)
                lineOptions.width(8f)
                lineOptions.color(Color.RED)
            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                if (mPolyline != null) {
                    mPolyline!!.remove()
                }
                mPolyline = mMap!!.addPolyline(lineOptions)
            } else Toast.makeText(applicationContext, "No route is found", Toast.LENGTH_LONG).show()
        }

        override fun doInBackground(vararg jsonData: String?): List<List<HashMap<String, String>>>? {
            val jObject: JSONObject
            var routes: List<List<HashMap<String, String>>>? = null
            try {
                jObject = JSONObject(jsonData[0])
                val parser = DirectionsJSONParser()

                // Starts parsing data
                routes = parser.parse(jObject)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return routes        }
    }

    fun CalculationByDistance(StartP: LatLng, EndP: LatLng): Double {
        val Radius = 6371 // radius of earth in Km
        val lat1 = StartP.latitude
        val lat2 = EndP.latitude
        val lon1 = StartP.longitude
        val lon2 = EndP.longitude
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = (Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + (Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2)))
        val c = 2 * Math.asin(Math.sqrt(a))
        val valueResult = Radius * c
        val km = valueResult / 1
        val newFormat = DecimalFormat("####")
        val kmInDec = Integer.valueOf(newFormat.format(km))
        val meter = valueResult % 1000
        val meterInDec = Integer.valueOf(newFormat.format(meter))
        Log.i(
            "Radius Value", "" + valueResult + "   KM  " + kmInDec
                    + " Meter   " + meterInDec
        )
        val miles = (0.6213711922 * km).toFloat()
        val milesInDec = Integer.valueOf(newFormat.format(miles))

      //  tv_distqnce!!.visibility = View.VISIBLE
       // tv_fair!!.visibility = View.VISIBLE
        val fair = milesInDec * 8
        //tv_fair!!.text = "$ $fair"
        //book_button!!.text = "Accept  $ $fair"
        tv_distqnce!!.text = "Distance  $milesInDec Miles"
        return Radius * c
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val PERMISSION_MY_LOCATION = 3
        private const val REQUEST_CHECK_SETTINGS = 1000
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Any> {
        TODO("Not yet implemented")
    }


      fun activeStatus(status: Boolean) {
          val sharedpreferences = getSharedPreferences(FixValue.MyPREFERENCES, MODE_PRIVATE)
          val edit = sharedpreferences.edit()
          Header = "Bearer " + sharedpreferences.getString(FixValue.TOKEN, "")
          val ids = sharedpreferences.getInt(FixValue.User_ID, 0)
          // SharedPreferences sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
          //  final SharedPreferences.Editor editor = sharedPreferences.edit();
          val url = FixValue.baseurl + "booking/" + ids + "/updateStatus/" + status
          val apiService = APIClient.client?.create(APIInterface::class.java)
          val call = apiService?.driverActive(Header, status,ids.toString())
          call?.enqueue(object : Callback<AddCarModel> {
              override fun onResponse(call: Call<AddCarModel>, response: Response<AddCarModel>) {
                  if (response.body()!!.status.equals("ok", ignoreCase = true)) {
                      //  bottonln!!.visibility = View.GONE

                      Toast.makeText(
                          this@MainActivity,
                          "" + response.body()!!.message,
                          Toast.LENGTH_SHORT
                      ).show()
                      if(status) edit.putString("status","1")
                      else edit.putString("status","0")

                      edit.apply()

                  } else {
                      Toast.makeText(
                          this@MainActivity,
                          "" + response.body()!!.message,
                          Toast.LENGTH_SHORT
                      ).show()
                  }
              }

              override fun onFailure(call: Call<AddCarModel>, t: Throwable) {}
          })
      }

      fun checkvlaid() {
          val sharedpreferences = getSharedPreferences(FixValue.MyPREFERENCES, MODE_PRIVATE)
          val edit = sharedpreferences.edit()
          Header = "Bearer " + sharedpreferences.getString(FixValue.TOKEN, "")
          val id = sharedpreferences.getInt(FixValue.User_ID, 0)
          val name = sharedpreferences.getString(FixValue.FIRSTNAME, "")
          val mobile = sharedpreferences.getString(FixValue.MOBILE, "")
          // SharedPreferences sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
          //  final SharedPreferences.Editor editor = sharedPreferences.edit();
          val url = FixValue.baseurl + "customer/" + id + "/cabs"
          val apiService = APIClient.client?.create(APIInterface::class.java)
          val call = apiService?.validator(Header)
          call?.enqueue(object : Callback<ValidResponse> {
              override fun onResponse(
                  call: Call<ValidResponse>,
                  response: Response<ValidResponse>
              ) {
                  if (response.body() != null) {
                      if (response.body()!!.status.equals(
                              "EXPECTATION_FAILED",
                              ignoreCase = true
                          )
                      ) {
                          Toast.makeText(
                              this@MainActivity,
                              "" + response.body()!!.message,
                              Toast.LENGTH_SHORT
                          ).show()
                          val intent1 = Intent(this@MainActivity, LogInActivity::class.java)
                          startActivity(intent1)
                          finish()
                      }


                  } else {
                      val preferences = getSharedPreferences(FixValue.MyPREFERENCES, MODE_PRIVATE)
                      val editor = preferences.edit()
                      editor.clear()
                      editor.apply()
                      againSetData()
                      val intent = Intent(this@MainActivity, LogInActivity::class.java)
                      intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                      startActivity(intent)
                      finish()
                  }
              }
              override fun onFailure(call: Call<ValidResponse>, t: Throwable) {
              }
          })
      }
      fun  invoiceDialog(gtotal:String,discount:String)
      {
          val view: View = layoutInflater.inflate(R.layout.dialog_invoice, null)
          val alertDialog = AlertDialog.Builder(this).create()
          alertDialog.setTitle("Invoice")
          alertDialog.setCancelable(false)

          val tvtotalfair = view.findViewById(R.id.tvtotalfair) as TextView

          val tv_discount = view.findViewById(R.id.tv_discount) as TextView
          val textView59 = view.findViewById(R.id.textView59) as TextView
          val textView58 = view.findViewById(R.id.textView58) as TextView
          val textView57 = view.findViewById(R.id.textView57) as TextView
          val textView56 = view.findViewById(R.id.textView56) as TextView
          val tv_gtotal = view.findViewById(R.id.tv_gtotal) as TextView
          val tv_tip = view.findViewById(R.id.tv_tip) as TextView
          val btnsave = view.findViewById(R.id.btnsave) as AppCompatButton


          tvtotalfair.setText("$"+gtotal)
          tv_discount.setText("$"+discount)
          val tot = gtotal.toDouble() +discount.toDouble()
          tv_gtotal.setText("$"+tot)
          btnsave.setOnClickListener {


              alertDialog.dismiss()

          }



          alertDialog.setView(view);
          alertDialog.show();
      }

  }