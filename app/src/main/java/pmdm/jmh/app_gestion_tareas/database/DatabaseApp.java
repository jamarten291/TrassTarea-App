package pmdm.jmh.app_gestion_tareas.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import pmdm.jmh.app_gestion_tareas.database.dao.TareaDAO;
import pmdm.jmh.app_gestion_tareas.database.entity.Tarea;

@Database(entities = {Tarea.class}, version = 1, exportSchema = false)
public abstract class DatabaseApp extends RoomDatabase {
    //Usando el patrón SINGLETON, nos aseguramos que solo haya una instancia de la
    //base de datos creada en nuestra aplicación.
    private static DatabaseApp INSTANCIA;

    public static DatabaseApp getInstance(Context context) {
        if (INSTANCIA == null) {
            INSTANCIA = Room.databaseBuilder(
                            context.getApplicationContext(),
                            DatabaseApp.class,
                            "dbCompra")
                    .build();
        }
        return INSTANCIA;
    }

    public static void destroyInstance() {
        INSTANCIA = null;
    }

    public abstract TareaDAO tareaDAO();
}
