package com.pedro.thingscontroller.presentation.view.composables

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pedro.thingscontroller.domain.model.command.LedCommand
import com.pedro.thingscontroller.domain.model.command.ThingCommand
import com.pedro.thingscontroller.domain.model.component.Component
import com.pedro.thingscontroller.domain.model.component.ComponentAction
import com.pedro.thingscontroller.domain.model.component.ComponentActionDescriptor
import com.pedro.thingscontroller.domain.model.component.ComponentType
import com.pedro.thingscontroller.domain.model.component.ComponentState
import com.pedro.thingscontroller.domain.model.component.instance.LedInstance
import com.pedro.thingscontroller.domain.model.component.toAction
import com.pedro.thingscontroller.presentation.view.ui.theme.LedBlue
import com.pedro.thingscontroller.presentation.view.ui.theme.LedGreen
import com.pedro.thingscontroller.presentation.view.ui.theme.LedRed
import com.pedro.thingscontroller.presentation.view.ui.theme.LedYellow
import com.pedro.thingscontroller.presentation.view.ui.theme.ThingsControllerTheme

@Composable
fun LedSection(
    led: Component,
    canInteract: Boolean,
    onActionClick: (ThingCommand) -> Unit
){
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(led.instances){ledInstance ->
            val ledColor = when (ledInstance.componentId.removeSuffix("Led").lowercase()) {
                "yellow" -> LedYellow
                "green" -> LedGreen
                "red" -> LedRed
                "blue" -> LedBlue
                else -> MaterialTheme.colorScheme.primary
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LedIcon(
                        color = ledColor,
                        state = ledInstance.state as ComponentState.LedState,
                        updatedAt = ledInstance.updatedAt,
                        modifier = Modifier.width(80.dp)
                    )

                    VerticalDivider(
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .fillMaxHeight(),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceAround
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = ledInstance.componentId.uppercase(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black
                            )

                            val statusColor = if (ledInstance.available)
                                MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error

                            Text(
                                text = if (ledInstance.available) "AVAILABLE" else "NOT AVAILABLE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if(ledInstance.pendingRequestId != null){
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp),
                                contentAlignment = Alignment.Center
                            ){
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }else{
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                led.actions.forEach { actionDescriptor ->
                                    val action = when(actionDescriptor){
                                        ComponentActionDescriptor.Led.ON -> ComponentAction.LedAction.ON
                                        ComponentActionDescriptor.Led.OFF -> ComponentAction.LedAction.OFF
                                        ComponentActionDescriptor.Led.BLINK -> ComponentAction.LedAction.BLINK
                                        else -> {ComponentAction.LedAction.OFF}
                                    }

                                    val isEnabled = canInteract && action != (ledInstance.state as? ComponentState.LedState)?.toAction()
                                    Button(
                                        onClick = {
                                            val ledCommand = LedCommand(
                                                componentId = ledInstance.componentId,
                                                componentType = ComponentType.LED,
                                                action = action
                                            )
                                            onActionClick(ledCommand)
                                        },
                                        modifier = Modifier.height(36.dp),
                                        contentPadding = PaddingValues(horizontal = 16.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        enabled = isEnabled
                                    ) {
                                        Text(
                                            text = actionDescriptor.toString(),
                                            style = MaterialTheme.typography.labelLarge,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Light Mode", showBackground = true)
@Composable
fun LedSectionPreview(){
    ThingsControllerTheme() {
        Surface(color = MaterialTheme.colorScheme.background) {
            LedSection(
                Component(
                    type = ComponentType.LED,
                    actions = listOf(ComponentActionDescriptor.Led.ON, ComponentActionDescriptor.Led.OFF, ComponentActionDescriptor.Led.BLINK),
                    instances = listOf(
                        LedInstance(
                            componentId = "redLed",
                            available = true,
                            state = ComponentState.LedState.ON,
                            updatedAt = System.currentTimeMillis(),
                            pendingRequestId = "12313131"
                        ),
                        LedInstance(
                            componentId = "greenLed",
                            available = true,
                            state = ComponentState.LedState.OFF,
                            updatedAt = System.currentTimeMillis(),
                            pendingRequestId = null
                        ),
                        LedInstance(
                            componentId = "yellowLed",
                            available = false,
                            state = ComponentState.LedState.BLINKING,
                            updatedAt = System.currentTimeMillis(),
                            pendingRequestId = null
                        )
                    )
                ),
                canInteract = true
            ) { }
        }
    }
}