package com.bartukaraa.devbook.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bartukaraa.devbook.R
import com.bartukaraa.devbook.databinding.IlanlarRowTasarimBinding
import com.bartukaraa.devbook.ui.fragment.IlanFragmentDirections
import com.bartukaraa.devbook.ui.model.IlanModel

class IlanAdapter(private val ilanList :ArrayList<IlanModel>) :RecyclerView.Adapter<IlanAdapter.IlanHolder>() {

    class IlanHolder(val tasarim : IlanlarRowTasarimBinding) :RecyclerView.ViewHolder(tasarim.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IlanHolder {
        val tasarim = IlanlarRowTasarimBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return IlanHolder(tasarim)
    }

    override fun onBindViewHolder(holder: IlanHolder, position: Int) {
        holder.tasarim.textViewIlgiAlani.text = ilanList.get(position).ilgiAlani
        holder.tasarim.textViewSehirIlan.text = ilanList.get(position).sehir
        holder.tasarim.textViewUniversite.text = ilanList.get(position).universite
        holder.tasarim.textViewKullaniciAdi.text = ilanList.get(position).adSoyad
        holder.tasarim.cardView.setOnClickListener {
            val gecis = IlanFragmentDirections.kullaniciProfileGoruntuleFragmentGecis(useruid = ilanList.get(position).uid)
            Navigation.findNavController(it).navigate(gecis)
        }
    }

    override fun getItemCount(): Int {
        return ilanList.size
    }


}