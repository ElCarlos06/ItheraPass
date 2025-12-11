package mx.edu.utez.itheraqr.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import mx.edu.utez.itheraqr.data.local.dao.FilaDao
import mx.edu.utez.itheraqr.data.local.model.Fila

//QRIthera2025
@Database(entities = [Fila::class],
    version = 1,
    exportSchema = true)
abstract class AppDataBase: RoomDatabase() {

    abstract fun filaDao(): FilaDao
    companion object{
        @Volatile
        private var INSTANCE: AppDataBase? = null
        fun getDatabase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "filas_db"
                ).build()
                INSTANCE = instance
                instance
            }

        }
    }

}