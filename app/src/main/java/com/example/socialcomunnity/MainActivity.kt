
package com.example.socialcomunnity

import com.example.socialcomunnity.ui.figma.AppSocialCommunity
import com.example.socialcomunnity.ui.screens.inicio.PantallaInicio as PantallaFeedInicio
import com.example.socialcomunnity.ui.screens.notificaciones.PantallaNotificaciones as PantallaFeedNotificaciones
import android.os.Bundle
import android.os.SystemClock
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialcomunnity.base.BaseDatosHelper
import com.example.socialcomunnity.datos.ApiService
import com.example.socialcomunnity.datos.JsonHelper
import com.example.socialcomunnity.datos.UsuarioJsonHelper
import com.example.socialcomunnity.datos.XmlHelper
import com.example.socialcomunnity.modelo.Publicacion
import com.example.socialcomunnity.modelo.PublicacionApi
import org.json.JSONObject
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.sqrt

private object ColoresApp {
    var fondo by mutableStateOf(Color(0xFF171717))
    var blanco by mutableStateOf(Color(0xFFFFFFFF))
    var gris by mutableStateOf(Color(0xFF292929))
    var textoSecundario by mutableStateOf(Color.LightGray)

    fun aplicarTema(oscuro: Boolean) {
        fondo = if (oscuro) Color(0xFF171717) else Color(0xFFF5F5F5)
        blanco = if (oscuro) Color(0xFFFFFFFF) else Color(0xFF202020)
        gris = if (oscuro) Color(0xFF292929) else Color(0xFFFFFFFF)
        textoSecundario = if (oscuro) Color.LightGray else Color(0xFF666666)
    }
}

private val Naranja = Color(0xFFFF7900)
private val Fondo get() = ColoresApp.fondo
private val Blanco get() = ColoresApp.blanco
private val Gris get() = ColoresApp.gris
private val TextoSecundario get() = ColoresApp.textoSecundario

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Termina la pantalla de arranque (color y logo) y vuelve al tema normal de la app
        setTheme(R.style.Theme_SocialComunnity)
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                AppSocialCommunity()
            }
        }
    }
}

@Composable
fun SocialComunnityApp() {
    val context = LocalContext.current
    val preferencias = remember {
        context.getSharedPreferences(
            "socialcomunnity_preferencias",
            Context.MODE_PRIVATE
        )
    }

    var temaOscuro by remember {
        mutableStateOf(preferencias.getBoolean("tema_oscuro", true))
    }

    LaunchedEffect(temaOscuro) {
        ColoresApp.aplicarTema(temaOscuro)
        preferencias.edit().putBoolean("tema_oscuro", temaOscuro).apply()
    }

    val usuariosHelper = remember {
        UsuarioJsonHelper(context).apply {
            inicializarAdmin()
        }
    }

    var pantalla by remember { mutableStateOf("inicio") }
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Fondo
    ) {
        when (pantalla) {
            "inicio" -> {
                PantallaInicio(
                    irALogin = {
                        error = ""
                        pantalla = "login"
                    },
                    irARegistro = {
                        usuario = ""
                        contrasena = ""
                        error = ""
                        pantalla = "registro"
                    }
                )
            }

            "login" -> {
                PantallaLogin(
                    usuario = usuario,
                    cambiarUsuario = {
                        usuario = it
                        error = ""
                    },
                    contrasena = contrasena,
                    cambiarContrasena = {
                        contrasena = it
                        error = ""
                    },
                    error = error,
                    iniciarSesion = {
                        if (usuariosHelper.validarUsuario(usuario, contrasena)) {
                            usuario = usuario.trim()
                            contrasena = ""
                            error = ""
                            pantalla = "principal"
                        } else {
                            error = "Usuario o contraseña incorrectos"
                        }
                    },
                    volver = {
                        error = ""
                        pantalla = "inicio"
                    }
                )
            }

            "registro" -> {
                PantallaRegistro(
                    registrar = { nuevoUsuario, nuevaContrasena ->
                        if (nuevoUsuario.isBlank() || nuevaContrasena.isBlank()) {
                            error = "Completa todos los campos"
                        } else if (
                            usuariosHelper.registrarUsuario(
                                nuevoUsuario,
                                nuevaContrasena
                            )
                        ) {
                            usuario = nuevoUsuario.trim()
                            contrasena = ""
                            error = "Cuenta creada. Ahora inicia sesión."
                            pantalla = "login"
                        } else {
                            error = "Ese usuario ya existe o no se pudo registrar"
                        }
                    },
                    volver = {
                        error = ""
                        pantalla = "inicio"
                    },
                    error = error
                )
            }

            "principal" -> {
                PantallaBienvenida(
                    usuario = usuario,
                    oscuro = temaOscuro,
                    cambiarTema = { temaOscuro = it },
                    cerrarSesion = {
                        usuario = ""
                        contrasena = ""
                        error = ""
                        pantalla = "inicio"
                    }
                )
            }
        }
    }
}

