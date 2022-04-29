package com.seen.user.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import com.seen.user.R
import com.seen.user.dialog.PaymentConfirmedDialogFragment
import com.seen.user.utils.SharedPreferenceUtility
import com.seen.user.utils.Utility
import com.seen.user.utils.Utility.Companion.payment_flag
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_tap_payment_gateway.view.*
class TapPaymentGatewayFragment : Fragment() {
    private var mView : View?=null
    private var paymentURL : String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            paymentURL = it.getString("url")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_tap_payment_gateway, container, false)
        Utility.changeLanguage(
            requireContext(),
            SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
        )
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mView!!.wv_payment_gateway.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) { //Make the bar disappear after URL is loaded, and changes string to Loading...
                mView!!.payment_gateway_progressBar.visibility= View.VISIBLE
                if(progress>=80){
                    mView!!.payment_gateway_progressBar.visibility= View.GONE
                }

            }
        }
        mView!!.wv_payment_gateway.webViewClient = object : WebViewClient(){
            override fun shouldOverrideUrlLoading(
                view: WebView, url: String?
            ): Boolean {
                val baseUrl = "https://seen-uae.com/"
                if(url!!.equals(baseUrl+"buyer-api/success")){
                    val paymentConfirmedDialogFragment = PaymentConfirmedDialogFragment()
                    paymentConfirmedDialogFragment.isCancelable = false
                    paymentConfirmedDialogFragment.setDataCompletionCallback(object : PaymentConfirmedDialogFragment.CheckStatusInterface{
                        override fun checkStatus() {
                        }
                    })
//                    requireActivity().finishAffinity()
                    payment_flag = true
                    val bundle = Bundle()
                    bundle.putInt("direction",1)
                    bundle.putString("type","")
                    findNavController().navigate(R.id.myOrdersFragment, bundle)
                    return true
                }else{
                    return false
                }
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }
        }
        mView!!.wv_payment_gateway.settings.javaScriptEnabled=true
        mView!!.wv_payment_gateway.settings.allowContentAccess=true
//        webView.settings.builtInZoomControls=true
        mView!!.wv_payment_gateway.settings.loadWithOverviewMode=true
        mView!!.wv_payment_gateway.settings.useWideViewPort=true
        mView!!.wv_payment_gateway.settings.loadsImagesAutomatically=true
        mView!!.wv_payment_gateway.loadUrl(paymentURL.toString())
    }

    override fun onResume() {
        super.onResume()
        Utility.changeLanguage(
            requireContext(),
            SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
        )
        /* requireActivity().backImg.visibility=View.GONE*/
        if (payment_flag){
            payment_flag = false
            findNavController().popBackStack()
        }

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