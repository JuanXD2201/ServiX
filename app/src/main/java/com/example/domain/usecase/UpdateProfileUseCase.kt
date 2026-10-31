package com.example.domain.usecase

import com.example.domain.model.UserProfile
import com.example.domain.repository.ConfigRepository

class UpdateProfileUseCase(
    private val configRepository: ConfigRepository
) {
    operator fun invoke(profile: UserProfile) {
        configRepository.updateProfile(profile)
    }
}