@Composable
fun PantallaInicio(
    irALogin: () -> Unit,
    irARegistro: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.imagen2),
            contentDescription = "Logo de SocialComunnity",
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "SocialComunnity",
            color = Naranja,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Conecta con tu comunidad",
            color = Blanco,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(35.dp))

        Text(
            text = "¡Bienvenido!",
            color = Blanco,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "¿Ya tienes una cuenta?",
            color = TextoSecundario,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(25.dp))

        Button(
            onClick = irALogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Naranja
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Ya tengo cuenta", color = Blanco)
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedButton(
            onClick = irARegistro,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Naranja
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Crear cuenta")
        }
    }
}

@Composable
fun PantallaLogin(
    usuario: String,
    cambiarUsuario: (String) -> Unit,
    contrasena: String,
    cambiarContrasena: (String) -> Unit,
    error: String,
    iniciarSesion: () -> Unit,
    volver: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.imagen2),
            contentDescription = "Logo de SocialComunnity",
            modifier = Modifier.size(115.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "SocialComunnity",
            color = Naranja,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Iniciar sesión",
            color = Blanco,
            fontSize = 23.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(25.dp))

        OutlinedTextField(
            value = usuario,
            onValueChange = cambiarUsuario,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Usuario") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Blanco,
                unfocusedTextColor = Blanco,
                focusedLabelColor = Naranja,
                unfocusedLabelColor = TextoSecundario,
                cursorColor = Naranja,
                focusedBorderColor = Naranja,
                unfocusedBorderColor = TextoSecundario
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = cambiarContrasena,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Blanco,
                unfocusedTextColor = Blanco,
                focusedLabelColor = Naranja,
                unfocusedLabelColor = TextoSecundario,
                cursorColor = Naranja,
                focusedBorderColor = Naranja,
                unfocusedBorderColor = TextoSecundario
            )
        )

        if (error.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = error,
                color = if (error.startsWith("Cuenta creada")) {
                    Color(0xFF8FE3A0)
                } else {
                    Color(0xFFFF6B6B)
                },
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = iniciarSesion,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Naranja,
                contentColor = Blanco
            )
        ) {
            Text(
                text = "Iniciar sesión",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }

        TextButton(onClick = volver) {
            Text("Volver", color = Naranja)
        }
    }
}

@Composable
fun PantallaRegistro(
    registrar: (String, String) -> Unit,
    volver: () -> Unit,
    error: String
) {
    var nuevoUsuario by remember { mutableStateOf("") }
    var nuevaContrasena by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.imagen2),
            contentDescription = "Logo de SocialComunnity",
            modifier = Modifier.size(110.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Crear cuenta",
            color = Naranja,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(25.dp))

        OutlinedTextField(
            value = nuevoUsuario,
            onValueChange = { nuevoUsuario = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nuevo usuario") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Blanco,
                unfocusedTextColor = Blanco,
                focusedLabelColor = Naranja,
                unfocusedLabelColor = TextoSecundario,
                cursorColor = Naranja,
                focusedBorderColor = Naranja,
                unfocusedBorderColor = TextoSecundario
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = nuevaContrasena,
            onValueChange = { nuevaContrasena = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nueva contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Blanco,
                unfocusedTextColor = Blanco,
                focusedLabelColor = Naranja,
                unfocusedLabelColor = TextoSecundario,
                cursorColor = Naranja,
                focusedBorderColor = Naranja,
                unfocusedBorderColor = TextoSecundario
            )
        )

        if (error.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = error,
                color = Color(0xFFFF6B6B),
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                registrar(nuevoUsuario, nuevaContrasena)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Naranja
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Registrarme", color = Blanco)
        }

        TextButton(onClick = volver) {
            Text("Volver", color = Naranja)
        }
    }
}

