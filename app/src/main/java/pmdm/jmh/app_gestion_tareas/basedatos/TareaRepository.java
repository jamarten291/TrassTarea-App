package pmdm.jmh.app_gestion_tareas.basedatos;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import pmdm.jmh.app_gestion_tareas.daos.TareaDAO;
import pmdm.jmh.app_gestion_tareas.entidades.Tarea;

public class TareaRepository {
    private final TareaDAO tareaDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public TareaRepository(Application application) {
        DatabaseApp db = DatabaseApp.getInstance(application);
        tareaDao = db.tareaDAO();
    }

    public LiveData<List<Tarea>> getTareasFiltradasYOrdenadas(String criterioOrdenamiento, boolean asc) {
        return tareaDao.getTareasFiltradasYOrdenadas(criterioOrdenamiento, asc ? 1 : 0);
    }

    // CRUD
    public boolean insertarTarea(Tarea t) {
        try {
            executor.execute(() -> tareaDao.insertAll(t));
            return true;
        } catch (Exception e) {
            return false;
        }
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
