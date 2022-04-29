package com.seen.user.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.seen.user.R
import com.seen.user.utils.SharedPreferenceUtility
import com.seen.user.utils.Utility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_web_view.view.*
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
            Utility.changeLanguage(
                requireContext(),
                SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
            )
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
                if(progress == 100){
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
}