package com.example.moodmatch

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView




class RecomendacionAdapter(private val context: Context, private val recomendaciones: List<Recomendacion>) : BaseAdapter() {
    override fun getCount() = recomendaciones.size
    override fun getItem(position: Int) = recomendaciones[position]
    override fun getItemId(position: Int) = recomendaciones[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_recomendacion, parent, false)

        val titulo = view.findViewById<TextView>(R.id.tvTitulo)
        val descripcion = view.findViewById<TextView>(R.id.tvDescripcion)

        val recomendacion = recomendaciones[position]
        titulo.text = recomendacion.titulo
        descripcion.text = recomendacion.descripcion

        return view
    }
}