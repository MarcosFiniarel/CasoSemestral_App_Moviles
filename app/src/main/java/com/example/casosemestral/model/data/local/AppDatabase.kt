package com.example.casosemestral.model.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.casosemestral.model.data.cliente.ClienteDao
import com.example.casosemestral.model.data.cliente.ClienteEntity
import com.example.casosemestral.model.data.user.UserDao
import com.example.casosemestral.model.data.user.UserEntity
import com.example.casosemestral.model.data.venta.VentaDao
import com.example.casosemestral.model.data.venta.VentaEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// BASE DE DATOS DE LA APLICACION
@Database(entities = [VentaEntity::class, UserEntity::class, ClienteEntity::class], version = 4)
abstract class AppDatabase : RoomDatabase() {

    abstract fun ventaDao(): VentaDao
    abstract fun userDao(): UserDao

    abstract fun clienteDao(): ClienteDao

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
            }
        }
    }
}