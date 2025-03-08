package com.zybooks.thebanddatabase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation

class ListFragment : Fragment() {

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                             savedInstanceState: Bundle?): View {
      val rootView = inflater.inflate(R.layout.fragment_list, container, false)
      val layout = rootView as LinearLayout

      // Create the buttons using the band names and ids from BandRepository
      val bandList = BandRepository.getInstance(requireContext()).bandList
      for (band in bandList) {
         val button = Button(context)
         val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
         )
         layoutParams.setMargins(0, 0, 0, 10) // 10px bottom margin
         button.layoutParams = layoutParams

         // Display band's name on button
         button.text = band.name

         // Navigate to detail screen when clicked
         button.setOnClickListener { buttonView: View ->
            Navigation.findNavController(buttonView).navigate(R.id.show_item_detail)
         }

         // Add button to the LinearLayout
         layout.addView(button)
      }

      return rootView
   }
}