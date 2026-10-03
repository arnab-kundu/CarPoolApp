package com.carpool.app.core.data
import com.carpool.app.core.model.*
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
/** Offline fixtures only. Real route matching and verification are server responsibilities. */
class DemoRideRepository : RideRepository {
 private val date = LocalDate.now().plusDays(1)
 private val state = MutableStateFlow(listOf(
 Ride("sample-1", "Aarav Sharma", "AS", "Indiranagar", "Whitefield", date.atTime(8,30), 45, 3, 120, "Honda City - White", "4.9", true, "A little music, a smooth commute. Pickup near the metro station."),
 Ride("sample-2", "Priya Nair", "PN", "Indiranagar", "Whitefield", date.atTime(9,0), 40, 2, 100, "Maruti Baleno - Blue", "4.8", true, "Heading to ITPL. Please arrive five minutes early."),
 Ride("sample-3", "Rohan Mehta", "RM", "Koramangala", "Electronic City", date.atTime(8,15), 50, 2, 150, "Hyundai i20 - Grey", "4.7", true, "A comfortable ride to work. No smoking, please.")))
 override val rides = state.asStateFlow()
 override suspend fun search(query: RideSearch): List<Ride> = state.value.filter {
 it.status == RideStatus.PUBLISHED && it.seats >= query.seats && it.departure.toLocalDate() == query.date &&
 it.origin.contains(query.origin.trim(), true) && it.destination.contains(query.destination.trim(), true)
 }
 override suspend fun saveDraft(draft: PublishDraft): Ride {
 require(draft.origin.isNotBlank() && draft.destination.isNotBlank())
 require(!draft.origin.trim().equals(draft.destination.trim(), true))
 require(draft.seats in 1..6 && draft.contribution in 0..10000)
 val ride = Ride(UUID.randomUUID().toString(), "You", "YO", draft.origin.trim(), draft.destination.trim(), draft.departure, 0,
 draft.seats, draft.contribution, "Vehicle not connected", "--", false, "Local draft. Connect the backend and verify your driver profile to publish.", RideStatus.DRAFT)
 state.value = state.value + ride
 return ride
 }
}
