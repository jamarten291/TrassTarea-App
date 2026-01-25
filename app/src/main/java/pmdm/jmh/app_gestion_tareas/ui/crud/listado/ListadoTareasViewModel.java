package pmdm.jmh.app_gestion_tareas.ui.crud.listado;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import pmdm.jmh.app_gestion_tareas.basedatos.DatabaseApp;
import pmdm.jmh.app_gestion_tareas.entidades.Tarea;

public class ListadoTareasViewModel extends AndroidViewModel {
    private final LiveData<List<Tarea>> tareas;

    public ListadoTareasViewModel(@NonNull Application application) {
        super(application);
        //Inicializamos el contenido de la lista, al de la tabla de la base de datos
        tareas = DatabaseApp
                .getInstance(application)
                .tareaDAO().getAll();
    }

    public LiveData<List<Tarea>> getTareas() {
        return tareas;
    }
}
