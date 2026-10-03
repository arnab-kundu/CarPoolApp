package com.carpool.app.feature.home
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carpool.app.core.designsystem.*
import com.carpool.app.core.model.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
private val timeFormat = DateTimeFormatter.ofPattern("h:mm a")
@Composable internal fun DateButton(date: LocalDate, change: (LocalDate) -> Unit, modifier: Modifier = Modifier) {
 val context = LocalContext.current
 TextButton(onClick = {
 DatePickerDialog(context, { _, y, m, d -> change(LocalDate.of(y,m+1,d)) }, date.year,date.monthValue-1,date.dayOfMonth).apply { datePicker.minDate = System.currentTimeMillis()-1000 }.show()
 }, modifier = modifier) { Icon(Icons.Outlined.CalendarToday, null, Modifier.size(17.dp)); Spacer(Modifier.width(7.dp)); Text(date.format(DateTimeFormatter.ofPattern("EEE, d MMM")), fontSize = 12.sp) }
}
@Composable internal fun RideCard(ride: Ride, open: () -> Unit) {
 Surface(onClick = open, shape = RoundedCornerShape(20.dp), color = Color.White) {
 Column(Modifier.padding(18.dp)) {
 Row(verticalAlignment = Alignment.CenterVertically) {
 Box(Modifier.size(44.dp).background(Color(0xFFEAF0DF),CircleShape),contentAlignment = Alignment.Center) { Text(ride.initials,color = Forest,fontWeight = FontWeight.Bold) }
 Spacer(Modifier.width(12.dp))
 Column(Modifier.weight(1f)) { Text(ride.driver,fontWeight = FontWeight.SemiBold); Text(ride.vehicle,color = Muted,fontSize = 12.sp) }
 Icon(Icons.Outlined.Star,null,tint = Color(0xFFB58A37),modifier = Modifier.size(15.dp)); Text(ride.rating,fontSize = 12.sp)
 }
 Spacer(Modifier.height(18.dp))
 Row { Text(ride.departure.format(timeFormat),fontWeight = FontWeight.Bold,modifier = Modifier.width(88.dp)); Text(ride.origin,fontWeight = FontWeight.Medium) }
 Row(Modifier.padding(vertical = 5.dp)) { Text(if (ride.durationMinutes > 0) "${ride.durationMinutes} min" else "Draft",color = Muted,fontSize = 11.sp,modifier = Modifier.width(88.dp)); Icon(Icons.Outlined.South,null,tint = Forest,modifier = Modifier.size(16.dp)) }
 Row { Text(if (ride.durationMinutes > 0) ride.departure.plusMinutes(ride.durationMinutes.toLong()).format(timeFormat) else "--",fontWeight = FontWeight.Bold,modifier = Modifier.width(88.dp)); Text(ride.destination,fontWeight = FontWeight.Medium) }
 Spacer(Modifier.height(16.dp)); HorizontalDivider(color = Paper); Spacer(Modifier.height(12.dp))
 Row(verticalAlignment = Alignment.CenterVertically) {
 Icon(Icons.Outlined.EventSeat,null,tint = Muted,modifier = Modifier.size(16.dp)); Spacer(Modifier.width(5.dp)); Text("${ride.seats} seats",color = Muted,fontSize = 12.sp,modifier = Modifier.weight(1f))
 if (ride.status == RideStatus.DRAFT) Text("LOCAL DRAFT",fontSize = 10.sp,color = Forest)
 else { Text("\u20B9${ride.priceRupees}",fontWeight = FontWeight.Bold,fontSize = 22.sp,color = Forest); Text(" / seat",color = Muted,fontSize = 11.sp) }
 }
 }
 }
}
@Composable internal fun RideDetails(ride: Ride, back: () -> Unit) {
 BackHandler(onBack = back)
 BackHeader("Ride details",back)
 Text("A better way\nto get there.",fontSize = 32.sp,lineHeight = 38.sp,fontWeight = FontWeight.Bold)
 Spacer(Modifier.height(16.dp)); RideCard(ride) {}
 Spacer(Modifier.height(20.dp)); Text("About this ride",fontSize = 20.sp,fontWeight = FontWeight.Bold)
 Spacer(Modifier.height(8.dp)); Text(ride.note,color = Muted)
 Spacer(Modifier.height(20.dp)); DetailRow(Icons.Outlined.CalendarToday,"Departure",ride.departure.format(DateTimeFormatter.ofPattern("EEE, d MMM - h:mm a")))
 DetailRow(Icons.Outlined.VerifiedUser,"Driver verification",if (ride.verified) "Verified - sample badge" else "Not submitted")
 DetailRow(Icons.Outlined.Payments,"Seat contribution","\u20B9${ride.priceRupees} - sample amount")
 Spacer(Modifier.height(24.dp)); Notice(if (ride.status == RideStatus.DRAFT) "This draft stays in memory until the app closes. Connect your account, vehicle, and a calculated route to publish." else "This is a sample ride. Requests require the live backend, verified identities, and server-side seat reservation.")
 Spacer(Modifier.height(20.dp)); OutlinedButton(onClick = back,modifier = Modifier.fillMaxWidth()) { Text("Back to rides") }
 Spacer(Modifier.height(24.dp))
}
@Composable internal fun PublishScreen(vm: HomeViewModel, back: () -> Unit) {
 BackHandler(onBack = back)
 var origin by rememberSaveable { mutableStateOf("") }
 var destination by rememberSaveable { mutableStateOf("") }
 var seats by rememberSaveable { mutableStateOf("3") }
 var price by rememberSaveable { mutableStateOf("120") }
 var dateText by rememberSaveable { mutableStateOf(LocalDate.now().plusDays(1).toString()) }
 var timeText by rememberSaveable { mutableStateOf("08:30") }
 val date = LocalDate.parse(dateText); val time = LocalTime.parse(timeText)
 val context = LocalContext.current
 BackHeader("Offer a ride",back)
 Text("Make room for\ngood company.",fontSize = 31.sp,lineHeight = 38.sp,fontWeight = FontWeight.Bold)
 Spacer(Modifier.height(12.dp)); Text("Plan your journey. Save a draft to review.",color = Muted)
 Spacer(Modifier.height(24.dp))
 OutlinedTextField(origin,{ origin = it },label = { Text("Pickup location") },singleLine = true,modifier = Modifier.fillMaxWidth())
 Spacer(Modifier.height(12.dp)); OutlinedTextField(destination,{ destination = it },label = { Text("Destination") },singleLine = true,modifier = Modifier.fillMaxWidth())
 Spacer(Modifier.height(12.dp)); Row(verticalAlignment = Alignment.CenterVertically) {
 DateButton(date,{ dateText = it.toString() },Modifier.weight(1f))
 TextButton(onClick = { TimePickerDialog(context,{ _, h, m -> timeText = LocalTime.of(h,m).toString() },time.hour,time.minute,false).show() }) { Icon(Icons.Outlined.Schedule,null,Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text(time.format(timeFormat)) }
 }
 Spacer(Modifier.height(12.dp)); OutlinedTextField(seats,{ seats = it },label = { Text("Available seats (1-6)") },singleLine = true,modifier = Modifier.fillMaxWidth())
 Spacer(Modifier.height(12.dp)); OutlinedTextField(price,{ price = it },label = { Text("Contribution per seat (\u20B9)") },singleLine = true,modifier = Modifier.fillMaxWidth())
 Spacer(Modifier.height(20.dp)); Notice("A verified driver, registered vehicle, and a calculated map route are required before publishing. This form saves a local demo draft.")
 Spacer(Modifier.height(20.dp)); Button(onClick = { vm.saveDraft(origin,destination,date.atTime(time),seats,price,back) },modifier = Modifier.fillMaxWidth().height(52.dp),shape = RoundedCornerShape(14.dp)) { Text("Save ride draft") }
 Spacer(Modifier.height(24.dp))
}
@Composable private fun BackHeader(title: String, back: () -> Unit) { Row(Modifier.padding(top = 12.dp,bottom = 20.dp),verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = back) { Icon(Icons.AutoMirrored.Outlined.ArrowBack,"Back") }; Text(title,fontWeight = FontWeight.SemiBold,fontSize = 18.sp) } }
@Composable internal fun PageTitle(title: String, subtitle: String) { Spacer(Modifier.height(30.dp)); Text(title,fontSize = 32.sp,fontWeight = FontWeight.Bold); Spacer(Modifier.height(8.dp)); Text(subtitle,color = Muted); Spacer(Modifier.height(28.dp)) }
@Composable private fun DetailRow(icon: ImageVector, title: String, value: String) { Row(Modifier.fillMaxWidth().padding(vertical = 14.dp),verticalAlignment = Alignment.CenterVertically) { Icon(icon,null,tint = Forest,modifier = Modifier.size(24.dp)); Spacer(Modifier.width(15.dp)); Column { Text(title,fontWeight = FontWeight.Medium); Text(value,color = Muted,fontSize = 12.sp) } }; HorizontalDivider(color = Color(0xFFE5E9E0)) }
@Composable internal fun Notice(text: String) { Surface(shape = RoundedCornerShape(16.dp),color = Color(0xFFEAF0DF)) { Row(Modifier.padding(16.dp)) { Icon(Icons.Outlined.Info,null,tint = Forest,modifier = Modifier.size(18.dp)); Spacer(Modifier.width(10.dp)); Text(text,fontSize = 12.sp,lineHeight = 18.sp,color = Forest) } } }
@Composable internal fun EmptyCard(icon: ImageVector, title: String, text: String) { Surface(shape = RoundedCornerShape(22.dp),color = Color.White) { Column(Modifier.fillMaxWidth().padding(28.dp),horizontalAlignment = Alignment.CenterHorizontally) { Icon(icon,null,tint = Forest,modifier = Modifier.size(42.dp)); Spacer(Modifier.height(16.dp)); Text(title,fontSize = 20.sp,fontWeight = FontWeight.SemiBold); Spacer(Modifier.height(8.dp)); Text(text,color = Muted,fontSize = 14.sp) } }; Spacer(Modifier.height(20.dp)) }
