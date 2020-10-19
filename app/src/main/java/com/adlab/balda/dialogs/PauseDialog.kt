package com.adlab.balda.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.adlab.balda.R
import com.adlab.balda.databinding.LayoutPauseBinding

class PauseDialog: DialogFragment() {

    private lateinit var binding: LayoutPauseBinding

    var callback: CallbackListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        isCancelable = false
        binding = DataBindingUtil.inflate(inflater, R.layout.layout_pause, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.bResumeGame.setOnClickListener {
            callback?.onResumeClicked()
            dismiss()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CallbackListener) {
            callback = context
        } else {
            throw RuntimeException("$context must implement CallbackListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }


    interface CallbackListener {
        fun onResumeClicked()
    }
}