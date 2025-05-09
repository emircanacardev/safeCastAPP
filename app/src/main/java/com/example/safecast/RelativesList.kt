package com.example.safecast

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.safecast.databinding.FragmentRelativesListBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class RelativesList : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentRelativesListBinding
    private lateinit var firestoreManager: FirestoreManager
    private lateinit var relativesAdapter: RelativesAdapter
    private val relativesList = ArrayList<RelativeDataClass>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRelativesListBinding.inflate(inflater, container, false)

        firestoreManager = FirestoreManager.getInstance()

        // RecyclerView için adapter'ı oluşturuyoruz
        //
        relativesAdapter = RelativesAdapter(requireContext(), relativesList)
        binding.relativesList.layoutManager = LinearLayoutManager(requireContext())
        binding.relativesList.adapter = relativesAdapter

        // Firestore'dan gelen veriyi alıp listeye ekliyoruz
        // BURAYA DİKKAT!!!!!!!!!!!
        fetchRelativesForUser("nQLgfw2tXpj56WQ4cDvT")
        return binding.root
    }


    private fun fetchRelativesForUser(userId: String) {
        firestoreManager.getRelatives(userId, { relatives ->
            // Gelen veriyi listeye ekliyoruz
            relativesList.clear()
            relativesList.addAll(relatives)
            relativesAdapter.notifyDataSetChanged()  // Adapter'ı güncelliyoruz
        }, { error ->
            Log.e("RelativesList", "Error fetching relatives", error)
        })
    }

}

