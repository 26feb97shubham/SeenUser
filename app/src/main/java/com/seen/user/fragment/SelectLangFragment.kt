package com.seen.user.fragment

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.seen.user.R
import com.seen.user.activity.HomeActivity
import com.seen.user.utils.LogUtils
import com.seen.user.utils.SharedPreferenceUtility
import com.seen.user.utils.Utility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_select_lang.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SelectLangFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SelectLangFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var mView: View
    private var selectLang:String=""

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
    ): View {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_select_lang, container, false)
        setUpViews()
        return mView
    }

    private fun setUpViews() {

        if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]=="en"){
            selectEnglish()

        }
        else if(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]=="ar"){
            selectArabic()

        }

        requireActivity().frag_other_backImg.visibility=View.VISIBLE

        requireActivity().frag_other_backImg.setOnClickListener {
            requireActivity().frag_other_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().frag_other_backImg)
            findNavController().popBackStack()
        }

        mView.arabicView.setOnClickListener {
            if(selectLang != "ar") {
                mView.arabicView.startAnimation(AlphaAnimation(1f, 0.5f))
                selectArabic()
                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.SelectedLang, selectLang)
            }
        }
        mView.englishView.setOnClickListener {
            if(selectLang != "en") {
                mView.englishView.startAnimation(AlphaAnimation(1f, 0.5f))
                selectEnglish()
                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.SelectedLang, selectLang)
            }
        }

        mView.btnNext.setOnClickListener {
            mView.btnNext.startAnimation(AlphaAnimation(1f, 0.5f))
            if(TextUtils.isEmpty(selectLang)){
                LogUtils.shortToast(requireContext(), getString(R.string.please_choose_your_language))
            }
            else{
                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.SelectedLang, selectLang)
                Utility.changeLanguage(requireContext(), selectLang)
                startActivity(Intent(requireContext(), HomeActivity::class.java))
//                findNavController().navigate(R.id.action_selectLangFragment_to_chooseLoginSingUpFragment)
            }

        }

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.setDecorFitsSystemWindows(false)
        } else {
//            requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }*/


    }

    private fun selectArabic() {
        selectLang = "ar"
        mView.imgTick1.visibility = View.VISIBLE
        mView.imgTick2.visibility = View.GONE
    }

    private fun selectEnglish() {
        selectLang = "en"
        mView.imgTick2.visibility = View.VISIBLE
        mView.imgTick1.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
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

    /*override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.setDecorFitsSystemWindows(true)
        } else {
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
//            requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }*/
}
