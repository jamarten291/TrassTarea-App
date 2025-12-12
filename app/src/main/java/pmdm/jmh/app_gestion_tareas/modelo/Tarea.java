package pmdm.jmh.app_gestion_tareas.modelo;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import pmdm.jmh.app_gestion_tareas.controlador.HelperClass;

public class Tarea implements Parcelable {
    private static int contId = 0;
    private final int id;
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

        this.id = contId++;
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
                .between(LocalDate.now(), this.fechaObjetivo);
    }

    public LocalDate getFechaLimite() {
        return fechaObjetivo;
    }

    public int getId() {
        return id;
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

    // ---------- Parcelable ----------
    protected Tarea(Parcel in) {
        id = in.readInt();
        titulo = in.readString();
        fechaCreacion = HelperClass.stringToDate(in.readString());
        fechaObjetivo = HelperClass.stringToDate(in.readString());
        progreso = in.readByte();
        prioritaria = in.readBoolean();
        descripcion = in.readString();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(titulo);
        dest.writeString(HelperClass.getFormattedDate(fechaCreacion));
        dest.writeString(HelperClass.getFormattedDate(fechaObjetivo));
        dest.writeByte(progreso);
        dest.writeBoolean(prioritaria);
        dest.writeString(descripcion);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Tarea> CREATOR = new Creator<Tarea>() {
        @Override
        public Tarea createFromParcel(Parcel in) {
            return new Tarea(in);
        }

        @Override
        public Tarea[] newArray(int size) {
            return new Tarea[size];
        }
    };
}
