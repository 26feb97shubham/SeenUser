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
import com.seen.user.utils.Utility.Companion.payment_flag
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_tap_payment_gateway.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TapPaymentGatewayFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TapPaymentGatewayFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
                    var paymentConfirmedDialogFragment = PaymentConfirmedDialogFragment()
                    paymentConfirmedDialogFragment.isCancelable = false
                    paymentConfirmedDialogFragment.setDataCompletionCallback(object : PaymentConfirmedDialogFragment.CheckStatusInterface{
                        override fun checkStatus() {
                        }
                    })
//                    requireActivity().finishAffinity()
                    payment_flag = true
                    findNavController().navigate(R.id.myOrdersFragment)
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


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TapPaymentGatewayFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                TapPaymentGatewayFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}