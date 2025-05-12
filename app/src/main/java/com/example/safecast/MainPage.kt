package com.example.safecast

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.safecast.databinding.FragmentMainPageBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainPage : Fragment() {
    private lateinit var binding: FragmentMainPageBinding
    private lateinit var subSocket: SubManager
    private lateinit var pushSocket: PushManager
    private lateinit var locationManager: LocationManager

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        binding = FragmentMainPageBinding.inflate(inflater, container, false)

        locationManager = LocationManager(requireContext())

        pushSocket = PushManager()
        subSocket = SubManager()

        pushSocket.initSocket()
        subSocket.initSocket()
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
            locationManager.requestSingleUpdate { location ->
                location?.let {
                    val latLngText = "Lat: ${it.latitude}, Lng: ${it.longitude}"
                    Log.d("Location", latLngText)

                    lifecycleScope.launch(Dispatchers.IO) {
                        Log.e("ZMQ", "Sending: ${needHelpButton.text} $latLngText")
                        pushSocket.sendMessage("${needHelpButton.text} $latLngText")
                    }
                } ?: run {
                    Toast.makeText(requireContext(), "Konum alınamadı", Toast.LENGTH_SHORT).show()
                }
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