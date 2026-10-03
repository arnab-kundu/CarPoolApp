package com.carpool.app
import com.carpool.app.core.data.DemoRideRepository
import com.carpool.app.core.model.*
import java.time.LocalDate
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
class DemoRideRepositoryTest {
 @Test fun filtersSeatsDateAndRoute() = runBlocking {
  val repo = DemoRideRepository()
  val date = LocalDate.now().plusDays(1)
  assertEquals(2,repo.search(RideSearch("Indiranagar","Whitefield",date,1)).size)
  assertEquals(1,repo.search(RideSearch("Indiranagar","Whitefield",date,3)).size)
  assertTrue(repo.search(RideSearch("Whitefield","Indiranagar",date,1)).isEmpty())
  assertTrue(repo.search(RideSearch("Indiranagar","Whitefield",date.plusDays(1),1)).isEmpty())
 }
 @Test fun draftsNeverAppearInPublishedSearch() = runBlocking {
  val repo = DemoRideRepository()
  val date = LocalDate.now().plusDays(1)
  val draft = repo.saveDraft(PublishDraft("Indiranagar","Whitefield",date.atTime(8,0),3,100))
  assertEquals(RideStatus.DRAFT,draft.status)
  assertFalse(draft.verified)
  assertEquals(2,repo.search(RideSearch("Indiranagar","Whitefield",date,1)).size)
 }
 @Test(expected = IllegalArgumentException::class) fun rejectsInvalidSeats() { runBlocking { DemoRideRepository().saveDraft(PublishDraft("A","B",LocalDate.now().plusDays(1).atStartOfDay(),7,100)) }; Unit }
}
