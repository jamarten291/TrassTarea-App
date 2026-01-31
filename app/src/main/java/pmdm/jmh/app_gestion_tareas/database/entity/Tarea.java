package pmdm.jmh.app_gestion_tareas.database.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import pmdm.jmh.app_gestion_tareas.util.HelperClass;

@Entity
public class Tarea implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private int id;

    @NonNull
    private String titulo;
    private String descripcion;

    @ColumnInfo(defaultValue = "0")
    private byte progreso;

    @NonNull
    private String fechaCreacion = HelperClass.dateToString(LocalDate.now());

    @NonNull
    private String fechaObjetivo = HelperClass.dateToString(LocalDate.now());

    @ColumnInfo(defaultValue = "0")
    private boolean prioritaria;

    private String URL_doc;
    private String URL_img;
    private String URL_aud;
    private String URL_vid;

    public Tarea(@NonNull String titulo, String descripcion, byte progreso, @NonNull String fechaCreacion, @NonNull String fechaObjetivo, boolean prioritaria, String URL_doc, String URL_img, String URL_aud, String URL_vid) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.progreso = progreso;
        this.fechaCreacion = fechaCreacion;
        this.fechaObjetivo = fechaObjetivo;
        this.prioritaria = prioritaria;
        this.URL_doc = URL_doc;
        this.URL_img = URL_img;
        this.URL_aud = URL_aud;
        this.URL_vid = URL_vid;
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

    public void setFechaCreacion(@NonNull String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public void setFechaObjetivo(@NonNull String fechaObjetivo) {
        this.fechaObjetivo = fechaObjetivo;
    }

    @NonNull
    public String getFechaCreacion() {
        return this.fechaCreacion;
    }

    @NonNull
    public String getFechaObjetivo() {
        return fechaObjetivo;
    }

    public LocalDate getFechaCreacionLocalDate() {
        return HelperClass.stringToDate(this.fechaCreacion);
    }

    public LocalDate getFechaObjetivoLocalDate() {
        return HelperClass.stringToDate(this.fechaObjetivo);
    }

    public boolean isPrioritaria() {
        return prioritaria;
    }

    public void setPrioritaria(boolean prioritaria) {
        this.prioritaria = prioritaria;
    }

    public long getDiasRestantes() {
        LocalDate fechaObjetivo = HelperClass.stringToDate(this.fechaObjetivo);

        return ChronoUnit
                .DAYS
                .between(LocalDate.now(), fechaObjetivo);
    }

    public LocalDate getFechaLimite() {
        return HelperClass.stringToDate(this.fechaObjetivo);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdTarea() {
        return this.id;
    }

    // Archivos adjuntos
    public String getURL_doc() {
        return URL_doc;
    }

    public void setURL_doc(String URL_doc) {
        this.URL_doc = URL_doc;
    }

    public String getURL_img() {
        return URL_img;
    }

    public void setURL_img(String URL_img) {
        this.URL_img = URL_img;
    }

    public String getURL_aud() {
        return URL_aud;
    }

    public void setURL_aud(String URL_aud) {
        this.URL_aud = URL_aud;
    }

    public String getURL_vid() {
        return URL_vid;
    }

    public void setURL_vid(String URL_vid) {
        this.URL_vid = URL_vid;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Tarea tarea = (Tarea) o;
        return id == tarea.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    // ---------- Parcelable ----------
    protected Tarea(Parcel in) {
        id = in.readInt();
        titulo = in.readString();
        fechaCreacion = in.readString();
        fechaObjetivo = in.readString();
        progreso = in.readByte();
        prioritaria = in.readBoolean();
        descripcion = in.readString();
        URL_img = in.readString();
        URL_aud = in.readString();
        URL_vid = in.readString();
        URL_doc = in.readString();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(titulo);
        dest.writeString(fechaCreacion);
        dest.writeString(fechaObjetivo);
        dest.writeByte(progreso);
        dest.writeBoolean(prioritaria);
        dest.writeString(descripcion);
        dest.writeString(URL_img);
        dest.writeString(URL_aud);
        dest.writeString(URL_vid);
        dest.writeString(URL_doc);
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
