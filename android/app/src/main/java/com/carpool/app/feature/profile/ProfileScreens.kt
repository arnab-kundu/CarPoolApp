package com.carpool.app.feature.profile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carpool.app.core.designsystem.*

data class DemoVehicle(val model: String, val plate: String, val color: String, val seats: Int)
class DemoProfileState {
    var phone by mutableStateOf("")
    var verifiedPhone by mutableStateOf("")
    var codeSent by mutableStateOf(false)
    var license by mutableStateOf(false)
    var selfie by mutableStateOf(false)
    var submitted by mutableStateOf(false)
    val vehicles = mutableStateListOf(DemoVehicle("Hyundai i20", "KA 01 AB 1234", "Ocean blue", 3))
    var contactName by mutableStateOf("Aditi Sharma")
    var contactPhone by mutableStateOf("9876543210")
    var shareTrip by mutableStateOf(true)
    var alerts by mutableStateOf(true)
    var showRating by mutableStateOf(true)
    var marketing by mutableStateOf(false)
}

@Composable
fun ProfileHub(name: String, s: DemoProfileState, open: (String) -> Unit, logout: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Your profile", fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Text("A little trust goes a long way.", color = Muted)
        Hero(Icons.Outlined.PersonOutline, name, "Your journey, your way.")
        Text("ACCOUNT & TRUST", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Link(Icons.Outlined.PhoneAndroid, "Phone authentication", if (s.verifiedPhone.isEmpty()) "Add your number" else "Verified in demo") { open("phone") }
        Link(Icons.Outlined.Badge, "Driver verification", if (s.submitted) "Demo review pending" else "Complete your driver checklist") { open("driver") }
        Link(Icons.Outlined.DirectionsCar, "Your vehicles", s.vehicles.size.toString() + " vehicles in your garage") { open("vehicles") }
        Text("SAFETY & PREFERENCES", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Link(Icons.Outlined.Shield, "Safety & emergency contacts", "Travel with peace of mind") { open("safety") }
        Link(Icons.Outlined.Lock, "Privacy", "Choose what you share") { open("privacy") }
        OutlinedButton(onClick = logout, modifier = Modifier.fillMaxWidth()) { Text("Sign out") }
        Text("Offline demo. Profile changes reset when this session ends.", color = Muted, fontSize = 12.sp)
    }
}

@Composable
private fun Link(icon: ImageVector, title: String, subtitle: String, open: () -> Unit) {
    Surface(onClick = open, color = Color.White, shape = RoundedCornerShape(18.dp)) {
        Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Forest)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.SemiBold); Text(subtitle, color = Muted, fontSize = 12.sp) }
            Icon(Icons.Outlined.ChevronRight, null, tint = Muted)
        }
    }
}

@Composable
fun ProfileDetail(page: String, s: DemoProfileState, back: () -> Unit, modifier: Modifier = Modifier) {
    BackHandler(onBack = back)
    val title = when (page) { "phone" -> "Phone authentication"; "driver" -> "Driver verification"; "vehicles" -> "Your vehicles"; "safety" -> "Safety & emergency contacts"; else -> "Privacy" }
    Column(modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = back) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back to profile") }
            Text(title, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        }
        when (page) { "phone" -> PhoneSettings(s); "driver" -> DriverSettings(s); "vehicles" -> VehicleSettings(s); "safety" -> SafetySettings(s); else -> PrivacySettings(s) }
    }
}

