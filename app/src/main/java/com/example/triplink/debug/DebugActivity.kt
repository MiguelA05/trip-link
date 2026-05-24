package com.example.triplink.debug

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.triplink.debug.DiagnosticUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DebugActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Surface(color = MaterialTheme.colorScheme.background) {
                var playServicesStatus by remember { mutableStateOf("—") }
                var dnsStatus by remember { mutableStateOf("—") }
                var cacheListing by remember { mutableStateOf("—") }
                var firestoreStatus by remember { mutableStateOf("—") }
                var tokenSignInStatus by remember { mutableStateOf("—") }
                var backendUrl by remember { mutableStateOf("http://10.0.2.2:3000/custom-token?uid=diag") }

                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Diagnostics (DEBUG)", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(modifier = Modifier.fillMaxWidth(), onClick = {
                        CoroutineScope(Dispatchers.Main).launch {
                            playServicesStatus = "Comprobando..."
                            val res = DiagnosticUtils.isGooglePlayServicesAvailable(this@DebugActivity)
                            playServicesStatus = if (res.first) "OK (code=${res.second})" else "NO disponible (code=${res.second})"
                        }
                    }) { Text("Comprobar Google Play Services") }
                    Text("Estado: $playServicesStatus", modifier = Modifier.padding(top = 8.dp))

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(modifier = Modifier.fillMaxWidth(), onClick = {
                        CoroutineScope(Dispatchers.Main).launch {
                            dnsStatus = "Resolviendo..."
                            dnsStatus = DiagnosticUtils.resolveHost("firestore.googleapis.com")
                        }
                    }) { Text("Resolver DNS firestore.googleapis.com") }
                    Text("Resultado: $dnsStatus", modifier = Modifier.padding(top = 8.dp))

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = backendUrl,
                        onValueChange = { backendUrl = it },
                        label = { Text("Backend custom-token URL") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(modifier = Modifier.fillMaxWidth(), onClick = {
                        CoroutineScope(Dispatchers.Main).launch {
                            cacheListing = "Listando..."
                            cacheListing = DiagnosticUtils.listImageHttpCache(this@DebugActivity)
                        }
                    }) { Text("Listar cache de imágenes (image_http_cache)") }
                    Text(cacheListing, modifier = Modifier.padding(top = 8.dp))

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(modifier = Modifier.fillMaxWidth(), onClick = {
                        CoroutineScope(Dispatchers.Main).launch {
                            firestoreStatus = "Probando Firestore..."
                            firestoreStatus = DiagnosticUtils.testFirestoreRead()
                        }
                    }) { Text("Probar lectura simple de Firestore") }
                    Text(firestoreStatus, modifier = Modifier.padding(top = 8.dp))

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(modifier = Modifier.fillMaxWidth(), onClick = {
                        CoroutineScope(Dispatchers.Main).launch {
                            tokenSignInStatus = "Intentando sign-in..."
                            tokenSignInStatus = DiagnosticUtils.attemptCustomTokenSignIn(backendUrl)
                        }
                    }) { Text("Intentar signInWithCustomToken (backend)") }
                    Text(tokenSignInStatus, modifier = Modifier.padding(top = 8.dp))

                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Nota: Esta pantalla es de diagnóstico. Use una emulación con Play Services si necesita funcionalidades relacionadas con GMS.")
                }
            }
        }
    }
}


