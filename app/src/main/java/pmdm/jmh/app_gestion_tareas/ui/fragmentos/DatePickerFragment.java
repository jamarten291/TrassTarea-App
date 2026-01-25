package pmdm.jmh.app_gestion_tareas.ui.fragmentos;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.IdRes;
import androidx.fragment.app.DialogFragment;

import java.time.LocalDate;
import java.util.Calendar;

import pmdm.jmh.app_gestion_tareas.controlador.HelperClass;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    private static final String ARG_ET_ID = "edit_text_id";

    public static DatePickerFragment newInstance(@IdRes int etId) {
        DatePickerFragment fragment = new DatePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_ET_ID, etId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker.
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it.
        return new DatePickerDialog(requireContext(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // El mes viene de 0‑11, lo convierto a 1‑12
        month += 1;

        LocalDate datePicked = LocalDate.of(year, month, day);
        String formattedDate = HelperClass.getFormattedDate(datePicked);

        if (getArguments() != null) {
            int resId = getArguments().getInt(ARG_ET_ID);

            // Llama a la actividad padre
            Activity parent = getActivity();
            if (parent != null) {
                EditText editText = parent.findViewById(resId);
                editText.setText(formattedDate);
            }
        }
    }
}
