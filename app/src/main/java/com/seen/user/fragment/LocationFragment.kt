package com.seen.user.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.seen.user.model.CountriesItem
import com.seen.user.model.CountryListResponse
import com.seen.user.utils.LogUtils
import com.seen.user.utils.Utility
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_location.view.*
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.ArrayList
import android.content.Context
import android.os.Looper
import com.seen.user.R
import com.google.android.gms.location.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LocationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LocationFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnCameraIdleListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lastLocation: Location?=null
    private var current_latitude = 0.0
    private var current_longitude = 0.0
    private var strAddress = ""
    private var strCity = ""
    private var flat_villa: String ?=null
    private var building_name : String?=null
    private var street_area : String?=null
    private var countryId : Int?=null
    private var countryName : String?=null
    private var mView : View ?=null
    var countryList = ArrayList<CountriesItem>()
    var countryNameList = ArrayList<String>()
    var selected_country_id = 0
    var selected_country_name = ""


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
        // Inflate the layout for this fragment
        mView = inflater.inflate(com.seen.user.R.layout.fragment_location, container, false)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        var mapViewBundle : Bundle? = null
        if (savedInstanceState!=null){
            mapViewBundle = savedInstanceState.getBundle(getString(R.string.google_maps_api_key))
        }

        mView!!.mapView.onCreate(mapViewBundle)
        mView!!.mapView.getMapAsync(this)

        getCountries()
    }

    fun fetchCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }else{
            try {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return
                }else{
                    fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
                        lastLocation = location
                        val currentLatLng = LatLng(lastLocation!!.latitude, lastLocation!!.longitude)
                        gMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                        mView!!.mapView.onResume()
                        gMap!!.setOnCameraIdleListener(this)
                    }
                }
            }catch (e : Exception){
                Log.e("exception", e.message.toString())
            }
        }
    }

    companion object {
        private var gMap : GoogleMap?=null

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LocationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        gMap!!.setMinZoomPreference(12F)
        gMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
        gMap!!.uiSettings.isCompassEnabled = true
        gMap!!.uiSettings.isMapToolbarEnabled = true
        gMap!!.uiSettings.isMyLocationButtonEnabled = true
        gMap!!.uiSettings.isScrollGesturesEnabled = true
        gMap!!.uiSettings.isScrollGesturesEnabledDuringRotateOrZoom = true
        fetchCurrentLocation()
    }

    override fun onCameraIdle() {
        val mapLatLng = gMap!!.cameraPosition.target
        setAddress(mapLatLng)
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
                    Log.e("tesr", "" + strAddress)
                }
                if (addressList[0].locality != null) {
                    flat_villa = addressList[0].featureName
                    building_name = addressList[0].featureName
                    var abc = addressList[0].adminArea + addressList[0].subAdminArea
                    var def = addressList[0].premises
                    var ghi = addressList[0].thoroughfare + addressList[0].subThoroughfare
                    street_area = addressList[0].subLocality +  " " + addressList[0].locality
                    countryName = addressList[0].countryName
                    Log.e("flat_villa", "" + flat_villa)
                    Log.e("country", "" + addressList[0].countryName)
                    Log.e("street_area", "" + street_area)
                    Log.e("countryId", "" + countryId)
                    Log.e("abc", "" + abc)
                    Log.e("def", "" + def)
                    Log.e("ghi", "" + ghi)
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
                    mView!!.mtv_location_address.text = strAddress
                }else{
                    strAddress = ""
                    mView!!.mtv_location_address.text = strAddress
                }
            }else{
                Log.e("err1", "err1")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        for(i in 0 until countryList.size){
            countryNameList.add(countryList[i].country_name!!)
        }

        mView!!.mtv_location_submit.setOnClickListener {
            if (countryNameList.contains(countryName)){
                val bundle = Bundle()
                returnCountryId(countryName!!,countryList)?.let { it1 ->
                    bundle.putInt("country_id",
                        it1
                    )
                }
                findNavController().navigate(R.id.filteredproductsfragment, bundle)
            }else{
                LogUtils.shortToast(requireContext(), "Country Not found!!!!!")
            }
        }
    }

    private fun returnCountryId(selectedCountry: String, countryList: ArrayList<CountriesItem>): Int? {
        for (country : CountriesItem in countryList) {
            if (country.country_name.equals(selectedCountry)) {
                return country.id
            }
        }
        return null
    }

    private fun getCountries() {
        val call = Utility.apiInterface.getCountriesList()
        call!!.enqueue(object : Callback<CountryListResponse?> {
            override fun onResponse(call: Call<CountryListResponse?>, response: Response<CountryListResponse?>) {
                try {
                    if (response.body() != null) {
                        countryList = response.body()!!.countries as ArrayList<CountriesItem>
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


    override fun onResume() {
        super.onResume()
        requireActivity().frag_other_toolbar.visibility=View.GONE
        requireActivity().home_frag_categories.visibility = View.VISIBLE
        requireActivity().toolbar.visibility=View.VISIBLE
        requireActivity().profile_fragment_toolbar.visibility=View.GONE
        requireActivity().supplier_fragment_toolbar.visibility=View.GONE
        requireActivity().about_us_fragment_toolbar.visibility=View.GONE

    }
    override fun onDestroy() {
        super.onDestroy()
//        requireActivity().backImg.visibility=View.VISIBLE
        requireActivity().frag_other_toolbar.visibility=View.GONE
        requireActivity().toolbar.visibility=View.GONE
        requireActivity().profile_fragment_toolbar.visibility=View.GONE
        requireActivity().supplier_fragment_toolbar.visibility=View.GONE
        requireActivity().home_frag_categories.visibility = View.GONE
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
        requireActivity().home_frag_categories.visibility = View.GONE

    }

}