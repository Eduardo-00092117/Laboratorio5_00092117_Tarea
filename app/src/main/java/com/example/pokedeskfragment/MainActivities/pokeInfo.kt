package com.example.pokedeskfragment.MainActivities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import com.example.pokedeskfragment.R
import com.example.pokedeskfragment.fragment.pokeFragmentOne
import kotlinx.android.synthetic.main.activity_poke_info.*
import kotlinx.android.synthetic.main.fragment_poke_fragment_one.*

class pokeInfo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poke_info)

        initFragmentOne(intent.getStringExtra("nombre"), intent.getStringExtra("url"))

        tv_cerrar.setOnClickListener{
             onBackPressed()
             this.finish()
        }

        tv_share.setOnClickListener {
            var datos = tv_nombre_poke.text.toString() + "\n" +
                    tv_peso_poke.text.toString() + "\n" +
                    tv_altura_poke.text.toString() + "\n" +
                    tv_experiencia_poke.text.toString() + "\n" +
                    tv_tipo.text.toString()
            var mIntentShare = Intent()
            mIntentShare.action = Intent.ACTION_SEND
            mIntentShare.type = "text/plain"
            mIntentShare.putExtra(Intent.EXTRA_TEXT, datos)
            startActivity(mIntentShare)
        }
    }

    //El seguundo inicializa el fragmento donde se colocara la info de un pokemon especifico.
    private fun initFragmentOne(nombre : String, url : String){
        var pokeOne = pokeFragmentOne.newInstance(url, nombre)
        agregateFragment(R.id.ll_principal_one, pokeOne)
    }

    //Funcion global que sirve para ir agregando los fragmentos en su correspondientes viewGroup.
    private fun agregateFragment(view : Int, fragment : Fragment) = supportFragmentManager.beginTransaction().replace(view, fragment).commit()
}
