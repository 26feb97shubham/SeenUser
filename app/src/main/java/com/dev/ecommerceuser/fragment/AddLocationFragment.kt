package com.dev.ecommerceuser.fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.animation.AlphaAnimation
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dev.ecommerceuser.R
import com.dev.ecommerceuser.rest.ApiClient
import com.dev.ecommerceuser.rest.ApiInterface
import com.dev.ecommerceuser.utils.LogUtils
import com.dev.ecommerceuser.utils.SharedPreferenceUtility
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import io.nlopez.smartlocation.OnLocationUpdatedListener
import io.nlopez.smartlocation.SmartLocation
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_add_location.view.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddLocationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddLocationFragment : Fragment(), OnMapReadyCallback{
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var mView: View?=null
    private val TAG = "AddLocationFragment"
    private lateinit var mMap:GoogleMap
    private val LOCATION_PERMISSION_CODE = 500
    private val REQUEST_CHECK_SETTINGS = 900
    private val RequestPermissionsSettings = 500
    private val PLACE_AUTOCOMPLETE_REQUEST_CODE = 101
    var latitude:Double=0.0
    var longitude:Double=0.0
    var address:String=""
    var isDefault:Int=0
    var title:String=""
    var country:String=""
    var city:String=""
    var street:String=""
    var type:String="1"
    var location_id:Int=0
    var building_num:String=""
    var apartment_num:String=""
    var responseBody:String=""
    var position:Int=-1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var lastLocation: Location
    var mGoogleApiClient: GoogleApiClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            responseBody = it.getString("responseBody", "")
            position = it.getInt("position", -1)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_add_location, container, false)
        setUpViews(savedInstanceState)
        return mView
    }
    private fun setUpViews(savedInstanceState: Bundle?) {
        requireActivity().frag_other_backImg.visibility=View.VISIBLE

        requireActivity().frag_other_backImg.setOnClickListener {
            requireActivity().frag_other_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().frag_other_backImg)
            findNavController().popBackStack()
        }

        var mapViewBundle : Bundle? = null
        if (savedInstanceState!=null){
            mapViewBundle = savedInstanceState.getBundle(getString(R.string.google_maps_api_key))
        }
        mView!!.mapView.onCreate(mapViewBundle)
        mView!!.mapView.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

     /*   val mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)*/


        mView!!.imgSetDef.setOnClickListener {
            if(isDefault==1){
                isDefault=0
                mView!!.imgSetDef.setImageResource(R.drawable.un_check)
            }
            else{
                isDefault=1
                mView!!.imgSetDef.setImageResource(R.drawable.check)
            }
        }

        mView!!.btnSaveLoc.setOnClickListener {
            mView!!.btnSaveLoc.startAnimation(AlphaAnimation(1f, .5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), mView!!.btnSaveLoc)
            validateAndSaveLocation()
        }
        mView!!.scrollView.setOnTouchListener(object :View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                mView!!.edtTitle.clearFocus()
                mView!!.edtCountry.clearFocus()
                mView!!.edtCity.clearFocus()
                mView!!.edtStreet.clearFocus()
                mView!!.edtApartment.clearFocus()
                mView!!.edtBuilding.clearFocus()
                return false
            }

        })

    }

    private fun editLocation() {
        val jsonObject = JSONObject(responseBody)
        if (jsonObject.getInt("response") == 1){
            val locations=jsonObject.getJSONArray("locations")

            for(i in 0  until locations.length()){
                if(position==i){
                    val obj = locations.getJSONObject(i)
                    location_id = obj.getInt("id")
                    isDefault = obj.getInt("set_as_default")
                    title = obj.getString("title")
                    address = obj.getString("address")
                    country = obj.getString("country")
                    city = obj.getString("city")
                    street = obj.getString("street")
                    apartment_num = obj.getString("apartmant_no")
                    building_num = obj.getString("building_no")
                    if(!TextUtils.isEmpty(obj.getString("latitude"))){
                        latitude = obj.getString("latitude").toDouble()
                    }
                    if(!TextUtils.isEmpty(obj.getString("longitude"))){
                        longitude = obj.getString("longitude").toDouble()
                    }

                    type="2"

                    mView!!.txtLoc.text=address
                    mView!!.edtTitle.setText(title)
                  /*  mView.edtCountry.text = country
                    mView.edtCity.text = city*/
                    mView!!.edtStreet.setText(street)
                    mView!!.edtApartment.setText(apartment_num)
                    mView!!.edtBuilding.setText(building_num)
                    if(isDefault==1){
                        mView!!.imgSetDef.setImageResource(R.drawable.check)
                    }
                    else{
                        mView!!.imgSetDef.setImageResource(R.drawable.un_check)
                    }
                    mMap.clear()
                    mMap.addMarker(MarkerOptions().position(LatLng(latitude, longitude)).icon(BitmapDescriptorFactory.defaultMarker()).title(address))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 12f))
                }

            }

        }
    }

    private fun validateAndSaveLocation() {
        title=mView!!.edtTitle.text.toString()
        country=mView!!.edtCountry.text.toString()
        city=mView!!.edtCity.text.toString()
        street=mView!!.edtStreet.text.toString()
        apartment_num=mView!!.edtApartment.text.toString()
        building_num=mView!!.edtBuilding.text.toString()
        if(TextUtils.isEmpty(title)){
            mView!!.edtTitle.requestFocus()
            mView!!.edtTitle.error="Please enter title for your location"
//            LogUtils.shortToast(requireContext(), "Please enter title for your location")
        }
        else if(TextUtils.isEmpty(country)){
            mView!!.edtCountry.requestFocus()
            mView!!.edtCountry.error="Please enter country name for your location"
//            LogUtils.shortToast(requireContext(), "Please enter country name for your location")
        }
        else if(TextUtils.isEmpty(city)){
            mView!!.edtCity.requestFocus()
            mView!!.edtCity.error="Please enter city name for your location"
//            LogUtils.shortToast(requireContext(), "Please enter city name for your location")
        }
        else if(TextUtils.isEmpty(street)){
            mView!!.edtStreet.requestFocus()
            mView!!.edtStreet.error="Please enter street name for your location"
//            LogUtils.shortToast(requireContext(), "Please enter street name for your location")
        }
        else if(TextUtils.isEmpty(apartment_num)){
            mView!!.edtApartment.requestFocus()
            mView!!.edtApartment.error="Please enter apartment number for your location"
//            LogUtils.shortToast(requireContext(), "Please enter apartment number for your location")
        }
        else if(TextUtils.isEmpty(building_num)){
            mView!!.edtBuilding.requestFocus()
            mView!!.edtBuilding.error="Please enter building number for your location"
//            LogUtils.shortToast(requireContext(), "Please enter building number for your location")
        }
        else{
            addLocations()
        }


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMinZoomPreference(12F)
        mMap.mapType =GoogleMap.MAP_TYPE_NORMAL
        mMap.uiSettings.isZoomGesturesEnabled=true
        mMap.uiSettings.isCompassEnabled=true
        mMap.uiSettings.isScrollGesturesEnabled=true

//       mMap.setOnCameraMoveStartedListener {
//            mView.google_map.parent.requestDisallowInterceptTouchEvent(true)
//
//        }
//
//        mMap.setOnCameraIdleListener {
//            mView.google_map.parent.requestDisallowInterceptTouchEvent(false)
//
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED) {
//                buildGoogleApiClient()
                mMap.setMyLocationEnabled(true)
                mMap.isMyLocationEnabled = true;
                mMap.uiSettings.isMapToolbarEnabled = true;
                mMap.uiSettings.isMyLocationButtonEnabled = true;
                requestLocation()
            }
        } else {
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMapToolbarEnabled = true;
            mMap.uiSettings.isMyLocationButtonEnabled = true;
            requestLocation()
        }

        val coordinates = LatLng(-34.toDouble(), 151.toDouble())
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15f))
        mView!!.mapView.onResume()

        if(!TextUtils.isEmpty(responseBody)){
            editLocation()
        }
        else{
            requestLocation()
        }
    }


    fun fetchCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
            // Got last known location. In some rare situations this can be null.
            // 3
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }
    }

    private fun callPlaceAutocompleteActivityIntent() {
        Places.initialize(requireContext(), getString(R.string.my_key))
        try {
            val fields = Arrays.asList(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.ADDRESS,
                    Place.Field.LAT_LNG)
            // Start the autocomplete intent.
            val intent = Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(requireContext())
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
        } catch (e: Exception) {
            LogUtils.e("error", e.message)
            // TODO: Handle the error.
        }
    }

    fun requestLocation() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            displayLocationSettingsRequest(requireActivity())
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CODE)
        }
    }

    private fun displayLocationSettingsRequest(context: Context) {
        val googleApiClient = GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build()
        googleApiClient.connect()
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 10000 / 2.toLong()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback { result ->
            val status = result.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> {
                    Log.i(TAG, "All location settings are satisfied.")
                    val client = LocationServices.getSettingsClient(requireContext())
                    val task = client.checkLocationSettings(builder.build())
                    task.addOnSuccessListener(requireActivity()) {it->
                        it.locationSettingsStates
                        fetchCurrentLocation()
                    }

                    task.addOnFailureListener(requireActivity()) { e ->
                        if (e is ResolvableApiException) {
                            // Location settings are not satisfied, but this can be fixed
                            // by showing the user a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                e.startResolutionForResult(
                                    requireActivity(),
                                    REQUEST_CHECK_SETTINGS
                                )
                            } catch (sendEx: IntentSender.SendIntentException) {
                                // Ignore the error.
                            }

                        }
                    }
                }
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ")
                    try {
                        startIntentSenderForResult(status.resolution?.intentSender, REQUEST_CHECK_SETTINGS, null, 0, 0, 0, null)

                    } catch (e: IntentSender.SendIntentException) {
                        Log.i(TAG, "PendingIntent unable to execute request.")
                    }
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.")

                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==REQUEST_CHECK_SETTINGS ){
            if(resultCode == Activity.RESULT_OK){
               // getSmartLocation()
            }
        }
        else if(requestCode==RequestPermissionsSettings ) {
            requestLocation()
        }
    }

    private fun goToSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        startActivityForResult(intent, RequestPermissionsSettings)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.size > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        displayLocationSettingsRequest(requireActivity())
                        mMap.isMyLocationEnabled = true
