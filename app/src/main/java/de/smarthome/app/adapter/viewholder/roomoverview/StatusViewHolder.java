package de.smarthome.app.adapter.viewholder.roomoverview;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Optional;

import de.smarthome.R;
import de.smarthome.app.model.Function;
import de.smarthome.app.adapter.RoomOverviewAdapter;

public class StatusViewHolder extends RoomOverviewAdapter.ViewHolder{
    private TextView textViewName;
    private TextView textViewStatus;

    public StatusViewHolder(@NonNull ViewGroup parent,
                            @NonNull RoomOverviewAdapter.OnItemClickListener onItemClickListener,
                            @NonNull RoomOverviewAdapter adapter) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_status, parent, false));

        findViewById();
        setOnClickListener(onItemClickListener, adapter);
    }

    private void setOnClickListener(@NonNull RoomOverviewAdapter.OnItemClickListener onItemClickListener, @NonNull RoomOverviewAdapter adapter) {
        itemView.setOnClickListener(v -> {
            int position = getAdapterPosition();
            if (onItemClickListener != null && position != RecyclerView.NO_POSITION) {
                onItemClickListener.onItemClick(adapter.getFunctionAt(position));
            }
        });
    }

    private void findViewById() {
        textViewName = itemView.findViewById(R.id.textView_item);
        textViewStatus = itemView.findViewById(R.id.textView_item_status);
    }

    @Override
    public void onBindViewHolder(RoomOverviewAdapter.ViewHolder holder, int position, Function function,  Optional<String> value) {
        textViewName.setText(function.getName().replace("_", " "));

        value.ifPresent(s -> textViewStatus.setText(s));
    }
}