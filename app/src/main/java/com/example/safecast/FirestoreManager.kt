package com.example.safecast

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreManager private constructor() {
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

    // Kullanıcı ekle (otomatik ID)
    fun addUser(user: UserDataClass, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("user")
            .add(user) // Firestore otomatik olarak ID atar
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "User added with ID: ${documentReference.id}")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error adding user", e)
                onFailure(e)
            }
    }

    // Akraba ekle (bir kullanıcıya ait)
    fun addRelative(userId: String, relative: RelativeDataClass, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("user")
            .document(userId)
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
    }

    // Kullanıcıları getir
    fun getAllUsers(onSuccess: (List<Pair<String, UserDataClass>>) -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("user")
            .get()
            .addOnSuccessListener { result ->
                val users = result.documents.mapNotNull { doc ->
                    val user = doc.toObject(UserDataClass::class.java)
                    user?.let { Pair(doc.id, it) } // Kullanıcıyı ve ID'sini döner
                }
                onSuccess(users)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error getting users", e)
                onFailure(e)
            }
    }

    fun getRelatives(
        userId: String,
        onSuccess: (List<RelativeDataClass>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore.collection("user") // Koleksiyon adı
            .document(userId) // Belirli bir belgeyi seçiyoruz
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
                    Log.e("Firestore", relativesList.toString())
                onSuccess(relativesList)
            }
            .addOnFailureListener { exception ->
                // Hata durumunda exception'ı döndürüyoruz
                onFailure(exception)
            }
    }

    fun deleteRelative(userID: String, relativeId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("user") // Koleksiyon adı
            .document(userID) // Belirli bir kullanıcıyı seçiyoruz
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
    }

}
