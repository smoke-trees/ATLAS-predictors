package org.firehound.atlas.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.firehound.atlas.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DeviceListRecyclerViewAdapter extends RecyclerView.Adapter<DeviceListRecyclerViewAdapter.DeviceListViewHolder> {
    private List<String> timeStamps;
    private List<String> hashes;
    private View.OnClickListener listener;

    public DeviceListRecyclerViewAdapter(List<String> timeStamps, List<String> hashes) {
        this.timeStamps = timeStamps;
        this.hashes = hashes;
    }

    @NonNull
    @Override
    public DeviceListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        RecyclerView.ViewHolder holder = new DeviceListViewHolder(view);
        holder.itemView.setOnClickListener(v -> {
            listener.onClick(v);
        });
        return new DeviceListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceListViewHolder holder, int position) {
        holder.timestampText.setText(String.format("Timestamp: %s", timeStamps.get(position)));
        holder.hashText.setText(hashes.get(position));
    }

    @Override
    public int getItemCount() {
        return timeStamps.size();
    }

    public void setClickListener(View.OnClickListener callback) {
        listener = callback;
    }

    class DeviceListViewHolder extends RecyclerView.ViewHolder {
        TextView timestampText, hashText;

        DeviceListViewHolder(@NonNull View itemView) {
            super(itemView);
            timestampText = itemView.findViewById(R.id.timestamp_text);
            hashText = itemView.findViewById(R.id.hash_text);
        }
    }
}
