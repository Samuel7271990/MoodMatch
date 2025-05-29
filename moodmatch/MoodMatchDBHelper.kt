package com.example.moodmatch


import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MoodMatchDBHelper(private val appContext: Context) :
    SQLiteOpenHelper(appContext, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "moodmatch.db"
        private const val DATABASE_VERSION = 3
    }
    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL("CREATE TABLE EstadoAnimo (id INTEGER PRIMARY KEY, nombre TEXT)")
        db.execSQL("CREATE TABLE Recomendacion (id INTEGER PRIMARY KEY AUTOINCREMENT, tipo TEXT, titulo TEXT, descripcion TEXT, estadoAnimoId INTEGER, imagen TEXT, plataformas TEXT)")
        db.execSQL("CREATE TABLE Usuario (id INTEGER PRIMARY KEY, nombre TEXT,email TEXT)")
        db.execSQL("""
        CREATE TABLE Favorito (
            usuarioId INTEGER,
            recomendacionId INTEGER,
            PRIMARY KEY(usuarioId, recomendacionId),
            FOREIGN KEY(usuarioId) REFERENCES Usuario(id) ON DELETE CASCADE,
            FOREIGN KEY(recomendacionId) REFERENCES Recomendacion(id) ON DELETE CASCADE
        )
    """)
        db.execSQL("INSERT INTO EstadoAnimo (id, nombre) VALUES (1, 'Feliz'), (2, 'Triste'), (3, 'Ansioso'), (4, 'Relajado')")

        try {
            val inputStream = appContext.assets.open("recomendaciones.json")
            val json = inputStream.bufferedReader(Charsets.UTF_8).readText()

            val gson = Gson()
            val type = object : TypeToken<List<Recomendacion>>() {}.type
            val recomendaciones: List<Recomendacion> = gson.fromJson(json, type)

            for (rec in recomendaciones) {
                Log.d("MoodMatch", "Insertado: ${rec.titulo}")
                val values = ContentValues().apply {
                    put("tipo", rec.tipo.lowercase())
                    put("titulo", rec.titulo)
                    put("descripcion", rec.descripcion)
                    put("estadoAnimoId", rec.estadoAnimoId)
                    put("imagen", rec.imagen)
                    put("plataformas", rec.plataformas)
                }
                db.insert("Recomendacion", null, values)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ...


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Favorito")
        db.execSQL("DROP TABLE IF EXISTS Recomendacion")
        db.execSQL("DROP TABLE IF EXISTS EstadoAnimo")
        db.execSQL("DROP TABLE IF EXISTS Usuario")
        onCreate(db)
    }
}
