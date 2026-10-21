package com.example.socialcomunnity


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val Naranja = Color(0xFFF47C35)
private val Fondo = Color(0xFFF6F3EF)
private val TextoOscuro = Color(0xFF252525)
private val Gris = Color(0xFF77716B)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Naranja,
                    background = Fondo,
                    surface = Color.White
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Fondo
                ) {
                    SocialCommunityApp()
                }
            }
        }
    }
}

@Composable
fun SocialCommunityApp() {
    var mensaje by remember { mutableStateOf("") }
    var mensajePublicado by remember {
        mutableStateOf("¡Bienvenido a nuestra comunidad!")
    }
    var contador by remember { mutableIntStateOf(0) }
    var mostrarSegundaPagina by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            contador++
        }
    }

    val colorContador = if (contador % 2 == 0) {
        Naranja
    } else {
        Color(0xFF3976C5)
    }

    if (mostrarSegundaPagina) {
        SegundaPagina(
            onVolver = { mostrarSegundaPagina = false }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Encabezado
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(Naranja, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "S",
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SocialCommunity",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro
                    )

                    Text(
                        text = "Tu espacio para conectar",
                        fontSize = 13.sp,
                        color = Gris
                    )
                }

                Text(text = "🔔", fontSize = 23.sp)
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Tarjeta de bienvenida
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "¡BIENVENIDO!",
                        color = Naranja,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Hola, mi app 👋",
                        color = TextoOscuro,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Comparte tus ideas y conecta con tu comunidad.",
                        color = Gris,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Campo de texto
                    OutlinedTextField(
                        value = mensaje,
                        onValueChange = { mensaje = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Escribe tu mensaje") },
                        placeholder = {
                            Text("¿Qué quieres compartir?")
                        },
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    // Botón para publicar
                    Button(
                        onClick = {
                            if (mensaje.isNotBlank()) {
                                mensajePublicado = mensaje.trim()
                                mensaje = ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Naranja
                        )
                    ) {
                        Text(
                            text = "Compartir mensaje →",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Mensaje publicado
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Mensaje de la comunidad",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = mensajePublicado,
                        fontSize = 15.sp,
                        color = Gris
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Contador que cambia de color
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Contador automático",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextoOscuro
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "$contador s",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorContador
                    )

                    Text(
                        text = "El color cambia cada segundo",
                        fontSize = 13.sp,
                        color = Gris
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Navegación a la segunda pantalla
            Button(
                onClick = { mostrarSegundaPagina = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TextoOscuro
                )
            ) {
                Text("Explorar la aplicación")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Hecho para conectar personas ❤️",
                color = Gris,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun SegundaPagina(onVolver: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Naranja, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "S",
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Explora tu comunidad",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = TextoOscuro,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Aquí podrás agregar nuevas funciones y espacios para tus usuarios.",
            fontSize = 16.sp,
            color = Gris,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onVolver,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Naranja
            )
        ) {
            Text("Volver al inicio")
        }
    }
}
