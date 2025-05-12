package com.example.safecast

import android.Manifest
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
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
    private lateinit var firestoreManager: FirestoreManager
    private val relativesList = ArrayList<RelativeDataClass>()

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        binding = FragmentMainPageBinding.inflate(inflater, container, false)

        locationManager = LocationManager(requireContext())

        val okButton = binding.okButton
        val needHelpButton = binding.needHelpButton

        var currentUserPhoneNumber: String
        var currentUserName: String

        firestoreManager = FirestoreManager.getInstance()
        firestoreManager.getUser(onSuccess = { user ->
            currentUserPhoneNumber = user.phoneNumber.trim()
            currentUserName = user.name.trim()

            okButton.setOnClickListener {
                    lifecycleScope.launch(Dispatchers.IO) {
                        Log.e("ZMQ", "Sending: ${okButton.text}")
                        pushSocket.sendMessage(currentUserName, currentUserPhoneNumber, okButton.text.toString(), "")
                    }
                }

            needHelpButton.setOnClickListener {
                locationManager.requestSingleUpdate { location ->
                    location?.let {
                        val latLngText = "Lat: ${it.latitude}, Lng: ${it.longitude}"
                        Log.d("Location", latLngText)
                        lifecycleScope.launch(Dispatchers.IO) {
                            Log.e("ZMQ", "Sending: ${needHelpButton.text} $latLngText")
                            pushSocket.sendMessage(currentUserName, currentUserPhoneNumber, needHelpButton.text.toString(), latLngText)
                        }
                    } ?: run {
                        Toast.makeText(requireContext(), "Konum alınamadı", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        },
        onFailure = { exception ->
            Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
        })

        pushSocket = PushManager()
        subSocket = SubManager { message -> // Callback fonksiyonunu geçiyoruz
            showMessage(message) // Mesaj alındığında buradaki metodu çağıracağız
        }

        pushSocket.initSocket()


        fetchRelativesForUser { phoneNumbers ->
            Log.e("ZMQ", "Relatives Phone Numbers: ${phoneNumbers}")

            subSocket.initSocket(phoneNumbers)
            subSocket.startReceivingMessages()
        }

        val addRelativesSheetButton = binding.addRelativesSheetButton

        addRelativesSheetButton.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.add_relatives_transaction)
        }

        val notificationViewButton = binding.notificationButton

        notificationViewButton.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.mainPage_to_NotificationView)
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
        pushSocket.close()
        subSocket.close()
        super.onDestroyView()
    }

    private fun fetchRelativesForUser(onReady: (List<String>) -> Unit) {
        firestoreManager.getRelatives({ relatives ->
            relativesList.clear()
            relativesList.addAll(relatives)

            // Sadece telefon numaralarını al
            val phoneNumbers = relativesList.mapNotNull { it.phoneNumber?.trim() }
                .filter { it.isNotEmpty() }

            // Callback ile geri döndür
            onReady(phoneNumbers)

        }, { error ->
            Log.e("RelativesList", "Error fetching relatives", error)
        })
    }

    private fun showMessage(message: String) {
        // UI thread'inde Toast mesajını göstermek için
        lifecycleScope.launch(Dispatchers.Main) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }

}