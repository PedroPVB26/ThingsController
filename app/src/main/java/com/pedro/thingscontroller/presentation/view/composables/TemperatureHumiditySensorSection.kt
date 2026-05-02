package com.pedro.thingscontroller.presentation.view.composables

import android.content.res.Configuration
import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pedro.thingscontroller.domain.model.component.Component
import com.pedro.thingscontroller.domain.model.component.ComponentPrecision
import com.pedro.thingscontroller.domain.model.component.ComponentState
import com.pedro.thingscontroller.domain.model.component.ComponentType
import com.pedro.thingscontroller.domain.model.component.instance.TemperatureUmidityInstance
import com.pedro.thingscontroller.presentation.view.ui.theme.ThingsControllerTheme
import java.util.Locale
import kotlin.times

@Composable
fun TemperatureHumiditySensorSection(
    sensor: Component
){
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(sensor.instances){it ->
            val instance = it as TemperatureUmidityInstance
            val state = instance.state as ComponentState.TemperatureHumidityState

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            text = instance.componentId.uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black
                        )

                        Text(
                            text = if (instance.available) "AVAILABLE" else "OFFLINE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (instance.available) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        SensorSection(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Thermostat,
                            label = "Temperature",
                            mainValue = String.format(Locale.US, "%.${instance.precision?.temperaturePrecision ?: 1}f°C", state.temperature),
                            secondaryValues = listOf(
                                String.format(Locale.US, "%.1f°F", state.temperature * 1.8 + 32),
                                String.format(Locale.US, "%.1fK", state.temperature + 273.15)
                            ),
                            precisionText = "Precision: ±${instance.precision?.temperaturePrecision ?: 0}°C",
                            iconColor = Color(0xFFFF5722)
                        )

                        VerticalDivider(
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .fillMaxHeight(),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )

                        SensorSection(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.WaterDrop,
                            label = "Umidity",
                            mainValue = String.format(Locale.US, "%.2f%%", state.humidity),
                            precisionText = "Precision: ±${instance.precision?.humidityPrecision ?: 0}%",
                            iconColor = Color(0xFF2196F3)
                        )

                    }
                }
            }
        }
    }
}

@Composable
private fun SensorSection(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    mainValue: String,
    secondaryValues: List<String> = emptyList(),
    precisionText: String,
    iconColor: Color
){
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = iconColor
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = mainValue,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (secondaryValues.isNotEmpty()) {
                    Text(
                        text = secondaryValues.joinToString(" | "),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = precisionText,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 8.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    }
}

@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Light Mode", showBackground = true)
@Composable
private fun TemperatureHumiditySensorSectionPreview(){
    ThingsControllerTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            TemperatureHumiditySensorSection(
                sensor = Component(
                    type = ComponentType.TEMPERATURE_UMIDITY_SENSOR,
                    actions = emptyList(),
                    instances = listOf(
                        TemperatureUmidityInstance(
                            componentId = "Enviroment 1",
                            available = true,
                            state = ComponentState.TemperatureHumidityState(
                                27.5,
                                69.1,
                                System.currentTimeMillis()
                            ),
                            updatedAt = System.currentTimeMillis(),
                            pendingRequestId = null,
                            precision = ComponentPrecision.TemperatureUmiditySensorPrecision(1, 1)
                        )
                    )
                )
            )
        }
    }
}