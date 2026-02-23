package pmdm.jmh.app_gestion_tareas.ui.fragments;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import pmdm.jmh.app_gestion_tareas.R;

public class VideoDialogFragment extends DialogFragment {
    private String videoUri;

    public VideoDialogFragment(String videoUri) {
        this.videoUri = videoUri;
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
        return inflater.inflate(R.layout.dialog_video, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        VideoView videoView = view.findViewById(R.id.video_view);
        videoView.setVideoURI(Uri.parse(videoUri));
        videoView.start();
    }
}
