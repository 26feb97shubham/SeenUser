package com.dev.ecommerceuser.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dev.ecommerceuser.R
import com.dev.ecommerceuser.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_web_view.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WebViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WebViewFragment : Fragment() {
    // TODO: Rename and change types of parameters
    var webUrl:String="https://www.privacypolicyonline.com/live.php?token=eVmpQy9Mb6H4HRFi7mderDrhJKaa6Bz6"
    var title:String=""
    var mView:View?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString("title", "")

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if(mView==null) {
            mView = inflater.inflate(R.layout.fragment_web_view, container, false)
            setUpViews()
        }
        return mView
    }
    private fun setUpViews() {
        requireActivity().frag_other_backImg.visibility = View.VISIBLE
        requireActivity().notificationImg.visibility = View.GONE
        requireActivity().menuImg.visibility = View.GONE

        requireActivity().frag_other_backImg.setOnClickListener {
            requireActivity().frag_other_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().frag_other_backImg)
            findNavController().popBackStack()
        }

        mView!!.header.text=title

        mView!!.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) { //Make the bar disappear after URL is loaded, and changes string to Loading...
                mView!!.progressBar.visibility= View.VISIBLE
                if(progress>=80){
                    mView!!.progressBar.visibility= View.GONE
                }

            }
        }
        mView!!.webView.settings.javaScriptEnabled=true
        mView!!.webView.settings.allowContentAccess=true
//        webView.settings.builtInZoomControls=true
        mView!!.webView.settings.loadWithOverviewMode=true
        mView!!.webView.settings.useWideViewPort=true
        mView!!.webView.settings.loadsImagesAutomatically=true
        mView!!.webView.loadUrl(webUrl)


    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WebViewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WebViewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}