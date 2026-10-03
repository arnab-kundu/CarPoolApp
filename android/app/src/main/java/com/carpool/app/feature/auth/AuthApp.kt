package com.carpool.app.feature.auth

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.carpool.app.core.designsystem.*
import com.carpool.app.feature.home.CarPoolApp

class MockAuthViewModel : ViewModel() {
    private data class Account(val name: String, val password: String)
    private val accounts = mutableMapOf("admin" to Account("Admin", "admin"))
    var displayName by mutableStateOf<String?>(null)
        private set

    fun login(username: String, password: String): String? {
        val account = accounts[username.trim()]
        if (account == null || account.password != password) return "Incorrect username or password."
        displayName = account.name
        return null
    }

    fun register(name: String, username: String, password: String, confirmation: String): String? {
        val key = username.trim()
        if (name.isBlank() || key.isBlank() || password.isBlank()) return "Please complete all fields."
        if (key.any { it.isWhitespace() }) return "Username cannot contain spaces."
        if (accounts.containsKey(key)) return "That username is already taken."
        if (password.length < 4) return "Use at least 4 characters for your password."
        if (password != confirmation) return "Passwords do not match."
        accounts[key] = Account(name.trim(), password)
        displayName = name.trim()
        return null
    }

    fun logout() { displayName = null }
}

@Composable
fun AuthApp(auth: MockAuthViewModel = viewModel()) {
    var registering by rememberSaveable { mutableStateOf(false) }
    if (auth.displayName != null) {
        CarPoolApp(displayName = auth.displayName!!, onLogout = {
            auth.logout()
            registering = false
        })
    } else {
        AuthScreen(registering, { registering = it }, auth)
    }
}

@Composable
private fun AuthScreen(registering: Boolean, navigate: (Boolean) -> Unit, auth: MockAuthViewModel) {
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmation by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(registering) {
        name = ""; username = ""; password = ""; confirmation = ""; error = null; visible = false
    }
    BackHandler(enabled = registering) { navigate(false) }
    Scaffold(containerColor = Paper) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).imePadding()
                .verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (registering) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navigate(false) }) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back to login") }
                    Text("Create account", fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(Modifier.height(28.dp))
            Surface(color = Forest, shape = RoundedCornerShape(24.dp)) {
                Icon(Icons.Outlined.DirectionsCar, null, tint = Lime, modifier = Modifier.padding(20.dp).size(42.dp))
            }
            Spacer(Modifier.height(16.dp))
            Text("together", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Forest)
            Text("Good company. Shared journeys.", color = Muted)
            Spacer(Modifier.height(32.dp))
            Surface(color = androidx.compose.ui.graphics.Color.White, shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(if (registering) "Join the journey" else "Welcome back", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Text(if (registering) "Create your account to start sharing rides." else "Sign in to find your next ride.", color = Muted)
                    if (registering) OutlinedTextField(
                        name, { name = it; error = null }, label = { Text("Full name") },
                        singleLine = true, modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        username, { username = it; error = null }, label = { Text("Username") },
                        singleLine = true, modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(autoCorrectEnabled = false)
                    )
                    OutlinedTextField(
                        password, { password = it; error = null }, label = { Text("Password") },
                        singleLine = true, modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { visible = !visible }) {
                                Icon(if (visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                    if (visible) "Hide password" else "Show password")
                            }
                        }
                    )
                    if (registering) OutlinedTextField(
                        confirmation, { confirmation = it; error = null }, label = { Text("Confirm password") },
                        singleLine = true, modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                    error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    Button(
                        onClick = {
                            error = if (registering) auth.register(name, username, password, confirmation)
                            else auth.login(username, password)
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp)
                    ) { Text(if (registering) "Create account" else "Sign in", fontWeight = FontWeight.SemiBold) }
                    TextButton(onClick = { navigate(!registering) }, modifier = Modifier.fillMaxWidth()) {
                        Text(if (registering) "Already have an account? Sign in" else "New here? Create an account")
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            Text("Mock authentication • accounts stay in memory only", color = Muted, fontSize = 12.sp)
            if (!registering) Text("Demo login: admin / admin", color = Forest, fontSize = 13.sp)
        }
    }
}
