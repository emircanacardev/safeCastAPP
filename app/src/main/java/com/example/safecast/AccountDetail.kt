package com.example.safecast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.safecast.databinding.FragmentAccountDetailBinding
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.graphics.Typeface


class AccountDetail : Fragment() {
    private lateinit var binding: FragmentAccountDetailBinding
    private lateinit var firestoreManager: FirestoreManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountDetailBinding.inflate(inflater, container, false)

        firestoreManager = FirestoreManager.getInstance()

        firestoreManager.getUser(onSuccess = { user ->
            // Kalın "Name:" ve düz kullanıcı adı
            val nameText = SpannableStringBuilder().apply {
                append("Name: ")
                setSpan(StyleSpan(Typeface.BOLD), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                append(user.name)
            }

            val emailText = SpannableStringBuilder().apply {
                append("Email: ")
                setSpan(StyleSpan(Typeface.BOLD), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                append(user.email)
            }

            val phoneText = SpannableStringBuilder().apply {
                append("Phone: ")
                setSpan(StyleSpan(Typeface.BOLD), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                append(user.phoneNumber)
            }

            binding.nameTextView.text = nameText
            binding.emailTextView.text = emailText
            binding.phoneTextView.text = phoneText
        },
            onFailure = { exception ->
                Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            })


        binding.backMainPage.setOnClickListener {
            Navigation.findNavController(it).navigateUp()
//            Navigation.findNavController(it).navigate((R.id.accountDetail_to_mainPage))

        }

        binding.logOutButton.setOnClickListener {

            firestoreManager.logoutUser()
            Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
            Navigation.findNavController(it).navigate((R.id.accountDetail_to_LoginPage))
        }

        return binding.root
    }

}