package com.pedro.thingscontroller.presentation.view.composables

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pedro.thingscontroller.R
import com.pedro.thingscontroller.domain.model.thing.Thing
import com.pedro.thingscontroller.domain.model.thing.ThingState
import com.pedro.thingscontroller.domain.model.thing.ThingStateStatus
import com.pedro.thingscontroller.presentation.view.ui.theme.SuccessDark
import com.pedro.thingscontroller.presentation.view.ui.theme.SuccessLight
import com.pedro.thingscontroller.presentation.view.ui.theme.ThingsControllerTheme

@Composable
fun ThingComposable(
    thing: Thing,
    onSeeComponents: (String) -> Unit
){
    val isConnected = thing.connection.status == ThingStateStatus.CONNECTED
    val successColor = if (isSystemInDarkTheme()) SuccessDark else SuccessLight

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        modifier = Modifier
            .padding(8.dp)
            .size(width = 240.dp, height = 380.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = MaterialTheme.colorScheme.primary,
                spotColor = MaterialTheme.colorScheme.primary
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = thing.userFriendlyName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            val esp32Image: Painter = if (isConnected){
                painterResource(R.drawable.esp32_on)
            }else{
                painterResource(R.drawable.esp32_off)

            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Box(
                    contentAlignment = Alignment.Center
                ){
                    Image(
                        painter = esp32Image,
                        contentDescription = "ESP32 device",
                        modifier = Modifier
                            .size(160.dp)
                            .padding(6.dp)
                            .then(
                                if(!isConnected) Modifier
                                    .blur(2.dp)
                                    .alpha(0.4f) else Modifier
                            )
                    )

                    if (!isConnected){
                        Text(
                            text = "Disconnected",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = thing.thingName,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
            if(isConnected){
                Text(
                    text = "Connected",
                    color = successColor,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = { onSeeComponents(thing.thingName) },
                modifier = Modifier.height(48.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Text(
                    text = "Components",
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}

@Preview(
    name = "Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun ThingComposablePreview() {

    val mockThing = Thing(
        thingName = "esp32_A2F00F4134B1",
        userFriendlyName = "Quarto",
        available = true,
        connection = ThingState(
            status = ThingStateStatus.CONNECTED,
            timestamp = System.currentTimeMillis()
        ),
        type = "ESP32"
    )
    ThingsControllerTheme {
        Surface(color = MaterialTheme.colorScheme.background){
            ThingComposable(
                thing = mockThing,
                onSeeComponents = {}
            )
        }
    }
}

@Preview(
    name = "Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun ThingComposableDisconnectedPreview() {

    val mockThing = Thing(
        thingName = "esp32_A2F11F4134B1",
        userFriendlyName = "Sala",
        available = true,
        connection = ThingState(
            status = ThingStateStatus.DISCONNECTED,
            timestamp = System.currentTimeMillis()
        ),
        type = "ESP32"
    )
    ThingsControllerTheme {
        Surface(color = MaterialTheme.colorScheme.background){
            ThingComposable(
                thing = mockThing,
                onSeeComponents = {}
            )
        }
    }
}