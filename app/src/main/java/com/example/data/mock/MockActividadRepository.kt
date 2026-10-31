package com.example.data.mock

import com.example.domain.model.Actividad
import com.example.domain.model.TipoActividad
import com.example.domain.repository.ActividadRepository
import kotlinx.coroutines.flow.StateFlow

class MockActividadRepository : ActividadRepository {
    override val actividades: StateFlow<List<Actividad>> = MockDataStore.actividades
    override fun logActividad(tipo: TipoActividad, titulo: String, descripcion: String) = MockDataStore.logActividad(tipo, titulo, descripcion)
}
