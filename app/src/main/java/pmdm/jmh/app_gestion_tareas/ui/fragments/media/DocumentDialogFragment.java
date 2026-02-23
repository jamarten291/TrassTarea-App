package pmdm.jmh.app_gestion_tareas.ui.fragments.media;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import pmdm.jmh.app_gestion_tareas.R;

public class DocumentDialogFragment extends DialogFragment {
    private String documentUri;

    public DocumentDialogFragment(String documentUri) {
        this.documentUri = documentUri;
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
        return inflater.inflate(R.layout.dialog_document, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        WebView webView = view.findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(false); // no necesitamos JS

        try {
            Uri uri = Uri.fromFile(new File(documentUri));
            String mimeType = getMimeType(uri);

            InputStream inputStream = requireContext()
                    .getContentResolver()
                    .openInputStream(uri);

            if (inputStream != null) {
                String contenido = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                inputStream.close();

                if ("text/html".equals(mimeType)) {
                    webView.loadData(contenido, "text/html", "UTF-8");
                } else {
                    // TXT: lo envolvemos en HTML básico para que sea legible
                    String html = "<html><body><pre>"
                            + contenido.replace("&", "&amp;")
                            .replace("<", "&lt;")
                            .replace(">", "&gt;")
                            + "</pre></body></html>";
                    webView.loadData(html, "text/html", "UTF-8");
                }
            }

        } catch (IOException e) {
            Log.e("DocumentDialog", "Error al leer el documento", e);
            Toast.makeText(requireContext(), "Error al abrir el documento", Toast.LENGTH_LONG).show();
        }
    }

    private String getMimeType(Uri uri) {
        return requireContext().getContentResolver().getType(uri);
    }
}