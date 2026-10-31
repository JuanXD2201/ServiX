package com.example.domain.repository

import com.example.domain.model.Actividad
import com.example.domain.model.TipoActividad
import kotlinx.coroutines.flow.StateFlow

interface ActividadRepository {
    val actividades: StateFlow<List<Actividad>>
    fun logActividad(tipo: TipoActividad, titulo: String, descripcion: String)
}