@Composable
fun PantallaBienvenida(
    usuario: String,
    oscuro: Boolean,
    cambiarTema: (Boolean) -> Unit,
    cerrarSesion: () -> Unit
) {
    var seccionSeleccionada by remember { mutableStateOf("inicio") }

    var contadorVideos by remember { mutableStateOf(0) }
    var contadorIniciado by remember { mutableStateOf(false) }

    LaunchedEffect(contadorIniciado) {
        if (contadorIniciado) {
            while (true) {
                delay(1000L)
                contadorVideos++
            }
        }
    }

    val transicionOvalo = rememberInfiniteTransition()

    val movimientoOvalo by transicionOvalo.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800),
            repeatMode = RepeatMode.Restart
        )
    )

    var inclinacionHorizontal by remember { mutableStateOf(0f) }
    var inclinacionVertical by remember { mutableStateOf(0f) }
    var numeroSacudidas by remember { mutableStateOf(0) }

    val desplazamientoRebote = remember { Animatable(0f) }

    LaunchedEffect(numeroSacudidas) {
        if (numeroSacudidas > 0) {
            desplazamientoRebote.snapTo(0f)

            desplazamientoRebote.animateTo(
                targetValue = -48f,
                animationSpec = tween(durationMillis = 130)
            )

            desplazamientoRebote.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = 0.35f,
                    stiffness = 500f
                )
            )
        }
    }

    val contextoSensor = LocalContext.current

    val administradorSensores = remember(contextoSensor) {
        contextoSensor.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    val acelerometro = remember(administradorSensores) {
        administradorSensores.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    DisposableEffect(administradorSensores, acelerometro) {
        if (acelerometro != null) {
            var ultimaSacudida = 0L

            val listener = object : SensorEventListener {
                override fun onSensorChanged(evento: SensorEvent) {
                    if (evento.values.size >= 3) {
                        val x = evento.values[0]
                        val y = evento.values[1]
                        val z = evento.values[2]

                        val aceleracionTotal = sqrt(x * x + y * y + z * z)
                        val ahora = SystemClock.elapsedRealtime()

                        if (
                            aceleracionTotal > 16.5f &&
                            ahora - ultimaSacudida > 650L
                        ) {
                            ultimaSacudida = ahora
                            numeroSacudidas++
                        }

                        val nuevoHorizontal =
                            (evento.values[0] * 4.5f).coerceIn(-40f, 40f)

                        val nuevoVertical =
                            (evento.values[1] * -4.5f).coerceIn(-40f, 40f)

                        inclinacionHorizontal =
                            inclinacionHorizontal * 0.75f +
                                    nuevoHorizontal * 0.25f

                        inclinacionVertical =
                            inclinacionVertical * 0.75f +
                                    nuevoVertical * 0.25f
                    }
                }

                override fun onAccuracyChanged(
                    sensor: Sensor?,
                    precision: Int
                ) = Unit
            }

            administradorSensores.registerListener(
                listener,
                acelerometro,
                SensorManager.SENSOR_DELAY_GAME
            )

            onDispose {
                administradorSensores.unregisterListener(listener)
            }
        } else {
            onDispose { }
        }
    }

    Scaffold(
        containerColor = Fondo,
        bottomBar = {
            NavigationBar(
                containerColor = Gris,
                contentColor = Blanco
            ) {
                val secciones = listOf(
                    Triple("inicio", "⌂", "Inicio"),
                    Triple("videos", "▶", "Videos"),
                    Triple("crear", "+", "Crear"),
                    Triple("buscar", "⌕", "Buscar"),
                    Triple("perfil", "●", "Perfil")
                )

                secciones.forEach { (id, simbolo, etiqueta) ->
                    NavigationBarItem(
                        selected = seccionSeleccionada == id,
                        onClick = {
                            seccionSeleccionada = id

                            if (id == "videos") {
                                contadorIniciado = true
                            }
                        },
                        icon = {
                            Text(
                                text = simbolo,
                                fontSize = if (id == "crear") 27.sp else 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        label = {
                            Text(etiqueta)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Naranja,
                            selectedTextColor = Naranja,
                            indicatorColor = Naranja.copy(alpha = 0.15f),
                            unselectedIconColor = TextoSecundario,
                            unselectedTextColor = TextoSecundario
                        )
                    )
                }
            }
        }
    ) { padding ->
        when (seccionSeleccionada) {
            "inicio" -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    PantallaFeedInicio(
                        usuario = usuario,
                        onAbrirNotificaciones = {
                            seccionSeleccionada = "notificaciones"
                        }
                    )
                }

            }

            // NOTIFICACIONES: ahora permite regresar a Inicio.
            "notificaciones" -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    PantallaFeedNotificaciones(
                        onVolver = {
                            seccionSeleccionada = "inicio"
                        }
                    )
                }
            }

            "crear" -> {
                PantallaCrearNavegacion(
                    usuario = usuario,
                    modifier = Modifier.padding(padding)
                )
            }

            "videos" -> {
                PantallaVideosActiva(
                    contador = contadorVideos,
                    movimientoOvalo = movimientoOvalo,
                    inclinacionHorizontal = inclinacionHorizontal,
                    inclinacionVertical = inclinacionVertical,
                    desplazamientoRebote = desplazamientoRebote.value,
                    tieneAcelerometro = acelerometro != null,
                    modifier = Modifier.padding(padding)
                )
            }

            "buscar" -> {
                PantallaBuscarDemostracion(
                    modifier = Modifier.padding(padding)
                )
            }

            "perfil" -> {
                PantallaPerfil(
                    usuario = usuario,
                    oscuro = oscuro,
                    cambiarTema = cambiarTema,
                    cerrarSesion = cerrarSesion,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
private fun PantallaCrearNavegacion(
    usuario: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val baseDatos = remember { BaseDatosHelper(context) }
    var contenido by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Fondo)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Crear publicación",
            color = Naranja,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))
        Text("Comparte algo con tu comunidad", color = TextoSecundario)
        Spacer(Modifier.height(22.dp))

        OutlinedTextField(
            value = contenido,
            onValueChange = { contenido = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("¿Qué quieres compartir?") },
            minLines = 5,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Blanco,
                unfocusedTextColor = Blanco,
                focusedLabelColor = Naranja,
                unfocusedLabelColor = TextoSecundario,
                cursorColor = Naranja,
                focusedBorderColor = Naranja,
                unfocusedBorderColor = TextoSecundario
            )
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (contenido.isBlank()) {
                    mensaje = "Escribe algo antes de publicar."
                } else {
                    val fecha = SimpleDateFormat(
                        "dd/MM/yyyy HH:mm",
                        Locale.getDefault()
                    ).format(Date())

                    baseDatos.insertar(
                        Publicacion(
                            usuario = usuario,
                            contenido = contenido.trim(),
                            fecha = fecha
                        )
                    )

                    contenido = ""
                    mensaje = "¡Tu publicación se guardó correctamente!"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Naranja
            )
        ) {
            Text("Publicar", color = Color.White)
        }

        if (mensaje.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            Text(mensaje, color = Naranja)
        }
    }
}

