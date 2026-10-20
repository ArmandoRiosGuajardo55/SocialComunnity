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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val Fondo = Color(0xFFF5F7FF)
private val Morado = Color(0xFF6547E8)
private val Azul = Color(0xFF31B9E8)
private val TextoOscuro = Color(0xFF20243A)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
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
    var nombre by remember { mutableStateOf("") }
    var mensaje by remember {
        mutableStateOf("¡Bienvenido a SocialComunnity!")
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

    val colorDinamico = when (contador % 3) {
        0 -> Morado
        1 -> Color(0xFF008F87)
        else -> Color(0xFFE87932)
    }

    if (mostrarSegundaPagina) {
        SegundaPagina(
            onVolver = { mostrarSegundaPagina = false }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Fondo)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Encabezado: primer cambio de la interfaz
            Text(
                text = "Bienvenido a SocialComunnity",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Morado,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Logo
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Morado, Azul)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "S",
                    color = Color.White,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Conecta, comparte y descubre",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(22.dp))

            // Tarjeta principal
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 5.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Tu espacio personal",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = mensaje,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorDinamico,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Escribe tu mensaje") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (nombre.isNotBlank()) {
                                mensaje = nombre.trim()
                                nombre = ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Morado
                        )
                    ) {
                        Text(
                            text = "Actualizar mensaje",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Contador con color dinámico
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor =
                        colorDinamico.copy(alpha = 0.10f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Contador activo",
                            fontWeight = FontWeight.Bold,
                            color = TextoOscuro
                        )

                        Text(
                            text = "El color cambia cada segundo",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Text(
                        text = "$contador s",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = colorDinamico
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = { mostrarSegundaPagina = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TextoOscuro
                )
            ) {
                Text("Explorar segunda pantalla")
            }
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
        Text(
            text = "¡Hola!",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Morado
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Esta es la segunda pantalla de SocialComunnity.",
            fontSize = 16.sp,
            color = TextoOscuro,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onVolver,
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Volver al inicio")
        }
    }
}
