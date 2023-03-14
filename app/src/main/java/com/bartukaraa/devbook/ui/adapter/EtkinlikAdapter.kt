package com.bartukaraa.devbook.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bartukaraa.devbook.databinding.EtkinlikRowTasarimBinding
import com.bartukaraa.devbook.databinding.IlanlarRowTasarimBinding
import com.bartukaraa.devbook.ui.model.EtkinliklerModel


class EtkinlikAdapter(private val etkinlikList :ArrayList<EtkinliklerModel>) :RecyclerView.Adapter<EtkinlikAdapter.EtkinlikHolder>(){

    class EtkinlikHolder(val tasarim : EtkinlikRowTasarimBinding) : RecyclerView.ViewHolder(tasarim.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EtkinlikHolder {
        val tasarim =EtkinlikRowTasarimBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return EtkinlikAdapter.EtkinlikHolder(tasarim)
    }

    override fun onBindViewHolder(holder: EtkinlikHolder, position: Int) {
        holder.tasarim.textViewEtkinlikKullaniciAdi.text = etkinlikList.get(position).adSoyad
        holder.tasarim.textViewKonuAd.text = etkinlikList.get(position).konu
        holder.tasarim.textViewMekanAd.text = etkinlikList.get(position).mekan
        holder.tasarim.textViewZamanAd.text = "${etkinlikList.get(position).zamanGun} - ${etkinlikList.get(position).zamanSaat}"
    }

    override fun getItemCount(): Int {
        return etkinlikList.size
    }


}