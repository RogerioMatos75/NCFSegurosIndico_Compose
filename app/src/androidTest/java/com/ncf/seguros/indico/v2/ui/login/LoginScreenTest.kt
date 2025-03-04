package com.ncf.seguros.indico.v2.ui.login

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_showsAllElements() {
        composeTestRule.setContent { LoginScreen(onNavigateToHome = {}) }

        composeTestRule.onNodeWithTag(TAG_EMAIL_FIELD).assertExists().assertIsDisplayed()

        composeTestRule.onNodeWithTag(TAG_PASSWORD_FIELD).assertExists().assertIsDisplayed()

        composeTestRule
                .onNodeWithTag(TAG_LOGIN_BUTTON)
                .assertExists()
                .assertIsDisplayed()
                .assertTextEquals("Entrar")
    }

    @Test
    fun loginScreen_showsErrorForInvalidEmail() {
        composeTestRule.setContent { LoginScreen(onNavigateToHome = {}) }

        composeTestRule.onNodeWithTag(TAG_EMAIL_FIELD).performTextInput("invalid-email")

        composeTestRule.onNodeWithTag(TAG_LOGIN_BUTTON).performClick()

        composeTestRule.onNodeWithText("Email inválido").assertExists()
    }
}
