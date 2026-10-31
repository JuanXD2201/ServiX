package com.example.data.mock

import com.example.domain.model.AppConfig
import com.example.domain.model.UserProfile
import com.example.domain.repository.ConfigRepository
import kotlinx.coroutines.flow.StateFlow

class MockConfigRepository : ConfigRepository {
    override val appConfig: StateFlow<AppConfig> = MockDataStore.appConfig
    override val userProfile: StateFlow<UserProfile> = MockDataStore.userProfile
    
    override fun updateObjetivos(servicios: Int, ingresos: Double) = MockDataStore.updateObjetivos(servicios, ingresos)
    override fun marcarOnboardingVisto(visto: Boolean) = MockDataStore.marcarOnboardingVisto(visto)
    override fun updateProfile(profile: UserProfile) = MockDataStore.updateProfile(profile)
    override fun updateConfig(config: AppConfig) = MockDataStore.updateConfig(config)
    override fun resetData() = MockDataStore.resetData()
}
