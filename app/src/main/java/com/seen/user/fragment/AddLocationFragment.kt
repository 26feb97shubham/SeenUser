package com.seen.user.fragment

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
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.animation.AlphaAnimation
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.seen.user.R
import com.seen.user.model.CountriesItem
import com.seen.user.model.CountryListResponse
import com.seen.user.rest.ApiClient
import com.seen.user.rest.ApiInterface
import com.seen.user.utils.LogUtils
import com.seen.user.utils.SharedPreferenceUtility
import com.seen.user.utils.Utility
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.filter_bottom_sheet_dialog.view.*
import kotlinx.android.synthetic.main.fragment_add_location.*
import kotlinx.android.synthetic.main.fragment_add_location.view.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

class AddLocationFragment : Fragment(), OnMapReadyCallback{
    private var mView: View?=null
    private val TAG = "AddLocationFragment"
    private lateinit var mMap:GoogleMap
    private val LOCATION_PERMISSION_CODE = 500
    private val REQUEST_CHECK_SETTINGS = 900
    private val RequestPermissionsSettings = 500
    private val PLACE_AUTOCOMPLETE_REQUEST_CODE = 101
    var latitude:Double=0.0
    var longitude:Double=0.0
    private var addressMap = HashMap<String, String>()
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
    var myType:String=""
    var position:Int=-1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var lastLocation: Location
    var mGoogleApiClient: GoogleApiClient? = null
    private var current_latitude = 0.0
    private var current_longitude = 0.0
    private var strAddress = ""
    private var strCity = ""
    private var str_City = ""
    private var flat_villa: String ?=null
    private var building_name : String?=null
    private var street_area : String?=null
    private var countryList = ArrayList<CountriesItem>()
    private var countryId : Int?=null
    private var countryName : String?=null
    private var mapLtLng : LatLng?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            responseBody = it.getString("responseBody", "")
            myType = it.getString("type", "")
            position = it.getInt("position", -1)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_add_location, container, false)
        Utility.changeLanguage(
            requireContext(),
            SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
        )
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

        getCountries()


        mView!!.mapView.onCreate(mapViewBundle)
        mView!!.mapView.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if(myType=="Edit"){
            editLocation()
        }else{
            fetchCurrentLocation()
        }


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
            mView!!.edtTitle.error=requireContext().getString(R.string.please_enter_title_for_your_location)
//            LogUtils.shortToast(requireContext(), "Please enter title for your location")
        }
        else if(TextUtils.isEmpty(country)){
            mView!!.edtCountry.requestFocus()
            mView!!.edtCountry.error=requireContext().getString(R.string.please_enter_country_name_for_your_location)
//            LogUtils.shortToast(requireContext(), "Please enter country name for your location")
        }
        else if(TextUtils.isEmpty(city)){
            mView!!.edtCity.requestFocus()
            mView!!.edtCity.error=requireContext().getString(R.string.please_enter_city_name_for_your_location)
//            LogUtils.shortToast(requireContext(), "Please enter city name for your location")
        }
        else if(TextUtils.isEmpty(street)){
            mView!!.edtStreet.requestFocus()
            mView!!.edtStreet.error=requireContext().getString(R.string.please_enter_street_name_for_your_location)
//            LogUtils.shortToast(requireContext(), "Please enter street name for your location")
        }
        else if(TextUtils.isEmpty(apartment_num)){
            mView!!.edtApartment.requestFocus()
            mView!!.edtApartment.error=requireContext().getString(R.string.please_enter_apartment_number_for_your_location)
//            LogUtils.shortToast(requireContext(), "Please enter apartment number for your location")
        }
        else if(TextUtils.isEmpty(building_num)){
            mView!!.edtBuilding.requestFocus()
            mView!!.edtBuilding.error=requireContext().getString(R.string.please_enter_building_number_for_your_location)
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
        mMap.uiSettings.isScrollGesturesEnabledDuringRotateOrZoom = true

        if(!TextUtils.isEmpty(responseBody)){
            editLocation()
            mMap.clear()
            val LatLng = LatLng(latitude, longitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng))
        }
        else{
            fetchCurrentLocation()
        }
        mView!!.mapView.onResume()

        mMap.setOnCameraIdleListener {
            val mapLatLng = mMap.cameraPosition.target
            mapLtLng = mapLatLng
            setAddress(mapLtLng)
        }

