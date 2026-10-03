package com.carpool.app

import com.carpool.app.feature.auth.MockAuthViewModel
import org.junit.Assert.*
import org.junit.Test

class MockAuthViewModelTest {
    @Test fun adminLoginRejectsWrongPasswordAndSupportsLogout() {
        val auth = MockAuthViewModel()
        assertNotNull(auth.login("admin", "wrong"))
        assertNull(auth.displayName)
        assertNull(auth.login("admin", "admin"))
        assertEquals("Admin", auth.displayName)
        auth.logout()
        assertNull(auth.displayName)
    }

    @Test fun registrationValidatesAndCreatesReusableAccount() {
        val auth = MockAuthViewModel()
        assertNotNull(auth.register("", "rider", "pass", "pass"))
        assertNotNull(auth.register("Rider", "admin", "pass", "pass"))
        assertNotNull(auth.register("Rider", "ride r", "pass", "pass"))
        assertNotNull(auth.register("Rider", "rider", "abc", "abc"))
        assertNotNull(auth.register("Rider", "rider", "pass", "oops"))
        assertNull(auth.register("Rider", "rider", "pass", "pass"))
        assertEquals("Rider", auth.displayName)
        auth.logout()
        assertNull(auth.login("rider", "pass"))
        assertEquals("Rider", auth.displayName)
    }
}
