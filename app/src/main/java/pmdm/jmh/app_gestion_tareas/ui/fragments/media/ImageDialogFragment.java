package pmdm.jmh.app_gestion_tareas.ui.fragments.media;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import pmdm.jmh.app_gestion_tareas.R;

public class ImageDialogFragment extends DialogFragment {
    private final String imageUri;

    public ImageDialogFragment(String imageUri) {
        this.imageUri = imageUri;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_image, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView imageView = view.findViewById(R.id.image_view);
        imageView.setImageURI(Uri.parse(imageUri));

        // Cerrar el diálogo al tocar la imagen
        imageView.setOnClickListener(v -> dismiss());
    }
}