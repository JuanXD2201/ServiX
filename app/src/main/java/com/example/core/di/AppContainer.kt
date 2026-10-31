package com.example.core.di

import android.content.Context
import android.util.Log
import com.example.data.ai.GeminiAiChatRepository
import com.example.data.firebase.FirebaseActividadRepository
import com.example.data.firebase.FirebaseAuthRepository
import com.example.data.firebase.FirebaseClienteRepository
import com.example.data.firebase.FirebaseConfigRepository
import com.example.data.firebase.FirebaseNotificationRepository
import com.example.data.firebase.FirebaseServicioRepository
import com.example.data.network.OkHttpMakeWebhookRepository
import com.example.domain.repository.ActividadRepository
import com.example.domain.repository.AiChatRepository
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.ClienteRepository
import com.example.domain.repository.ConfigRepository
import com.example.domain.repository.MakeWebhookRepository
import com.example.domain.repository.NotificationRepository
import com.example.domain.repository.ServicioRepository
import com.example.domain.usecase.*
import com.google.firebase.FirebaseApp

/**
 * Service Locator / Composition Root para la inyección de dependencias de ServiX.
 * 
 * Centraliza la instanciación de repositorios y UseCases.
 * En la Fase 3, los repositorios persistentes se basan en Firebase / Cloud Firestore / Firebase Auth.
 * Proporciona fallback seguro hacia Mock repositories si Firebase no está disponible.
 */
object AppContainer {

    private var isInitialized = false
    private var customAuthRepository: AuthRepository? = null
    private var customClienteRepository: ClienteRepository? = null
    private var customServicioRepository: ServicioRepository? = null
    private var customNotificationRepository: NotificationRepository? = null
    private var customConfigRepository: ConfigRepository? = null
    private var customActividadRepository: ActividadRepository? = null