@Composable
private fun PantallaVideosActiva(
    contador: Int,
    movimientoOvalo: Float,
    inclinacionHorizontal: Float,
    inclinacionVertical: Float,
    desplazamientoRebote: Float,
    tieneAcelerometro: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Fondo)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Videos",
            color = Naranja,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Contador en funcionamiento",
            color = TextoSecundario,
            fontSize = 15.sp
        )

        Spacer(Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Gris),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = 36.dp,
                    vertical = 22.dp
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Tiempo transcurrido",
                    color = TextoSecundario,
                    fontSize = 14.sp
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "$contador s",
                    color = Naranja,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(38.dp))

        Text(
            text = if (tieneAcelerometro) {
                "Óvalo controlado por el sensor"
            } else {
                "Acelerómetro no disponible"
            },
            color = if (tieneAcelerometro) Naranja else Color(0xFFFF6B6B),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Inclina el teléfono para girarlo y sacúdelo para hacerlo rebotar.",
            color = TextoSecundario,
            fontSize = 13.sp
        )

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Gris, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(52.dp)
                    .graphicsLayer {
                        rotationX = inclinacionVertical
                        rotationY = inclinacionHorizontal
                        translationY = desplazamientoRebote
                    }
                    .rotate(movimientoOvalo)
                    .background(Naranja, RoundedCornerShape(percent = 50))
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Puedes cambiar de sección: el contador y el movimiento seguirán activos.",
            color = TextoSecundario,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun PantallaBuscarDemostracion(
    modifier: Modifier = Modifier
) {
    var busqueda by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Fondo)
            .padding(20.dp)
    ) {
        Text(
            "Buscar",
            color = Naranja,
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Buscar publicaciones o usuarios") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Blanco,
                unfocusedTextColor = Blanco,
                focusedLabelColor = Naranja,
                unfocusedLabelColor = TextoSecundario,
                cursorColor = Naranja,
                focusedBorderColor = Naranja,
                unfocusedBorderColor = TextoSecundario
            )
        )

        Spacer(Modifier.height(12.dp))

        Text(
            if (busqueda.isBlank()) {
                "Escribe para comenzar tu búsqueda."
            } else {
                "La búsqueda estará disponible próximamente."
            },
            color = TextoSecundario
        )
    }
}

