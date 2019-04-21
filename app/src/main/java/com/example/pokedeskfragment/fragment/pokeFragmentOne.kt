package com.example.pokedeskfragment.fragment

import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pokedeskfragment.R
import com.example.pokedeskfragment.data.imageData
import com.example.pokedeskfragment.utilities.NetworkUtil
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.fragment_poke_fragment_one.view.*
import java.io.IOException


class pokeFragmentOne : Fragment() {

    //Se declaran e inicializan variables a utilizar en la clase.
    var URL = ""; var nombre = ""
    lateinit var pokeArray : JsonArray; lateinit var stats : JsonArray; lateinit var type : JsonArray
    var pokeParse : JsonParser = JsonParser()
    lateinit var pokeOb : JsonObject
    var peso : Double = 0.0
    var alto : Int = 0; var base : Int = 0
    var imagenes : ArrayList<imageData> = ArrayList()
    lateinit var viewGlobal : View

    //Se crea funcion estatica, que serviria para obtener una instancia de la clase en cuestion, es decir pokeFragmentOne.
    //Se crea un singleton
    companion object{
        fun newInstance(url : String, nombre : String) : pokeFragmentOne {
            //En la funcion de la instancia se aprovecha para pasar datos o variables de una clase a otra, con los parametros de la funcion.
            var pokeOne = pokeFragmentOne()
            pokeOne.URL = url
            pokeOne.nombre = nombre
            return pokeOne
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_poke_fragment_one, container, false)

        viewGlobal = view

        guardarImagenes()

        if (URL != ""){
            FetchPokemonTask().execute("")
        }

        return view
    }

    //Funcion que guardar las imagenes en una lista, osea llaman un metodo donde las guarda.
    fun guardarImagenes(){
        agregarImagen("poison", R.drawable.poison)
        agregarImagen("grass", R.drawable.grass)
        agregarImagen("bug", R.drawable.bug)
        agregarImagen("dragon", R.drawable.dragon)
        agregarImagen("ice", R.drawable.ice)
        agregarImagen("fighting", R.drawable.fighting)
        agregarImagen("fire", R.drawable.fire)
        agregarImagen("flying", R.drawable.flying)
        agregarImagen("ghost", R.drawable.ghost)
        agregarImagen("ground", R.drawable.ground)
        agregarImagen("electric", R.drawable.electric)
        agregarImagen("psychic", R.drawable.psychic)
        agregarImagen("rock", R.drawable.rock)
        agregarImagen("water", R.drawable.water)
        agregarImagen("dark", R.drawable.dark)
        agregarImagen("steel", R.drawable.steel)
        agregarImagen("fairy", R.drawable.fairy)
        agregarImagen("bird", R.drawable.bird)
        agregarImagen("normal", R.drawable.poke)
    }

    //Funcion donde se guardan las imagenes en la lista, previamente declarada arriba.
    fun agregarImagen(tipo: String, imagen : Int){
        var nImagen = imageData(tipo, imagen)
        imagenes.add(nImagen)
    }

    //Clase donde se busca la informacion de un pokemon, y se parcea.
    //Y se coloca la informacion en su respectivo TextView.
    inner class FetchPokemonTask : AsyncTask<String, Void, String>(){
        lateinit var pDialog : ProgressDialog

        override fun onPreExecute() {
            super.onPreExecute()
            pDialog = ProgressDialog(viewGlobal.context)
            pDialog.setMessage("Loading the pokemons")
            pDialog.setCancelable(true)
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            pDialog.show()
        }

        override fun doInBackground(vararg params: String?): String {
            var url = NetworkUtil.getUrl(URL)
            try {
                var result = NetworkUtil.getResponseFromHttpUrl(url)
                result = "[" + result + "]";
                pokeArray = pokeParse.parse(result).asJsonArray;

                for (i in 0 .. (pokeArray.size()-1)){
                    var pokeElement : JsonElement = pokeArray.get(i)
                    pokeOb = pokeElement.asJsonObject
                    peso = pokeOb.get("weight").asDouble /10
                    alto = pokeOb.get("height").asInt *10
                    base = pokeOb.get("base_experience").asInt
                    type = pokeOb.get("types").asJsonArray
                    stats = pokeOb.get("stats").asJsonArray
                }

                return result
            } catch (e : IOException){
                e.printStackTrace()
                return ""
            }
        }

        override fun onPostExecute(s: String?) {
            super.onPostExecute(s)
            if (s != null){
                var pokePa = JsonParser()
                var typeOb : JsonObject; var typeObjI : JsonObject
                var typeIS : String; var cantTipo = ""
                viewGlobal.tv_label_touch.visibility = View.VISIBLE
                viewGlobal.tv_nombre_poke.text = "Name: " + nombre
                viewGlobal.tv_altura_poke.text = "Height: " + alto + " cm"
                viewGlobal.tv_peso_poke.text = "Weight: " + peso + " kg"
                viewGlobal.tv_experiencia_poke.text = "Base experience: " + base

                var datoE : ArrayList<Int> = ArrayList()

                for (i in 0 .. (type.size()-1)){
                    typeIS = type.get(i).toString()
                    typeOb = pokePa.parse(typeIS).asJsonObject
                    typeObjI = typeOb.get("type").asJsonObject
                    for (j in 0 .. (imagenes.size-1)){
                        if (imagenes[j].tipo.equals(typeObjI.get("name").asString) &&
                                !typeObjI.get("name").asString.equals("normal")) {
                            datoE.add(imagenes[j].imagen)
                        }
                    }
                    if (type.size() == i + 1) {
                        cantTipo += typeObjI.get("name").asString
                    } else {
                        cantTipo += typeObjI.get("name").asString + "/"
                    }
                }
                if(datoE.size != 0){
                    datoE.add(imagenes.get(18).imagen)
                } else{
                    viewGlobal.tv_label_touch.visibility = View.INVISIBLE
                }

                var i = 0

                viewGlobal.tv_pokeImage.setOnClickListener{
                    if (datoE.size != 0){
                        viewGlobal.tv_pokeImage.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, datoE[i])
                        if (datoE.size == i +1){
                            i = -1
                        }
                        i += 1
                    }
                }

                var statsI = JsonParser()

                viewGlobal.tv_sp.text = statsI.parse(stats.get(0).toString()).asJsonObject.get("base_stat").toString()
                viewGlobal.tv_s_d.text = statsI.parse(stats.get(1).toString()).asJsonObject.get("base_stat").toString()
                viewGlobal.tv_s_a.text = statsI.parse(stats.get(2).toString()).asJsonObject.get("base_stat").toString()
                viewGlobal.tv_d.text = statsI.parse(stats.get(3).toString()).asJsonObject.get("base_stat").toString()
                viewGlobal.tv_a.text = statsI.parse(stats.get(4).toString()).asJsonObject.get("base_stat").toString()
                viewGlobal.tv_hp.text = statsI.parse(stats.get(5).toString()).asJsonObject.get("base_stat").toString()

                viewGlobal.tv_tipo.text = "Type: "+cantTipo
            }

            pDialog.dismiss()
        }

    }
}
