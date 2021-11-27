package com.dev.ecommerceuser.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.navigation.fragment.findNavController
import com.dev.ecommerceuser.R
import com.dev.ecommerceuser.dialog.RateYourServicePopUpDialog
import com.dev.ecommerceuser.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.view.*
import kotlinx.android.synthetic.main.fragment_rating.view.*

class RatingFragment : Fragment() {
    var mView:View?=null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_rating, container, false)
        setUpViews()
        return mView
    }

    private fun setUpViews() {
        requireActivity().frag_other_toolbar.frag_other_backImg.setOnClickListener {
            requireActivity().frag_other_toolbar.frag_other_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().frag_other_toolbar.frag_other_backImg)
            findNavController().popBackStack()
        }

        mView!!.tv_add_rating.setOnClickListener {
            val rateYourServicePopUpDialog = RateYourServicePopUpDialog.newInstance(requireContext())
            rateYourServicePopUpDialog.show(requireActivity().supportFragmentManager, RateYourServicePopUpDialog.TAG)
        }
    }

}