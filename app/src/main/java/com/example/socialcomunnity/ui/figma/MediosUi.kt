package com.example.socialcomunnity.ui.figma

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.LruCache
import android.media.MediaPlayer
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.socialcomunnity.nube.Medios
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request

/** Foto o video que la persona eligió en la pantalla "Crear". */
data class MedioElegido(val uri: Uri, val esVideo: Boolean)

// ---------------------------------------------------------------- Direcciones de Cloudinary

/** Foto reducida y comprimida por Cloudinary, para que cargue rápido. */
fun urlImagenOptimizada(url: String, ancho: Int = 900): String =
    url.replace("/upload/", "/upload/w_$ancho,c_limit,q_auto/")

/** Foto de perfil cuadrada y pequeña, centrada en la cara. */
fun urlFotoPerfil(url: String): String =
    url.replace("/upload/", "/upload/w_240,h_240,c_fill,g_face,q_auto/")

/** Primer cuadro del video, como foto de portada. */
fun urlPortadaVideo(url: String): String =
    url.substringBeforeLast('.').replace("/upload/", "/upload/so_0,w_900,c_limit/") + ".jpg"

// ---------------------------------------------------------------- Imágenes de internet

internal object CacheImagenes {
    private val cache = object : LruCache<String, Bitmap>((Runtime.getRuntime().maxMemory() / 8).toInt()) {
        override fun sizeOf(key: String, value: Bitmap): Int = value.byteCount
    }

    fun obtener(url: String): ImageBitmap? = cache.get(url)?.asImageBitmap()

    /** Descarga la imagen (se llama fuera del hilo principal). */
    fun cargar(url: String): ImageBitmap? {
        obtener(url)?.let { return it }
        return try {
            Medios.cliente.newCall(Request.Builder().url(url).build()).execute().use { respuesta ->
                if (!respuesta.isSuccessful) return null
                val bytes = respuesta.body?.bytes() ?: return null
                val imagen = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return null
                cache.put(url, imagen)
                imagen.asImageBitmap()
            }
        } catch (e: Exception) {
            null
        }
    }
}

/** Muestra una imagen de internet; mientras carga se ve un cuadro gris. */
@Composable
fun ImagenRemota(
    url: String,
    modifier: Modifier = Modifier,
    escala: ContentScale = ContentScale.Crop
) {
    val imagen by produceState<ImageBitmap?>(initialValue = CacheImagenes.obtener(url), key1 = url) {
        if (value == null) {
            value = withContext(Dispatchers.IO) { CacheImagenes.cargar(url) }
        }
    }
    val actual = imagen
    if (actual != null) {
        Image(bitmap = actual, contentDescription = null, modifier = modifier, contentScale = escala)
    } else {
        Box(modifier = modifier.background(SC.Borde))
    }
}

/** Vista previa de un archivo local (foto, o primer cuadro de un video). */
@Composable
fun VistaPreviaMedio(medio: MedioElegido, modifier: Modifier = Modifier) {
    val contexto = androidx.compose.ui.platform.LocalContext.current
    val imagen by produceState<ImageBitmap?>(initialValue = null, key1 = medio.uri) {
        value = withContext(Dispatchers.IO) { Medios.miniatura(contexto, medio.uri, medio.esVideo) }
    }
    val actual = imagen
    if (actual != null) {
        Image(bitmap = actual, contentDescription = null, modifier = modifier, contentScale = ContentScale.Crop)
    } else {
        Box(modifier = modifier.background(Color.DarkGray))
    }
}

// ---------------------------------------------------------------- Reproductor de video

private fun tiempo(milis: Int): String {
    val total = milis / 1000
    return "%d:%02d".format(total / 60, total % 60)
}

/**
 * Reproductor a pantalla completa con controles propios:
 * toca la pantalla para mostrar u ocultar los controles, que se esconden solos a los 3 segundos.
 */
