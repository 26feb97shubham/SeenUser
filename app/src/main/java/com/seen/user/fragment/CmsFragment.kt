package com.seen.user.fragment

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.seen.user.R
import com.seen.user.extra.MyWebViewClient
import com.seen.user.rest.ApiClient
import com.seen.user.rest.ApiInterface
import com.seen.user.utils.LogUtils
import com.seen.user.utils.SharedPreferenceUtility
import com.seen.user.utils.Utility
import kotlinx.android.synthetic.main.about_us_more_info_frag_toolbar.view.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.frag_other_backImg
import kotlinx.android.synthetic.main.activity_terms_and_conditions.*
import kotlinx.android.synthetic.main.fragment_cms.view.*
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
 * Use the [CmsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CmsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mView: View
    var title:String=""
    private var isRedirected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString("title", "")

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_cms, container, false)
        setUpViews()
        getCmsContent()
        return mView
    }

    private fun setUpViews() {
        requireActivity().frag_other_backImg.visibility= View.VISIBLE
        requireActivity().frag_other_backImg.setOnClickListener {
            requireActivity().frag_other_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(
                requireContext(),
                requireActivity().frag_other_backImg
            )
            findNavController().popBackStack()
        }

        requireActivity().about_us_fragment_toolbar.frag_about_us_backImg.setOnClickListener {
            requireActivity().about_us_fragment_toolbar.frag_about_us_backImg.startAnimation(
                AlphaAnimation(
                    1f,
                    0.5f
                )
            )
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(
                requireContext(),
                requireActivity().about_us_fragment_toolbar.frag_about_us_backImg
            )
            findNavController().popBackStack()
        }

        mView.webView.webViewClient = object : WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                mView.progressBar.visibility= View.GONE
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                mView.progressBar.visibility= View.GONE
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return super.shouldOverrideUrlLoading(view, url)
                view!!.loadUrl(url!!)
            }
        }
        mView.webView.settings.javaScriptEnabled = true
        mView.webView.settings.setSupportZoom(true)
        mView.webView.getSettings().setBuiltInZoomControls(true)
        //Enable Multitouch if supported by ROM
        mView.webView.getSettings().setUseWideViewPort(true)
        mView.webView.getSettings().setLoadWithOverviewMode(false)
        mView.webView.setBackgroundColor(Color.TRANSPARENT)
        mView.webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
    }
    private fun getCmsContent() {
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        mView.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(
            arrayOf("lang"),
            arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString())
        )

        val call: Call<ResponseBody?>? = when(title){
            getString(R.string.terms_amp_conditions) -> {
                requireActivity().mainView.setBackgroundResource(R.drawable.terms_background)
                requireActivity().about_us_fragment_toolbar.visibility=View.GONE
                mView!!.rvList.visibility = View.GONE
                mView!!.webView.visibility = View.VISIBLE
                apiInterface.getTermsConditions(builder.build())
            }
            getString(R.string.privacy_and_policy) -> {
                requireActivity().mainView.setBackgroundResource(R.drawable.data_policy_bg)
                requireActivity().about_us_fragment_toolbar.visibility=View.GONE
                mView.rvList.visibility = View.GONE
                mView.webView.visibility = View.VISIBLE
                apiInterface.getPrivacyPolicy(builder.build())
            }
            else -> {
                requireActivity().mainView.setBackgroundResource(R.drawable.about_us_bg)
                requireActivity().about_us_fragment_toolbar.visibility=View.VISIBLE
                mView!!.rvList.visibility = View.VISIBLE
                mView!!.webView.visibility = View.GONE
                apiInterface.getAboutUs(builder.build())
            }
        }

        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getInt("response") == 1) {
                            val data = jsonObject.getJSONObject("data")
                            mView.webView.loadUrl(data.getString("url"))
                            mView.txt.text = HtmlCompat.fromHtml(data.getString("content"), 0)
                        } else {
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CmsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                CmsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun onResume() {
        super.onResume()
        if (mView.header.text.equals(getString(R.string.about_us))){
            requireActivity().frag_other_toolbar.visibility=View.GONE
            requireActivity().toolbar.visibility=View.GONE
            requireActivity().profile_fragment_toolbar.visibility=View.GONE
            requireActivity().about_us_fragment_toolbar.visibility=View.VISIBLE
            requireActivity().home_frag_categories.visibility=View.GONE
        }else{
            requireActivity().frag_other_toolbar.visibility=View.VISIBLE
            requireActivity().toolbar.visibility=View.GONE
            requireActivity().profile_fragment_toolbar.visibility=View.GONE
            requireActivity().about_us_fragment_toolbar.visibility=View.GONE
            requireActivity().home_frag_categories.visibility=View.GONE
        }
        requireActivity().home_frag_categories.visibility = View.GONE
        requireActivity().supplier_fragment_toolbar.visibility=View.GONE
        Utility.changeLanguage(requireContext(),SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""])
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().mainView.setBackgroundColor(Color.parseColor("#FFFFFF"))
        requireActivity().frag_other_toolbar.visibility=View.VISIBLE
        requireActivity().toolbar.visibility=View.GONE
        requireActivity().profile_fragment_toolbar.visibility=View.GONE
        requireActivity().about_us_fragment_toolbar.visibility=View.GONE
        requireActivity().home_frag_categories.visibility = View.GONE
        requireActivity().supplier_fragment_toolbar.visibility=View.GONE
    }

    override fun onStop() {
        super.onStop()
        requireActivity().mainView.setBackgroundColor(Color.parseColor("#FFFFFF"))
        requireActivity().frag_other_toolbar.visibility=View.VISIBLE
        requireActivity().toolbar.visibility=View.GONE
        requireActivity().profile_fragment_toolbar.visibility=View.GONE
        requireActivity().about_us_fragment_toolbar.visibility=View.GONE
        requireActivity().home_frag_categories.visibility = View.GONE
        requireActivity().supplier_fragment_toolbar.visibility=View.GONE
    }

}