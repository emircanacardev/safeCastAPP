package com.example.safecast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.safecast.databinding.FragmentNotificationViewBinding

class NotificationView : Fragment() {

    private lateinit var binding: FragmentNotificationViewBinding
    private lateinit var adapter: NotificationAdapter
    private val notificationList = ArrayList<NotificationDataClass>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentNotificationViewBinding.inflate(inflater, container, false)

        binding.backToMainPageButton.setOnClickListener {
            Navigation.findNavController(it).navigateUp()
        }

        // RecyclerView setup
        adapter = NotificationAdapter(requireContext(), notificationList)
        binding.notificationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.notificationRecyclerView.adapter = adapter

        // Örnek veriler
        notificationList.add(
            NotificationDataClass(
                sender = "SafeCast System",
                message = "Radiation level increased!",
                location = "Zone A",
                timestamp = "12:00 - 01/01/2025"
            )
        )

        notificationList.add(
            NotificationDataClass(
                sender = "Sensor 3B",
                message = "All clear.",
                location = "Zone B",
                timestamp = "12:05 - 01/01/2025"
            )
        )

        adapter.notifyDataSetChanged()

        return binding.root
    }
}
