package pmdm.jmh.app_gestion_tareas.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import pmdm.jmh.app_gestion_tareas.database.DatabaseApp;
import pmdm.jmh.app_gestion_tareas.database.dao.TareaDAO;
import pmdm.jmh.app_gestion_tareas.database.entity.Tarea;

public class TareaRepository {
    private final TareaDAO tareaDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public TareaRepository(Application application) {
        DatabaseApp db = DatabaseApp.getInstance(application);
        tareaDao = db.tareaDAO();
    }

    // Este method construye una consulta personalizada según sus criterios
    public LiveData<List<Tarea>> getTareas(String criterioOrdenamiento, boolean asc) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM tarea");

        // ORDER BY
        String columna;
        switch (criterioOrdenamiento) {
            case "2": columna = "fechaCreacion"; break;
            case "3": columna = "fechaObjetivo"; break;
            case "4": columna = "progreso"; break;
            case "1":
            default: columna = "titulo"; break;
        }
        sql.append(" ORDER BY ").append(columna).append(asc ? " ASC" : " DESC");

        SimpleSQLiteQuery query = new SimpleSQLiteQuery(sql.toString());
        return tareaDao.getTareasConQuery(query);
    }

    // CRUD implementado en el repositorio
    public boolean insertarTarea(Tarea t) {
        try {
            // Lanzo un runnable que inserta una tarea con executor
            executor.execute(() -> tareaDao.insertAll(t));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public long insertarTareaYDevolverId(Tarea t) {
        // Callable permite que un Runnable devuelva un valor
        Callable<Long> insertCallable = () -> tareaDao.insertTareaAndReturnId(t);
        long rowId = 0;

        try {
            // Se devuelve el valor, en caso de que no se pueda, saltará una excepción
            rowId = insertCallable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return rowId;
    }

    public LiveData<List<Tarea>> getAll() {
        return tareaDao.getAll();
    }

    public boolean actualizarTarea(Tarea t) {
        try {
            executor.execute(() -> tareaDao.update(t));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean borrarTarea(Tarea t) {
        try {
            executor.execute(() -> tareaDao.delete(t));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
