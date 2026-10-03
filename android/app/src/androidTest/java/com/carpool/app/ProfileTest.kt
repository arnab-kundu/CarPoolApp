package com.carpool.app
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
class ProfileTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    @Before fun login() {
        compose.onNodeWithText("Username").performTextInput("admin")
        compose.onNodeWithText("Password").performTextInput("admin")
        compose.onNodeWithText("Sign in").performScrollTo().performClick()
        compose.onNodeWithText("Profile").performClick()
    }
    @Test fun phoneVerification() {
        compose.onNodeWithText("Phone authentication").performScrollTo().performClick()
        compose.onNodeWithText("Mobile number (+91)").performTextInput("9876543210")
        compose.onNodeWithText("Send demo code").performScrollTo().performClick()
        compose.onNodeWithText("Verification code").performScrollTo().performTextInput("123456")
        compose.onNodeWithText("Verify phone").performScrollTo().performClick()
        compose.onNodeWithText("Phone verified in demo: +91 9876543210").assertExists()
        compose.onNodeWithContentDescription("Back to profile").performScrollTo().performClick()
        compose.onNodeWithText("Verified in demo").assertExists()
    }
    @Test fun allSectionsOpen() {
        listOf("Driver verification" to "Driving licence", "Your vehicles" to "Hyundai i20",
            "Safety & emergency contacts" to "Emergency contact", "Privacy" to "Show my rating").forEach { (section, content) ->
            compose.onNodeWithText(section).performScrollTo().performClick()
            compose.onNodeWithText(content).assertExists()
            compose.onNodeWithContentDescription("Back to profile").performScrollTo().performClick()
        }
    }
}
