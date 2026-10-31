package com.example.core.utils

object Validators {
    fun isValidName(name: String): Boolean {
        return name.trim().length >= 3
    }

    fun isValidPhone(phone: String): Boolean {
        if (phone.isBlank()) return true
        val digits = phone.filter { it.isDigit() }
        return digits.length >= 7
    }

    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return true
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return emailRegex.matches(email.trim())
    }

    fun isValidPrice(priceStr: String): Boolean {
        val price = priceStr.toDoubleOrNull() ?: return false
        return price >= 0.0
    }

    fun isValidServiceTitle(title: String): Boolean {
        return title.trim().length >= 3
    }
}
