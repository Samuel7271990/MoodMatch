package com.example.moodmatch

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso


class DetalleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle)

        val imagen = intent.getStringExtra("imagen")
        val titulo = intent.getStringExtra("titulo")
        val descripcion = intent.getStringExtra("descripcion")
        val plataformas = intent.getStringExtra("plataformas")


        val imageView = findViewById<ImageView>(R.id.imagenDetalle)

        Log.d("DetalleActivity", "Imagen recibida: $imagen")

        if (!imagen.isNullOrBlank()) {
            if (imagen.startsWith("http")) {
                Log.d("DetalleActivity", "Es una URL, cargando con Picasso")
                Picasso.get().load(imagen).into(imageView)
            } else {
                val nombreNormalizado = imagen
                    .substringBeforeLast(".")
                    .lowercase()
                    .replace("[^a-z0-9_]".toRegex(), "")

                Log.d("DetalleActivity", "Nombre normalizado: $nombreNormalizado")
                val resId = resources.getIdentifier(nombreNormalizado, "drawable", packageName)

                Log.d("DetalleActivity", "Resource ID: $resId")
                if (resId != 0) {
                    imageView.setImageResource(resId)
                } else {
                    Log.e("DetalleActivity", "No se encontró recurso drawable para $nombreNormalizado")
                    imageView.setImageResource(R.drawable.amelie)
                }
            }
        } else {
            Log.e("DetalleActivity", "No se recibió imagen")
            imageView.setImageResource(R.drawable.amelie)
        }

        findViewById<TextView>(R.id.tituloDetalle).text = titulo
        findViewById<TextView>(R.id.descripcionDetalle).text = descripcion
        findViewById<TextView>(R.id.plataformasDetalle).text = plataformas

    }
}









