package com.seen.user.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.seen.user.R
import com.seen.user.adapter.GlobalMarketAdapter
import com.seen.user.extra.SpaceItemDecoration
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.GlobalMarkets
import com.seen.user.rest.ApiClient
import com.seen.user.rest.ApiInterface
import com.seen.user.utils.LogUtils
import com.seen.user.utils.SharedPreferenceUtility
import com.seen.user.utils.Utility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_global_market.view.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class GlobalMarketFragment : Fragment() {
    var mView: View?=null
    var globalMarketList=ArrayList<GlobalMarkets>()
    private var globalMarketAdapter:GlobalMarketAdapter?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            mView = inflater.inflate(R.layout.fragment_global_market, container, false)
        Utility.changeLanguage(
            requireContext(),
            SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
        )
        getGlobalMarket()
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().frag_other_toolbar.visibility = View.VISIBLE
        requireActivity().frag_other_backImg.visibility=View.GONE
        requireActivity().home_frag_categories.visibility=View.VISIBLE
        requireActivity().frag_other_backImg.setOnClickListener {
            requireActivity().frag_other_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance()
                .hideSoftKeyBoard(requireContext(), requireActivity().frag_other_backImg)
            findNavController().navigate(R.id.homeFragment)
        }

        mView!!.firstFlagCard.setOnClickListener {
            mView!!.firstFlagCard.startAnimation(AlphaAnimation(1f, 0.5f))
            val name = mView!!.firstFlagName.text.trim()

            val ref_key = returnCountryId(name.toString(), globalMarketList)
            val flag = returnCountryFlag(name.toString(), globalMarketList)

            val bundle=Bundle()
            bundle.putString("ref_key", ref_key.toString())
            bundle.putString("name", name.toString())
            bundle.putString("flag", flag)
            findNavController().navigate(R.id.action_globalMarketFragment_to_globalMarketDetailsFragment, bundle)
        }

        mView!!.lastFlagCard.setOnClickListener {
            mView!!.lastFlagCard.startAnimation(AlphaAnimation(1f, 0.5f))
            val name = mView!!.lastFlagName.text.trim()
            val ref_key = returnCountryId(name.toString(), globalMarketList)
            val flag = returnCountryFlag(name.toString(), globalMarketList)

            val bundle=Bundle()
            bundle.putString("ref_key", ref_key.toString())
            bundle.putString("name", name.toString())
            bundle.putString("flag", flag)
            findNavController().navigate(R.id.action_globalMarketFragment_to_globalMarketDetailsFragment, bundle)
        }
    }

    private fun returnCountryId(name: String, globalMarketList: ArrayList<GlobalMarkets>): Int {
        var id = 0
        for (i in 0 until globalMarketList.size){
            if (name.equals(globalMarketList[i].country_name) || name.equals(globalMarketList[i].country_name_ar)){
                id = globalMarketList[i].id
                break
            }
        }
        return id
    }


    private fun returnCountryFlag(name: String, globalMarketList: ArrayList<GlobalMarkets>): String {
        var flag = ""
        for (i in 0 until globalMarketList.size){
            if (name.equals(globalMarketList[i].country_name) || name.equals(globalMarketList[i].country_name_ar)){
                flag = globalMarketList[i].image
                break
            }
        }
        return flag
    }

    private fun getGlobalMarket() {
        mView!!.progressBar.visibility = View.VISIBLE
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))
        val call = apiInterface.getGlobalMarket(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        val countries_to_be_served = jsonObject.getJSONArray("countries_to_be_served")
                        globalMarketList.clear()
                        if(countries_to_be_served.length() != 0) {
                            mView!!.flagsConstraintLayout.visibility = View.VISIBLE
                            mView!!.txtNoDataFound.visibility = View.GONE
                            for (i in 0 until countries_to_be_served.length()) {
                                val jsonObj = countries_to_be_served.getJSONObject(i)
                                val g = GlobalMarkets()
                                g.country_name = jsonObj.getString("country_name")
                                g.country_name_ar = jsonObj.getString("country_name_ar")

                                when {
                                    jsonObj.getString("country_name").equals("United Arab Emirates", ignoreCase = false) ||
                                            jsonObj.getString("country_name_ar").equals("الإمارات العربية المتحدة") -> {
                                        g.id = 1
                                    }
                                    jsonObj.getString("country_name").equals("Saudi Arabia", ignoreCase = false) ||
                                            jsonObj.getString("country_name_ar").equals("المملكة العربية السعودية") -> {
                                        g.id = 2
                                    }
                                    jsonObj.getString("country_name").equals("Oman",ignoreCase = false) ||
                                            jsonObj.getString("country_name_ar").equals("سلطنة عمان") -> {
                                        g.id = 3
                                    }
                                    jsonObj.getString("country_name").equals("Kuwait") ||
                                            jsonObj.getString("country_name_ar").equals("الكويت") -> {
                                        g.id = 4
                                    }
                                    jsonObj.getString("country_name").equals("Bahrain") ||
                                            jsonObj.getString("country_name_ar").equals("البحرين") -> {
                                        g.id = 5
                                    }
                                    jsonObj.getString("country_name").equals("Qatar") ||
                                            jsonObj.getString("country_name_ar").equals("دولة قطر") -> {
                                        g.id = 6
                                    }
                                    else -> {
                                        g.id = 0
                                    }
                                }
                                g.image = jsonObj.getString("image")
                                globalMarketList.add(g)
                            }
                            setFlagsData(globalMarketList)
                        }else{
                            mView!!.txtNoDataFound.visibility = View.VISIBLE
                            mView!!.flagsConstraintLayout.visibility = View.GONE
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
                mView!!.txtNoDataFound.visibility = View.VISIBLE
                mView!!.flagsConstraintLayout.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })
    }

    private fun setFlagsData(globalMarketList: ArrayList<GlobalMarkets>) {
        if (globalMarketList.size>2 && globalMarketList.size%2==0){
            mView!!.firstFlagCard.visibility=View.VISIBLE
            mView!!.lastFlagCard.visibility=View.VISIBLE
            mView!!.flagsRecyclerView.visibility = View.VISIBLE

            Glide.with(requireContext()).load(globalMarketList[0].image).into(mView!!.firstFlagImage)
            Glide.with(requireContext()).load(globalMarketList[globalMarketList.size-1].image).into(mView!!.lastFlagImage)
            if (SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "").equals("ar")){
                mView!!.firstFlagName.text = globalMarketList[0].country_name_ar
                mView!!.lastFlagName.text = globalMarketList[globalMarketList.size-1].country_name_ar
            }else{
                mView!!.firstFlagName.text = globalMarketList[0].country_name
                mView!!.lastFlagName.text = globalMarketList[globalMarketList.size-1].country_name
            }
            var newGlobalMarketList = ArrayList<GlobalMarkets>()
            for (i in 1 until globalMarketList.size-1){
                newGlobalMarketList.add(globalMarketList[i])
            }
            setFlagsAdapter(newGlobalMarketList)

        }else if (globalMarketList.size>2 && globalMarketList.size%2!=0){
            mView!!.firstFlagCard.visibility=View.VISIBLE
            mView!!.lastFlagCard.visibility=View.GONE
            mView!!.flagsRecyclerView.visibility = View.VISIBLE
            var newGlobalMarketList = ArrayList<GlobalMarkets>()
            for (i in 1 until globalMarketList.size){
                newGlobalMarketList.add(globalMarketList[i])
            }
            setFlagsAdapter(newGlobalMarketList)
        }else{
            mView!!.firstFlagCard.visibility=View.GONE
            mView!!.lastFlagCard.visibility=View.GONE
            mView!!.flagsRecyclerView.visibility = View.GONE
        }
    }

    private fun setFlagsAdapter(newGlobalMarketList: ArrayList<GlobalMarkets>) {
        mView!!.flagsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        globalMarketAdapter = GlobalMarketAdapter(requireContext(), newGlobalMarketList, object : ClickInterface.ClickPosInterface{
            override fun clickPostion(pos: Int, type: String) {
                val name = if(SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "").equals("ar")){
                    newGlobalMarketList[pos].country_name_ar
                }else{
                    newGlobalMarketList[pos].country_name
                }

                val ref_key = returnCountryId(name, globalMarketList)
                val flag = returnCountryFlag(name, globalMarketList)

                val bundle=Bundle()
                bundle.putString("ref_key", ref_key.toString())
                bundle.putString("name", name)
                bundle.putString("flag", flag)
                findNavController().navigate(R.id.action_globalMarketFragment_to_globalMarketDetailsFragment, bundle)
            }
        })
        mView!!.flagsRecyclerView.adapter = globalMarketAdapter
        mView!!.flagsRecyclerView.addItemDecoration(SpaceItemDecoration(10))
    }

    override fun onResume() {
        super.onResume()
        Utility.changeLanguage(
            requireContext(),
            SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
        )
        requireActivity().home_frag_categories.visibility=View.VISIBLE
        requireActivity().frag_other_toolbar.visibility=View.GONE
        requireActivity().supplier_fragment_toolbar.visibility=View.GONE
        requireActivity().toolbar.visibility=View.VISIBLE
        requireActivity().profile_fragment_toolbar.visibility=View.GONE
        requireActivity().about_us_fragment_toolbar.visibility=View.GONE
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