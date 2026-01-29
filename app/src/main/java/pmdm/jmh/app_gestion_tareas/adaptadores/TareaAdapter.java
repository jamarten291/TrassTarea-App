package pmdm.jmh.app_gestion_tareas.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pmdm.jmh.app_gestion_tareas.R;
import pmdm.jmh.app_gestion_tareas.controlador.HelperClass;
import pmdm.jmh.app_gestion_tareas.entidades.Tarea;
import pmdm.jmh.app_gestion_tareas.interfaces.DataArguments;
import pmdm.jmh.app_gestion_tareas.ui.crud.DescripcionActivity;

public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.TareaViewHolder>
    implements DataArguments {
    private List<Tarea> adaptadorTarea;
    private int posicion;

    public TareaAdapter(List<Tarea> adaptadorTarea) {
        this.adaptadorTarea = adaptadorTarea;
    }

    public List<Tarea> getDatos() {
        return adaptadorTarea;
    }

    public void setDatos(List<Tarea> datos) {
        this.adaptadorTarea = datos;
        notifyDataSetChanged();
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    @NonNull
    @Override
    public TareaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new TareaViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull TareaViewHolder holder, int position) {
        Tarea tareaActual = adaptadorTarea.get(position);
        holder.setTarea(tareaActual);

        //Si detectamos un click, hacemos que el atributo "posicion" del Adaptador
        //sea igual a la posición del elemento del RecyclerView donde se haga el click largo.
        //Así conseguimos guardar el elemento sobre el que tenemos que actuar.
        holder.itemView.setOnLongClickListener(v -> {
            setPosicion(holder.getBindingAdapterPosition());
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return adaptadorTarea.size();
    }

    public static class TareaViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNombreTarea;
        private ProgressBar progressTarea;
        private TextView tvFechaLimite;
        private TextView tvCantDias;
        private Context c;
        private Tarea tareaActual;

        public TareaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreTarea = itemView.findViewById(R.id.tv_nombre_tarea);
            progressTarea = itemView.findViewById(R.id.progress_tarea);
            tvFechaLimite = itemView.findViewById(R.id.tv_fecha_limite);
            tvCantDias = itemView.findViewById(R.id.tv_cant_dias);

            // Guarda el contexto del ItemView, desde el cual se podrá acceder a los recursos de la tarea
            c = itemView.getContext();

            itemView.setOnClickListener(this::onItemViewClick);
            itemView.setOnCreateContextMenuListener(
                    (menu, v, menuInfo) -> {
                        int position = getBindingAdapterPosition();

                        if (position == RecyclerView.NO_POSITION) return;

                        // Asigno como groupId el valor de la posición actual en el adaptador
                        menu.add(Menu.NONE, R.id.mc_editar,
                                position, R.string.mc_editar);
                        menu.add(Menu.NONE, R.id.mc_borrar,
                                position, R.string.mc_borrar);
                    });
        }

        public void setTarea(Tarea t) {
            tvNombreTarea.setText(t.getTitulo());
            progressTarea.setProgress(t.getProgreso(), true);
            tvFechaLimite.setText(HelperClass.dateToString(t.getFechaLimite()));

            if (t.isPrioritaria()) {
                // El operador | agrega otro flag sin perder los flags anteriores
                tvNombreTarea.setPaintFlags(tvNombreTarea.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);

                // Añado un drawable al inicio (left) y dependiendo de si es prioritaria o no, le asigno un ícono
                tvNombreTarea.setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(c, android.R.drawable.btn_star_big_on),
                        null,
                        null,
                        null
                );
            } else {
                tvNombreTarea.setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(c, android.R.drawable.btn_star_big_off),
                        null,
                        null,
                        null
                );
            }

            // Comprueba si la tarea no ha sido completada antes de su fecha de entrega
            if (t.getDiasRestantes() < 0) {
                tvCantDias.setText("0");
                tvCantDias.setTextColor(Color.RED);
            } else {
                tvCantDias.setText(String.valueOf(t.getDiasRestantes()));
            }

            // Comprueba si la tarea ha sido terminada
            if (t.getProgreso() == 100) {
                tvCantDias.setText("0");

                // Esta flag tacha el TextView si el progreso es 100 (es decir, tarea terminada)
                tvNombreTarea.setPaintFlags(tvCantDias.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }

            // Asigno la tarea actual al adaptador para que al dar click se acceda a sus propiedades
            tareaActual = t;
        }

        public void onItemViewClick(View v) {
            // Consigo la posición del adaptador para acceder a la tarea clickeada y mostrar su descripción
            int pos = getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                Intent intent = new Intent(c, DescripcionActivity.class);
                intent.putExtra(ARG_PARAM6, tareaActual.getDescripcion());
                intent.putExtra(ARG_IMAGEN, tareaActual.getURL_img());
                intent.putExtra(ARG_VIDEO, tareaActual.getURL_vid());
                intent.putExtra(ARG_AUDIO, tareaActual.getURL_aud());
                intent.putExtra(ARG_DOC, tareaActual.getURL_doc());
                c.startActivity(intent);
            }
        }
    }
}
