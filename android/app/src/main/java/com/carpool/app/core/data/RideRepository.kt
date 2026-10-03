package com.carpool.app.core.data
import com.carpool.app.core.model.*
import kotlinx.coroutines.flow.StateFlow
interface RideRepository {
 val rides: StateFlow<List<Ride>>
 suspend fun search(query: RideSearch): List<Ride>
 suspend fun saveDraft(draft: PublishDraft): Ride
}
