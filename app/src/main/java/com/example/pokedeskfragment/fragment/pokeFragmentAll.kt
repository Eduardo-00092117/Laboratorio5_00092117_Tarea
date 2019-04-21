package com.example.pokedeskfragment.fragment

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.example.pokedeskfragment.MainActivities.MainActivity
import com.example.pokedeskfragment.R
import com.example.pokedeskfragment.data.pokemonResul
import com.example.pokedeskfragment.model.AdapterDatos
import kotlinx.android.synthetic.main.fragment_poke_fragment_all.*
import kotlinx.android.synthetic.main.fragment_poke_fragment_all.view.*


class pokeFragmentAll : Fragment() {

    //Se declaran e inicializan variables que se ocuparan en toda la clase.
    var listado = ArrayList<pokemonResul>(); var listadoSearch = ArrayList<pokemonResul>()
    var listenerTool :  pokeListener? = null
    var search = ""
    lateinit var viewGlobal : View

    //Se crea funcion estatica, que serviria para obtener una instancia de la clase en cuestion, es decir pokeFragmentAll.
    //Se crea un singleton
    companion object{
        fun newInstance(listadoI : ArrayList<pokemonResul>, listadoSI : ArrayList<pokemonResul>, searchI : String) : pokeFragmentAll {
            //En la funcion de la instancia se aprovecha para pasar datos o variables de una clase a otra, con los parametros de la funcion.
            var pokeAll = pokeFragmentAll()
            pokeAll.listado = listadoI
            pokeAll.listadoSearch = listadoSI
            pokeAll.search = searchI
            return pokeAll
        }
    }

    //Se crea una interface con metodos para colocar en un onClickListener.
    //Se crea un metodo por cada orientacion de pantalla.
    //La finalidad de esta interface es poder obtener dichos metodos desde el mainActivity hasta la propia clase.
    interface pokeListener{
        fun portraitClickListener(pokemon : pokemonResul)
        fun landscapeClickListener(pokemon: pokemonResul)
        fun recargar()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        var view = inflater.inflate(R.layout.fragment_poke_fragment_all, container, false)

        viewGlobal = view

        view.et_name_pokemon.isFocusableInTouchMode = false

        view.et_name_pokemon.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                et_name_pokemon.isFocusableInTouchMode = true
                return false
            }
        })

        var orientation = resources.configuration.orientation

        view.tv_nothing.visibility = View.GONE

        view.et_name_pokemon.setText(search)

        view.btn_search.setOnClickListener{
            quitarTeclado()
            buscarDatos(listado, orientation)
            view.et_name_pokemon.isFocusable = false
        }

        view.btn_all.setOnClickListener{
            initRecycler(orientation, listado)
            quitarTeclado()
            listadoSearch.clear()
            view.et_name_pokemon.isFocusable = false
            view.et_name_pokemon.setText("")
            view.tv_nothing.visibility = View.GONE
            listenerTool?.recargar()
        }

        view.et_name_pokemon.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                quitarTeclado()
                buscarDatos(listado, orientation)
            }
        }

        if (listadoSearch.size != 0){
            initRecycler(orientation, listadoSearch)
        } else{
            initRecycler(orientation, listado)
        }

        return view
    }

    //Funcion que sirve para que al tener activado el teclado virtual del telefono, este se cierre.
    private fun quitarTeclado(){
        var inm : InputMethodManager = context?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        inm.hideSoftInputFromWindow(viewGlobal.et_name_pokemon.windowToken, 0)
    }

    //Funcion que inicializa el recyclerView.
    private fun initRecycler(orientation : Int,listado : ArrayList<pokemonResul>){
        /*Con la ayuda de una clase externa, AdapterDatos, se llena el recyclerView,
        * pero a su ve con el if de acontinuacion se coloca como parametro el metodo correspondiente a la orientacion de la pantalla,
        * ya sea portrait o landscape, dichos metodos se encuentran como interface y son sobreescritos y llenados en el MainActivity*/
        var adapterIni : AdapterDatos? = null
        if(orientation == Configuration.ORIENTATION_PORTRAIT){
            adapterIni = AdapterDatos(listado, {pokemon:pokemonResul->listenerTool?.portraitClickListener(pokemon)})
        }
        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            adapterIni = AdapterDatos(listado, {pokemon:pokemonResul->listenerTool?.landscapeClickListener(pokemon)})
        }

        viewGlobal.rv_pokemon.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = adapterIni
        }
    }

    //Funcion que busca a un pokemon por su respectivo nombre o en base a iniciales.
    private fun buscarDatos(listado : ArrayList<pokemonResul>, orientation: Int){
        if(viewGlobal.et_name_pokemon.text.toString().equals("")){
            //Si en el caso no hay nada escrito en el buscador, el recyclerView devuelve la lista completa de los pokemons.
            initRecycler(orientation, listado)
            viewGlobal.tv_nothing.visibility = View.GONE
        } else{
            /*En el caso no esta vacio el buscador, intentara buscar recurrencias en la lista y en el caso no encuentre ninguna,
            * simplemente mostrara un mensaje que no encontro nada*/
            listadoSearch.clear()
            for (i in 0 .. (listado.size-1)){
                if(listado[i].name.startsWith(viewGlobal.et_name_pokemon.text.toString().toLowerCase())){
                    var datoU = pokemonResul(listado[i].name, listado[i].url)
                    listadoSearch.add(datoU)
                }
            }
            if (listadoSearch.size == 0){
                viewGlobal.tv_nothing.visibility = View.VISIBLE
            } else{
                viewGlobal.tv_nothing.visibility = View.GONE
            }
            initRecycler(orientation, listadoSearch)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //En el if, se valida que la clase esta implementada en el MainActivity, en el caso que no siemplemente mostrará una excepcion.
        if (context is pokeListener) {
            //La variable que contiene el nombre de la interface necesita del contexto donde se esta implementando.
            listenerTool = context
        } else {
            throw RuntimeException(context.toString() + " must implement pokeListener") as Throwable
        }
    }

    override fun onDetach() {
        super.onDetach()
        //Se quita la referencia del contexto a la variable.
        listenerTool = null
    }
}
