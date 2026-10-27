package com.example.casosemestral.model.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.casosemestral.model.data.cliente.ClienteDao
import com.example.casosemestral.model.data.cliente.ClienteEntity
import com.example.casosemestral.model.data.proveedor.ProveedorDao
import com.example.casosemestral.model.data.proveedor.ProveedorEntity
import com.example.casosemestral.model.data.servicio.ServicioDao
import com.example.casosemestral.model.data.servicio.ServicioEntity
import com.example.casosemestral.model.data.servicio_proveedor.ServicioProveedorDao
import com.example.casosemestral.model.data.servicio_proveedor.ServicioProveedorEntity
import com.example.casosemestral.model.data.user.UserDao
import com.example.casosemestral.model.data.user.UserEntity
import com.example.casosemestral.model.data.venta.VentaDao
import com.example.casosemestral.model.data.venta.VentaEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// BASE DE DATOS DE LA APLICACION
@Database(entities = [
    VentaEntity::class,
    UserEntity::class,
    ClienteEntity::class,
    ServicioEntity::class,
    ProveedorEntity::class,
    ServicioProveedorEntity::class
                     ], version = 5)
abstract class AppDatabase : RoomDatabase() {

    abstract fun ventaDao(): VentaDao
    abstract fun userDao(): UserDao
    abstract fun clienteDao(): ClienteDao
    abstract fun servicioDao(): ServicioDao
    abstract fun proveedorDao(): ProveedorDao
    abstract fun servicioProveedorDao(): ServicioProveedorDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
        }

        // CALLBACK PARA INICIALIZAR DATA EN LA APLICACION CUANDO SE CREE POR PRIMERA VEZ
        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                val fechaActual = UserEntity.obtenerFechaActual()

                // PRECARGA DE USUARIOS
                db.execSQL("""
                    INSERT INTO user (nombre, clave, perfil, zonaSucursal, estadoCuenta, fechaCreacion, fechaActualizacion) 
                    VALUES ('Marcos', '12345', 'Vendedor', 'Puente Alto', 1, '$fechaActual', '$fechaActual')
                """)

                db.execSQL("""
                    INSERT INTO user (nombre, clave, perfil, zonaSucursal, estadoCuenta, fechaCreacion, fechaActualizacion) 
                    VALUES ('Boris', '54321', 'Vendedor', 'Puente Alto', 1, '$fechaActual', '$fechaActual')
                """)

                db.execSQL("""
                    INSERT INTO user (nombre, clave, perfil, zonaSucursal, estadoCuenta, fechaCreacion, fechaActualizacion) 
                    VALUES ('Marcos', '12345', 'Gestor', 'Puente Alto', 1, '$fechaActual', '$fechaActual')
                """)

                db.execSQL("""
                    INSERT INTO user (nombre, clave, perfil, zonaSucursal, estadoCuenta, fechaCreacion, fechaActualizacion) 
                    VALUES ('Boris', '54321', 'Gestor', 'Puente Alto', 0, '$fechaActual', '$fechaActual')
                """)

                // PRECARGA DE CLIENTES
                db.execSQL("""
                    INSERT INTO cliente (nombre, identificadorTributario, correo, telefono, nombreMascosta, consentimiento) 
                    VALUES ('Ana Pérez', '12.345.678-9', 'ana.perez@correo.cl', '+56911111111', 'Luna', 1)
                """)

                db.execSQL("""
                    INSERT INTO cliente (nombre, identificadorTributario, correo, telefono, nombreMascosta, consentimiento) 
                    VALUES ('Carlos Soto', '15.987.654-K', 'carlos.soto@correo.cl', '+56922222222', 'Firulais', 1)
                """)

                db.execSQL("""
                    INSERT INTO cliente (nombre, identificadorTributario, correo, telefono, nombreMascosta, consentimiento) 
                    VALUES ('María Rojas', '18.111.222-3', 'maria.rojas@correo.cl', '+56933333333', 'Kira', 1)
                """)

                db.execSQL("""
                    INSERT INTO cliente (nombre, identificadorTributario, correo, telefono, nombreMascosta, consentimiento) 
                    VALUES ('José Morales', '14.555.666-7', 'jose.morales@correo.cl', '+56944444444', NULL, 1)
                """)

                db.execSQL("""
                    INSERT INTO cliente (nombre, identificadorTributario, correo, telefono, nombreMascosta, consentimiento) 
                    VALUES ('Sofía Valdés', '19.888.999-K', 'sofia.valdes@correo.cl', '+56955555555', 'Simba', 1)
                """)

                // PRECARGA DE SERVICIOS
                val servicios = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")
                servicios.forEach { letra ->
                    db.execSQL("""
                        INSERT INTO servicio (nombre, detalle) 
                        VALUES ('Servicio $letra', 'Detalle descriptivo para el servicio $letra')
                    """)
                }

                // PRECARGA DE PROVEEDORES
                for (i in 1..6) {
                    db.execSQL("""
                        INSERT INTO proveedor (nombre, detalle) 
                        VALUES ('Proveedor $i', 'Información y contacto del proveedor $i')
                    """)
                }

                // PRECARGA DE RELACIONES
                val relaciones = listOf(
                    Pair(1, 1), // Servicio A -> Proveedor 1
                    Pair(1, 2), // Servicio A -> Proveedor 2
                    Pair(2, 2), // Servicio B -> Proveedor 2
                    Pair(2, 3), // Servicio B -> Proveedor 3
                    Pair(3, 1), // Servicio C -> Proveedor 1
                    Pair(3, 4), // Servicio C -> Proveedor 4
                    Pair(4, 5), // Servicio D -> Proveedor 5
                    Pair(5, 3), // Servicio E -> Proveedor 3
                    Pair(5, 6), // Servicio E -> Proveedor 6
                    Pair(6, 1), // Servicio F -> Proveedor 1
                    Pair(6, 5), // Servicio F -> Proveedor 5
                    Pair(6, 6), // Servicio F -> Proveedor 6
                    Pair(7, 2), // Servicio G -> Proveedor 2
                    Pair(7, 4), // Servicio G -> Proveedor 4
                    Pair(8, 3), // Servicio H -> Proveedor 3
                    Pair(9, 1), // Servicio I -> Proveedor 1
                    Pair(9, 6), // Servicio I -> Proveedor 6
                    Pair(10, 2),// Servicio J -> Proveedor 2
                    Pair(10, 5) // Servicio J -> Proveedor 5
                )

                relaciones.forEach { (servId, provId) ->
                    db.execSQL("""
                        INSERT INTO servicio_proveedor (servicioId, proveedorId) 
                        VALUES ($servId, $provId)
                    """)
                }

            }
        }
    }
}