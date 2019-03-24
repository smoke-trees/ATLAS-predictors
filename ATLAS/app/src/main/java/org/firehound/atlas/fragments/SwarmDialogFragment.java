package org.firehound.atlas.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.firehound.atlas.R;
import org.firehound.atlas.models.EventDataModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class SwarmDialogFragment extends DialogFragment {
    private EventDataModel eventObject;

    @SuppressLint("ValidFragment")
    SwarmDialogFragment(EventDataModel eventObject) {
        this.eventObject = eventObject;
    }

    public SwarmDialogFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.swarm_dialog, null);
        TextView eventStatus, eventData;
        eventStatus = view.findViewById(R.id.event_status_text);
        eventData = view.findViewById(R.id.event_data_text);

        eventStatus.setText(String.format("Event status: %s", eventObject.getEventStatus().toString()));
        eventData.setText(String.format("Event data: %s", eventObject.getEventData()));

        builder.setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });



        return builder.create();
    }
}
