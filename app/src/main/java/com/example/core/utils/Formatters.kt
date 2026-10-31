package com.example.core.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Formatters {
    private val currencyFormatter: NumberFormat by lazy {
        val format = NumberFormat.getNumberInstance(Locale("es", "CO"))
        format.maximumFractionDigits = 0
        format.minimumFractionDigits = 0
        format
    }

    fun formatCurrency(amount: Double, symbol: String = "$"): String {
        return "$symbol ${currencyFormatter.format(amount)}"
    }

    fun parsePrice(priceStr: String): Double? {
        val digits = priceStr.filter { it.isDigit() }
        return digits.toDoubleOrNull()
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("es", "CO"))
        return sdf.format(Date(timestamp))
    }

    fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale("es", "CO"))
        return sdf.format(Date(timestamp))
    }
}
