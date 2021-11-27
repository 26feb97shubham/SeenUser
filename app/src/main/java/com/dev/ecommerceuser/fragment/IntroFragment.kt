package com.dev.ecommerceuser.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.dev.ecommerceuser.R
import com.dev.ecommerceuser.activity.ChooseLangActivity
import com.dev.ecommerceuser.activity.LoginActivity
import com.dev.ecommerceuser.activity.SignUpActivity
import com.dev.ecommerceuser.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.fragment_intro.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [IntroFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class IntroFragment(val position: Int) : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var mainView : LinearLayout? = null
    private var mainView2 : RelativeLayout? = null
    private var btnLogin: TextView?= null
    private var btnSignUp: TextView?= null
    private var selectLangFragment : SelectLangFragment?= null
    private var isFirstTime : Boolean = true
    private var isLangSelected : Boolean = false
    lateinit var mView: View

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
        mView = inflater.inflate(R.layout.fragment_intro, container, false)
        mainView = mView.findViewById(R.id.mainView)
        mainView2 = mView.findViewById(R.id.third_screen_layout)
        btnLogin = mView.findViewById(R.id.btnLogin)
        btnSignUp = mView.findViewById(R.id.btnSignUp)
        setUpViews()
        return mView

    }

    private fun setUpViews() {
       /* if(position==0){
            mView.img.setImageResource(R.drawable.welcome1)
            mView.txt1.setText(getString(R.string.lorem_ipsum_dolor_sit))
            mView.txt2.setText(getString(R.string.lorem_ipsum_is_simply_dummy_text_of_the_printing_and_typesetting_industry_lorem_ipsum_has_been_the_industry_s_standard))
        }
        else if(position==1){
            mView.img.setImageResource(R.drawable.welcome2)
            mView.txt1.setText(getString(R.string.lorem_ipsum_dolor_sit))
            mView.txt2.setText(getString(R.string.lorem_ipsum_is_simply_dummy_text_of_the_printing_and_typesetting_industry_lorem_ipsum_has_been_the_industry_s_standard))
        }
        if(position==2){
            mView.img.setImageResource(R.drawable.welcome3)
            mView.txt1.setText(getString(R.string.lorem_ipsum_dolor_sit))
            mView.txt2.setText(getString(R.string.lorem_ipsum_is_simply_dummy_text_of_the_printing_and_typesetting_industry_lorem_ipsum_has_been_the_industry_s_standard))
        }*/

        if(position==0){
            mainView!!.setBackgroundResource(R.drawable.welcome_slider_1)
            mainView2!!.visibility = View.GONE

            //Commented by Shubham Jain 13/08/2021
//            mView.txt1.setText(getString(R.string.lorem_ipsum_dolor_sit))
//            mView.txt2.setText(getString(R.string.lorem_ipsum_is_simply_dummy_text_of_the_printing_and_typesetting_industry_lorem_ipsum_has_been_the_industry_s_standard))
        }

        else if(position==1){
            mainView!!.setBackgroundResource(R.drawable.welcome_slider_2)
            mainView2!!.visibility = View.GONE
            //Commented by Shubham Jain 13/08/2021
//            mView.txt1.setText(getString(R.string.lorem_ipsum_dolor_sit))
//            mView.txt2.setText(getString(R.string.lorem_ipsum_is_simply_dummy_text_of_the_printing_and_typesetting_industry_lorem_ipsum_has_been_the_industry_s_standard))
        }

        if(position==2){
            mainView!!.setBackgroundResource(R.drawable.welcome_slider_3)
            mainView2!!.visibility = View.VISIBLE
            selectLangFragment = SelectLangFragment()

            btnLogin!!.setOnClickListener {
                isSelected = "Login"
                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.isSelected, isSelected)
                if(isFirstTime && !isLangSelected){
                    isFirstTime = false
   /*                 sharedPreferences!!.save("isFirstTime", isFirstTime)
                    sharedPreferences!!.save("isLangSelected", isLangSelected)*/
                    SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.isFirstTime, isFirstTime)
                    btnLogin!!.startAnimation(AlphaAnimation(1f, 0.5f))
                    startActivity(Intent(requireActivity(),ChooseLangActivity::class.java))
                }else if(!isFirstTime){
                    if(SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.isLangSelected)){
/*                        sharedPreferences!!.save("isFirstTime", isFirstTime)
                        sharedPreferences!!.save("isLangSelected", isLangSelected)*/
                        btnLogin!!.startAnimation(AlphaAnimation(1f, 0.5f))
                        startActivity(Intent(requireActivity(), LoginActivity::class.java))
                    }else{
/*                        sharedPreferences!!.save("isFirstTime", isFirstTime)
                        sharedPreferences!!.save("isLangSelected", isLangSelected)*/
                        btnLogin!!.startAnimation(AlphaAnimation(1f, 0.5f))
                        startActivity(Intent(requireActivity(),ChooseLangActivity::class.java))
                    }
                }else {
/*                    sharedPreferences!!.save("isFirstTime", isFirstTime)
                    sharedPreferences!!.save("isLangSelected", isLangSelected)*/
                    btnLogin!!.startAnimation(AlphaAnimation(1f, 0.5f))
                    startActivity(Intent(requireActivity(), LoginActivity::class.java))
                }

//                btnLogin!!.startAnimation(AlphaAnimation(1f, 0.5f))
//                startActivity(Intent(requireActivity(), LoginActivity::class.java))
            }
            btnSignUp!!.setOnClickListener {
//                btnSignUp!!.startAnimation(AlphaAnimation(1f, 0.5f))
//                startActivity(Intent(requireActivity(), SignUpActivity::class.java))
                isSelected = "Create Account"
                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.isSelected, isSelected)
                if(isFirstTime && !isLangSelected){
                    isFirstTime = false
/*                    sharedPreferences!!.save("isFirstTime", isFirstTime)
                    sharedPreferences!!.save("isLangSelected", isLangSelected)*/
                    SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.isFirstTime, isFirstTime)
                    btnLogin!!.startAnimation(AlphaAnimation(1f, 0.5f))
                    startActivity(Intent(requireActivity(),ChooseLangActivity::class.java))
                }else if(!isFirstTime){
                    if(SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.isLangSelected)){
        /*                sharedPreferences!!.save("isFirstTime", isFirstTime)
                        sharedPreferences!!.save("isLangSelected", isLangSelected)*/
                        btnLogin!!.startAnimation(AlphaAnimation(1f, 0.5f))
                        startActivity(Intent(requireActivity(), SignUpActivity::class.java))
                    }else{
/*                        sharedPreferences!!.save("isFirstTime", isFirstTime)
                        sharedPreferences!!.save("isLangSelected", isLangSelected)*/
                        btnLogin!!.startAnimation(AlphaAnimation(1f, 0.5f))
                        startActivity(Intent(requireActivity(), ChooseLangActivity::class.java))
                    }
                }else {
/*                    sharedPreferences!!.save("isFirstTime", isFirstTime)
                    sharedPreferences!!.save("isLangSelected", isLangSelected)*/
                    btnLogin!!.startAnimation(AlphaAnimation(1f, 0.5f))
                    startActivity(Intent(requireActivity(), SignUpActivity::class.java))
                }

            }

            //Commented by Shubham Jain 13/08/2021
//            mView.txt1.setText(getString(R.string.lorem_ipsum_dolor_sit))
//            mView.txt2.setText(getString(R.string.lorem_ipsum_is_simply_dummy_text_of_the_printing_and_typesetting_industry_lorem_ipsum_has_been_the_industry_s_standard))
        }

    }
    companion object{
        var isSelected = ""
    }


}