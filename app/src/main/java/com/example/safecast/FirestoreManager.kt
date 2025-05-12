package com.example.safecast

import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseUser


class FirestoreManager private constructor() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Singleton örneği
    companion object {
        @Volatile
        private var INSTANCE: FirestoreManager? = null

        // Singleton örneğini döndüren fonksiyon
        fun getInstance(): FirestoreManager =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: FirestoreManager().also { INSTANCE = it }
            }
    }

    // REGISTER USER
    fun registerUser(userData: UserDataClass,
                     onComplete: (Boolean, String) -> Unit // Callback fonksiyonu ekledik
    ) {
        auth.createUserWithEmailAndPassword(userData.email, userData.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        // Kullanıcıyı Firestore'a kaydediyoruz
                        addUser(userData) { success, message ->
                            onComplete(success, message) // Callback ile sonucu dönüyoruz
                        }
                    }
                } else {
                    onComplete(false, task.exception?.message.toString())
                }
            }
    }

    // Kullanıcı ekle (otomatik ID)
    fun addUser(user: UserDataClass, onComplete: (Boolean, String) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid // Kullanıcının UID'si

        // UID kontrolü yapılmalı
        if (userId != null) {
            firestore.collection("user")
                .document(userId)  // Kullanıcının UID'si ile belge adı belirleniyor
                .set(user)  // Kullanıcı bilgilerini ekliyoruz
                .addOnSuccessListener {
                    Log.d("Firestore", "User added with ID: $userId")
                    onComplete(true, "User added successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error adding user", e)
                    onComplete(false, "Error adding user: ${e.message}")
                }
        } else {
            Log.e("Firestore", "User ID is null")
            onComplete(false, "User ID is null")
        }
    }

    fun loginUser(email: String, password: String, onComplete: (Boolean, String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, "Login successful")
                } else {
                    val error = task.exception
                    val errorMsg = when (error) {
                        is FirebaseAuthInvalidUserException,
                        is FirebaseAuthInvalidCredentialsException -> "Email or password is incorrect!"
                        else -> error?.localizedMessage ?: "Login failed"
                    }
                    onComplete(false, errorMsg)
                }
            }
    }

    fun logoutUser() {
        auth.signOut()
    }

    // Akraba ekle (bir kullanıcıya ait)
    fun addRelative(relative: RelativeDataClass, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            firestore.collection("user")
                .document(userId) // Giriş yapmış kullanıcının UID'sini kullanıyoruz
                .collection("relatives")
                .add(relative) // Firestore otomatik olarak ID atar
                .addOnSuccessListener { documentReference ->
                    Log.d("Firestore", "Relative added with ID: ${documentReference.id}")
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error adding relative", e)
                    onFailure(e)
                }
        } else {
            Log.e("Firestore", "User not authenticated")
            onFailure(Exception("User not authenticated"))
        }
    }


    // Kullanıcıyı getir
    fun getUser(onSuccess: (UserDataClass) -> Unit, onFailure: (Exception) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null)
        {
            firestore.collection("user").document(userId).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val user = snapshot.toObject(UserDataClass::class.java)
                    if (user != null) {
                        onSuccess(user)
                    } else {
                        onFailure(Exception("User conversion failed"))
                    }
                } else {
                    onFailure(Exception("User not found"))
                }
            }
        }
    }


    fun getRelatives(onSuccess: (List<RelativeDataClass>) -> Unit, onFailure: (Exception) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            firestore.collection("user") // Koleksiyon adı
                .document(userId) // Giriş yapmış kullanıcının UID'si ile belgeyi seçiyoruz
                .collection("relatives") // relatives koleksiyonunu alıyoruz
                .get() // Veriyi çekiyoruz
                .addOnSuccessListener { querySnapshot ->
                    val relativesList = querySnapshot.documents.map { document ->
                        // Her bir dokümanı işleyip RelativeDataClass'a dönüştürüyoruz
                        RelativeDataClass(
                            id = document.id.toString(),
                            name = document.getString("name") ?: "", // name alanı
                            phoneNumber = document.getString("phoneNumber") ?: "" // phoneNumber alanı
                        )
                    }

                    // Veriyi başarıyla aldık, listeyi döndürüyoruz
                    Log.d("Firestore", relativesList.toString())
                    onSuccess(relativesList)
                }
                .addOnFailureListener { exception ->
                    // Hata durumunda exception'ı döndürüyoruz
                    Log.e("Firestore", "Error getting relatives", exception)
                    onFailure(exception)
                }
        } else {
            Log.e("Firestore", "User not authenticated")
            onFailure(Exception("User not authenticated"))
        }
    }

    fun deleteRelative(relativeId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        // FirebaseAuth ile oturum açan kullanıcının ID'sini alıyoruz
        val userID = FirebaseAuth.getInstance().currentUser?.uid

        if (userID != null) {
            firestore.collection("user") // Koleksiyon adı
                .document(userID) // Şu anki kullanıcının ID'sini alıyoruz
                .collection("relatives") // relatives koleksiyonunu alıyoruz
                .document(relativeId) // Belirli relative'ın id'sini seçiyoruz
                .delete() // Relative'ı siliyoruz
                .addOnSuccessListener {
                    // Silme işlemi başarılı
                    onSuccess()
                }
                .addOnFailureListener { exception ->
                    // Hata durumunda exception'ı döndürüyoruz
                    onFailure(exception)
                }
        } else {
            // Kullanıcı giriş yapmamışsa, hata mesajı döndürüyoruz
            onFailure(Exception("User not authenticated"))
        }
    }
}