    fun init(context: Context) {
        if (isInitialized) return
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
        } catch (e: Exception) {
            Log.w("AppContainer", "FirebaseApp init caught exception: ${e.message}. Using mock fallback.", e)
            useMockRepositories()
        }
        isInitialized = true
    }

    // Repositorios singleton respaldados por Firebase Firestore y Auth por defecto
    val authRepository: AuthRepository
        get() = customAuthRepository ?: defaultFirebaseAuthRepository

    val clienteRepository: ClienteRepository
        get() = customClienteRepository ?: defaultFirebaseClienteRepository

    val servicioRepository: ServicioRepository
        get() = customServicioRepository ?: defaultFirebaseServicioRepository

    val notificationRepository: NotificationRepository
        get() = customNotificationRepository ?: defaultFirebaseNotificationRepository

    val configRepository: ConfigRepository
        get() = customConfigRepository ?: defaultFirebaseConfigRepository

    val actividadRepository: ActividadRepository
        get() = customActividadRepository ?: defaultFirebaseActividadRepository

    private var customAtencionRepository: com.example.domain.repository.AtencionRepository? = null

    val atencionRepository: com.example.domain.repository.AtencionRepository
        get() = customAtencionRepository ?: defaultFirebaseAtencionRepository

    private val defaultFirebaseAuthRepository: AuthRepository by lazy {
        try {
            FirebaseAuthRepository()
        } catch (e: Throwable) {
            Log.e("AppContainer", "Failed to instantiate FirebaseAuthRepository: ${e.message}. Using mock.", e)
            com.example.data.mock.MockAuthRepository()
        }
    }

    private val defaultFirebaseClienteRepository: ClienteRepository by lazy {
        try {
            FirebaseClienteRepository()
        } catch (e: Throwable) {
            Log.e("AppContainer", "Failed to instantiate FirebaseClienteRepository: ${e.message}. Using mock.", e)
            com.example.data.mock.MockClienteRepository()
        }
    }

    private val defaultFirebaseServicioRepository: ServicioRepository by lazy {
        try {
            FirebaseServicioRepository()
        } catch (e: Throwable) {
            Log.e("AppContainer", "Failed to instantiate FirebaseServicioRepository: ${e.message}. Using mock.", e)
            com.example.data.mock.MockServicioRepository()
        }
    }

    private val defaultFirebaseNotificationRepository: NotificationRepository by lazy {
        try {
            FirebaseNotificationRepository()
        } catch (e: Throwable) {
            Log.e("AppContainer", "Failed to instantiate FirebaseNotificationRepository: ${e.message}. Using mock.", e)
            com.example.data.mock.MockNotificationRepository()
        }
    }

    private val defaultFirebaseConfigRepository: ConfigRepository by lazy {
        try {
            FirebaseConfigRepository()
        } catch (e: Throwable) {
            Log.e("AppContainer", "Failed to instantiate FirebaseConfigRepository: ${e.message}. Using mock.", e)
            com.example.data.mock.MockConfigRepository()
        }
    }

    private val defaultFirebaseActividadRepository: ActividadRepository by lazy {
        try {
            FirebaseActividadRepository()
        } catch (e: Throwable) {
            Log.e("AppContainer", "Failed to instantiate FirebaseActividadRepository: ${e.message}. Using mock.", e)
            com.example.data.mock.MockActividadRepository()
        }
    }

    private val defaultFirebaseAtencionRepository: com.example.domain.repository.AtencionRepository by lazy {
        try {
            com.example.data.firebase.FirebaseAtencionRepository()
        } catch (e: Throwable) {
            Log.e("AppContainer", "Failed to instantiate FirebaseAtencionRepository: ${e.message}. Using mock.", e)
            com.example.data.mock.MockAtencionRepository()
        }
    }

    fun useMockRepositories() {
        customAuthRepository = com.example.data.mock.MockAuthRepository()
        customClienteRepository = com.example.data.mock.MockClienteRepository()
        customServicioRepository = com.example.data.mock.MockServicioRepository()
        customNotificationRepository = com.example.data.mock.MockNotificationRepository()
        customConfigRepository = com.example.data.mock.MockConfigRepository()
        customActividadRepository = com.example.data.mock.MockActividadRepository()
        customAtencionRepository = com.example.data.mock.MockAtencionRepository()
    }

    fun useFirebaseRepositories() {
        customAuthRepository = null
        customClienteRepository = null
        customServicioRepository = null
        customNotificationRepository = null
        customConfigRepository = null
        customActividadRepository = null
        customAtencionRepository = null
    }

    // UseCases de Autenticación
    val signInUseCase: SignInUseCase
        get() = SignInUseCase(authRepository)

    val signUpUseCase: SignUpUseCase
        get() = SignUpUseCase(authRepository)

    val signOutUseCase: SignOutUseCase
        get() = SignOutUseCase(authRepository)

    val sendPasswordResetUseCase: SendPasswordResetUseCase
        get() = SendPasswordResetUseCase(authRepository)

    val signInAnonymouslyUseCase: SignInAnonymouslyUseCase
        get() = SignInAnonymouslyUseCase(authRepository)

    // UseCases de Clientes
    val addClienteUseCase: AddClienteUseCase
        get() = AddClienteUseCase(clienteRepository, actividadRepository, notificationRepository)

    val updateClienteUseCase: UpdateClienteUseCase
        get() = UpdateClienteUseCase(clienteRepository, actividadRepository)

    val deleteClienteUseCase: DeleteClienteUseCase
        get() = DeleteClienteUseCase(clienteRepository, actividadRepository)

    val restaurarClienteUseCase: RestaurarClienteUseCase
        get() = RestaurarClienteUseCase(clienteRepository, actividadRepository)

    val toggleClienteFavoritoUseCase: ToggleClienteFavoritoUseCase
        get() = ToggleClienteFavoritoUseCase(clienteRepository)

    val makeWebhookRepository: MakeWebhookRepository by lazy {
        OkHttpMakeWebhookRepository()
    }

    val aiChatRepository: AiChatRepository by lazy {
        GeminiAiChatRepository()
    }

    val crearSolicitudAtencionUseCase: CrearSolicitudAtencionUseCase
        get() = CrearSolicitudAtencionUseCase(atencionRepository, notificationRepository, actividadRepository)

    val tomarSolicitudAtencionUseCase: TomarSolicitudAtencionUseCase
        get() = TomarSolicitudAtencionUseCase(atencionRepository, actividadRepository)

    val responderSolicitudAtencionUseCase: ResponderSolicitudAtencionUseCase
        get() = ResponderSolicitudAtencionUseCase(atencionRepository, actividadRepository)

    val resolverSolicitudAtencionUseCase: ResolverSolicitudAtencionUseCase
        get() = ResolverSolicitudAtencionUseCase(atencionRepository, actividadRepository)

    val sendAiChatMessageUseCase: SendAiChatMessageUseCase
        get() = SendAiChatMessageUseCase(aiChatRepository, crearSolicitudAtencionUseCase)

    // UseCases de Servicios
    val addServicioUseCase: AddServicioUseCase
        get() = AddServicioUseCase(
            servicioRepository,
            clienteRepository,
            actividadRepository,
            notificationRepository,
            makeWebhookRepository,
            configRepository,
            authRepository
        )

    val updateServicioUseCase: UpdateServicioUseCase
        get() = UpdateServicioUseCase(
            servicioRepository,
            actividadRepository,
            clienteRepository,
            configRepository,
            authRepository,
            makeWebhookRepository,
            notificationRepository
        )

    val deleteServicioUseCase: DeleteServicioUseCase
        get() = DeleteServicioUseCase(servicioRepository, actividadRepository)

    val restaurarServicioUseCase: RestaurarServicioUseCase
        get() = RestaurarServicioUseCase(servicioRepository, actividadRepository)

    val updateServicioEstadoUseCase: UpdateServicioEstadoUseCase
        get() = UpdateServicioEstadoUseCase(
            servicioRepository,
            actividadRepository,
            notificationRepository,
            clienteRepository,
            configRepository,
            authRepository,
            makeWebhookRepository
        )

    val duplicarServicioUseCase: DuplicarServicioUseCase
        get() = DuplicarServicioUseCase(servicioRepository, clienteRepository, actividadRepository)

    val toggleServicioFavoritoUseCase: ToggleServicioFavoritoUseCase
        get() = ToggleServicioFavoritoUseCase(servicioRepository)

    // UseCases de Notificaciones
    val marcarNotificacionLeidaUseCase: MarcarNotificacionLeidaUseCase
        get() = MarcarNotificacionLeidaUseCase(notificationRepository)

    val deleteNotificacionUseCase: DeleteNotificacionUseCase
        get() = DeleteNotificacionUseCase(notificationRepository)

    val marcarTodasNotificacionesLeidasUseCase: MarcarTodasNotificacionesLeidasUseCase
        get() = MarcarTodasNotificacionesLeidasUseCase(notificationRepository)

    // UseCases de Configuración y Perfil
    val updateObjetivosUseCase: UpdateObjetivosUseCase
        get() = UpdateObjetivosUseCase(configRepository)

    val marcarOnboardingVistoUseCase: MarcarOnboardingVistoUseCase
        get() = MarcarOnboardingVistoUseCase(configRepository)

    val updateProfileUseCase: UpdateProfileUseCase
        get() = UpdateProfileUseCase(configRepository)

    val updateConfigUseCase: UpdateConfigUseCase
        get() = UpdateConfigUseCase(configRepository)

    val resetDataUseCase: ResetDataUseCase
        get() = ResetDataUseCase(configRepository)
}
