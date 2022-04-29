package com.seen.user.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.seen.user.R
import com.seen.user.utils.SharedPreferenceUtility
import com.seen.user.utils.Utility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_choose_login_sing_up.view.*

class ChooseLoginSingUpFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var mView: View
    var reference:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            reference = it.getString("reference", "")

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        mView = inflater.inflate(R.layout.fragment_choose_login_sing_up, container, false)
        Utility.changeLanguage(
            requireContext(),
            SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
        )
        setUpViews()
        return mView
    }

    private fun setUpViews() {

        requireActivity().toolbar.visibility=View.GONE
        requireActivity().bottomNavigationView.visibility=View.GONE

        mView.btnLogin.setOnClickListener {
            mView.btnLogin.startAnimation(AlphaAnimation(1f, 0.5f))
            val args=Bundle()
            args.putString("reference", reference)
            findNavController().navigate(R.id.action_chooseLoginSingUpFragment_to_loginFragment, args)
        }
        mView.btnSignUp.setOnClickListener {
            mView.btnSignUp.startAnimation(AlphaAnimation(1f, 0.5f))
            val args=Bundle()
            args.putString("reference", reference)
            findNavController().navigate(R.id.action_chooseLoginSingUpFragment_to_signUpFragment, args)
        }

        mView.btnGuestUser.setOnClickListener {
            mView.btnGuestUser.startAnimation(AlphaAnimation(1f, .5f))
            requireActivity().toolbar.visibility=View.VISIBLE
            requireActivity().bottomNavigationView.visibility=View.VISIBLE
            requireActivity().itemDiscount.setImageResource(R.drawable.discount)
            //requireActivity().itemCart.setImageResource(R.drawable.shopping_cart)
            requireActivity().itemHome.setImageResource(R.drawable.home_active)
//        requireActivity().itemSearch.setImageResource(R.drawable.search)
            //requireActivity().itemProfile.setImageResource(R.drawable.profile_5)
            findNavController().navigate(R.id.homeFragment)
        }

    }

    override fun onResume() {
        super.onResume()
        Utility.changeLanguage(
            requireContext(),
            SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, "")
        )
        requireActivity().home_frag_categories.visibility=View.GONE
    }

}