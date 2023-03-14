package com.bartukaraa.devbook.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bartukaraa.devbook.databinding.SoruRowTasarimBinding
import com.bartukaraa.devbook.ui.fragment.SoruFragmentDirections
import com.bartukaraa.devbook.ui.model.SoruModel

class SoruAdapter(private val soruList : ArrayList<SoruModel>) : RecyclerView.Adapter<SoruAdapter.SoruHolder>(){

    class SoruHolder(val tasarim : SoruRowTasarimBinding) : RecyclerView.ViewHolder(tasarim.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoruHolder {
        val tasarim = SoruRowTasarimBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SoruAdapter.SoruHolder(tasarim)
    }

    override fun onBindViewHolder(holder: SoruHolder, position: Int) {
        holder.tasarim.textViewKullaniciAdiSoru.text = soruList.get(position).adSoyad
        holder.tasarim.textViewKonu.text = soruList.get(position).konu
        holder.tasarim.textViewDil.text = soruList.get(position).dil
        holder.tasarim.imageViewMesaj.setOnClickListener {
            // mesaj atma durumuna tıklanırsa karşı ekrana useruid gönderilicek.

            val gecis = SoruFragmentDirections.chatFragmentGecis(useruid = soruList.get(position).soruid)
            Navigation.findNavController(it).navigate(gecis)

        }
    }

    override fun getItemCount(): Int {
        return soruList.size
    }


}