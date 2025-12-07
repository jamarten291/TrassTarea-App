package pmdm.jmh.app_gestion_tareas.modelo;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Tarea {
    private String titulo;
    private String descripcion;

    // Byte debido a que se usarán valores entre 0 y 100
    private byte progreso;
    private LocalDate fechaCreacion;
    private LocalDate fechaObjetivo;
    private boolean prioritaria;

    public Tarea(String titulo, LocalDate fechaCreacion, LocalDate fechaObjetivo, byte progreso, boolean prioritaria, String descripcion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.progreso = progreso;
        this.fechaCreacion = fechaCreacion;
        this.fechaObjetivo = fechaObjetivo;
        this.prioritaria = prioritaria;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public byte getProgreso() {
        return progreso;
    }

    public void setProgreso(byte progreso) {
        this.progreso = progreso;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDate getFechaObjetivo() {
        return fechaObjetivo;
    }

    public void setFechaObjetivo(LocalDate fechaObjetivo) {
        this.fechaObjetivo = fechaObjetivo;
    }

    public boolean isPrioritaria() {
        return prioritaria;
    }

    public void setPrioritaria(boolean prioritaria) {
        this.prioritaria = prioritaria;
    }

    public long getDiasRestantes() {
        return ChronoUnit
                .DAYS
                .between(LocalDateTime.now(), this.fechaObjetivo);
    }

    public String getFechaLimiteFormateada() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return this.fechaObjetivo.format(format);
    }

    @NonNull
    @Override
    public String toString() {
        return "Tarea{" +
                "titulo='" + titulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", progreso=" + progreso +
                ", fechaCreacion=" + fechaCreacion +
                ", fechaObjetivo=" + fechaObjetivo +
                ", prioritaria=" + prioritaria +
                '}';
    }
}
