package com.example.safecast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.safecast.databinding.FragmentLoginPageBinding

class SignIn : Fragment() {

    private lateinit var binding: FragmentLoginPageBinding
    private lateinit var firestoreManager: FirestoreManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginPageBinding.inflate(inflater, container, false)

        binding.signInButton.setOnClickListener {
            val email = binding.inputLoginEmail.text.toString().trim()
            val password = binding.inputLoginPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firestoreManager = FirestoreManager.getInstance()

                firestoreManager.loginUser(email, password) { success, message ->
                    if (success) {
                        Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                        Navigation.findNavController(it).navigate(R.id.login_auth)
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }


        binding.switchSignUp.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_loginPage_to_signUp)
        }

        return binding.root
    }
}