package com.example.moodmatch

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class FavoritosAdapter(private val context: Context, private val favoritos: List<Recomendacion>) : BaseAdapter() {

    override fun getCount(): Int = favoritos.size
    override fun getItem(position: Int): Any = favoritos[position]
    override fun getItemId(position: Int): Long = favoritos[position].estadoAnimoId.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_favorito, parent, false)

        val titulo = view.findViewById<TextView>(R.id.tituloFavorito)
        val descripcion = view.findViewById<TextView>(R.id.descripcionFavorito)

        val favorito = favoritos[position]
        titulo.text = favorito.titulo
        descripcion.text = favorito.descripcion

        return view
    }
}