@Composable
private fun PantallaPerfil(
    usuario: String,
    oscuro: Boolean,
    cambiarTema: (Boolean) -> Unit,
    cerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Fondo)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Mi perfil",
            color = Naranja,
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(24.dp))

        Text("●", color = Naranja, fontSize = 64.sp)
        Text(
            usuario,
            color = Blanco,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(30.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Gris)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "Apariencia",
                        color = Blanco,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        if (oscuro) "Tema oscuro" else "Tema claro",
                        color = TextoSecundario,
                        fontSize = 13.sp
                    )
                }

                Switch(
                    checked = oscuro,
                    onCheckedChange = cambiarTema,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Naranja,
                        checkedTrackColor = Naranja.copy(alpha = 0.35f)
                    )
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = cerrarSesion,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Naranja
            )
        ) {
            Text("Cerrar sesión", color = Color.White)
        }
    }
}

@Composable
fun PantallaContenidoInicio(
    usuario: String,
    cerrarSesion: () -> Unit
) {
    val context = LocalContext.current
    val baseDatos = remember { BaseDatosHelper(context) }

    var contenido by remember { mutableStateOf("") }
    var publicaciones by remember { mutableStateOf(emptyList<Publicacion>()) }
    var idEditando by remember { mutableStateOf<Int?>(null) }
    var resultadoExportacion by remember { mutableStateOf("") }
    var contenidoParaGuardar by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    val guardarJson = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver
                    .openOutputStream(uri)
                    ?.bufferedWriter()
                    ?.use {
                        it.write(contenidoParaGuardar)
                    }

                mensaje = "Archivo JSON guardado correctamente"
            } catch (e: Exception) {
                mensaje = "Error al guardar el archivo JSON"
            }
        }
    }

    val guardarXml = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/xml")
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver
                    .openOutputStream(uri)
                    ?.bufferedWriter()
                    ?.use {
                        it.write(contenidoParaGuardar)
                    }

                mensaje = "Archivo XML guardado correctamente"
            } catch (e: Exception) {
                mensaje = "Error al guardar el archivo XML"
            }
        }
    }

    fun cargarPublicaciones() {
        publicaciones = baseDatos.obtenerTodas()
    }

    LaunchedEffect(Unit) {
        cargarPublicaciones()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.imagen2),
            contentDescription = "Logo de SocialComunnity",
            modifier = Modifier.size(90.dp)
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "¡Hola, $usuario!",
            color = Naranja,
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Comunidad de publicaciones",
            color = Blanco,
            fontSize = 16.sp
        )

        ApiRestSection()

        Spacer(Modifier.height(24.dp))

        Text(
            text = if (idEditando == null) {
                "Crear publicación"
            } else {
                "Editar publicación"
            },
            color = Naranja,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = contenido,
            onValueChange = { contenido = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("¿Qué quieres compartir?") },
            minLines = 3,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Blanco,
                unfocusedTextColor = Blanco,
                focusedLabelColor = Naranja,
                unfocusedLabelColor = TextoSecundario,
                cursorColor = Naranja,
                focusedBorderColor = Naranja,
                unfocusedBorderColor = TextoSecundario
            )
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                if (contenido.isNotBlank()) {
                    val fecha = SimpleDateFormat(
                        "dd/MM/yyyy HH:mm",
                        Locale.getDefault()
                    ).format(Date())

                    if (idEditando == null) {
                        baseDatos.insertar(
                            Publicacion(
                                usuario = usuario,
                                contenido = contenido.trim(),
                                fecha = fecha
                            )
                        )

                        mensaje = "Publicación creada"
                    } else {
                        baseDatos.actualizar(
                            Publicacion(
                                id = idEditando!!,
                                usuario = usuario,
                                contenido = contenido.trim(),
                                fecha = fecha
                            )
                        )

                        mensaje = "Publicación actualizada"
                    }

                    contenido = ""
                    idEditando = null
                    cargarPublicaciones()
                    resultadoExportacion = ""
                } else {
                    mensaje = "Escribe algo antes de publicar"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Naranja
            )
        ) {
            Text(
                text = if (idEditando == null) {
                    "Publicar"
                } else {
                    "Guardar cambios"
                },
                color = Blanco
            )
        }

        if (idEditando != null) {
            TextButton(
                onClick = {
                    idEditando = null
                    contenido = ""
                    mensaje = ""
                }
            ) {
                Text("Cancelar edición", color = Blanco)
            }
        }

        if (mensaje.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(mensaje, color = Naranja)
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Mis publicaciones",
            color = Blanco,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        if (publicaciones.isEmpty()) {
            Text(
                text = "Todavía no hay publicaciones.",
                color = TextoSecundario,
                modifier = Modifier.padding(16.dp)
            )
        }

        publicaciones.forEach { publicacion ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 7.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Gris
                )
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        text = publicacion.usuario,
                        color = Naranja,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = publicacion.contenido,
                        color = Blanco
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = publicacion.fecha,
                        color = TextoSecundario,
                        fontSize = 12.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                idEditando = publicacion.id
                                contenido = publicacion.contenido
                                mensaje = ""
                            }
                        ) {
                            Text("Editar", color = Naranja)
                        }

                        TextButton(
                            onClick = {
                                baseDatos.eliminar(publicacion.id)
                                cargarPublicaciones()
                                resultadoExportacion = ""
                                mensaje = "Publicación eliminada"
                            }
                        ) {
                            Text(
                                "Eliminar",
                                color = Color(0xFFFF6B6B)
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Exportar publicaciones",
            color = Naranja,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                resultadoExportacion =
                    JsonHelper.convertirAJson(publicaciones)

                contenidoParaGuardar = resultadoExportacion
                mensaje = "JSON generado. Puedes guardarlo."
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Gris
            )
        ) {
            Text("Ver formato JSON", color = Blanco)
        }

        Button(
            onClick = {
                resultadoExportacion =
                    XmlHelper.convertirAXml(publicaciones)

                contenidoParaGuardar = resultadoExportacion
                mensaje = "XML generado. Puedes guardarlo."
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Gris
            )
        ) {
            Text("Ver formato XML", color = Blanco)
        }

        if (resultadoExportacion.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))

            Text(
                text = "Contenido generado",
                color = Naranja,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(Gris, RoundedCornerShape(8.dp))
                    .padding(12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = resultadoExportacion,
                    color = Color(0xFF8FE3A0),
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    guardarJson.launch("publicaciones.json")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Naranja
                )
            ) {
                Text("Guardar archivo JSON", color = Blanco)
            }

            Button(
                onClick = {
                    guardarXml.launch("publicaciones.xml")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Naranja
                )
            ) {
                Text("Guardar archivo XML", color = Blanco)
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = cerrarSesion,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Naranja
            )
        ) {
            Text("Cerrar sesión", color = Blanco)
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun ApiRestSection() {
    var tituloPost by remember { mutableStateOf("") }
    var contenidoPost by remember { mutableStateOf("") }

    var idPut by remember { mutableStateOf("1") }
    var tituloPut by remember { mutableStateOf("") }
    var contenidoPut by remember { mutableStateOf("") }

    var idPatch by remember { mutableStateOf("1") }
    var tituloPatch by remember { mutableStateOf("") }
    var contenidoPatch by remember { mutableStateOf("") }

    var idDelete by remember { mutableStateOf("1") }

    var respuesta by remember { mutableStateOf("") }
    var mensajeUsuario by remember { mutableStateOf("") }
    var publicacionProcesada by remember {
        mutableStateOf<PublicacionApi?>(null)
    }
    var error by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }
    var datosCargadosDesdeGet by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    fun ejecutar(
        alRecibirRespuesta: ((String) -> Unit)? = null,
        accion: suspend () -> String
    ) {
        scope.launch {
            cargando = true
            error = ""
            respuesta = ""
            mensajeUsuario = ""

            try {
                respuesta = accion()

                mensajeUsuario =
                    "La operación se completó correctamente. Revisa los datos recibidos del servidor."

                publicacionProcesada = null

                try {
                    val json = JSONObject(respuesta)

                    if (json.has("title") && json.has("body")) {
                        publicacionProcesada = PublicacionApi(
                            userId = json.optInt("userId", 1),
                            id = json.optInt("id", 0),
                            title = json.optString("title"),
                            body = json.optString("body")
                        )
                    }
                } catch (_: Exception) {
                    // Algunas respuestas, como DELETE, pueden no contener una publicación.
                }

                alRecibirRespuesta?.invoke(respuesta)
            } catch (e: Exception) {
                error = e.message
                    ?: "Ocurrió un error al consultar la API."
            } finally {
                cargando = false
            }
        }
    }

    @Composable
    fun campo(
        valor: String,
        cambiar: (String) -> Unit,
        etiqueta: String,
        numerico: Boolean = false
    ) {
        OutlinedTextField(
            value = valor,
            onValueChange = cambiar,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(etiqueta) },
            singleLine = true,
            keyboardOptions = if (numerico) {
                KeyboardOptions(keyboardType = KeyboardType.Number)
            } else {
                KeyboardOptions.Default
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Blanco,
                unfocusedTextColor = Blanco,
                focusedLabelColor = Naranja,
                unfocusedLabelColor = TextoSecundario,
                cursorColor = Naranja,
                focusedBorderColor = Naranja,
                unfocusedBorderColor = TextoSecundario
            )
        )
    }

    @Composable
    fun boton(
        etiqueta: String,
        accion: () -> Unit
    ) {
        Button(
            onClick = accion,
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Naranja
            )
        ) {
            Text(etiqueta, color = Blanco)
        }
    }

    Text(
        text = "API REST - JSONPlaceholder",
        color = Naranja,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold
    )

    Text(
        text = "Prueba los cinco métodos HTTP",
        color = TextoSecundario,
        fontSize = 14.sp
    )

    Spacer(Modifier.height(12.dp))

    // GET
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Gris)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(
                "GET - Consultar publicación",
                color = Naranja,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Consulta una publicación y carga su título y descripción.",
                color = Blanco
            )

            boton("Consultar (GET)") {
                ejecutar(
                    accion = {
                        val p = ApiService.obtenerPublicacion()

                        JSONObject().apply {
                            put("userId", p.userId)
                            put("id", p.id)
                            put("title", p.title)
                            put("body", p.body)
                        }.toString()
                    },
                    alRecibirRespuesta = { jsonRespuesta ->
                        val datos = JSONObject(jsonRespuesta)

                        val publicacion = PublicacionApi(
                            userId = datos.optInt("userId", 1),
                            id = datos.optInt("id", 1),
                            title = datos.optString("title"),
                            body = datos.optString("body")
                        )

                        publicacionProcesada = publicacion.copy(
                            title = publicacion.title + " banco"
                        )

                        tituloPost = publicacion.title
                        contenidoPost = publicacion.body

                        idPut = publicacion.id.toString()
                        tituloPut = publicacion.title
                        contenidoPut = publicacion.body

                        idPatch = publicacion.id.toString()
                        tituloPatch = publicacion.title
                        contenidoPatch = publicacion.body

                        datosCargadosDesdeGet = true

                        mensajeUsuario =
                            "Datos obtenidos. Revisa los campos y edítalos antes de enviar."
                    }
                )
            }
        }
    }

    Spacer(Modifier.height(10.dp))

    // POST
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Gris)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(
                "POST - Crear publicación",
                color = Naranja,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Edita estos campos: serán los datos que enviaremos al servidor.",
                color = TextoSecundario,
                fontSize = 12.sp
            )

            campo(tituloPost, { tituloPost = it }, "Título")

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = contenidoPost,
                onValueChange = { contenidoPost = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Contenido") },
                minLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Blanco,
                    unfocusedTextColor = Blanco,
                    focusedLabelColor = Naranja,
                    unfocusedLabelColor = TextoSecundario,
                    cursorColor = Naranja,
                    focusedBorderColor = Naranja,
                    unfocusedBorderColor = TextoSecundario
                )
            )

            boton("Enviar (POST)") {
                if (tituloPost.isBlank() || contenidoPost.isBlank()) {
                    error = "Escribe el título y el contenido para POST."
                } else {
                    ejecutar(
                        accion = {
                            ApiService.crearPublicacion(
                                tituloPost.trim(),
                                contenidoPost.trim()
                            )
                        },
                        alRecibirRespuesta = { jsonRespuesta ->
                            val datos = JSONObject(jsonRespuesta)

                            val publicacion = PublicacionApi(
                                userId = datos.optInt("userId", 1),
                                id = datos.optInt("id", 0),
                                title = datos.optString("title"),
                                body = datos.optString("body")
                            )

                            publicacionProcesada = publicacion.copy(
                                title = if (publicacion.title.endsWith(" banco")) {
                                    publicacion.title
                                } else {
                                    "${publicacion.title} banco"
                                }
                            )

                            mensajeUsuario =
                                "Publicación creada y respuesta procesada correctamente."
                        }
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // PUT
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Gris)
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        "PUT - Actualizar publicación completa",
                        color = Naranja,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "Los valores que escribas reemplazarán los datos de la publicación enviada.",
                        color = TextoSecundario,
                        fontSize = 12.sp
                    )

                    campo(tituloPut, { tituloPut = it }, "Nuevo título")
                    campo(contenidoPut, { contenidoPut = it }, "Nuevo contenido")

                    boton("Actualizar (PUT)") {
                        val id = idPut.toIntOrNull()

                        if (
                            id == null ||
                            id <= 0 ||
                            tituloPut.isBlank() ||
                            contenidoPut.isBlank()
                        ) {
                            error = "Indica un ID válido, título y contenido."
                        } else {
                            ejecutar {
                                ApiService.actualizarPublicacion(
                                    id,
                                    tituloPut.trim(),
                                    contenidoPut.trim()
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // PATCH
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Gris)
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        "PATCH - Modificar parcialmente",
                        color = Naranja,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "Modifica el título, la descripción o ambos. Solo se enviarán los campos que hayas completado.",
                        color = Blanco
                    )

                    campo(tituloPatch, { tituloPatch = it }, "Nuevo título")

                    OutlinedTextField(
                        value = contenidoPatch,
                        onValueChange = { contenidoPatch = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Nueva descripción") },
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Blanco,
                            unfocusedTextColor = Blanco,
                            focusedLabelColor = Naranja,
                            unfocusedLabelColor = TextoSecundario,
                            cursorColor = Naranja,
                            focusedBorderColor = Naranja,
                            unfocusedBorderColor = TextoSecundario
                        )
                    )

                    boton("Modificar publicación (PATCH)") {
                        val id = idPatch.toIntOrNull()

                        if (
                            id == null ||
                            id <= 0 ||
                            (tituloPatch.isBlank() && contenidoPatch.isBlank())
                        ) {
                            error =
                                "Escribe un título o una descripción para modificar."
                        } else {
                            ejecutar {
                                ApiService.modificarParcialmente(
                                    id = id,
                                    nuevoTitulo = tituloPatch.trim(),
                                    nuevaDescripcion = contenidoPatch.trim()
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // DELETE
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Gris)
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        "DELETE - Eliminar publicación",
                        color = Naranja,
                        fontWeight = FontWeight.Bold
                    )

                    boton("Eliminar (DELETE)") {
                        val id = idDelete.toIntOrNull()

                        if (id == null || id <= 0) {
                            error = "Escribe un ID válido para eliminar."
                        } else {
                            ejecutar {
                                ApiService.eliminarPublicacion(id)
                            }
                        }
                    }
                }
            }

            if (cargando) {
                Spacer(Modifier.height(12.dp))

                CircularProgressIndicator(color = Naranja)

                Text(
                    "Conectando con JSONPlaceholder...",
                    color = Blanco
                )
            }

            if (error.isNotBlank()) {
                Spacer(Modifier.height(10.dp))

                Text(
                    "Error: $error",
                    color = Color(0xFFFF6B6B)
                )
            }

            if (datosCargadosDesdeGet) {
                Spacer(Modifier.height(8.dp))

                Text(
                    "Los datos de GET se copiaron a los formularios de POST, PUT y PATCH. Puedes modificarlos antes de enviar cada petición.",
                    color = Color(0xFF8FE3A0),
                    fontSize = 12.sp
                )
            }

            if (mensajeUsuario.isNotBlank() && !cargando) {
                Spacer(Modifier.height(8.dp))

                Text(
                    mensajeUsuario,
                    color = Color(0xFF8FE3A0),
                    fontSize = 13.sp
                )
            }

            publicacionProcesada?.let { publicacion ->
                Spacer(Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Gris)
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text(
                            "Publicación procesada",
                            color = Naranja,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            "Título",
                            color = Naranja,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            publicacion.title,
                            color = Blanco
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            "Descripción",
                            color = Naranja,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            publicacion.body,
                            color = Blanco
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Nota: JSONPlaceholder simula las operaciones de escritura. POST, PUT, PATCH y DELETE no modifican permanentemente sus datos.",
                color = TextoSecundario,
                fontSize = 12.sp
            )
        }
    }
}
