package com.example.safecast

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.safecast.databinding.RelativesItemViewBinding
import java.util.ArrayList

class RelativesAdapter (var mContext: Context, var relativesList: ArrayList<RelativeDataClass>)
    : RecyclerView.Adapter<RelativesAdapter.ViewHolderClass>(){

    private lateinit var firestoreManager: FirestoreManager

    inner class ViewHolderClass (var relatives_item_view: RelativesItemViewBinding): RecyclerView.ViewHolder(relatives_item_view.root){

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderClass {
        val  relatives_item_view = RelativesItemViewBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return ViewHolderClass(relatives_item_view)
    }

    override fun onBindViewHolder(
        holder: ViewHolderClass,
        position: Int
    ) {
        val person = relativesList.get(position)
        val t = holder.relatives_item_view

        t.textPhoneNumber.text = person.phoneNumber
        t.textName.text = person.name

        t.deleteIconView.setOnClickListener {

            firestoreManager = FirestoreManager.getInstance()

            firestoreManager.deleteRelative("nQLgfw2tXpj56WQ4cDvT", person.id, {
                // Silme işlemi başarılı
                Log.e("RelativesAdapter", "Relative successfully deleted")
                // Silinen relative'ı listeden de kaldırıyoruz
                relativesList.remove(person)
                notifyDataSetChanged()  // Adapter'ı güncelliyoruz
            }, { error ->
                // Silme işlemi başarısız
                Log.e("RelativesAdapter", "Error deleting relative", error)
            })
        }
    }

    override fun getItemCount(): Int {
        return relativesList.size
    }
}
