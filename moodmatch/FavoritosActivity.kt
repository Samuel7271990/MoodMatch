package com.example.moodmatch
import android.database.Cursor
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class FavoritosActivity : AppCompatActivity() {
    private lateinit var dbHelper: MoodMatchDBHelper
    private lateinit var favoritos: MutableList<Recomendacion>
    private lateinit var adapter: FavoritosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favoritos)

        dbHelper = MoodMatchDBHelper(this)
        favoritos = obtenerFavoritosDesdeBD().toMutableList()

        val listView = findViewById<ListView>(R.id.listaFavoritos)
        adapter = FavoritosAdapter(this, favoritos)
        listView.adapter = adapter

        listView.setOnItemLongClickListener { _, _, position, _ ->
            val favorito = favoritos[position]
            eliminarFavorito(1, favorito.id)

            favoritos.removeAt(position)
            adapter.notifyDataSetChanged()

            Toast.makeText(this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun obtenerFavoritosDesdeBD(): List<Recomendacion> {
        val favoritos = mutableListOf<Recomendacion>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT r.id, r.tipo, r.titulo, r.descripcion, r.estadoAnimoId, r.imagen, r.plataformas
            FROM Recomendacion r
            INNER JOIN Favorito f ON r.id = f.recomendacionId
            WHERE f.usuarioId = ?
            """.trimIndent(), arrayOf("1")
        )

        with(cursor) {
            while (moveToNext()) {

                val recomendacion = Recomendacion(
                    id = getInt(0),
                    tipo = getString(1),
                    titulo = getString(2),
                    descripcion = getString(3),
                    estadoAnimoId = getInt(4),
                    imagen = getStringOrNull(5) ?: "",
                    plataformas = getStringOrNull(6) ?: "",

                    )
                favoritos.add(recomendacion)
            }
            close()
        }
        return favoritos
    }

    fun Cursor.getStringOrNull(index: Int): String? {
        return if (isNull(index)) null else getString(index)
    }

    private fun eliminarFavorito(usuarioId: Int, recomendacionId: Int) {
        val db = dbHelper.writableDatabase
        db.delete(
            "Favorito",
            "usuarioId=? AND recomendacionId=?",
            arrayOf(usuarioId.toString(), recomendacionId.toString())
        )
    }
}