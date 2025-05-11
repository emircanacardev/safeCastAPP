package com.example.safecast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.safecast.databinding.FragmentSignUpBinding

class SignUp : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var firestoreManager: FirestoreManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        binding.signUpButton.setOnClickListener {
            val name = binding.inputSignUpName.text
            val email = binding.inputSignUpEmail.text
            val phoneNumber = binding.inputSignUpPhoneNumber.text
            val password = binding.inputSignUpPassword.text
            val passwordAgain = binding.inputSignUpPasswordAgain.text

            firestoreManager = FirestoreManager.getInstance()

            if (name.isNotEmpty() && email.isNotEmpty() && phoneNumber.isNotEmpty() && password.isNotEmpty() && passwordAgain.isNotEmpty()) {

                if (password.toString() == passwordAgain.toString()) {


                    val user = UserDataClass(name.toString(), email.toString(), phoneNumber.toString(), password.toString())
                    firestoreManager.registerUser(user) { success, message ->
                        if (success) {
                            Toast.makeText(
                                context,
                                "Registration successful!",
                                Toast.LENGTH_SHORT
                            ).show()
                                Navigation.findNavController(it).navigate(R.id.action_signUp_to_loginPage)
                        } else {
                            // Kayıt başarısız ise hata mesajını göster
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(context, "Hepsini doldur", Toast.LENGTH_SHORT).show()

            }
        }

        binding.switchSignIn.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_signUp_to_loginPage)
        }

        return binding.root
    }
}