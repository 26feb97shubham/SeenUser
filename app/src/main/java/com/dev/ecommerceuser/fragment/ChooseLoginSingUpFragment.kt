package com.dev.ecommerceuser.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dev.ecommerceuser.R
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_choose_login_sing_up.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChooseLoginSingUpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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
        requireActivity().home_frag_categories.visibility=View.GONE
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChooseLoginSingUpFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                ChooseLoginSingUpFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}