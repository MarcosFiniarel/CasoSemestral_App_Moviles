package com.example.casosemestral.model.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.casosemestral.model.data.user.UserDao
import com.example.casosemestral.model.data.user.UserEntity
import com.example.casosemestral.model.data.venta.VentaDao
import com.example.casosemestral.model.data.venta.VentaEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// BASE DE DATOS DE LA APLICACION
@Database(entities = [VentaEntity::class, UserEntity::class], version = 3)
abstract class AppDatabase : RoomDatabase() {

    abstract fun ventaDao(): VentaDao
    abstract fun userDao(): UserDao

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
            }
        }
    }
}