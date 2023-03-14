package com.bartukaraa.devbook.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.navigation.Navigation
import com.bartukaraa.devbook.R
import com.bartukaraa.devbook.databinding.FragmentGirisYapBinding
import com.bartukaraa.devbook.databinding.FragmentIlanBinding
import com.bartukaraa.devbook.databinding.FragmentIlanVerBinding
import com.bartukaraa.devbook.ui.model.KullaniciProfile
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso


class IlanVerFragment : Fragment() {

    private lateinit var tasarim : FragmentIlanVerBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore :FirebaseFirestore
    private lateinit var storage : FirebaseStorage
    private lateinit var sehirSecim :String
    private lateinit var adSoyad : String
    private lateinit var kullaniciProfile : KullaniciProfile


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        tasarim = FragmentIlanVerBinding.inflate(inflater,container,false)
        val view = tasarim.root

        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage

        getDataKullanici()

        val spinnerSehir = tasarim.spinnerSehir.findViewById(R.id.spinnerSehir) as Spinner
        var sehirlerList = resources.getStringArray(R.array.sehirler)

        if(spinnerSehir != null){
            val adapter: ArrayAdapter<String> = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,sehirlerList)
            spinnerSehir.adapter = adapter

            spinnerSehir.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    // secilen değeri veriyor
                    sehirSecim = sehirlerList[p2]
                    Log.e("mesaj",sehirSecim)
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
        }


        tasarim.buttonIlanVer.setOnClickListener {

            val ilgiAlan = tasarim.editTextIlgiAlani.text.toString()
            val sehir = sehirSecim
            val universite = tasarim.editTextUniversite.text.toString()
            val tarih = Timestamp.now()
            val adSoyadYeni = adSoyad

            if(auth.currentUser != null){

                val postMap = hashMapOf<String,Any>()

                postMap.put("ilgiAlani",ilgiAlan)
                postMap.put("sehir",sehir)
                postMap.put("universite",universite)
                postMap.put("tarih",tarih)
                postMap.put("adSoyad",adSoyadYeni)
                postMap.put("uid", auth.currentUser!!.uid)

                firestore.collection("Ilanlar").document(auth.currentUser!!.uid).set(postMap).addOnSuccessListener {
                    // ilan başarılı yüklenirse
                    Navigation.findNavController(tasarim.buttonIlanVer).navigate(R.id.ilanYayinlamaBitisGecis)
                }.addOnFailureListener {
                    // bir hata olursa
                    Toast.makeText(requireActivity(),"İlan yayınlanırken bir hata oluştu!",Toast.LENGTH_LONG).show()
                }



            }








        }




        return view
    }

    private fun getDataKullanici(){

        firestore.collection("KullaniciProfile").addSnapshotListener { value, error ->
            if(error != null){
                // hata var demektir!
            }else{
                // hata yok demektir
                if(value != null){
                    if(!value.isEmpty){
                        val documents = value.documents

                        for(documentKullaniciProfile in documents){
                            val email = documentKullaniciProfile.get("userEmail") as String
                            val gelenAdSoyad = documentKullaniciProfile.get("adSoyad") as String
                            val cinsiyet = documentKullaniciProfile.get("cinsiyet") as String
                            val gelistirdigiDil = documentKullaniciProfile.get("gelistirdigiDil") as String
                            val gitHub = documentKullaniciProfile.get("github") as String
                            val ilgiAlani = documentKullaniciProfile.get("ilgiAlani") as String
                            val linkedin = documentKullaniciProfile.get("linkedin") as String
                            val sehir = documentKullaniciProfile.get("sehir") as String
                            val yas = documentKullaniciProfile.get("yas") as String
                            val kullaniciProfileResim = documentKullaniciProfile.get("downloadUrlKullaniciProfil") as String
                            val userUid = documentKullaniciProfile.get("uiid") as String

                            kullaniciProfile = KullaniciProfile(email,gelenAdSoyad,sehir,ilgiAlani,gelistirdigiDil,yas,cinsiyet,gitHub,linkedin,kullaniciProfileResim,userUid)

                            val userEmailActive = auth.currentUser?.email as String

                            if(kullaniciProfile.email.equals(userEmailActive)){
                                adSoyad = gelenAdSoyad
                            }

                        }
                    }
                }
            }
        }
    }


}