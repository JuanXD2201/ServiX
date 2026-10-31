package com.example

import com.example.data.mock.MockAuthRepository
import com.example.domain.model.AuthUser
import com.example.domain.repository.AuthResult
import com.example.domain.usecase.SignInUseCase
import com.example.domain.usecase.SignUpUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ServixAuthUnitTests {

    private lateinit var authRepository: MockAuthRepository
    private lateinit var signInUseCase: SignInUseCase
    private lateinit var signUpUseCase: SignUpUseCase

    @Before
    fun setUp() {
        authRepository = MockAuthRepository()
        signInUseCase = SignInUseCase(authRepository)
        signUpUseCase = SignUpUseCase(authRepository)
    }

    @Test
    fun testInitialUserAuthenticatedInMock() {
        assertNotNull(authRepository.currentUser.value)
        assertTrue(authRepository.isUserLoggedIn)
    }

    @Test
    fun testSignInSuccess() = runBlocking {
        val result = signInUseCase("tecnico@servixapp.com", "123456")
        assertTrue(result is AuthResult.Success)
        val user = (result as AuthResult.Success).user
        assertEquals("tecnico@servixapp.com", user.email)
    }

    @Test
    fun testSignInValidationFailsOnShortPassword() = runBlocking {
        val result = signInUseCase("tecnico@servixapp.com", "123")
        assertTrue(result is AuthResult.Error)
        assertEquals("La contraseña debe tener al menos 6 caracteres", (result as AuthResult.Error).message)
    }

    @Test
    fun testSignUpSuccess() = runBlocking {
        val result = signUpUseCase("Andres Montoya", "andres@servix.com", "password123", "+573110001122")
        assertTrue(result is AuthResult.Success)
        val user = (result as AuthResult.Success).user
        assertEquals("Andres Montoya", user.displayName)
        assertEquals("andres@servix.com", user.email)
    }

    @Test
    fun testSignOut() = runBlocking {
        authRepository.signOut()
        assertNull(authRepository.currentUser.value)
        assertFalse(authRepository.isUserLoggedIn)
    }
}
