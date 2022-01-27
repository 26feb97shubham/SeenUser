package com.seen.user.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.seen.user.R
import com.seen.user.model.GlobalMarkets
import com.seen.user.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_global_market.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GlobalMarketFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GlobalMarketFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var mView: View?=null
    var globalMarketList=ArrayList<GlobalMarkets>()

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
            mView = inflater.inflate(R.layout.fragment_global_market, container, false)
            setUpViews()
            //getGlobalMarket(false)
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

        mView!!.card_uae.setOnClickListener {
            mView!!.card_uae.startAnimation(AlphaAnimation(1f, 0.5f))
            val bundle=Bundle()
            bundle.putString("ref_key", "1")
            findNavController().navigate(R.id.action_globalMarketFragment_to_globalMarketDetailsFragment, bundle)
        }
        mView!!.card_saudi_arabia.setOnClickListener {
            mView!!.card_saudi_arabia.startAnimation(AlphaAnimation(1f, 0.5f))
            val bundle=Bundle()
            bundle.putString("ref_key", "2")
            findNavController().navigate(R.id.action_globalMarketFragment_to_globalMarketDetailsFragment, bundle)
        }

        mView!!.card_onam.setOnClickListener {
            mView!!.card_onam.startAnimation(AlphaAnimation(1f, 0.5f))
            val bundle=Bundle()
            bundle.putString("ref_key", "3")
            findNavController().navigate(R.id.action_globalMarketFragment_to_globalMarketDetailsFragment, bundle)
        }
        mView!!.card_kuwait.setOnClickListener {
            mView!!.card_kuwait.startAnimation(AlphaAnimation(1f, 0.5f))
            val bundle=Bundle()
            bundle.putString("ref_key", "4")
            findNavController().navigate(R.id.action_globalMarketFragment_to_globalMarketDetailsFragment, bundle)
        }

        mView!!.card_bahrain.setOnClickListener {
            mView!!.card_bahrain.startAnimation(AlphaAnimation(1f, 0.5f))
            val bundle=Bundle()
            bundle.putString("ref_key", "5")
            findNavController().navigate(R.id.action_globalMarketFragment_to_globalMarketDetailsFragment, bundle)
        }

        mView!!.card_qatar.setOnClickListener {
            mView!!.card_qatar.startAnimation(AlphaAnimation(1f, 0.5f))
            val bundle=Bundle()
            bundle.putString("ref_key", "6")
            findNavController().navigate(R.id.action_globalMarketFragment_to_globalMarketDetailsFragment, bundle)
        }

    }

    private fun setUpViews() {

        /*mView!!.swipeRefresh.setOnRefreshListener {
            getGlobalMarket(true)
        }

        mView!!.rvList.layoutManager=GridLayoutManager(requireContext(), 2)
        globalMarketAdapter= GlobalMarketAdapter(requireContext(), globalMarketList, object : ClickInterface.ClickPosInterface{
            override fun clickPostion(pos: Int) {
                val bundle=Bundle()
                bundle.putString("name", globalMarketList[pos].country_name)
                bundle.putString("flag", globalMarketList[pos].image)
                bundle.putInt("id", globalMarketList[pos].id)
                findNavController().navigate(R.id.action_globalMarketFragment_to_globalMarketDetailsFragment, bundle)
            }

        })
        mView!!.rvList.adapter=globalMarketAdapter*/

     /*   mView.llUAE.setOnClickListener {
            mView.llUAE.startAnimation(AlphaAnimation(1f, 0.5f))
            val bundle=Bundle()
            bundle.putString("ref_key", "1")
            findNavController().navigate(R.id.action_globalMarketFragment_to_globalMarketDetailsFragment, bundle)
        }
        mView.llSA.setOnClickListener {
            mView.llSA.startAnimation(AlphaAnimation(1f, 0.5f))
            val bundle=Bundle()
            bundle.putString("ref_key", "2")
            findNavController().navigate(R.id.action_globalMarketFragment_to_globalMarketDetailsFragment, bundle)
        }
        mView.llOman.setOnClickListener {
            mView.llOman.startAnimation(AlphaAnimation(1f, 0.5f))
            val bundle=Bundle()
            bundle.putString("ref_key", "3")
            findNavController().navigate(R.id.action_globalMarketFragment_to_globalMarketDetailsFragment, bundle)
        }
        mView.llKuwait.setOnClickListener {
            mView.llKuwait.startAnimation(AlphaAnimation(1f, 0.5f))
            val bundle=Bundle()
            bundle.putString("ref_key", "4")
            findNavController().navigate(R.id.action_globalMarketFragment_to_globalMarketDetailsFragment, bundle)
        }
        mView.llBahrain.setOnClickListener {
            mView.llBahrain.startAnimation(AlphaAnimation(1f, 0.5f))
            val bundle=Bundle()
            bundle.putString("ref_key", "5")
            findNavController().navigate(R.id.action_globalMarketFragment_to_globalMarketDetailsFragment, bundle)
        }*/

    }
    /*rivate fun getGlobalMarket(isRefresh: Boolean) {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        if(!isRefresh) {
            mView!!.progressBar.visibility = View.VISIBLE
        }

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))
        val call = apiInterface.getGlobalMarket(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
               *//* if(mView!!.swipeRefresh.isRefreshing){
                    mView!!.swipeRefresh.isRefreshing=false
                }*//*
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        val countries_to_be_served = jsonObject.getJSONArray("countries_to_be_served")
                        globalMarketList.clear()
                        if(countries_to_be_served.length() != 0) {
                            for (i in 0 until countries_to_be_served.length()) {
                                val jsonObj = countries_to_be_served.getJSONObject(i)
                                val g = GlobalMarkets()
                                g.country_name = jsonObj.getString("country_name")
                                g.id = jsonObj.getInt("id")
                                g.image = jsonObj.getString("image")
                                globalMarketList.add(g)

                            }
                        }

                        globalMarketAdapter.notifyDataSetChanged()

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
              *//*  if(mView!!.swipeRefresh.isRefreshing){
                    mView!!.swipeRefresh.isRefreshing=false
                }*//*
                LogUtils.e("msg", throwable.message)
                LogUtils.shortToast(requireContext(), getString(R.string.check_internet))
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })
    }*/
    override fun onResume() {
        super.onResume()
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GlobalMarketFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GlobalMarketFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}