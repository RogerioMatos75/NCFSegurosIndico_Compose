package com.ncf.seguros.indico.v2.ui.profile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class ProfileScreenTest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun profileScreen_showsUserData() {
        val testUserData =
                UserData(
                        name = "John Doe",
                        email = "john@example.com",
                        phone = "123456789",
                        totalReferrals = 10,
                        approvedReferrals = 5
                )

        composeTestRule.setContent { UserProfileContent(testUserData) }

        composeTestRule.onNodeWithText("John Doe").assertExists()
        composeTestRule.onNodeWithText("john@example.com").assertExists()
        composeTestRule.onNodeWithText("123456789").assertExists()
        composeTestRule.onNodeWithText("10").assertExists()
        composeTestRule.onNodeWithText("5").assertExists()
    }
}
