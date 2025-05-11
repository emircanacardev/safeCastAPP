package com.example.safecast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.safecast.databinding.FragmentAddRelativesBinding
import com.example.safecast.databinding.FragmentMainPageBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddRelatives : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAddRelativesBinding
    private lateinit var firestoreManager: FirestoreManager
    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddRelativesBinding.inflate(inflater , container, false)

        binding.addRelativeButton.setOnClickListener {
            firestoreManager = FirestoreManager.getInstance()
            var relative = RelativeDataClass("", binding.inputTextName.text.toString(), binding.inputPhoneNumber.text.toString())

            //BURAYA DÜZENLEME GELECEK USER ID İÇİN !!!!!!!!!!
            firestoreManager.addRelative(relative, {
                // Başarılı işlem
                Toast.makeText(requireContext(), "Relative added!", Toast.LENGTH_SHORT).show()
                binding.inputTextName.setText("")
                binding.inputPhoneNumber.setText("")
            }, { error ->
                // Hata durumu
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            })
            }

        return binding.root
    }
}