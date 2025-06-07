package app.naga.audiotranscription.feature.voiceOrder

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.naga.audiotranscription.domain.model.VoiceOrder
import androidx.compose.ui.res.stringResource
import app.naga.audiotranscription.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceOrderScreen(
    state: VoiceOrderUiState,
    onAdd: (String, VoiceOrder.Action) -> Unit,
    onDelete: (VoiceOrder) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedAction by remember { mutableStateOf<VoiceOrder.Action>(VoiceOrder.Action.Dialog) }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = {
                    Text(stringResource(R.string.voice_order_title))
                },
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text(stringResource(R.string.voice_order_command_label)) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box {
                    Button(onClick = { expanded = true }) {
                        Text(selectedAction.name())
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.voice_order_action_dialog)) },
                            onClick = {
                                selectedAction = VoiceOrder.Action.Dialog
                                expanded = false
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (text.isNotBlank()) {
                            onAdd(text, selectedAction)
                            text = ""
                        }
                    }
                ) { Text(stringResource(R.string.common_add)) }
            }
            Spacer(modifier = Modifier.height(24.dp))
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.orders) { order ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(order.text, style = MaterialTheme.typography.bodyLarge)
                                Text(order.action.name(), style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { onDelete(order) }) {
                                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.common_delete))
                            }
                        }
                    }
                }
            }
        }

    }
} 