//                        getSmartLocation()
//                        getLocationFromLocationManager()
                    }
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Location Permission Required")
                    builder.setMessage("Please enable  location permissions in settings")
                    builder.setPositiveButton(R.string.settings) { dialog, which ->
                        dialog.cancel()
                        goToSettings()
                    }
                    builder.setNegativeButton(R.string.cancel) { dialog, which ->
                        dialog.cancel()
                    }
                    builder.show()

                }


            }
        }

    }
    private fun addLocations() {

        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView!!.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("address", "title", "country", "city", "street", "apartmant_no", "building_no", "set_as_default", "type", "location_id", "latitude", "longitude", "user_id",  "lang"),
            arrayOf(address, title, country, city, street, apartment_num, building_num, isDefault.toString(), type, location_id.toString(), latitude.toString(), longitude.toString()
                , SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))




        val call = apiInterface.addLocations(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getInt("response") == 1) {
//                            LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
                            findNavController().popBackStack()
                        }

                        else {
                            LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, throwable: Throwable) {
                LogUtils.e("msg", throwable.message)
                LogUtils.shortToast(requireContext(), getString(R.string.check_internet))
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })

    }

    override fun onResume() {
        super.onResume()
        /* requireActivity().backImg.visibility=View.GONE*/
        requireActivity().frag_other_toolbar.visibility=View.VISIBLE
        requireActivity().home_frag_categories.visibility = View.GONE
        requireActivity().toolbar.visibility=View.GONE
        requireActivity().profile_fragment_toolbar.visibility=View.GONE
        requireActivity().supplier_fragment_toolbar.visibility=View.GONE
        requireActivity().about_us_fragment_toolbar.visibility=View.GONE

    }
    override fun onDestroy() {
        super.onDestroy()
//        requireActivity().backImg.visibility=View.VISIBLE
        requireActivity().frag_other_toolbar.visibility=View.VISIBLE
        requireActivity().toolbar.visibility=View.GONE
        requireActivity().profile_fragment_toolbar.visibility=View.GONE
        requireActivity().supplier_fragment_toolbar.visibility=View.GONE
        requireActivity().about_us_fragment_toolbar.visibility=View.GONE
    }

    override fun onStop() {
        super.onStop()
//        requireActivity().backImg.visibility=View.VISIBLE
        requireActivity().frag_other_toolbar.visibility=View.VISIBLE
        requireActivity().toolbar.visibility=View.GONE
        requireActivity().profile_fragment_toolbar.visibility=View.GONE
        requireActivity().supplier_fragment_toolbar.visibility=View.GONE
        requireActivity().about_us_fragment_toolbar.visibility=View.GONE

    }
}