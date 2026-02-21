package pmdm.jmh.app_gestion_tareas.ui.tarea.list;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

import pmdm.jmh.app_gestion_tareas.database.repository.TareaRepository;
import pmdm.jmh.app_gestion_tareas.database.entity.Tarea;

public class ListadoTareasViewModel extends AndroidViewModel {
    private final TareaRepository repository;
    private final MutableLiveData<Criterios> criterios = new MutableLiveData<>();

    // Se expone un LiveData a la UI por seguridad, ya que MutableLiveData permite cambios en los
    // datos
    private LiveData<List<Tarea>> tareas;

    public ListadoTareasViewModel(@NonNull Application application) {
        super(application);
        // Inicializamos un repositorio de tareas
        repository = new TareaRepository(application);

        // Hace que el LiveData cambie si sus criterios de ordenación son cambiados
        tareas = Transformations.switchMap(criterios, c ->
                repository.getTareas(c.soloPrioritarias, c.criterio, c.asc)
        );
    }

    public LiveData<List<Tarea>> getTareas() {
        return tareas;
    }

    public void setParams(boolean soloPrioritarias, String criterio, boolean asc) {
        // Se cambian los criterios de ordenación por otros pasados por parámetro
        criterios.setValue(new Criterios(soloPrioritarias, criterio, asc));
    }

    // Esta clase representa los criterios de ordenacion que se tomarán al mostrar la lista de tareas
    private static class Criterios {
        boolean soloPrioritarias;
        final String criterio;
        final boolean asc;

        Criterios(boolean s, String c, boolean a) {
            soloPrioritarias = s;
            criterio = c;
            asc = a;
        }
    }
}
