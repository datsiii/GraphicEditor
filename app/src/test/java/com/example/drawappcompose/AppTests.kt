package com.example.drawappcompose

import android.content.Context
import android.widget.Toast
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.drawappcompose.home.HomeViewModel
import com.example.drawappcompose.login.LoginViewModel
import com.example.drawappcompose.repository.AuthRepository
import com.example.drawappcompose.repository.Resources
import com.example.drawappcompose.repository.StorageRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AppTests {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var authRepository: AuthRepository
    private lateinit var storageRepository: StorageRepository
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var context: Context

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        authRepository = mockk(relaxed = true)
        storageRepository = mockk(relaxed = true)
        context = mockk(relaxed = true)
        loginViewModel = LoginViewModel(authRepository)
        homeViewModel = HomeViewModel(storageRepository)
        mockkStatic(Toast::class)
    }
    @After
    fun tearDown() {
        unmockkAll()
    }
    @Test
    fun `onUserNameChange updates userName in loginUiState`() {
        val newName = "testUser"
        loginViewModel.onUserNameChange(newName)
        assertEquals(newName, loginViewModel.loginUiState.userName)
    }

    @Test
    fun `onPasswordChange updates password in loginUiState`() {
        val newPassword = "testPassword"
        loginViewModel.onPasswordChange(newPassword)
        assertEquals(newPassword, loginViewModel.loginUiState.password)
    }
    @Test
    fun `signOut calls repository's signOut`() {
        homeViewModel.signOut()
        verify { storageRepository.signOut() }
    }
    @Test
    fun `deleteDraw updates drawDeletedStatus`() {
        val drawId = "drawId"
        every { storageRepository.deleteDraw(drawId, any()) } answers {
            secondArg<(Boolean) -> Unit>().invoke(true)
        }
        homeViewModel.deleteDraw(drawId)
        assertTrue(homeViewModel.homeUiState.drawDeletedStatus)
    }
    @Test
    fun `loadDraws when user is not logged in`() {
        every { storageRepository.hasUser() } returns false
       homeViewModel.loadDraws()
        assertTrue(homeViewModel.homeUiState.drawList is Resources.Error)
    }
    @Test
    fun `loadDraws when user is logged in`() {
        every { storageRepository.hasUser() } returns true
        every { storageRepository.getUserId() } returns "userId"
        coEvery { storageRepository.getUserDraws(any()) } returns flowOf(Resources.Success(emptyList()))
        homeViewModel.loadDraws()
        coVerify { storageRepository.getUserDraws("userId") }
        assertTrue(homeViewModel.homeUiState.drawList is Resources.Success)
    }

}
