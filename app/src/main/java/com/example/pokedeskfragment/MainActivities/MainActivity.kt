package com.example.pokedeskfragment.MainActivities

import android.app.ProgressDialog
import android.content.Intent
import android.content.res.Configuration
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.View
import com.example.pokedeskfragment.R
import com.example.pokedeskfragment.data.pokeResultInfo
import com.example.pokedeskfragment.data.pokemonResul
import com.example.pokedeskfragment.fragment.pokeFragmentAll
import com.example.pokedeskfragment.fragment.pokeFragmentOne
import com.example.pokedeskfragment.utilities.NetworkUtil
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_poke_fragment_all.*
import kotlinx.android.synthetic.main.fragment_poke_fragment_one.*
import java.io.IOException
import java.net.URL

//Se extiende la clase pokeFragmentAll ya que contiene una interface que se necesita
class MainActivity : AppCompatActivity(), pokeFragmentAll.pokeListener{

    /*Se declaran e inicializan las variables que se utilizaran en la clase*/
    var listado = ArrayList<pokemonResul>(); var listadoSearch = ArrayList<pokemonResul>()
    val LISTADO_KEY = "listado"; val LISTADO_KEY_S = "listadoSearch"; val SEARCH = "busqueda"
    var search = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Se restauran los valores de las variables con el savedInstanceState
        if (savedInstanceState != null){
            listado = savedInstanceState.getParcelableArrayList(LISTADO_KEY)
            listadoSearch = savedInstanceState.getParcelableArrayList(LISTADO_KEY_S)
            search = savedInstanceState.getString(SEARCH)
        } else {
            //Busca solo una vez los datos ya que los guarda en una lista.
            FetchPokemonTask().execute()
        }

        /*Inicia el fragmento donde contiene todos los datos de los pokemons es la segunda vez que lo haria al iniciar la actividad
        * ya despues solo se ejecuta una vez cada vez que se intente restaurar los datos con el savedInstanceState*/
        initFragmentAll()

        //Inicializa el segundo fragmento que contiene la informacion de solo un pokemon, pero solo en el modo LandScape.
        if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            initFragmentOne("", "")
            tv_share.setOnClickListener{
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
    }

    //Guarda el estado de las variables.
    override fun onSaveInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState?.putParcelableArrayList(LISTADO_KEY, listado)
        savedInstanceState?.putParcelableArrayList(LISTADO_KEY_S, listadoSearch)
        savedInstanceState?.putString(SEARCH, et_name_pokemon.text.toString())
        super.onSaveInstanceState(savedInstanceState)
    }

    /*Se colocan las funciones interface de la clase pokeFragmentAll*/
    //La primera funcion sirve solamente en la pantalla Potrait, y se mandara a llamar en la funcion click de cada boton en el recyclerView.
    override fun portraitClickListener(pokemon: pokemonResul) {
        //Simplemente manda a llamar otra actividad si la orientacion del telefono es vertical, donde se muestra la info de un pokemon.
        var intent = Intent(this, pokeInfo::class.java)
        intent.putExtra("nombre", pokemon.name)
        intent.putExtra("url", pokemon.url)
        this.startActivity(intent)
    }

    //La segunda funcion sirve solamente en la pantalla LandScape, y se manda a llamar en la funcion click de cada boton del recyclerView.
    override fun landscapeClickListener(pokemon: pokemonResul) {
        /*En lugar de mandar a llamar una actividad, solo agrega un fragmento con la informacion
        * de un pokemon, a la izquierda de la actividad principal*/
        initFragmentOne(pokemon.name, pokemon.url)
        tv_share.visibility = View.VISIBLE
    }

    //Funcion para volver a cargar los datos del sitio web
    override fun recargar() {
        listado.clear()
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            initFragmentOne("", "")
        }
        FetchPokemonTask().execute()
    }

    /*Se crean 2 funciones en las cuales se inicializan los 2 fragmentos*/
    //El primero inicializa el fragmento donde aparece la informacion de todos los pokemons.
    private fun initFragmentAll(){
        var pokeAll = pokeFragmentAll.newInstance(listado, listadoSearch, search)
        agregateFragment(R.id.ll_principal, pokeAll)
    }

    //El seguundo inicializa el fragmento donde se colocara la info de un pokemon especifico.
    private fun initFragmentOne(nombre : String, url : String){
        var pokeOne = pokeFragmentOne.newInstance(url, nombre)
        agregateFragment(R.id.ll_secundario, pokeOne)
    }

    //Funcion global que sirve para ir agregando los fragmentos en su correspondientes viewGroup.
    private fun agregateFragment(view : Int, fragment : Fragment) = supportFragmentManager.beginTransaction().replace(view, fragment).commit()

    //En esta clase asincrona es donde los datos son buscados en la URL y luego guardados en unas listas para ser ocupados donde se le necesite.
    inner class FetchPokemonTask : AsyncTask<String, Void, String>(){
        lateinit var pDialog : ProgressDialog

        override fun onPreExecute() {
            super.onPreExecute()
            pDialog = ProgressDialog(this@MainActivity)
            pDialog.setMessage("Loading the pokemons")
            pDialog.setCancelable(true)
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            pDialog.show()
        }

        override fun doInBackground(vararg params: String?): String {
            var url : URL = NetworkUtil.buiURL()
            try {
                var result = NetworkUtil.getResponseFromHttpUrl(url)
                var gson = Gson()
                var element = gson.fromJson(result, pokeResultInfo::class.java)
                for(i in 0 .. (element.results.size-1)){
                    var datosU = pokemonResul(element.results[i].name, element.results[i].url)
                    listado.add(datosU)
                }
                return ""
            } catch (e : IOException){
                e.printStackTrace()
                return ""
            }
        }

        override fun onPostExecute(s: String?) {
            super.onPostExecute(s)
                if (s != null){
                    initFragmentAll()
                    pDialog.dismiss()
                }
        }
    }
}
