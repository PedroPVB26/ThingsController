package com.pedro.thingscontroller.presentation.view.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pedro.thingscontroller.R
import com.pedro.thingscontroller.domain.model.component.ComponentState
import com.pedro.thingscontroller.presentation.view.ui.theme.ThingsControllerTheme
import kotlinx.coroutines.delay

@Composable
fun LedIcon(
    color: Color,
    state: ComponentState.LedState,
    updatedAt: Long,
    modifier: Modifier = Modifier
) {
    // Estado para controlar a alternância do brilho no modo BLINKING
    var blinkToggle by remember { mutableStateOf(false) }

    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(updatedAt) {
        while (true) {
            currentTime = System.currentTimeMillis()
            delay(1000)
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
            modifier = Modifier
                .size(64.dp)
                .then(
                    if (blinkToggle || state == ComponentState.LedState.ON){
                        Modifier.drawBehind {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        color.copy(alpha = 0.8f), // centro forte
                                        color.copy(alpha = 0.7f),
                                        color.copy(alpha = 0.6f),
                                        color.copy(alpha = 0.5f),
                                        color.copy(alpha = 0.4f),
                                        color.copy(alpha = 0.3f),
                                        color.copy(alpha = 0.2f),
                                        color.copy(alpha = 0.1f),
                                        color.copy(alpha = 0.05f),
                                        Color.Transparent // borda some
                                    ),
                                    radius = size.maxDimension / 2
                                )
                            )
                        }
                    }else{
                        Modifier
                    }
                )

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

@Preview(showBackground = true)
@Composable
fun LedIconPreview() {
    ThingsControllerTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LedIcon(
                    color = Color.Red,
                    state = ComponentState.LedState.ON,
                    updatedAt = System.currentTimeMillis() / 1000 - 30 // 30s atrás
                )
                LedIcon(
                    color = Color.Green,
                    state = ComponentState.LedState.OFF,
                    updatedAt = System.currentTimeMillis() / 1000 - 3600 // 1h atrás
                )
                LedIcon(
                    color = Color.Yellow,
                    state = ComponentState.LedState.BLINKING,
                    updatedAt = System.currentTimeMillis() / 1000 - 60 // 1m atrás
                )
            }
        }
    }
}
