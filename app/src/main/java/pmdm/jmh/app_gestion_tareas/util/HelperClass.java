package pmdm.jmh.app_gestion_tareas.util;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import pmdm.jmh.app_gestion_tareas.R;

public class HelperClass {

    /**
     * Muestra un AlertDialog básico con un botón de "Aceptar".
     * @param c Contexto desde el cual se lanzará el AlertDialog
     * @param title Título del AlertDialog
     * @param message Mensaje mostrado en el AlertDialog
     */
    public static void showBasicAlertDialog(Context c, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.alert_aceptar, (dialog, which) -> {});
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Muestra un AlertDialog básico con un botón de "Aceptar".
     * @param c Contexto desde el cual se lanzará el AlertDialog
     * @param resId1 Id del recurso en el que se guarda el título del AlertDialog
     * @param resId2 Id del recurso en el que se guarda el mensaje mostrado en el AlertDialog
     */
    public static void showBasicAlertDialog(Context c, int resId1, int resId2) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(resId1)
                .setMessage(resId2)
                .setPositiveButton(R.string.alert_aceptar, (dialog, which) -> {});
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Devuelve una cadena de la fecha recibida por parámetro en formato ISO_LOCAL_DATE.
     * @param dateTime Fecha a formatear
     * @return Cadena que contiene la fecha en formato estándar
     */
    public static String dateToString(LocalDate dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        return dateTime.format(formatter);
    }

    public static LocalDate stringToDate(String date) {
        return LocalDate.parse(date);
    }

}
