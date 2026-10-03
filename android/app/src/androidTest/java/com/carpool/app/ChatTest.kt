package com.carpool.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChatTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Before fun openInbox() {
        compose.onNodeWithText("Username").performTextInput("admin")
        compose.onNodeWithText("Password").performTextInput("admin")
        compose.onNodeWithText("Sign in").performScrollTo().performClick()
        compose.onNodeWithText("Inbox").performClick()
    }

    @Test fun sendMessageAndReturnToConversation() {
        compose.onNodeWithText("Ananya Rao").performClick()
        compose.onNodeWithContentDescription("Send message").assertIsNotEnabled()
        compose.onNodeWithText("Write a message…").performTextInput("See you at the pickup!")
        compose.onNodeWithContentDescription("Send message").performClick()
        compose.onNodeWithText("See you at the pickup!").assertIsDisplayed()
        compose.onNodeWithContentDescription("Back to inbox").performClick()
        compose.onNodeWithText("You: See you at the pickup!").assertExists()
        compose.onNodeWithText("Ananya Rao").performClick()
        compose.onNodeWithText("See you at the pickup!").assertExists()
    }

    @Test fun searchFiltersConversations() {
        compose.onNodeWithText("Search people or routes").performTextInput("Electronic")
        compose.onNodeWithText("Rahul Mehta").assertExists()
        compose.onNodeWithText("Ananya Rao").assertDoesNotExist()
    }
}
