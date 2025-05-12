package com.example.safecast

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.safecast.databinding.FragmentMainPageBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainPage : Fragment() {
    private lateinit var binding: FragmentMainPageBinding
    private lateinit var subSocket: SubManager
    private lateinit var pushSocket: PushManager
    private var lastKnownLocationUrl: String = ""
    private lateinit var locationManager: LocationManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        binding = FragmentMainPageBinding.inflate(inflater, container, false)

        locationManager = LocationManager(requireContext()) { location ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                lastKnownLocationUrl = "Lat: $latitude, Lng: $longitude"
            } else {
                lastKnownLocationUrl = "Konum alınamadı."
            }
        }

        locationManager.getLastKnownLocation()

        pushSocket = PushManager()
        subSocket = SubManager()

        pushSocket.initSocket("tcp://10.0.2.2:4712")
        subSocket.initSocket("tcp://10.0.2.2:5556", listOf("I","2"))
        subSocket.startReceivingMessages()

        val okButton = binding.okButton

        okButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                Log.e("ZMQ", "Sending: ${okButton.text}")
                pushSocket.sendMessage(okButton.text)
            }
        }

        val needHelpButton = binding.needHelpButton

        needHelpButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                Log.e("ZMQ", "Sending: ${needHelpButton.text}")
                while (lastKnownLocationUrl.isEmpty())
                {
                    delay(100)
                }
                pushSocket.sendMessage(needHelpButton.text.toString() + " " + lastKnownLocationUrl)
            }
        }

        val addRelativesSheetButton = binding.addRelativesSheetButton

        addRelativesSheetButton.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.add_relatives_transaction)
        }

        val relativesListButton = binding.relativesListButton
        relativesListButton.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.relative_list_transaction)
        }

        val accountDetailButton = binding.accountDetailButton

        accountDetailButton.setOnClickListener {
            Navigation.findNavController(it).navigate((R.id.mainPage_to_accountDetail))
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}