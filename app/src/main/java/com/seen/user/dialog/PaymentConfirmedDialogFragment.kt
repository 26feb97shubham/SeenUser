package com.seen.user.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.seen.user.R
import kotlinx.android.synthetic.main.payment_confirmed_dialog.view.*

class PaymentConfirmedDialogFragment : DialogFragment() {
    private var checkStatusCallback : CheckStatusInterface?=null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val view = inflater.inflate(R.layout.payment_confirmed_dialog, container, false)
        setUpViews(view)
        return view
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    fun setDataCompletionCallback(checkStatusCallback: CheckStatusInterface?) {
        this.checkStatusCallback = checkStatusCallback
    }


    private fun setUpViews(view: View?) {
        view?.btnViewOrder!!.setOnClickListener {
            checkStatusCallback!!.checkStatus()
            dismiss()
        }
    }

    interface CheckStatusInterface {
        fun checkStatus()
    }

}