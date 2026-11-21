package com.example.casosemestral.model.venta

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VentaDao {
    // FUNCION PARA INSERTAR VENTAS
    @Insert
    suspend fun insertar(venta: VentaEntity)

    // FUNCION PARA ACTUALIZAR VENTAS
    @Update
    suspend fun actualizar(venta: VentaEntity)

    // FUNCION PARA ACTUALIZAR VENTAS
    @Delete
    suspend fun borrar(venta: VentaEntity)

    // FUNCION PARA BUSCAR UNA VENTA POR ID
    @Query("SELECT * FROM venta WHERE id = :ventaId")
    suspend fun obtenerVentaPorId(ventaId: Long): VentaEntity?

    // FUNCION PARA TRAER LA LISTA DE VENTAS DE UN VENDEDOR DETERMINADO
    @Query("SELECT * FROM venta WHERE vendedor_responsable = :nombreVendedor ORDER BY id DESC")
    fun obtenerVentasPorVendedor(nombreVendedor: String): Flow<List<VentaEntity>>

    // FUNCION PARA TRAER LA LISTA DE TODAS LAS VENTAS QUE SE ENCUENTREN REGISTRADAS
    @Query("SELECT * FROM venta ORDER BY id DESC")
    fun obtenerVentas(): Flow<List<VentaEntity>>

    // FUNCION PARA TRAER LA LISTA DE TODAS LAS VENTAS QUE SE ENCUENTREN REGISTRADAS
    @Query("SELECT * FROM venta WHERE estado = 'Enviado' ORDER BY fecha_de_ultima_actualizacion DESC")
    fun obtenerVentasEnviadas(): Flow<List<VentaEntity>>

    // FUNCION PARA TRAER LA LISTA DE TODAS LAS VENTAS QUE SE ENCUENTREN REGISTRADAS
    @Query("SELECT * FROM venta WHERE estado IN ('Rechazada','Aprobada','Corregir') ORDER BY fecha_de_ultima_actualizacion DESC")
    fun obtenerVentasAuditadas(): Flow<List<VentaEntity>>

    // FUNCION PARA CONTAR CUANTAS VECES APARECE UN CODIGO DE AUTORIZACION
    @Query("SELECT COUNT(*) FROM venta WHERE codigo_autorizacion = :codigo")
    suspend fun contarCodigo(codigo: String): Int

    // Contador para la burbuja del Gestor (ventas enviadas aún no vistas por gestor)
    @Query("SELECT COUNT(*) FROM venta WHERE estado = 'Enviado' AND visto_gestor = 0")
    fun contarNuevasParaGestor(): Flow<Int>

    // Contador para la burbuja del Vendedor (ventas ya auditadas aún no vistas por vendedor)
    @Query(
        """
                SELECT COUNT(*) FROM venta 
                WHERE vendedor_responsable = :nombreVendedor 
                AND estado IN ('Aprobada', 'Rechazada', 'Corregir') 
                AND visto_vendedor = 0
                """
    )
    fun contarNovedadesParaVendedor(nombreVendedor: String): Flow<Int>

    // Marcar como vistas por el gestor todas las ventas enviadas
    @Query("UPDATE venta SET visto_gestor = 1 WHERE estado = 'Enviado' AND visto_gestor = 0")
    suspend fun marcarVistasPorGestor()

    // Marcar como vistas por el vendedor sus ventas auditadas
    @Query(
        """
                UPDATE venta SET visto_vendedor = 1 
                WHERE vendedor_responsable = :nombreVendedor 
                    AND estado IN ('Aprobada', 'Rechazada', 'Corregir') 
                    AND visto_vendedor = 0
                """
    )
    suspend fun marcarVistasPorVendedor(nombreVendedor: String)

    @Query(
        """
    SELECT COUNT(*) FROM venta 
    WHERE estado IN ('Aprobada', 'Rechazada', 'Corregir') 
      AND historico_visto_gestor = 0
    """
    )
    fun contarHistoricoNuevoParaGestor(): Flow<Int>

    @Query(
        """
    UPDATE venta SET historico_visto_gestor = 1 
    WHERE estado IN ('Aprobada', 'Rechazada', 'Corregir') 
      AND historico_visto_gestor = 0
    """
    )
    suspend fun marcarHistoricoVistoPorGestor()

    // Ventas aprobadas aún no contabilizadas en comisión para un vendedor
    @Query(
        """
    SELECT * FROM venta
    WHERE vendedor_responsable = :nombreVendedor
      AND estado = 'Aprobada'
      AND contadoEnComision = 0
    ORDER BY fecha_y_hora ASC, id ASC
    """
    )
    suspend fun obtenerVentasAprobadasNoComisionadas(nombreVendedor: String): List<VentaEntity>

    // Marcar un conjunto de IDs de ventas como ya contabilizadas en comisión
    @Query("UPDATE venta SET contadoEnComision = 1 WHERE id IN (:ventaIds)")
    suspend fun marcarVentasComoComisionadas(ventaIds: List<Long>)

    @Query("SELECT DISTINCT vendedor_responsable FROM venta WHERE vendedor_responsable != '' ORDER BY vendedor_responsable ASC")
    fun obtenerVendedoresRegistrados(): Flow<List<String>>
}