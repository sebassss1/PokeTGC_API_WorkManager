package com.example.poketgc_api.ui.Pantalla

import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.poketgc_api.worker.WorkManagerHelper
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun LoginPantalla(onLoginSuccess: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = CredentialManager.create(context)

    val WEB_CLIENT_ID = "16445304583-os0qa6r6dec5jhs3ctmbliav4tuob54f.apps.googleusercontent.com"

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF121212)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bienvenido a PokeTGC",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Button(
                onClick = {
                    scope.launch {
                        try {
                            val nonce = UUID.randomUUID().toString()
                            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                                .setFilterByAuthorizedAccounts(false)
                                .setServerClientId(WEB_CLIENT_ID)
                                .setNonce(nonce)
                                .setAutoSelectEnabled(false)
                                .build()

                            val request: GetCredentialRequest = GetCredentialRequest.Builder()
                                .addCredentialOption(googleIdOption)
                                .build()

                            val result = credentialManager.getCredential(
                                request = request,
                                context = context,
                            )
                            
                            // Iniciamos el trabajo en segundo plano al loguear
                            WorkManagerHelper.scheduleOneTimeSync(context)
                            
                            handleSignIn(result, onLoginSuccess)
                        } catch (e: NoCredentialException) {
                            Toast.makeText(context, "No se encontró cuenta de Google", Toast.LENGTH_SHORT).show()
                        } catch (e: GetCredentialException) {
                            Log.e("Auth", "Error: ${e.message}")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Acceder con Google")
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    val intent = Intent(Settings.ACTION_ADD_ACCOUNT).apply {
                        putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Añadir cuenta de Google", color = Color.Cyan)
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            TextButton(
                onClick = { 
                    // También iniciamos el worker aquí para que lo veas funcionar
                    WorkManagerHelper.scheduleOneTimeSync(context)
                    onLoginSuccess() 
                }
            ) {
                Text("Entrar sin Google (Desarrollo)", color = Color.Gray)
            }
        }
    }
}

private fun handleSignIn(result: GetCredentialResponse, onLoginSuccess: () -> Unit) {
    val credential = result.credential
    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        try {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            Log.d("Auth", "Login exitoso: ${googleIdTokenCredential.displayName}")
            onLoginSuccess()
        } catch (e: GoogleIdTokenParsingException) {
            Log.e("Auth", "Error parsing: ${e.message}")
        }
    }
}
