package com.bartukaraa.devbook.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bartukaraa.devbook.R
import com.bartukaraa.devbook.databinding.FragmentSplashScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashScreenFragment : Fragment() {

    private lateinit var tasarim : FragmentSplashScreenBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        tasarim = FragmentSplashScreenBinding.inflate(inflater,container,false)
        val view = tasarim.root

        auth = Firebase.auth


        val currentUser = auth.currentUser



        Handler(Looper.myLooper()!!).postDelayed({

            if(currentUser != null){
                findNavController().navigate(R.id.kullaniciVarGirisi)
            }else{
                findNavController().navigate(R.id.girisYapFragnmentGecis)
            }
        },3000)








        return view
    }
}