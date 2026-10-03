package com.carpool.app
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
class ExploreTest {
 @get:Rule val compose = createAndroidComposeRule<MainActivity>()
 @Before fun login() { compose.onNodeWithText("Username").performTextInput("admin"); compose.onNodeWithText("Password").performTextInput("admin"); compose.onNodeWithText("Sign in").performScrollTo().performClick() }
 @Test fun searchShowsDemoResults() {
  compose.onNodeWithText("Find my ride").performScrollTo().performClick()
  compose.onNodeWithText("Your ride matches").performScrollTo().assertIsDisplayed()
  compose.onNodeWithText("2 sample rides - local text filtering").assertExists()
 }
 @Test fun driverCanOpenDraftForm() {
  compose.onNodeWithText("Offer a ride").performClick()
  compose.onNodeWithText("Create a ride draft").performScrollTo().performClick()
  compose.onNodeWithText("Save ride draft").assertExists()
 }
}
