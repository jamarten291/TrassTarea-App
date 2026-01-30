package pmdm.jmh.app_gestion_tareas.ui.crud.listado;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import pmdm.jmh.app_gestion_tareas.basedatos.DatabaseApp;
import pmdm.jmh.app_gestion_tareas.basedatos.TareaRepository;
import pmdm.jmh.app_gestion_tareas.entidades.Tarea;

public class ListadoTareasViewModel extends AndroidViewModel {
    private final TareaRepository repository;
    private final LiveData<List<Tarea>> tareas;

    public ListadoTareasViewModel(@NonNull Application application) {
        super(application);
        //Inicializamos el contenido de la lista, al de la tabla de la base de datos
        repository = new TareaRepository(application);
        tareas = repository.getAll();
    }

    public LiveData<List<Tarea>> getTareas() {
        return tareas;
    }

    public LiveData<List<Tarea>> getTareasOrdenadas(String criterio, boolean asc) {
        return repository.getTareasFiltradasYOrdenadas(criterio, asc);
    }
}
