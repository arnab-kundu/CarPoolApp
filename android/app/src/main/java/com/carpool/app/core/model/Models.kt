package com.carpool.app.core.model
import java.time.LocalDateTime
enum class RideStatus { DRAFT, PUBLISHED, IN_PROGRESS, COMPLETED, CANCELLED, EXPIRED }
enum class BookingStatus { REQUESTED, ACCEPTED, PAYMENT_PENDING, CONFIRMED, TRIP_STARTED, COMPLETED, REJECTED, CANCELLED_BY_DRIVER, CANCELLED_BY_PASSENGER, NO_SHOW, PAYMENT_FAILED, REFUNDED }
enum class VerificationStatus { NOT_SUBMITTED, SUBMITTED, UNDER_REVIEW, VERIFIED, REJECTED, EXPIRED }
data class Ride(val id: String, val driver: String, val initials: String, val origin: String,
 val destination: String, val departure: LocalDateTime, val durationMinutes: Int,
 val seats: Int, val priceRupees: Int, val vehicle: String, val rating: String,
 val verified: Boolean, val note: String, val status: RideStatus = RideStatus.PUBLISHED)
data class RideSearch(val origin: String, val destination: String, val date: java.time.LocalDate, val seats: Int)
data class PublishDraft(val origin: String, val destination: String, val departure: LocalDateTime, val seats: Int, val contribution: Int)