/*        val coordinates = LatLng(-34.toDouble(), 151.toDouble())
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15f))
        mView!!.mapView.onResume()

        mMap.setOnCameraIdleListener {
            val mapLatLng = mMap.cameraPosition.target
            mapLtLng = mapLatLng
            setAddress(mapLatLng)
        }

        if(!TextUtils.isEmpty(responseBody)){
            editLocation()
        }
        else{
            fetchCurrentLocation()
        }

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
        }*/

    }

    fun setAddress(maplatLng: LatLng?) {
        val geocoder = Geocoder(requireContext())
        try {
            val addressList = geocoder.getFromLocation(maplatLng!!.latitude, maplatLng.longitude, 1)
            if (addressList != null && addressList.size > 0) {
                val locality = addressList[0].getAddressLine(0)
                val country = addressList[0].countryName
                if (locality != null && country != null)
                {
                    current_latitude = addressList[0].latitude
                    current_longitude = addressList[0].longitude
                    Log.e("cureent_lati", current_latitude.toString())
                    Log.e("cureent_longni", current_longitude.toString())
                    strAddress = addressList[0].getAddressLine(0)
//                    flat_villa = addressList[0].getAddressLine(1)
                    Log.e("tesr", "" + strAddress)
//                    Log.e("flat_villa", "" + flat_villa)
                }
                if (addressList[0].locality != null) {
                    flat_villa = addressList[0].featureName
                    building_name = addressList[0].featureName
                    var abc = addressList[0].adminArea + addressList[0].subAdminArea
                    var def = addressList[0].premises
                    var ghi = addressList[0].thoroughfare + addressList[0].subThoroughfare



                    if (addressList[0].subLocality!=null){
                        street_area = addressList[0].subLocality +  " " + addressList[0].locality
                    }else{
                        street_area = addressList[0].locality
                    }

                    countryName = addressList[0].countryName
                    countryId = returnCountryId(countryName!!, countryList)

                    if(addressList[0].adminArea!=null){
                        Log.e("adminArea: ", addressList[0].adminArea.toString())
                    }else{
                        Log.e("adminArea: ", "null")
                    }

                    if(addressList[0].subAdminArea!=null){
                        Log.e("subAdminArea: ", addressList[0].subAdminArea.toString())
                    }else{
                        Log.e("subAdminArea: ", "null")
                    }

                    if(addressList[0].premises!=null){
                        Log.e("premises: ", addressList[0].premises.toString())
                    }else{
                        Log.e("premises: ", "null")
                    }

                    if(addressList[0].featureName!=null){
                        Log.e("featureName: ", addressList[0].featureName.toString())
                    }else{
                        Log.e("featureName: ", "null")
                    }

                    if(addressList[0].locality!=null){
                        Log.e("locality: ", addressList[0].locality.toString())
                    }else{
                        Log.e("locality: ", "null")
                    }

                    if(addressList[0].subLocality!=null){
                        Log.e("subLocality: ", addressList[0].subLocality.toString())
                    }else{
                        Log.e("subLocality: ", "null")
                    }

/*


                    Log.e("flat_villa", "" + flat_villa)
                    Log.e("country", "" + addressList[0].countryName)
                    Log.e("street_area", "" + street_area)
                    Log.e("countryId", "" + countryId)
                    Log.e("abc", "" + abc)
                    Log.e("def", "" + def)
                    Log.e("ghi", "" + ghi)*/

                    str_City = addressList[0].locality
                    street_area = addressList[0].subLocality
                    strCity = addressList[0].locality + addressList[0].countryCode + addressList[0].countryName
                    val addList = strAddress.split(",".toRegex()).toTypedArray()
//                    val addList = strAddress.split(",".toRegex(), 5)
                    Log.e("addList", "" + addList.toString())
                    strAddress = ""
                    for (s in addList) {
                        strAddress = if (strCity.equals(s.trim { it <= ' ' }, ignoreCase = true)) {
                            break
                        } else {
                            strAddress + s
                        }
                    }
                    Log.e("address ", strAddress)
//                    if(addressList[0].postalCode!=null) {
//                        addressMap.put("flat_villa", flat_villa!!)
//                        addressMap.put("street_area", street_area!!)
//                        addressMap.put("country", countryName!!)
//                        addressMap.put("countryId", countryId!!.toString())
//                    }
//                    else
//                    {
//                        addressMap.put("flat_villa", flat_villa!!)
//                        addressMap.put("street_area", street_area!!)
//                        addressMap.put("country", countryName!!)
//                    }

                    edtCountry.setText(countryName)
                    edtCity.setText(str_City)
                    edtStreet.setText(street_area)
                    edtApartment.setText(flat_villa)
                    edtBuilding.setText(flat_villa)
                }else{
                    strAddress = ""
                    edtCountry.setText("")
                    edtCity.setText("")
                    edtStreet.setText("")
                    edtApartment.setText("")
                    edtBuilding.setText("")
                }
            }else{
                Log.e("err1", "err1")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun returnCountryId(selectedCountry: String, countryList: ArrayList<CountriesItem>): Int? {
        for (country : CountriesItem in countryList) {
            if (country.country_name.equals(selectedCountry.lowercase(Locale.getDefault()))) {
                return country.id
            }
        }
        return null
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
//                        fetchCurrentLocation()
                        if(myType=="Edit"){
                            editLocation()
                        }else{
                            fetchCurrentLocation()
                        }
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
                    builder.setTitle(requireContext().getString(R.string.location_permission_required))
                    builder.setMessage(requireContext().getString(R.string.please_enable_location_permissions_from_settings))
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
        if (strAddress.equals("")){
            address = apartment_num+" " + building_num + " " + street + " " + city + " " +country
        }else{
            address = strAddress
        }

        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView!!.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("address", "title", "country", "city", "street", "apartmant_no", "building_no", "set_as_default", "type", "location_id", "latitude", "longitude", "user_id",  "lang"),
            arrayOf(address, title, country, city, street, apartment_num, building_num, isDefault.toString(), type, location_id.toString(), mapLtLng!!.latitude.toString(), mapLtLng!!.longitude.toString()
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
                            findNavController().navigate(R.id.myLocationFragment)
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
        Utility.changeLanguage(
            requireContext(),
            SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
        )
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


    private fun getCountries() {
        val call = Utility.apiInterface.getCountriesList()
        call!!.enqueue(object : Callback<CountryListResponse?> {
            override fun onResponse(call: Call<CountryListResponse?>, response: Response<CountryListResponse?>) {
                try {
                    if (response.body() != null) {
                        countryList = response.body()!!.countries as ArrayList<CountriesItem>
//                        txtCountryCode.text=country_code[0]
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<CountryListResponse?>, throwable: Throwable) {
                LogUtils.e("msg", throwable.message)
            }
        })
    }
}