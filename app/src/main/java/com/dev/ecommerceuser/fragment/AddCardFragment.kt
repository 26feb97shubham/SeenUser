package com.dev.ecommerceuser.fragment

import android.R.attr.editable
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dev.ecommerceuser.R
import com.dev.ecommerceuser.extra.AsteriskPasswordTransformationMethod
import com.dev.ecommerceuser.rest.ApiClient
import com.dev.ecommerceuser.rest.ApiInterface
import com.dev.ecommerceuser.utils.LogUtils
import com.dev.ecommerceuser.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_addcard.view.*
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
 * Use the [AddCardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddCardFragment : Fragment() {
    lateinit var mView: View
    var cardNumber:String=""
    var expiryMonth:String=""
    var expiryYear:String=""
    var expiryDate : String = ""
    var cvv:String=""
    var brand:String=""
    var isDefault:Int=0
    var cardId:Int=0
    var type:String="1"
    var pos:Int=0
    var responseBody:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getString("type", "1")
            pos = it.getInt("pos", -1)
            responseBody = it.getString("responseBody", "")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_addcard, container, false)
        setUpViews()
        return mView
    }

    private fun setUpViews() {
        requireActivity().home_frag_categories.visibility = View.GONE
        requireActivity().frag_other_backImg.visibility=View.VISIBLE

        requireActivity().frag_other_backImg.setOnClickListener {
            requireActivity().frag_other_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().frag_other_backImg)
            findNavController().popBackStack()
        }

        mView.et_expiry.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                if (editable!!.length > 0 && editable.length == 3) {
                    val c: Char = editable[editable.length-1]
                    if ('/' == c) {
                        editable.delete(editable.length - 1, editable.length)
                    }
                }
                if (editable.length > 0 && editable.length == 3) {
                    val c: Char = editable[editable.length-1]
                    if (Character.isDigit(c) && TextUtils.split(editable.toString(), "/").size <= 2) {
                        editable.insert(editable.length - 1, "/")
                    }
                }
            }
        })
        mView.et_cvv.transformationMethod = AsteriskPasswordTransformationMethod()

        mView.toggle_remember_card.setOnCheckedChangeListener{ compoundButton: CompoundButton, cheked: Boolean ->
            isDefault = if(cheked){
                1
            } else{
                0
            }
        }
        mView.btnSaveCard.setOnClickListener {
            mView.btnSaveCard.startAnimation(AlphaAnimation(1f, .5f))
            validateAndSave()
        }
//        mView.cardInputWidget.postalCodeEnabled=false

        if(type=="2"){
            mView.header.text=getString(R.string.edit_card)
            editCard()
        }
    }

    private fun editCard() {
        val jsonObject = JSONObject(responseBody)
        if (jsonObject.getInt("response") == 1){
            val cards=jsonObject.getJSONArray("cards")
            for(i in 0  until cards.length()){
                if(i==pos) {
                    val obj = cards.getJSONObject(i)
                    cardId = obj.getInt("id")
                    isDefault= obj.getInt("set_as_default")
                    cardNumber = obj.getString("card_number")
                    cvv = obj.getString("cvv")
                    expiryMonth = obj.getString("expiry").split("/")[0]
                    expiryYear = obj.getString("expiry").split("/")[1]
                   /* mView.cardInputWidget.setCardNumber(cardNumber)
                    mView.cardInputWidget.setCvcCode(cvv)
                    mView.cardInputWidget.setExpiryDate(expiryMonth.toInt(), expiryYear.toInt())*/
                    mView.toggle_remember_card.isChecked = isDefault==1

                }
            }

        }

    }

    private fun validateAndSave(){
        cardNumber = mView.et_card_number.text.toString().trim()
        expiryDate = mView.et_expiry.text.toString().trim()
        cvv = mView.et_cvv.text.toString().trim()

        if (TextUtils.isEmpty(cardNumber)){
            mView.et_card_number.requestFocus()
            mView.et_card_number.error = getString(R.string.please_enter_card_number)
        }else if (cardNumber.length<16){
            mView.et_card_number.requestFocus()
            mView.et_card_number.error = getString(R.string.please_enter_valid_card_number)
        }else if (TextUtils.isEmpty(expiryDate)){
            mView.et_expiry.requestFocus()
            mView.et_expiry.error = getString(R.string.please_enter_expiry_date)
        }else if(expiryDate.length<7){
            mView.et_expiry.requestFocus()
            mView.et_expiry.error = getString(R.string.please_enter_valid_expiry_date)
        }else if (TextUtils.isEmpty(cvv)){
            mView.et_cvv.requestFocus()
            mView.et_cvv.error = getString(R.string.please_enter_cvv)
        }else if (cvv.length<3){
            mView.et_cvv.requestFocus()
            mView.et_cvv.error = getString(R.string.please_enter_valid_cvv)
        }else {
            addCards()
        }

       /* if(mView.cardInputWidget.card==null){
            LogUtils.shortToast(requireContext(), getString(R.string.please_enter_card_details))
        }
        else{
            cardNumber= mView.cardInputWidget.card?.number.toString()
            expiryMonth= mView.cardInputWidget.card?.expMonth.toString()
            expiryYear= mView.cardInputWidget.card?.expYear.toString()
            cvv= mView.cardInputWidget.card?.cvc.toString()
            brand= mView.cardInputWidget.card?.brand.toString()
            addCards()
        }*/

    }
    private fun addCards() {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView.frag_add_card_progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("card_number", "expiry", "cvv", "set_as_default", "type", "card_id", "user_id", "lang"),
                arrayOf(cardNumber, expiryDate, cvv, isDefault.toString(), type, cardId.toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString()))

        val call = apiInterface.addCards(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView.frag_add_card_progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getInt("response") == 1) {
//                            LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
                            findNavController().popBackStack()
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
                mView.frag_add_card_progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })

    }

    override fun onResume() {
        super.onResume()
        /* requireActivity().backImg.visibility=View.GONE*/
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
         * @return A new instance of fragment AddcardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                AddCardFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}