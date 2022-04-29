package com.seen.user.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arasthel.spannedgridlayoutmanager.SpanSize
import com.arasthel.spannedgridlayoutmanager.SpannedGridLayoutManager
import com.seen.user.R
import com.seen.user.adapter.GalleryListAdapter
import com.seen.user.interfaces.ClickInterface
import com.seen.user.model.Gallery
import com.seen.user.rest.ApiClient
import com.seen.user.rest.ApiInterface
import com.seen.user.utils.LogUtils
import com.seen.user.utils.SharedPreferenceUtility
import com.seen.user.utils.Utility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_gallery.view.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class GalleryFragment : Fragment() {
    lateinit var mView: View
    lateinit var galleryListAdapter: GalleryListAdapter
    var galleryList=ArrayList<Gallery>()
    var accessValue:Int=0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_gallery, container, false)
        Utility.changeLanguage(
            requireContext(),
            SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
        )
        setUpViews()
        getGallery(false)
        return mView
    }

    private fun setUpViews() {

        /*requireActivity().frag_other_backImg.visibility= View.VISIBLE
        requireActivity().frag_other_backImg.setOnClickListener {
            requireActivity().frag_other_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().frag_other_backImg)
            findNavController().popBackStack()
        }*/

        requireActivity().frag_other_backImg.visibility=View.VISIBLE
        requireActivity().frag_other_backImg.setOnClickListener {
            requireActivity().frag_other_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().frag_other_backImg)
            findNavController().popBackStack()
        }

        mView.swipeRefresh.setOnRefreshListener {
            getGallery(true)
        }

        /*mView.rvList.layoutManager = GridLayoutManager(activity, 3).also {
            it.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {

                    return if(position==0){
                        3
                    } else if (position == 4 || position==accessValue){
                        accessValue=position+6
                        2
                    } else{
                        1
                    }

                }
            }
        }*/


    }
    private fun getGallery(isRefresh: Boolean) {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        if(!isRefresh) {
            mView.progressBar.visibility = View.VISIBLE
        }

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))

        val call = apiInterface.getGallery(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if(mView.swipeRefresh.isRefreshing){
                    mView.swipeRefresh.isRefreshing=false
                }
                mView.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())

                        val gallerys = jsonObject.getJSONArray("gallerys")
                        galleryList.clear()
                        if(gallerys.length() != 0) {
                            mView.txtNoDataFound.visibility=View.GONE
                            mView.rvList.visibility=View.VISIBLE
                            for (i in 0 until gallerys.length()) {
                                val jsonObj = gallerys.getJSONObject(i)
                                val g = Gallery()
                                g.id = jsonObj.getInt("id")
                                g.files = jsonObj.getString("files")
                                g.files_path = jsonObj.getString("files_path")
                                g.extension = jsonObj.getString("extension")
                                g.thumbnail = jsonObj.getString("thumbnail")
                                galleryList.add(g)
                            }
                            val spannedGridLayoutManager = SpannedGridLayoutManager(
                                orientation = SpannedGridLayoutManager.Orientation.VERTICAL,
                                spans = 3)
                            spannedGridLayoutManager.spanSizeLookup = SpannedGridLayoutManager.SpanSizeLookup { position ->
                                when {
                                    position == 0 -> {
                                        /**
                                         * 150f is now static
                                         * should calculate programmatically in runtime
                                         * for to manage header hight for different resolution devices
                                         */
                                        SpanSize(3, 2)
                                    }
                                    position == 4 || position==accessValue ->{
                                        accessValue=position+6
                                        SpanSize(2, 2)
                                    }

                                    else ->
                                        SpanSize(1, 1)
                                }
                            }
                            mView.rvList.layoutManager =spannedGridLayoutManager

                            /*mView.rvList.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)*/
                            galleryListAdapter= GalleryListAdapter(requireContext(), galleryList, object : ClickInterface.ClickPosInterface {
                                override fun clickPostion(pos: Int, type : String) {
                                    val bundle=Bundle()
                                    bundle.putString("thumbnail", galleryList[pos].thumbnail)
                                    bundle.putString("files_path", galleryList[pos].files_path)
                                    bundle.putString("extension", galleryList[pos].extension)
                                    bundle.putString("files", galleryList[pos].files)
                                    findNavController().navigate(R.id.action_galleryFragment_to_playerViewFragment, bundle)
                                }

                            })

                            mView.rvList.adapter=galleryListAdapter
                            galleryListAdapter.notifyDataSetChanged()
                        }
                        else{
                            mView.txtNoDataFound.visibility=View.VISIBLE
                            mView.rvList.visibility=View.GONE
                        }

//                        galleryListAdapter.notifyDataSetChanged()


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
                if(mView.swipeRefresh.isRefreshing){
                    mView.swipeRefresh.isRefreshing=false
                }
                LogUtils.e("msg", throwable.message)
                LogUtils.shortToast(requireContext(), getString(R.string.check_internet))
                mView.progressBar.visibility = View.GONE
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