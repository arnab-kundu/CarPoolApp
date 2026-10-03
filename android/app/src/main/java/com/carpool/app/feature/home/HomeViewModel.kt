package com.carpool.app.feature.home
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carpool.app.core.data.*
import com.carpool.app.core.model.*
import java.time.LocalDate
import java.time.LocalDateTime
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
data class HomeState(val driverMode: Boolean = false, val origin: String = "Indiranagar", val destination: String = "Whitefield",
 val date: LocalDate = LocalDate.now().plusDays(1), val seats: Int = 1, val results: List<Ride> = emptyList(),
 val searched: Boolean = false, val busy: Boolean = false, val message: String? = null)
class HomeViewModel(private val repository: RideRepository = DemoRideRepository()) : ViewModel() {
 private val mutable = MutableStateFlow(HomeState())
 val state = mutable.asStateFlow()
 val rides = repository.rides
 fun mode(driver: Boolean) { mutable.update { it.copy(driverMode = driver) } }
 fun origin(value: String) { mutable.update { it.copy(origin = value, searched = false) } }
 fun destination(value: String) { mutable.update { it.copy(destination = value, searched = false) } }
 fun swap() { mutable.update { it.copy(origin = it.destination, destination = it.origin, searched = false) } }
 fun date(value: LocalDate) { mutable.update { it.copy(date = value, searched = false) } }
 fun seats(value: Int) { mutable.update { it.copy(seats = value.coerceIn(1,6), searched = false) } }
 fun clearMessage() { mutable.update { it.copy(message = null) } }
 fun search() {
 val s = state.value
 if (s.origin.isBlank() || s.destination.isBlank() || s.origin.trim().equals(s.destination.trim(), true)) {
 mutable.update { it.copy(message = "Enter different pickup and destination locations.") }; return
 }
 viewModelScope.launch {
 mutable.update { it.copy(busy = true) }
 runCatching { repository.search(RideSearch(s.origin, s.destination, s.date, s.seats)) }
 .onSuccess { list -> mutable.update { it.copy(results = list, searched = true, busy = false) } }
 .onFailure { mutable.update { it.copy(busy = false, message = "Could not load rides. Please try again.") } }
 }
 }
 fun saveDraft(origin: String, destination: String, dateTime: LocalDateTime, seats: String, price: String, done: () -> Unit) {
 viewModelScope.launch {
 runCatching {
 require(dateTime.isAfter(LocalDateTime.now()))
 repository.saveDraft(PublishDraft(origin, destination, dateTime, seats.toInt(), price.toInt()))
 }.onSuccess { mutable.update { it.copy(message = "Draft saved for this demo session.") }; done() }
 .onFailure { mutable.update { it.copy(message = "Check locations, future departure, seats (1-6), and contribution (0-10,000).") } }
 }
 }
}
