package com.dev.ecommerceuser.dialog

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.dev.ecommerceuser.R
import com.dev.ecommerceuser.fragment.FilterBottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RateYourServicePopUpDialog : BottomSheetDialogFragment() {
    private var btnOk: TextView? = null
    private var btnCancel: TextView? = null
    override fun getTheme(): Int {
        return R.style.RateYourServicePopUpDialogTheme
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_rate_your_service_pop_up_dialog, container, false)
        btnOk = view.findViewById(R.id.btnOk)
        btnCancel = view.findViewById(R.id.btnCancel)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btnCancel!!.setOnClickListener {
            dismiss()
        }

        btnOk!!.setOnClickListener {
            dismiss()
        }
    }

    override fun onDetach() {
        super.onDetach()
    }

    companion object {
        const val TAG = "RateYourServicePopUpDialog"
        fun newInstance(context: Context?): RateYourServicePopUpDialog {
            //this.context = context;
            return RateYourServicePopUpDialog()
        }
    }
    interface OnOkClick{
        fun okay()
    }
}