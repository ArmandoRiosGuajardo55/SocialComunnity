package com.example.socialcomunnity



import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay

// FUENTE POPPINS
val poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_bold, FontWeight.Bold)
)

// COLORES DEL MODO NORMAL
val NaranjaNormal = Color(0xFFFF9800)
val NegroNormal = Color(0xFF222222)

// COLORES DEL MODO CUPERTINO
val AzulCupertino = Color(0xFF007AFF)
val MoradoCupertino = Color(0xFF8E44FF)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AplicacionPrincipal()
        }
    }
}

@Composable
fun AplicacionPrincipal() {

    var modoCupertino by remember {
        mutableStateOf(false)
    }

    var textoEntrada by remember {
        mutableStateOf("")
    }

    var mensajeActual by remember {
        mutableStateOf("¡Bienvenido a mi app!")
    }

    var contador by remember {
        mutableIntStateOf(0)
    }

    var mostrarAlerta by remember {
        mutableStateOf(false)
    }

    // CONTROL DE LA SEGUNDA PÁGINA
    var mostrarSegundaPagina by remember {
        mutableStateOf(false)
    }

    // CONTADOR: AUMENTA CADA 5 SEGUNDOS
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            contador++
        }
    }

    val fondo = if (modoCupertino) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFEAF2FF),
                Color(0xFFF5F0FF),
                Color.White
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color.White,
                Color(0xFFFFF8EF)
            )
        )
    }

    Scaffold(
        containerColor = Color.Transparent
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(fondo)
                .padding(paddingValues)
        ) {

            // MOSTRAR UNA PÁGINA U OTRA
            if (mostrarSegundaPagina) {

                SegundaPagina(
                    modoCupertino = modoCupertino,
                    onVolver = {
                        mostrarSegundaPagina = false
                    }
                )

            } else {

                // PÁGINA PRINCIPAL
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            horizontal = 24.dp,
                            vertical = 16.dp
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    // SWITCH DE MODOS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "Normal",
                            fontFamily = poppins,
                            fontSize = 14.sp,
                            color = if (!modoCupertino) {
                                NaranjaNormal
                            } else {
                                Color.Gray
                            }
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Switch(
                            checked = modoCupertino,
                            onCheckedChange = {
                                modoCupertino = it
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = AzulCupertino,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = NaranjaNormal
                            )
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "Cupertino",
                            fontFamily = poppins,
                            fontSize = 14.sp,
                            color = if (modoCupertino) {
                                AzulCupertino
                            } else {
                                Color.Gray
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // TÍTULO
                    if (modoCupertino) {
                        TextoStackCupertino(
                            texto = "Hola mi app",
                            tamano = 40
                        )
                    } else {
                        Text(
                            text = "Hola mi app",
                            fontFamily = poppins,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            color = NaranjaNormal
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // MENSAJE
                    if (modoCupertino) {
                        TextoStackCupertino(
                            texto = mensajeActual,
                            tamano = 18
                        )
                    } else {
                        Text(
                            text = mensajeActual,
                            fontFamily = poppins,
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp,
                            color = NegroNormal,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // IMAGEN CON CONTADOR SUPERPUESTO
                    Box(
                        modifier = Modifier
                            .size(
                                width = 280.dp,
                                height = 160.dp
                            )
                            .then(
                                if (modoCupertino) {
                                    Modifier
                                        .shadow(
                                            10.dp,
                                            RoundedCornerShape(22.dp)
                                        )
                                        .clip(
                                            RoundedCornerShape(22.dp)
                                        )
                                        .border(
                                            BorderStroke(
                                                2.dp,
                                                Brush.linearGradient(
                                                    listOf(
                                                        AzulCupertino,
                                                        MoradoCupertino
                                                    )
                                                )
                                            ),
                                            RoundedCornerShape(22.dp)
                                        )
                                } else {
                                    Modifier.clip(
                                        RoundedCornerShape(8.dp)
                                    )
                                }
                            )
                    ) {

                        Image(
                            painter = painterResource(
                                id = R.drawable.imagen2
                            ),
                            contentDescription = "Imagen principal",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // CONTADOR SUPERPUESTO
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(10.dp)
                                .shadow(5.dp, CircleShape)
                                .background(
                                    color = if (modoCupertino) {
                                        AzulCupertino
                                    } else {
                                        NaranjaNormal
                                    },
                                    shape = CircleShape
                                )
                                .padding(
                                    horizontal = 14.dp,
                                    vertical = 8.dp
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$contador",
                                color = Color.White,
                                fontFamily = poppins,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // CAMPO DE TEXTO
                    OutlinedTextField(
                        value = textoEntrada,
                        onValueChange = {
                            textoEntrada = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(
                                text = "Escribe un mensaje",
                                fontFamily = poppins
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(
                            if (modoCupertino) 16.dp else 8.dp
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // ACTUALIZAR MENSAJE
                    BotonPersonalizado(
                        texto = "Actualizar texto",
                        modoCupertino = modoCupertino,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (textoEntrada.isNotBlank()) {
                            mensajeActual = textoEntrada.trim()
                            textoEntrada = ""
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // MOSTRAR ALERTA
                    BotonPersonalizado(
                        texto = "Mostrar alerta",
                        modoCupertino = modoCupertino,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        mostrarAlerta = true
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // NUEVO BOTÓN: IR A OTRA PÁGINA
                    BotonPersonalizado(
                        texto = "Ir a otra página",
                        modoCupertino = modoCupertino,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        mostrarSegundaPagina = true
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // CONTADOR EN TEXTO
                    if (modoCupertino) {
                        TextoStackCupertino(
                            texto = "Contador: $contador",
                            tamano = 16
                        )
                    } else {
                        Text(
                            text = "Contador: $contador",
                            fontFamily = poppins,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = NegroNormal
                        )
                    }
                }
            }
        }
    }

    // ALERTAS PERSONALIZADAS
    if (mostrarAlerta && !mostrarSegundaPagina) {

        if (modoCupertino) {

            AlertaCupertino(
                titulo = "Cupertino",
                mensaje = "¡Esta es tu alerta personalizada!",
                onAceptar = {
                    mostrarAlerta = false
                }
            )

        } else {

            AlertDialog(
                onDismissRequest = {
                    mostrarAlerta = false
                },
                title = {
                    Text(
                        text = "Alerta normal",
                        fontFamily = poppins,
                        fontWeight = FontWeight.Bold,
                        color = NaranjaNormal
                    )
                },
                text = {
                    Text(
                        text = "¡Esta es tu alerta del modo normal!",
                        fontFamily = poppins,
                        color = NegroNormal
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            mostrarAlerta = false
                        }
                    ) {
                        Text(
                            text = "Aceptar",
                            fontFamily = poppins,
                            fontWeight = FontWeight.Bold,
                            color = NaranjaNormal
                        )
                    }
                },
                containerColor = Color.White
            )
        }
    }
}

// TEXTO STACK PARA CUPERTINO
@Composable
fun TextoStackCupertino(
    texto: String,
    tamano: Int = 32
) {
    Box(
        contentAlignment = Alignment.Center
    ) {

        // CAPA TRASERA OSCURA
        Text(
            text = texto,
            fontFamily = poppins,
            fontWeight = FontWeight.Bold,
            fontSize = tamano.sp,
            lineHeight = (tamano + 8).sp,
            color = Color(0xFF3A0878),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(
                start = 8.dp,
                top = 8.dp
            )
        )

        // CAPA INTERMEDIA MORADA
        Text(
            text = texto,
            fontFamily = poppins,
            fontWeight = FontWeight.Bold,
            fontSize = tamano.sp,
            lineHeight = (tamano + 8).sp,
            color = Color(0xFF9B45FF),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(
                start = 4.dp,
                top = 4.dp
            )
        )

        // CAPA PRINCIPAL CON DEGRADADO
        Text(
            text = texto,
            fontFamily = poppins,
            fontWeight = FontWeight.ExtraBold,
            fontSize = tamano.sp,
            lineHeight = (tamano + 8).sp,
            style = TextStyle(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF00C6FF),
                        Color(0xFF007AFF),
                        Color(0xFF6A00FF)
                    )
                )
            ),
            textAlign = TextAlign.Center
        )
    }
}

// BOTONES PERSONALIZADOS
@Composable
fun BotonPersonalizado(
    texto: String,
    modoCupertino: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.then(
            if (modoCupertino) {
                Modifier.shadow(
                    4.dp,
                    RoundedCornerShape(16.dp)
                )
            } else {
                Modifier
            }
        ),
        shape = RoundedCornerShape(
            if (modoCupertino) 16.dp else 8.dp
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (modoCupertino) {
                AzulCupertino
            } else {
                NaranjaNormal
            },
            contentColor = Color.White
        )
    ) {
        Text(
            text = texto,
            fontFamily = poppins,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

// SEGUNDA PÁGINA
@Composable
fun SegundaPagina(
    modoCupertino: Boolean,
    onVolver: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (modoCupertino) {
                    Color(0xFFF2F5FA)
                } else {
                    Color.White
                }
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            if (modoCupertino) {
                TextoStackCupertino(
                    texto = "¡Segunda página!",
                    tamano = 32
                )
            } else {
                Text(
                    text = "¡Segunda página!",
                    fontFamily = poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = NaranjaNormal,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Ahora estás en otra pantalla de tu aplicación.",
                fontFamily = poppins,
                fontSize = 16.sp,
                color = if (modoCupertino) {
                    AzulCupertino
                } else {
                    NegroNormal
                },
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            BotonPersonalizado(
                texto = "Volver al inicio",
                modoCupertino = modoCupertino,
                modifier = Modifier.fillMaxWidth()
            ) {
                onVolver()
            }
        }
    }
}

// ALERTA PERSONALIZADA CUPERTINO
@Composable
fun AlertaCupertino(
    titulo: String,
    mensaje: String,
    onAceptar: () -> Unit
) {
    Dialog(
        onDismissRequest = onAceptar
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = Color(0xFF252631),
            shadowElevation = 18.dp
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = titulo,
                    fontFamily = poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 21.sp,
                    color = Color(0xFF64A8FF),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = mensaje,
                    fontFamily = poppins,
                    fontSize = 15.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(
                        horizontal = 22.dp
                    )
                )

                Spacer(modifier = Modifier.height(22.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFF454653))
                )

                TextButton(
                    onClick = onAceptar,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "OK",
                        fontFamily = poppins,
                        fontWeight = FontWeight.Medium,
                        fontSize = 17.sp,
                        color = Color(0xFF64A8FF)
                    )
                }
            }
        }
    }
}