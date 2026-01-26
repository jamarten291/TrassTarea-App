package pmdm.jmh.app_gestion_tareas.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import pmdm.jmh.app_gestion_tareas.entidades.Tarea;

@Dao
public interface TareaDAO {

    // Listar todas las tareas
    @Query("SELECT * FROM tarea")
    LiveData<List<Tarea>> getAll();

    //Anotación que permite realizar una consulta para las tareas con unos ids determinados
    @Query("SELECT * FROM tarea WHERE _id IN (:tareaIds)")
    //Método que realiza la consulta anterior
    List<Tarea> loadAllByIds(int[] tareaIds);

    //Anotación que permite realizar una consulta para un producto para un nombre determinado
    @Query("SELECT * FROM tarea WHERE titulo LIKE :tarea LIMIT 1")
    //Método que realiza la consulta anterior
    Tarea findByTarea(String tarea);

    //Anotación que permite realizar la inserción de una relación de productos
    @Insert
    //Método que realiza la inserción anterior
    void insertAll(Tarea... tareas);

    //Anotación que permite realizar el borrado de un producto
    @Delete
    //Método que realiza el borrado anterior
    void delete(Tarea tarea);

    @Update
    // Método que realiza una operación de actualización
    void update(Tarea tarea);
}
