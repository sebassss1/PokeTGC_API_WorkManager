package com.example.poketgc_api

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.poketgc_api.Data.PokeCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * Objeto de utilidad para descargar el bitmap de una carta mediante Coil y guardarlo
 * físicamente en el almacenamiento del dispositivo (Galería) usando MediaStore.
 */
object GuardarCarta {

    /**
     * Descarga la imagen de una carta, la convierte en un bitmap y la guarda en la galería.
     * @param context Contexto de la aplicación.
     * @param pokeCard La carta cuya imagen se va a guardar.
     */
    suspend fun savePokemonImage(context: Context, pokeCard: PokeCard) {
        val imageUrl = "${pokeCard.imagen}/high.png"
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .allowHardware(false) // Necesario para obtener el bitmap
            .build()

        val result = loader.execute(request)
        if (result is SuccessResult) {
            val bitmap = (result.drawable as? BitmapDrawable)?.bitmap
            if (bitmap != null) {
                val fileName = "${pokeCard.nombre?.replace(" ", "_") ?: "pokemon"}_${pokeCard.id}.jpg"
                val saved = saveBitmapToGallery(context, bitmap, fileName)
                withContext(Dispatchers.Main) {
                    if (saved) {
                        Toast.makeText(context, "Carta guardada en galería (PokeCards)", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Error al guardar la carta", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    /**
     * Guarda un bitmap en la galería del dispositivo, organizándolo en un álbum específico.
     * @param context Contexto de la aplicación.
     * @param bitmap El bitmap a guardar.
     * @param fileName El nombre del archivo para la imagen guardada.
     * @return Boolean `true` si se guardó correctamente, `false` en caso contrario.
     */
    private fun saveBitmapToGallery(context: Context, bitmap: Bitmap, fileName: String): Boolean {
        val albumName = "PokeCards"
        val outputStream: OutputStream?
        
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/$albumName")
                }
                val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                outputStream = imageUri?.let { resolver.openOutputStream(it) }
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
                val albumDir = File(imagesDir, albumName)
                if (!albumDir.exists()) {
                    albumDir.mkdirs()
                }
                val file = File(albumDir, fileName)
                outputStream = FileOutputStream(file)
            }

            outputStream?.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