@Composable
fun DialogoVideo(url: String, alCerrar: () -> Unit) {
    val referencia = remember { arrayOfNulls<VideoView>(1) }
    var cargando by remember { mutableStateOf(true) }
    var reproduciendo by remember { mutableStateOf(false) }
    var terminado by remember { mutableStateOf(false) }
    var fallo by remember { mutableStateOf(false) }
    var duracion by remember { mutableStateOf(0) }
    var posicion by remember { mutableStateOf(0) }
    var arrastrando by remember { mutableStateOf(false) }
    var controles by remember { mutableStateOf(true) }
    var toques by remember { mutableStateOf(0) }

    // Mantiene al día la barra de progreso
    LaunchedEffect(Unit) {
        while (true) {
            val video = referencia[0]
            if (video != null) {
                if (!arrastrando) posicion = video.currentPosition
                reproduciendo = video.isPlaying
            }
            delay(250)
        }
    }
    // Esconde los controles a los 3 segundos si el video se está reproduciendo
    LaunchedEffect(controles, reproduciendo, toques, arrastrando) {
        if (controles && reproduciendo && !arrastrando) {
            delay(3000)
            controles = false
        }
    }

    Dialog(
        onDismissRequest = alCerrar,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            AndroidView(
                factory = { contexto ->
                    VideoView(contexto).apply {
                        referencia[0] = this
                        setOnPreparedListener { reproductor ->
                            duracion = reproductor.duration
                            cargando = false
                            start()
                        }
                        setOnCompletionListener {
                            terminado = true
                            reproduciendo = false
                            controles = true
                        }
                        setOnErrorListener { _, _, _ ->
                            fallo = true
                            cargando = false
                            true
                        }
                        setOnInfoListener { _, que, _ ->
                            if (que == MediaPlayer.MEDIA_INFO_BUFFERING_START) cargando = true
                            if (que == MediaPlayer.MEDIA_INFO_BUFFERING_END ||
                                que == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START
                            ) {
                                cargando = false
                            }
                            false
                        }
                        setVideoURI(Uri.parse(url))
                    }
                },
                onRelease = { it.stopPlayback() },
                modifier = Modifier.fillMaxSize()
            )

            // Capa que recibe el toque para mostrar u ocultar los controles
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        controles = !controles
                        toques++
                    }
            )

            if (cargando && !fallo) {
                CircularProgressIndicator(
                    color = SC.Naranja,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            if (fallo) {
                Text(
                    "No se pudo reproducir el video.",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Los controles solo se desvanecen (sin encogerse hacia una esquina, que es la animación por defecto)
            AnimatedVisibility(
                visible = controles || terminado || fallo,
                enter = fadeIn(animationSpec = tween(150)),
                exit = fadeOut(animationSpec = tween(250))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Sombras arriba y abajo para que se lean los controles
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)))
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))))
                    )

                    // Cerrar
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .statusBarsPadding()
                            .padding(12.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.45f))
                            .clickable { alCerrar() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                    }

                    // Reproducir, pausar o repetir
                    if (!cargando && !fallo) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.92f))
                                .clickable {
                                    val video = referencia[0]
                                    if (video != null) {
                                        if (terminado) {
                                            video.seekTo(0)
                                            video.start()
                                            terminado = false
                                        } else if (video.isPlaying) {
                                            video.pause()
                                        } else {
                                            video.start()
                                        }
                                        reproduciendo = video.isPlaying
                                        toques++
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when {
                                    terminado -> Icons.Default.Refresh
                                    reproduciendo -> IconoPausa
                                    else -> Icons.Default.PlayArrow
                                },
                                contentDescription = "Reproducir o pausar",
                                tint = SC.Naranja,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }

                    // Barra de progreso y tiempos
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Slider(
                            value = if (duracion > 0) posicion.toFloat() / duracion else 0f,
                            onValueChange = {
                                arrastrando = true
                                posicion = (it * duracion).toInt()
                            },
                            onValueChangeFinished = {
                                referencia[0]?.seekTo(posicion)
                                terminado = false
                                arrastrando = false
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = SC.Naranja,
                                activeTrackColor = SC.Naranja,
                                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(tiempo(posicion), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Text(tiempo(duracion), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}
