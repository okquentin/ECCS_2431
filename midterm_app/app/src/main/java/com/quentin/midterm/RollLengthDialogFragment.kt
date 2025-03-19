package com.quentin.midterm

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

/**
 * A DialogFragment that allows the user to select the roll length.
 */
class RollLengthDialogFragment : DialogFragment() {

    interface OnRollLengthSelectedListener {
        fun onRollLengthClick(which: Int)
    }

    private lateinit var listener: OnRollLengthSelectedListener

    /**
     * Creates the dialog for selecting roll length.
     * @param savedInstanceState The last saved instance state of the Fragment, or null if this is a freshly created Fragment.
     * @return A new Dialog instance to be displayed by the Fragment.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(R.string.pick_roll_length)
        builder.setItems(R.array.length_array) { dialog, which ->
            // 'which' is the index position chosen
            listener.onRollLengthClick(which)
        }
        return builder.create()
    }

    /**
     * Attaches the fragment to the context and initializes the listener.
     * @param context The context to which the fragment is attached.
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as OnRollLengthSelectedListener
    }
}