@Composable private fun Hero(icon: ImageVector, title: String, subtitle: String) {
    Surface(color = Forest, shape = RoundedCornerShape(24.dp)) {
        Column(Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(icon, null, tint = Lime, modifier = Modifier.size(34.dp))
            Text(title, fontSize = 27.sp, lineHeight = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(subtitle, color = Lime, fontSize = 13.sp)
        }
    }
}
@Composable private fun Field(value: String, label: String, numeric: Boolean = false, change: (String) -> Unit) {
    OutlinedTextField(value, change, label = { Text(label) }, singleLine = true, shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(keyboardType = if (numeric) KeyboardType.Number else KeyboardType.Text), modifier = Modifier.fillMaxWidth())
}
@Composable private fun Note(text: String) {
    Surface(color = Color(0xFFEAF0DF), shape = RoundedCornerShape(16.dp)) {
        Text(text, Modifier.fillMaxWidth().padding(16.dp), color = Forest, fontSize = 12.sp, lineHeight = 18.sp)
    }
}

@Composable private fun PhoneSettings(s: DemoProfileState) {
    var code by rememberSaveable { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    Hero(Icons.Outlined.PhoneAndroid, "Stay connected.", "A verified number helps your ride companions reach you.")
    if (s.verifiedPhone.isNotEmpty()) Note("Phone verified in demo: +91 " + s.verifiedPhone)
    Field(s.phone, "Mobile number (+91)", true) { s.phone = it.filter(Char::isDigit).take(10); s.codeSent = false; code = ""; error = null }
    Button(onClick = {
        if (s.phone.length != 10) error = "Enter a 10-digit mobile number." else { s.codeSent = true; error = null }
    }, modifier = Modifier.fillMaxWidth()) { Text(if (s.codeSent) "Resend demo code" else "Send demo code") }
    if (s.codeSent) {
        Note("Demo code: 123456. No SMS is sent.")
        Field(code, "Verification code", true) { code = it.filter(Char::isDigit).take(6); error = null }
        Button(onClick = {
            if (code == "123456") { s.verifiedPhone = s.phone; s.codeSent = false; code = ""; error = null }
            else error = "Enter the demo code 123456."
        }, modifier = Modifier.fillMaxWidth()) { Text("Verify phone") }
    }
    error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    Text("Demo verification only. This does not verify your identity.", color = Muted, fontSize = 12.sp)
}

@Composable private fun DriverSettings(s: DemoProfileState) {
    Hero(Icons.Outlined.VerifiedUser, "Build trust. Share the road.", "Complete these steps before offering your first ride.")
    Note(if (s.submitted) "Demo application submitted - awaiting review" else "Your checklist: " + listOf(s.license, s.selfie).count { it } + " of 2 steps ready")
    Link(Icons.Outlined.Badge, "Driving licence", if (s.license) "Sample licence added" else "Add a sample document") { if (!s.submitted) s.license = !s.license }
    Link(Icons.Outlined.Face, "Identity photo", if (s.selfie) "Sample photo added" else "Add a sample photo") { if (!s.submitted) s.selfie = !s.selfie }
    Text("How it works", fontWeight = FontWeight.Bold, fontSize = 20.sp)
    Text("Add your licence and photo, submit your application, then check its status here.", color = Muted)
    Button(onClick = { s.submitted = true }, enabled = s.license && s.selfie && !s.submitted, modifier = Modifier.fillMaxWidth()) {
        Text(if (s.submitted) "Demo review pending" else "Submit demo verification")
    }
    Note("These actions attach sample placeholders. No camera, files, identity checks, or document uploads are used.")
}

@Composable private fun VehicleSettings(s: DemoProfileState) {
    var editing by rememberSaveable { mutableStateOf<Int?>(null) }
    var adding by rememberSaveable { mutableStateOf(false) }
    var model by rememberSaveable { mutableStateOf("") }
    var plate by rememberSaveable { mutableStateOf("") }
    var color by rememberSaveable { mutableStateOf("") }
    var seats by rememberSaveable { mutableStateOf("3") }
    var error by remember { mutableStateOf<String?>(null) }
    Hero(Icons.Outlined.DirectionsCar, "Your garage.", "Make it easy for riders to recognise your car.")
    s.vehicles.forEachIndexed { index, v ->
        Surface(color = Color.White, shape = RoundedCornerShape(22.dp)) {
            Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Outlined.DirectionsCar, null, tint = Forest, modifier = Modifier.size(40.dp))
                Text(v.model, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                Text(v.plate, color = Forest, fontWeight = FontWeight.SemiBold)
                Text(v.color + " - " + v.seats + " passenger seats", color = Muted)
                TextButton(onClick = { editing = index; adding = false; model = v.model; plate = v.plate; color = v.color; seats = v.seats.toString(); error = null }) { Text("Edit vehicle") }
            }
        }
    }
    OutlinedButton(onClick = { adding = true; editing = null; model = ""; plate = ""; color = ""; seats = "3"; error = null }, modifier = Modifier.fillMaxWidth()) {
        Icon(Icons.Outlined.Add, null); Text("Add vehicle")
    }
    if (adding || editing != null) {
        Text(if (adding) "Add your car" else "Edit your car", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Field(model, "Make and model") { model = it; error = null }
        Field(plate, "Registration number") { plate = it.uppercase(); error = null }
        Field(color, "Colour") { color = it; error = null }
        Field(seats, "Passenger seats (1-6)", true) { seats = it; error = null }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(onClick = {
            val count = seats.toIntOrNull()
            if (model.isBlank() || plate.isBlank() || color.isBlank() || count == null || count !in 1..6) error = "Complete all fields and choose 1 to 6 seats."
            else if (s.vehicles.withIndex().any { it.index != editing && it.value.plate.replace(" ", "").equals(plate.replace(" ", ""), true) }) error = "This registration number is already in your garage."
            else {
                val vehicle = DemoVehicle(model.trim(), plate.trim(), color.trim(), count)
                val index = editing
                if (index == null) s.vehicles.add(vehicle) else s.vehicles[index] = vehicle
                adding = false; editing = null
            }
        }, modifier = Modifier.fillMaxWidth()) { Text("Save vehicle") }
        TextButton(onClick = { adding = false; editing = null }) { Text("Cancel") }
    }
    Note("Sample vehicle details only. Ownership and documents are not verified.")
}

@Composable private fun SafetySettings(s: DemoProfileState) {
    var name by rememberSaveable { mutableStateOf(s.contactName) }
    var phone by rememberSaveable { mutableStateOf(s.contactPhone) }
    var feedback by remember { mutableStateOf<String?>(null) }
    Hero(Icons.Outlined.Shield, "Peace of mind, every mile.", "Keep someone you trust close to your journey.")
    Text("Emergency contact", fontSize = 20.sp, fontWeight = FontWeight.Bold)
    Note(s.contactName + " - +91 " + s.contactPhone + " - sample contact")
    Field(name, "Contact name") { name = it; feedback = null }
    Field(phone, "Contact phone", true) { phone = it.filter(Char::isDigit).take(10); feedback = null }
    Button(onClick = {
        if (name.isBlank() || phone.length != 10) feedback = "Enter a name and 10-digit phone number."
        else { s.contactName = name.trim(); s.contactPhone = phone; feedback = "Contact saved for this demo session." }
    }, modifier = Modifier.fillMaxWidth()) { Text("Save emergency contact") }
    feedback?.let { Note(it) }
    Toggle("Trip sharing", "Demo preference for sharing your journey with your contact.", s.shareTrip) { s.shareTrip = it }
    Toggle("Safety reminders", "Demo preference for reminders before your ride.", s.alerts) { s.alerts = it }
    Text("Before you ride", fontSize = 20.sp, fontWeight = FontWeight.Bold)
    Note("Check the driver and vehicle details, agree on a public pickup point, and let someone you trust know your plans.")
    Text("This demo does not contact emergency services or share your location.", color = Muted, fontSize = 12.sp)
}

@Composable private fun PrivacySettings(s: DemoProfileState) {
    Hero(Icons.Outlined.Lock, "Your privacy. Your choices.", "Choose how you appear to your ride companions.")
    Toggle("Show my rating", "Demo preference for displaying your profile rating.", s.showRating) { s.showRating = it }
    Toggle("Product updates", "Demo preference for occasional news and updates.", s.marketing) { s.marketing = it }
    Text("Your data in this demo", fontSize = 20.sp, fontWeight = FontWeight.Bold)
    Note("Profile forms and preferences stay in memory. No location is collected, and no profile information is sent to a server.")
    Text("Phone visibility", fontWeight = FontWeight.SemiBold)
    Text("Visible only to you in this demo.", color = Muted)
    Text("These switches showcase privacy controls. They do not change device permissions or backend settings.", color = Muted, fontSize = 12.sp)
}

@Composable private fun Toggle(title: String, subtitle: String, checked: Boolean, change: (Boolean) -> Unit) {
    Surface(color = Color.White, shape = RoundedCornerShape(18.dp)) {
        Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.SemiBold); Text(subtitle, color = Muted, fontSize = 12.sp) }
            Spacer(Modifier.width(12.dp)); Switch(checked, change)
        }
    }
}
