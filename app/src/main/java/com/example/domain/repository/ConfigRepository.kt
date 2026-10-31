package com.example.domain.repository

import com.example.domain.model.AppConfig
import com.example.domain.model.UserProfile
import kotlinx.coroutines.flow.StateFlow

interface ConfigRepository {
    val appConfig: StateFlow<AppConfig>
    val userProfile: StateFlow<UserProfile>

    fun updateObjetivos(servicios: Int, ingresos: Double)
    fun marcarOnboardingVisto(visto: Boolean)
    fun updateProfile(profile: UserProfile)
    fun updateConfig(config: AppConfig)
    fun resetData()
}
