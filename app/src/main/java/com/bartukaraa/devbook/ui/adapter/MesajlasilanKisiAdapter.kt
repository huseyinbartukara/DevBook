package com.bartukaraa.devbook.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bartukaraa.devbook.databinding.MesajlarRowTasarimBinding
import com.bartukaraa.devbook.ui.model.MesajlasilanKisiModel

class MesajlasilanKisiAdapter (private val mesajlasilanKisiList : ArrayList<MesajlasilanKisiModel>) : RecyclerView.Adapter<MesajlasilanKisiAdapter.MesajlasilanKisiHolder>(){

    class MesajlasilanKisiHolder(val tasarim : MesajlarRowTasarimBinding) : RecyclerView.ViewHolder(tasarim.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MesajlasilanKisiHolder {
        val tasarim = MesajlarRowTasarimBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MesajlasilanKisiAdapter.MesajlasilanKisiHolder(tasarim)
    }

    override fun onBindViewHolder(holder: MesajlasilanKisiHolder, position: Int) {
        holder.tasarim.textViewMesajKullaniciAd.text = mesajlasilanKisiList.get(position).useruid
    }

    override fun getItemCount(): Int {
        return mesajlasilanKisiList.size
    }

}