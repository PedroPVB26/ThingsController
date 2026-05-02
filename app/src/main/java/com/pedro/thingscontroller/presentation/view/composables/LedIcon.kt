package com.pedro.thingscontroller.presentation.view.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pedro.thingscontroller.R
import com.pedro.thingscontroller.domain.model.component.ComponentState
import kotlinx.coroutines.delay

@Composable
fun LedIcon(
    color: Color,
    state: ComponentState.LedState,
    updatedAt: Long, // Passamos o timestamp do último estado
    modifier: Modifier = Modifier
) {
    // Estado para controlar a alternância do brilho no modo BLINKING
    var blinkToggle by remember { mutableStateOf(false) }

    // Estado para o tempo atual, atualizado a cada segundo para precisão
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(updatedAt) {
        while (true) {
            currentTime = System.currentTimeMillis()
            delay(1000) // Atualiza a cada 1 segundo
        }
    }
    // Efeito para alternar o blinkToggle a cada 500ms se estiver piscando
    LaunchedEffect(state) {
        if (state == ComponentState.LedState.BLINKING) {
            while (true) {
                blinkToggle = !blinkToggle
                delay(500) // Ajuste a velocidade aqui
            }
        }
    }

    val timeAgo = if (updatedAt > 0) {
        val diffSeconds = 0L.coerceAtLeast((currentTime - (updatedAt * 1000)) / 1000)
        
        val hours = diffSeconds / 3600
        val minutes = (diffSeconds % 3600) / 60
        val seconds = diffSeconds % 60

        when {
            diffSeconds < 60 -> "${seconds}s"
            diffSeconds < 3600 -> "${minutes}m ${seconds}s"
            else -> "${hours}h ${minutes}m ${seconds}s"
        }
    } else ""

    Column(
        modifier = modifier.width(80.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val ledIcon: Painter
        val text: String

        when(state){
            ComponentState.LedState.ON -> {
                ledIcon = painterResource(R.drawable.led_on_blur)
                text = "ON"
            }
            ComponentState.LedState.OFF -> {
                ledIcon = painterResource(R.drawable.led_off_blur)
                text = "OFF"
            }
            ComponentState.LedState.BLINKING -> {
                ledIcon = if (blinkToggle) {
                    painterResource(R.drawable.led_on_blur)
                } else {
                    painterResource(R.drawable.led_off_blur)
                }
                text = "BLINKING"
            }
        }

        Image(
            painter = ledIcon,
            contentDescription = null,
            colorFilter = ColorFilter.tint(color),
            modifier = Modifier.size(64.dp)
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
        )

        // Exibe o tempo relativo formatado
        if (timeAgo.isNotEmpty()) {
            Text(
                text = timeAgo,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                fontWeight = FontWeight.Normal
            )
        }
    }
}
