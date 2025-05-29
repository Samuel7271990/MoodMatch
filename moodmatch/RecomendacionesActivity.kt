package com.example.moodmatch

import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class RecomendacionesActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var dbHelper: MoodMatchDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recomendaciones)

        listView = findViewById(R.id.listViewRecomendaciones)
        dbHelper = MoodMatchDBHelper(this)

        val estadoId = intent.getIntExtra("estado_id", 1)
        val tipo = intent.getStringExtra("tipo")?.lowercase() ?: ""

        if (tipo.isBlank()) {
            Toast.makeText(this, "Tipo de recomendación no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        Thread {
            val recomendaciones = obtenerRecomendaciones(estadoId, tipo)

            runOnUiThread {
                Log.d("RECOMENDACIONES", "Total recomendaciones: ${recomendaciones.size}")
                val adapter = RecomendacionAdapter(this, recomendaciones)
                listView.adapter = adapter

                listView.setOnItemClickListener { _, _, position, _ ->
                    val recomendacionSeleccionada = recomendaciones[position]
                    Log.d("RecomendacionesActivity", "Imagen seleccionada: ${recomendacionSeleccionada.imagen}")
                    val intent = Intent(this, DetalleActivity::class.java).apply {
                        putExtra("titulo", recomendacionSeleccionada.titulo)
                        putExtra("descripcion", recomendacionSeleccionada.descripcion)
                        putExtra("imagen", recomendacionSeleccionada.imagen)
                        putExtra("plataformas", recomendacionSeleccionada.plataformas)
                    }
                    startActivity(intent)
                }

                listView.setOnItemLongClickListener { _, _, position, _ ->
                    val recomendacionSeleccionada = recomendaciones[position]
                    guardarFavorito(1, recomendacionSeleccionada.id)
                    Toast.makeText(this, "Agregado a favoritos", Toast.LENGTH_SHORT).show()
                    true

                }


            }
        }.start()
    }

    private fun obtenerRecomendaciones(estadoId: Int, tipo: String): List<Recomendacion> {
        val lista = mutableListOf<Recomendacion>()
        try {
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery(
                "SELECT * FROM Recomendacion WHERE estadoAnimoId = ? AND tipo = ?",
                arrayOf(estadoId.toString(), tipo)
            )

            cursor.use {
                while (it.moveToNext()) {
                    val imagenDb = it.getStringOrNull(it.getColumnIndexOrThrow("imagen")) ?: ""
                    Log.d("RecomendacionDAO", "Imagen leída de BD: '$imagenDb'")

                    val rec = Recomendacion(
                        id = it.getInt(it.getColumnIndexOrThrow("id")),
                        tipo = it.getString(it.getColumnIndexOrThrow("tipo")),
                        titulo = it.getString(it.getColumnIndexOrThrow("titulo")) ,
                        descripcion = it.getString(it.getColumnIndexOrThrow("descripcion")) ,
                        estadoAnimoId = it.getInt(it.getColumnIndexOrThrow("estadoAnimoId")),
                        imagen = it.getStringOrNull(it.getColumnIndexOrThrow("imagen")) ?: "",
                        plataformas = it.getStringOrNull(it.getColumnIndexOrThrow("plataformas")) ?: "",

                        )
                    lista.add(rec)
                }
            }

            db.close()
        } catch (e: Exception) {
            Log.e("RecomendacionDAO", "Error al obtener recomendaciones", e)
        }
        return lista
    }
    fun Cursor.getStringOrNull(columnIndex: Int): String? {
        return if (isNull(columnIndex)) null else getString(columnIndex)
    }

    private fun guardarFavorito(usuarioId: Int, recomendacionId: Int) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("usuarioId", usuarioId)
            put("recomendacionId", recomendacionId)
        }
        db.insert("Favorito", null, values)
    }
}