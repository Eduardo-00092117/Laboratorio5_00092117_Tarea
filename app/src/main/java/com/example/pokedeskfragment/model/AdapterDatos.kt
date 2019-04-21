package com.example.pokedeskfragment.model

import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.Button
import com.example.pokedeskfragment.*
import com.example.pokedeskfragment.data.pokemonResul

/*En esta clase lo que cambia es que como parametro se pasa una funcion,
que lo que hara es pasar una funcion especifica dependiendo de la orientacion de la pantalla.*/
class AdapterDatos(var listado : ArrayList<pokemonResul>, val clickListener: (pokemonResul) -> Unit) : RecyclerView.Adapter<AdapterDatos.ViewHolderDatos>(){

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ViewHolderDatos {
        var view : View = LayoutInflater.from(parent.context).inflate(R.layout.item_pokemon, null, false)
        view.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
        return ViewHolderDatos(view)
    }

    override fun getItemCount(): Int = listado.size

    //Al mandar a llamar la funcion bind de la clase ViewHolder se manda por parametro el item del pokemon y la funcion especifica del parametro.
    override fun onBindViewHolder(holder: ViewHolderDatos, position: Int) = holder.bin(listado[position], clickListener)

    inner class ViewHolderDatos(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var nombre : Button
        //En el metodo bind, se le pasa un parametro donde se coloca la funcion especifica de la orientacion que se desea.
        fun bin(item : pokemonResul, clickListener: (pokemonResul) -> Unit) = with(itemView){
            nombre = itemView.findViewById(R.id.btn_nombre)
            nombre.text = item.name
            //Se hace la funcion onClickListener, lo cual solo contiene la funcion que se le esta pasando por parametro en el metodo bind.
            nombre.setOnClickListener{ clickListener(item) }
        }
    }
}