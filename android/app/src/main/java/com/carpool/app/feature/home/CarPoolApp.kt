package com.carpool.app.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.carpool.app.core.designsystem.*
import com.carpool.app.core.model.*
import com.carpool.app.feature.chat.*
import com.carpool.app.feature.profile.*

@Composable fun CarPoolApp(vm: HomeViewModel = viewModel(), displayName: String = "Admin", onLogout: () -> Unit = {}) {
 val state by vm.state.collectAsStateWithLifecycle()
 val rides by vm.rides.collectAsStateWithLifecycle()
 val profileState = remember { DemoProfileState() }
 var profilePage by rememberSaveable { mutableStateOf<String?>(null) }
 val chatState = remember { DemoChatState() }
 var conversationId by rememberSaveable { mutableStateOf<String?>(null) }
 var tab by rememberSaveable { mutableStateOf(0) }
 var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
 var publishing by rememberSaveable { mutableStateOf(false) }
 val selected = rides.firstOrNull { it.id == selectedId }
 val snackbar = remember { SnackbarHostState() }
 LaunchedEffect(state.message) { state.message?.let { snackbar.showSnackbar(it); vm.clearMessage() } }
 Scaffold(containerColor = Paper, snackbarHost = { SnackbarHost(snackbar) }, bottomBar = {
 if (selected == null && !publishing && conversationId == null && profilePage == null) NavigationBar(modifier = Modifier.shadow(8.dp), containerColor = Color(0xFFE8EEE5), tonalElevation = 3.dp) {
 listOf("Explore" to Icons.Outlined.Explore, "My rides" to Icons.Outlined.DirectionsCar, "Inbox" to Icons.Outlined.ChatBubbleOutline, "Profile" to Icons.Outlined.PersonOutline).forEachIndexed { index, pair ->
 NavigationBarItem(selected = tab == index, onClick = { tab = index }, icon = { Icon(pair.second, null) }, label = { Text(pair.first) }, colors = NavigationBarItemDefaults.colors(indicatorColor = Lime))
 }
 }
 }) { padding ->
 if (tab == 3 && selected == null && !publishing) {
 val page = profilePage
 if (page == null) ProfileHub(displayName, profileState, { profilePage = it }, onLogout, Modifier.padding(padding))
 else ProfileDetail(page, profileState, { profilePage = null }, Modifier.padding(padding))
 } else if (tab == 2 && selected == null && !publishing) {
 val activeChat = conversationId
 if (activeChat == null) ChatInbox(chatState, { conversationId = it }, Modifier.padding(padding))
 else ChatConversation(chatState, activeChat, { conversationId = null }, Modifier.padding(padding))
 } else Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)) {
 when {
 selected != null -> RideDetails(selected) { selectedId = null }
 publishing -> PublishScreen(vm) { publishing = false }
 tab == 0 -> {
 Header()
 ModePicker(state.driverMode, vm::mode)
 Spacer(Modifier.height(28.dp))
 Text(if (state.driverMode) "Good company.\nShared journeys." else "Your commute,\nbetter together.", fontSize = 34.sp, lineHeight = 39.sp, fontWeight = FontWeight.Bold, color = Ink)
 Spacer(Modifier.height(10.dp))
 Text(if (state.driverMode) "Offer your empty seats. Make the journey count." else "Find your people. Share the ride. Split the cost.", color = Muted, fontSize = 14.sp)
 Spacer(Modifier.height(24.dp))
 if (state.driverMode) {
 Surface(shape = RoundedCornerShape(24.dp), color = Forest) {
 Column(Modifier.padding(24.dp)) {
 Icon(Icons.Outlined.DirectionsCar, null, tint = Lime, modifier = Modifier.size(36.dp))
 Spacer(Modifier.height(20.dp))
 Text("One car. More possibilities.", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Bold)
 Spacer(Modifier.height(8.dp))
 Text("Plan a ride and invite a better way to commute.", color = Color(0xFFD8E5DC))
 Spacer(Modifier.height(20.dp))
 Button(onClick = { publishing = true }, colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = Ink), modifier = Modifier.fillMaxWidth()) { Text("Create a ride draft"); Spacer(Modifier.width(10.dp)); Icon(Icons.Outlined.Add, null) }
 }
 }
 Spacer(Modifier.height(20.dp)); Notice("Driver verification, maps, and publishing connect in the next phase.")
 } else {
 SearchCard(state, vm)
 Spacer(Modifier.height(24.dp))
 Row(verticalAlignment = Alignment.CenterVertically) {
 Text(if (state.searched) "Your ride matches" else "A few rides to explore", fontSize = 21.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
 Text("DEMO", color = Forest, fontWeight = FontWeight.Bold, fontSize = 11.sp)
 }
 Spacer(Modifier.height(6.dp))
 Text(if (state.searched) "${state.results.size} sample rides - local text filtering" else "Sample journeys around Bengaluru", color = Muted, fontSize = 13.sp)
 Spacer(Modifier.height(16.dp))
 val visible = if (state.searched) state.results else rides.filter { it.status == RideStatus.PUBLISHED }
 if (visible.isEmpty()) EmptyCard(Icons.Outlined.SearchOff, "No sample rides found", "Try Indiranagar to Whitefield, tomorrow, with one or two seats.")
 visible.forEach { ride -> RideCard(ride) { selectedId = ride.id }; Spacer(Modifier.height(12.dp)) }
 }
 Spacer(Modifier.height(12.dp)); Notice("Offline demo - no real bookings or payments. Profile badges and reviews are sample data.")
 Spacer(Modifier.height(24.dp))
 }
 tab == 1 -> {
 PageTitle("My rides", "Your next journey starts here.")
 val drafts = rides.filter { it.status == RideStatus.DRAFT }
 if (drafts.isEmpty()) EmptyCard(Icons.Outlined.Route, "A fresh road ahead", "Your saved drafts appear here. Real ride history will come from the backend.")
 drafts.forEach { RideCard(it) { selectedId = it.id }; Spacer(Modifier.height(12.dp)) }
 Button(onClick = { publishing = true }, modifier = Modifier.fillMaxWidth()) { Text("Create a ride draft") }
 }


 }
 }
 }
}
@Composable private fun Header() {
 Row(Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 24.dp), verticalAlignment = Alignment.CenterVertically) {
 Box(Modifier.size(36.dp).background(Forest, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) { Icon(Icons.Outlined.DirectionsCar, null, tint = Lime, modifier = Modifier.size(23.dp)) }
 Spacer(Modifier.width(9.dp)); Text("together", fontWeight = FontWeight.Bold, fontSize = 25.sp, letterSpacing = (-1).sp, modifier = Modifier.weight(1f))
 Surface(color = Color(0xFFE9EDE3), shape = CircleShape) { Text("YO", Modifier.padding(11.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Forest) }
 }
}
@Composable private fun ModePicker(driver: Boolean, change: (Boolean) -> Unit) {
 Row(Modifier.fillMaxWidth().background(Color(0xFFE8ECE3), RoundedCornerShape(15.dp)).padding(5.dp)) {
 listOf(false to "Find a ride", true to "Offer a ride").forEach { (mode, label) ->
 Surface(color = if (driver == mode) Color.White else Color.Transparent, shape = RoundedCornerShape(11.dp), modifier = Modifier.weight(1f).clickable { change(mode) }) {
 Row(Modifier.padding(vertical = 12.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
 Icon(if (mode) Icons.Outlined.DirectionsCar else Icons.Outlined.Search, null, Modifier.size(18.dp), tint = Forest)
 Spacer(Modifier.width(8.dp)); Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Forest)
 }
 }
 }
 }
}
@Composable private fun SearchCard(s: HomeState, vm: HomeViewModel) {
 Surface(shape = RoundedCornerShape(24.dp), color = Color.White) {
 Column(Modifier.padding(20.dp)) {
 OutlinedTextField(s.origin, vm::origin, label = { Text("Pickup") }, leadingIcon = { Icon(Icons.Outlined.MyLocation, null, tint = Forest) }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp))
 Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) { IconButton(onClick = vm::swap, modifier = Modifier.size(34.dp)) { Icon(Icons.Outlined.SwapVert, "Swap pickup and destination", tint = Forest) } }
 OutlinedTextField(s.destination, vm::destination, label = { Text("Destination") }, leadingIcon = { Icon(Icons.Outlined.LocationOn, null, tint = Forest) }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp))
 Spacer(Modifier.height(16.dp))
 Row(verticalAlignment = Alignment.CenterVertically) {
 DateButton(s.date, vm::date, Modifier.weight(1f))
 IconButton(onClick = { vm.seats(s.seats - 1) }, enabled = s.seats > 1) { Icon(Icons.Outlined.Remove, "Fewer seats") }
 Text("${s.seats}", fontWeight = FontWeight.Bold)
 IconButton(onClick = { vm.seats(s.seats + 1) }, enabled = s.seats < 6) { Icon(Icons.Outlined.Add, "More seats") }
 Icon(Icons.Outlined.PersonOutline, "Seats", tint = Muted, modifier = Modifier.size(18.dp))
 }
 Spacer(Modifier.height(16.dp))
 Button(onClick = vm::search, enabled = !s.busy, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth().height(52.dp)) {
 Text(if (s.busy) "Searching..." else "Find my ride", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
 Spacer(Modifier.width(12.dp)); Icon(Icons.AutoMirrored.Outlined.ArrowForward, null, modifier = Modifier.size(20.dp))
 }
 }
 }
}
