package com.example.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.core.di.AppContainer

class MainViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(
                AppContainer.authRepository,
                AppContainer.clienteRepository,
                AppContainer.servicioRepository,
                AppContainer.notificationRepository,
                AppContainer.configRepository,
                AppContainer.actividadRepository,
                AppContainer.signInUseCase,
                AppContainer.signUpUseCase,
                AppContainer.signOutUseCase,
                AppContainer.sendPasswordResetUseCase,
                AppContainer.signInAnonymouslyUseCase,
                AppContainer.addClienteUseCase,
                AppContainer.updateClienteUseCase,
                AppContainer.deleteClienteUseCase,
                AppContainer.restaurarClienteUseCase,
                AppContainer.toggleClienteFavoritoUseCase,
                AppContainer.addServicioUseCase,
                AppContainer.updateServicioUseCase,
                AppContainer.deleteServicioUseCase,
                AppContainer.restaurarServicioUseCase,
                AppContainer.updateServicioEstadoUseCase,
                AppContainer.duplicarServicioUseCase,
                AppContainer.toggleServicioFavoritoUseCase,
                AppContainer.marcarNotificacionLeidaUseCase,
                AppContainer.deleteNotificacionUseCase,
                AppContainer.marcarTodasNotificacionesLeidasUseCase,
                AppContainer.updateObjetivosUseCase,
                AppContainer.marcarOnboardingVistoUseCase,
                AppContainer.updateProfileUseCase,
                AppContainer.updateConfigUseCase,
                AppContainer.resetDataUseCase,
                AppContainer.sendAiChatMessageUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
