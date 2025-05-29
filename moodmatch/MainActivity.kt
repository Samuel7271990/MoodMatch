package com.example.moodmatch

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.nio.charset.StandardCharsets

class MainActivity : AppCompatActivity() {

    private lateinit var spinnerEstados: Spinner
    private lateinit var spinnerTipo: Spinner
    private lateinit var btnVerRecomendacion: Button
    private lateinit var dbHelper: MoodMatchDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinnerEstados = findViewById(R.id.spinnerEstados)
        spinnerTipo = findViewById(R.id.spinnerTipo)
        btnVerRecomendacion = findViewById(R.id.btnVerRecomendacion)

        val btnVerFavoritos = findViewById<Button>(R.id.btnVerFavoritos)
        btnVerFavoritos.setOnClickListener {
            val intent = Intent(this, FavoritosActivity::class.java)
            startActivity(intent)
            enviarJSONaServidor()
        }


        dbHelper = MoodMatchDBHelper(this)

        val estados = obtenerEstados()
        val tipos = listOf("Pelicula", "Serie", "Libro")

        val adapterEstado = ArrayAdapter(this, android.R.layout.simple_spinner_item, estados.map { it.nombre })
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEstados.adapter = adapterEstado

        val adapterTipo = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos)
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipo.adapter = adapterTipo

        btnVerRecomendacion.setOnClickListener {
            val estadoSeleccionado = estados[spinnerEstados.selectedItemPosition]
            val tipoSeleccionado = tipos[spinnerTipo.selectedItemPosition]

            val intent = Intent(this, RecomendacionesActivity::class.java)
            intent.putExtra("estado_id", estadoSeleccionado.id)
            intent.putExtra("tipo", tipoSeleccionado)
            startActivity(intent)
        }
    }

    private fun obtenerEstados(): List<EstadoDeAnimo> {
        val estados = mutableListOf<EstadoDeAnimo>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, nombre FROM EstadoAnimo", null)

        cursor.use {
            while (it.moveToNext()) {
                val estado = EstadoDeAnimo(
                    id = it.getInt(it.getColumnIndexOrThrow("id")),
                    nombre = it.getString(it.getColumnIndexOrThrow("nombre"))
                )
                estados.add(estado)
            }
        }
        return estados
    }


    private fun enviarJSONaServidor(){
        try{
            //Leer archivos JSON desde assets
            val inputStream = assets.open("recomendaciones.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()

            val json = String(buffer, StandardCharsets.UTF_8)

            //enviar JSON con volley
            val url = "http://192.168.0.16/moodmatch/insertar_json.php"//emulador android estudio
            val queue = Volley.newRequestQueue(this)

            val request = object : StringRequest(Method.POST, url,
                {response ->
                    println("Respuesta del servidor: $response")
                },
                { error->
                    error.printStackTrace()
                }
            ){
                override fun getBody(): ByteArray {
                    return json.toByteArray(StandardCharsets.UTF_8)
                }

                override fun getBodyContentType(): String {
                    return "moodmatch/json; charset=utf-8"
                }
            }

            queue.add(request)

        }catch (e: Exception){
            e.printStackTrace()
        }


    }




}
