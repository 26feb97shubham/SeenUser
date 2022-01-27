package com.seen.user.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.seen.user.R
import com.seen.user.adapter.MyLocationsAdapter
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.MyLocations
import com.seen.user.rest.ApiClient
import com.seen.user.rest.ApiInterface
import com.seen.user.utils.LogUtils
import com.seen.user.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_my_location.view.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyLocationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyLocationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mView: View
    lateinit var locationAdapter: MyLocationsAdapter
    var locationList=ArrayList<MyLocations>()
    var responseBody:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_my_location, container, false)
        setUpViews()
        myLocations(false)
        return mView
    }

    private fun setUpViews() {
        requireActivity().frag_other_backImg.visibility=View.VISIBLE

        requireActivity().frag_other_backImg.setOnClickListener {
            requireActivity().frag_other_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().frag_other_backImg)
            findNavController().popBackStack()
        }
        mView.btnAddLoc.setOnClickListener {
            mView.btnAddLoc.startAnimation(AlphaAnimation(1f, 0.5f))
            findNavController().navigate(R.id.action_myLocationFragment_to_addLocationFragment)
        }

        mView.swipeRefresh.setOnRefreshListener {
            myLocations(true)
        }

        mView.rvList.layoutManager=LinearLayoutManager(requireContext())
        locationAdapter= MyLocationsAdapter(requireContext(), locationList, object : ClickInterface.ClickPosTypeInterface{
            override fun clickPostionType(pos: Int, type: String) {
                if(type=="Delete"){
                    deleteLocation(pos)
                }
                else if(type=="Default"){
                    makeDefaultLocation(pos)
                }
                else{
                    val args=Bundle()
                    args.putString("responseBody", responseBody)
                    args.putInt("position", pos)
                    findNavController().navigate(R.id.action_myLocationFragment_to_addLocationFragment, args)
                }
            }

        })
        mView.rvList.adapter=locationAdapter
    }
    private fun myLocations(isRefresh: Boolean) {

        if(!isRefresh){
            mView.progressBar.visibility= View.VISIBLE
        }
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)


        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))


        val call = apiInterface.myLocations(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView.progressBar.visibility= View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if(mView.swipeRefresh.isRefreshing){
                    mView.swipeRefresh.isRefreshing=false
                }
                try {
                    if (response.body() != null) {
                        responseBody=response.body()!!.string()
                            val jsonObject = JSONObject(responseBody)
                            if (jsonObject.getInt("response") == 1){
                                val locations=jsonObject.getJSONArray("locations")
                                locationList.clear()
                                mView.txtNoDataFound.visibility=View.GONE
                                mView.rvList.visibility=View.VISIBLE
                                var pos=-1
                                for(i in 0  until locations.length()){
                                    val obj = locations.getJSONObject(i)
                                    val m = MyLocations()
                                    m.id = obj.getInt("id")
                                    m.user_id = obj.getInt("user_id")
                                    m.set_as_default = obj.getInt("set_as_default")
                                    m.title = obj.getString("title")
                                    m.address = obj.getString("address")
                                    m.country = obj.getString("country")
                                    m.city = obj.getString("city")
                                    m.street = obj.getString("street")
                                    m.apartmant_no = obj.getString("apartmant_no")
                                    m.building_no = obj.getString("building_no")
                                    m.latitude = obj.getString("latitude")
                                    m.longitude = obj.getString("longitude")
                                    locationList.add(m)
                                    if(obj.getInt("set_as_default")==1){
                                        pos=i
                                    }
                                }
                                if(pos!=-1){
                                    SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.IsDefaultLoc, locationList[pos].address)
                                    SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.SavedLat, locationList[pos].latitude)
                                    SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.SavedLng, locationList[pos].longitude)
                                }


                            }

                        else {
                            locationList.clear()
                            mView.txtNoDataFound.visibility=View.VISIBLE
                            mView.rvList.visibility=View.GONE
//                            LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
                        }
                        locationAdapter.notifyDataSetChanged()
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
                mView.progressBar.visibility= View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if(mView.swipeRefresh.isRefreshing){
                    mView.swipeRefresh.isRefreshing=false
                }
            }
        })


    }
    private fun deleteLocation(pos: Int) {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("location_id", "user_id", "lang"),
                arrayOf(locationList[pos].id.toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))


        val call = apiInterface.deleteLocation(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())

                        if(jsonObject.getInt("response")==1){
                            locationAdapter.notifyItemRemoved(pos)
                            locationList.removeAt(pos)
                            locationAdapter.notifyDataSetChanged()
                        }
                        else{
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
                mView.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })
    }
    private fun makeDefaultLocation(pos: Int) {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("location_id", "user_id", "set_as_default", "lang"),
                arrayOf(locationList[pos].id.toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString()
                        , "1", SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))

        val call = apiInterface.makeDefaultLocation(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())

                        if(jsonObject.getInt("response")==1){
                            myLocations(false)
                        }
                        else{
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
                mView.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        requireActivity().home_frag_categories.visibility=View.GONE
        requireActivity().frag_other_toolbar.visibility=View.VISIBLE
        requireActivity().supplier_fragment_toolbar.visibility=View.GONE
        requireActivity().toolbar.visibility=View.GONE
        requireActivity().profile_fragment_toolbar.visibility=View.GONE
        requireActivity().about_us_fragment_toolbar.visibility=View.GONE
        requireActivity().home_frag_categories.visibility=View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().frag_other_toolbar.visibility=View.VISIBLE
        requireActivity().toolbar.visibility=View.GONE
        requireActivity().profile_fragment_toolbar.visibility=View.GONE
        requireActivity().about_us_fragment_toolbar.visibility=View.GONE
        requireActivity().home_frag_categories.visibility = View.GONE
        requireActivity().supplier_fragment_toolbar.visibility=View.GONE
    }

    override fun onStop() {
        super.onStop()
        requireActivity().frag_other_toolbar.visibility=View.VISIBLE
        requireActivity().toolbar.visibility=View.GONE
        requireActivity().profile_fragment_toolbar.visibility=View.GONE
        requireActivity().about_us_fragment_toolbar.visibility=View.GONE
        requireActivity().home_frag_categories.visibility = View.GONE
        requireActivity().supplier_fragment_toolbar.visibility=View.GONE
    }

}