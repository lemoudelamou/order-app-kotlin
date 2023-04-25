package com.example.solubox.Utils

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.example.solubox.R
import com.example.solubox.databinding.LayCustomDialogBinding


class CustomAlert {

    private lateinit var bindingDialog: LayCustomDialogBinding


    fun showDialog(activity: Activity?, msg: String?, title: String?) {
        val dialog = Dialog(activity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.lay_custom_dialog)
        dialog.apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        val text = dialog.findViewById<View>(R.id.text_dialog) as TextView
        val text_tit = dialog.findViewById<View>(R.id.text_title) as TextView
        text.text = msg
        text_tit.text = title
        val dialogButton: Button = dialog.findViewById<View>(R.id.btn_dialog) as Button
        dialogButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}